package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.implementations.pride.PridePeptideHit;
import com.compomics.peptizer.util.datatools.implementations.pride.PrideProteinHit;
import com.compomics.peptizer.util.datatools.implementations.pride.PrideSpectrum;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import uk.ac.ebi.pride.jaxb.model.GelFreeIdentification;
import uk.ac.ebi.pride.jaxb.model.Identification;
import uk.ac.ebi.pride.jaxb.model.PeptideItem;
import uk.ac.ebi.pride.jaxb.xml.PrideXmlReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:21:09
 * To change this template use File | Settings | File Templates.
 */
public class PrideXMLIterator implements PeptideIdentificationIterator {

    private HashMap<String, ArrayList<String>> spectrumToPrideID;
    private HashMap<String, ArrayList<PrideProteinHit>> peptideToProteinMap;

    private PrideXmlReader prideReader;
    private Iterator<String> spectrumIt;
    private int count = 0;

    public PrideXMLIterator(File aFile) {
        prideReader = new PrideXmlReader(aFile);
        createMaps();
        spectrumIt = spectrumToPrideID.keySet().iterator();
    }

    public PeptideIdentification next() {
        Vector<PeptizerPeptideHit> peptideHits = new Vector<PeptizerPeptideHit>();
        HashMap<SearchEngineEnum, Integer> advocates = new HashMap<SearchEngineEnum, Integer>();
        String spectrumId = spectrumIt.next();
        String experiment = spectrumId.substring(0, spectrumId.indexOf("_"));
        ArrayList<String> foundPeptides = new ArrayList<String>();
        int spectrumNumber = new Integer(spectrumId.substring(spectrumId.indexOf("_") + 1));
        for (String id : spectrumToPrideID.get(spectrumId)) {
            Identification currentIdentification = prideReader.getGelFreeIdentById(id);
            for (PeptideItem peptideItem : currentIdentification.getPeptideItem()) {
                SearchEngineEnum searchEngine = getSearchEngine(currentIdentification.getSearchEngine());
                if (spectrumNumber == peptideItem.getSpectrum().getId() && !foundPeptides.contains(peptideItem.getSequence())) {
                    peptideHits.add(new PridePeptideHit(peptideItem, currentIdentification, searchEngine, peptideToProteinMap.get(peptideItem.getSequence())));
                    advocates.put(searchEngine, 0);
                    foundPeptides.add(peptideItem.getSequence());
                }
            }
        }
        PrideSpectrum spectrum = new PrideSpectrum(prideReader.getSpectrumById(spectrumNumber + ""), experiment);
        count++;
        return new PeptideIdentification(spectrum, peptideHits, new Advocate(advocates));
    }

    public boolean hasNext() {
        return spectrumIt.hasNext();
    }

    public void remove() {
        //never called.
    }

    public int estimateSize() {
        return spectrumToPrideID.size();
    }

    public int estimateToDo() {
        return spectrumToPrideID.size() - count;
    }

    public String getCurrentFileDescription() {
        return "Pride xml file";
    }

    public String getGeneralDescription() {
        return getCurrentFileDescription();
    }

    private void createMaps() {
        spectrumToPrideID = new HashMap<String, ArrayList<String>>();
        peptideToProteinMap = new HashMap<String, ArrayList<PrideProteinHit>>();

        GelFreeIdentification currentIdentification;
        String spectrumId, experimentId;
        Integer start, end;
        String accession, peptide;
        SearchEngineEnum searchEngine;
        for (String identification : prideReader.getGelFreeIdentIds()) {
            experimentId = prideReader.getExpAccession();
            currentIdentification = prideReader.getGelFreeIdentById(identification);
            accession = currentIdentification.getAccession();
            searchEngine = getSearchEngine(currentIdentification.getSearchEngine());
            for (PeptideItem peptideItem : currentIdentification.getPeptideItem()) {
                spectrumId = experimentId + "_" + peptideItem.getSpectrum().getId();
                peptide = peptideItem.getSequence();
                try {
                    start = peptideItem.getStart().intValue();
                } catch (Exception e) {
                    start = null;
                }
                try {
                    end = peptideItem.getEnd().intValue();
                } catch (Exception e) {
                    end = null;
                }
                if (spectrumToPrideID.containsKey(spectrumId)) {
                    spectrumToPrideID.get(spectrumId).add(identification);
                } else {
                    ArrayList<String> peptides = new ArrayList<String>();
                    peptides.add(identification);
                    spectrumToPrideID.put(spectrumId, peptides);
                }
                if (peptideToProteinMap.containsKey(peptide)) {
                    boolean found = false;
                    PrideProteinHit tempProt = new PrideProteinHit(accession, start, end, searchEngine);
                    for (PrideProteinHit proteinHit : peptideToProteinMap.get(peptide)) {
                        if (proteinHit.isSameAs(tempProt)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        peptideToProteinMap.get(peptide).add(tempProt);
                    }
                } else {
                    ArrayList<PrideProteinHit> proteins = new ArrayList<PrideProteinHit>();
                    proteins.add(new PrideProteinHit(accession, start, end, searchEngine));
                    peptideToProteinMap.put(peptide, proteins);
                }
            }
        }
    }

    private SearchEngineEnum getSearchEngine(String aString) {
        if (aString != null) {
            if (aString.toLowerCase().contains("mascot")) {
                return SearchEngineEnum.Mascot;
            } else if (aString.toLowerCase().contains("omssa")) {
                return SearchEngineEnum.OMSSA;
            } else if (aString.toLowerCase().contains("tandem")) {
                return SearchEngineEnum.XTandem;
            } else if (aString.toLowerCase().contains("sequest")) {
                return SearchEngineEnum.Sequest;
            }
        }
        return SearchEngineEnum.unknown; // Search engine not recognized by this crappy indexing strategy
    }
}
