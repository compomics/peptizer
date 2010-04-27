package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.xtandem.XTandemPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.xtandem.XTandemSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.PeptideMap;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.XTandemFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 21:51:34
 * To change this template use File | Settings | File Templates.
 */
public class XTandemIterator implements PeptideIdentificationIterator {

    /**
     * The XTandem file instance of the Iterator.
     */
    private XTandemFile iXTandemfile = null;

    private File iOutputFile;
    /**
     * Integer index to track the iterator. This represents the index of the next PeptideIdentification that will be
     * returned when the next method is invoked.
     */
    private int iCountIndex = 0;

    public XTandemIterator(File aOutputFile) throws SAXException {
        iXTandemfile = new XTandemFile(aOutputFile.getPath());
        iOutputFile = aOutputFile;
    }

    public PeptideIdentification next() {
        if (hasNext()) {
            // Get the information of the next.
            // QueryList is a Vector, 0 returns Query 1 whereas the QueryToPeptideMap returns Query 1 for if 1 is given as a parameter.
            ArrayList<Spectrum> spectraList = iXTandemfile.getSpectraList();
            XTandemSpectrum lSpectrum = new XTandemSpectrum(spectraList.get(iCountIndex), iXTandemfile.getSupportData(iCountIndex + 1));

            // Get the peptide map.
            PeptideMap lPeptideMap = iXTandemfile.getPeptideMap();

            if (lPeptideMap != null) {
                ArrayList<Peptide> xTandemPeptides = lPeptideMap.getAllPeptides(iCountIndex + 1);
                int hitsNumber = 0;
                if (xTandemPeptides != null) {
                    hitsNumber = xTandemPeptides.size();
                }
                Vector peptizerPeptideHits = new Vector(hitsNumber);
                // translation into peptizer object
                for (int i = 0; i < hitsNumber; i++) {
                    if (xTandemPeptides.get(i) != null) {
                        peptizerPeptideHits.add(new XTandemPeptideHit((Peptide) xTandemPeptides.get(i), iXTandemfile, i + 1));
                    }
                }
                // Create a new PeptideIdentification and shift the index.
                PeptideIdentification lPeptideIdentification = new PeptideIdentification(lSpectrum, peptizerPeptideHits, SearchEngineEnum.XTandem);
                iCountIndex++;
                return lPeptideIdentification;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        boolean lResult = false;
        if (iXTandemfile != null) {
            if (iCountIndex < iXTandemfile.getSpectraNumber()) {
                lResult = true;
            }
        }
        return lResult;
    }

    public void remove() {
    }

    public int estimateSize() {
        return iXTandemfile.getSpectraNumber();
    }

    public int estimateToDo() {
        return (iXTandemfile.getSpectraNumber() - iCountIndex);
    }

    public String getCurrentFileDescription() {
        String s = "";
        if (iOutputFile != null) {
            s = iOutputFile.getName();
        } else {
            s = "XTandem xml File";
        }
        return s;
    }

    public String getGeneralDescription() {
        return getCurrentFileDescription();
    }
}