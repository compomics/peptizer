package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:26:14
 * To change this template use File | Settings | File Templates.
 */
public class PridePeptideHit extends PeptizerPeptideHit implements Serializable {
	// Class specific log4j logger for PridePeptideHit instances.
	 private static Logger logger = Logger.getLogger(PridePeptideHit.class);

    // peptide relative informations
    private PeptideItem originalPeptideItem;
    private ArrayList<PrideFragmentIon> fragmentIons = new ArrayList<PrideFragmentIon>();
    private ArrayList<PrideProteinHit> proteins;
    private ArrayList<PeptizerModification> modifications;

    // identification general informations
    private Identification originalIdentification;

    private SearchEngineEnum searchEngine;

    public PridePeptideHit(PeptideItem peptideItem, Identification identification, SearchEngineEnum searchEngine, ArrayList<PrideProteinHit> proteins) {
        this.searchEngine = searchEngine;
        advocate = new Advocate(searchEngine, 0);
        annotationType = new ArrayList<AnnotationType>();
        annotationType.add(new AnnotationType("Pride", 0, searchEngine));
        originalIdentification = identification;
        originalPeptideItem = peptideItem;
        for (FragmentIon originalFragmentIon : originalPeptideItem.getFragmentIon()) {
            fragmentIons.add(new PrideFragmentIon(originalFragmentIon, searchEngine));
        }
        this.proteins = proteins;
        importModifications();
    }

    private void importModifications() {
        modifications = new ArrayList<PeptizerModification>();
        String name, accession;
        double deltaMass;
        int location;
        for (ModificationItem mod : originalPeptideItem.getModificationItem()) {
            name = mod.getModAccession();
            for (CvParam cvParam : mod.getAdditional().getCvParam()) {
                if (cvParam.getAccession().equals(name)) {
                    name = cvParam.getName();
                    break;
                }
            }
            name = name.replaceAll("-", "");
            accession = mod.getModAccession();
            deltaMass = new Double(mod.getModMonoDelta().get(0));
            location = mod.getModLocation().intValue();
            modifications.add(new PrideModification(name, accession, deltaMass, location, false, searchEngine));
        }
    }

    @Override
    public String getSequence() {
        return originalPeptideItem.getSequence();
    }

    @Override
    public int getBTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(IonTypeEnum.b);
    }

    @Override
    public int getYTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(IonTypeEnum.y);
    }

    private int getIonTag(IonTypeEnum ionType) {
        boolean[] goodIons = new boolean[originalPeptideItem.getSequence().length()];
        for (PrideFragmentIon fragmentIon : fragmentIons) {
            if (fragmentIon.getType() == ionType) {
                goodIons[fragmentIon.getNumber()] = true;
            }
        }
        int ionTag = 0;
        int tempMax = 0;
        for (boolean validated : goodIons) {
            if (validated) {
                tempMax++;
                if (tempMax > ionTag) {
                    ionTag = tempMax;
                }
            } else tempMax = 0;
        }
        return ionTag;
    }

    @Override
    public Double getTheoMass() {
        return null;
    }

    @Override
    public Double getDeltaMass() {
        return null;
    }

    @Override
    public ArrayList getProteinHits() {
        return proteins;
    }

    @Override
    public String getDatabase(PeptideIdentification aPeptideIdentification) {
        return originalIdentification.getDatabase();
    }

    @Override
    protected HashMap getAnnotation(PeptideIdentification aPeptideIdentification, int id) {
        Vector annotations = new Vector(fragmentIons);
        HashMap result = new HashMap();
        result.put(0 + "" + searchEngine.getId() + (id + 1), annotations);
        return result;
    }

    @Override
    public int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification) {
        int length = originalPeptideItem.getSequence().length();
        boolean[] b = new boolean[length];
        boolean[] y = new boolean[length];
        boolean[] all = new boolean[length];
        for (PrideFragmentIon ion : fragmentIons) {
            if (ion.getType() == IonTypeEnum.b) {
                b[ion.getNumber()] = true;
            } else if (ion.getType() == IonTypeEnum.y) {
                y[ion.getNumber()] = true;
            }
            all[ion.getNumber()] = true;
        }
        int[] sequenceCoverage = new int[3];
        for (int i = 0; i < all.length; i++) {
            if (b[i]) {
                sequenceCoverage[0]++;
            }
            if (y[i]) {
                sequenceCoverage[1]++;
            }
            if (all[i]) {
                sequenceCoverage[2]++;
            }
        }
        return sequenceCoverage;
    }

    @Override
    public Double getIonsScore() {
        switch (searchEngine) {
            case Mascot:
                return new Double(getAdditionalValue("PRIDE:0000069"));
            default:
                return 0.0;
        }
    }

    @Override
    public Double getHomologyThreshold() {
        switch (searchEngine) {
            case Mascot:
                return null;
            default:
                return null;
        }
    }

    @Override
    public Double calculateThreshold(double aConfidenceInterval) {
        return null;
    }

    @Override
    public boolean scoresAboveThreshold(double aConfidenceInterval) {
        return true;
    }

    @Override
    public Double getExpectancy(double aConfidenceInterval) {
        switch (searchEngine) {
            case Mascot:
                return new Double(getAdditionalValue("PRIDE:0000212"));
            case XTandem:
                return new Double(getAdditionalValue("PRIDE:0000183"));
            default:
                return null;
        }
    }

    @Override
    public Double calculateThreshold() {
        return null;
    }

    @Override
    protected boolean scoresAboveThreshold() {
        return true;
    }

    private String getAdditionalValue(String anAccession) {
        for (CvParam cvParam : originalPeptideItem.getAdditional().getCvParam()) {
            if (cvParam.getAccession().equals(anAccession)) {
                return cvParam.getValue();
            }
        }
        return null;
    }

    @Override
    public ArrayList<PeptizerModification> getModifications() {
        return modifications;
    }

}
