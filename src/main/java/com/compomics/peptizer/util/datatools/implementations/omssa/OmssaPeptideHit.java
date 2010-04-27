package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.*;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 15:17:38
 * To change this template use File | Settings | File Templates.
 */
public class OmssaPeptideHit extends PeptizerPeptideHit implements Serializable {
    private MSHits msHits;
    public HashMap<Integer, OmssaModification> modifs;
    private MSSearchSettings msSearchSettings;
    private int msResponseScale;

    /**
     * The final annotation types available.
     */
    private final ArrayList<AnnotationType> iAnnotationType = createAnnotationType();

    public OmssaPeptideHit(MSHits msHits, HashMap modifs, MSSearchSettings msSearchSettings, int msResponseScale, int rank) {
        originalPeptideHits.put(SearchEngineEnum.OMSSA, msHits);
        advocate = new Advocate(SearchEngineEnum.OMSSA, rank);
        annotationType = createAnnotationType();
        this.msHits = msHits;
        this.modifs = modifs;
        this.msSearchSettings = msSearchSettings;
        this.msResponseScale = msResponseScale;
    }

    private ArrayList<AnnotationType> createAnnotationType() {
        ArrayList<AnnotationType> result = new ArrayList();
        AnnotationType all = new AnnotationType("OMSSA", 0, SearchEngineEnum.OMSSA);
        result.add(all);
//        AnnotationType nonSuspect = new AnnotationType("Non-suspect", 1);
//        result.add(nonSuspect);
        return result;
    }

    public String getSequence() {
        return msHits.MSHits_pepstring;
    }

    public String[] decomposeSequence(String sequence) {
        String[] decomposedSequence = new String[sequence.length()];
        for (int i = 0; i < sequence.length(); i++) {
            Character a = new Character(sequence.charAt(i));
            decomposedSequence[i] = a.toString();
        }
        return decomposedSequence;
    }

    public String getModifiedSequence() {

        String[] decomposedSequence = decomposeSequence(getSequence());
        // First handle the fixed modifications
        List<Integer> fixedModifications = msSearchSettings.MSSearchSettings_fixed.MSMod;

        for (int i = 0; i < fixedModifications.size(); i++) {
            OmssaModification currentmodification = modifs.get(fixedModifications.get(i).intValue());
            String modType = currentmodification.getModName();
            Vector<String> modResidue = currentmodification.getModResidues();
            /* The following cases are not taken into account:
            - modifications at the begining or end of a protein
            - multiple modifications on the same AA or terminus.
             */
            boolean nModified = false;
            boolean cModified = false;
            if (currentmodification.getModType() == OmssaModification.MODAA) {
                for (int j = 0; j < decomposedSequence.length; j++) {
                    for (int k = 0; k < modResidue.size(); k++) {
                        if (decomposedSequence[j].compareTo(modResidue.get(k)) == 0) {
                            decomposedSequence[j] = modResidue.get(k) + "<" + modType + ">";
                        }
                    }
                }
            } else if (currentmodification.getModType() == OmssaModification.MODNP) {
                if (nModified) {
                    decomposedSequence[0] += "<" + modType + ">";
                } else {
                    decomposedSequence[0] = "<" + modType + ">";
                    nModified = true;
                }
            } else if (currentmodification.getModType() == OmssaModification.MODNPAA && decomposedSequence[0].compareTo(modResidue.get(0)) == 0) {
                if (nModified) {
                    decomposedSequence[0] += "<" + modType + ">";
                } else {
                    decomposedSequence[0] = "<" + modType + ">";
                }
            } else if (currentmodification.getModType() == OmssaModification.MODCP) {
                if (cModified) {
                    decomposedSequence[decomposedSequence.length - 1] += "<" + modType + ">";
                } else {
                    decomposedSequence[decomposedSequence.length - 1] = "<" + modType + ">";
                    cModified = true;
                }
            } else if (currentmodification.getModType() == OmssaModification.MODCPAA && decomposedSequence[decomposedSequence.length - 1].compareTo(modResidue.get(0)) == 0) {
                decomposedSequence[decomposedSequence.length - 1] = decomposedSequence[decomposedSequence.length - 1] + "<" + modType + ">";
            }
        }

        // Then the variable modifications found in this hit
        List<MSModHit> hitModifs = msHits.MSHits_mods.MSModHit;
        for (int i = 0; i < hitModifs.size(); i++) {
            int mod = hitModifs.get(i).MSModHit_modtype.MSMod;
            String modName = modifs.get(mod).getModName();
            decomposedSequence[hitModifs.get(i).MSModHit_site] += "<" + modName + ">";
        }


        // Concat everything
        String modifiedSequence = "NH2-";
        for (int i = 0; i < decomposedSequence.length; i++) {
            modifiedSequence += decomposedSequence[i];
        }
        modifiedSequence += "-COOH";
        return modifiedSequence;
    }

    public JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification) {
        String[] decomposedSequence = decomposeSequence(getSequence());
        // First handle the fixed modifications
        List<Integer> fixedModifications = msSearchSettings.MSSearchSettings_fixed.MSMod;

        for (int i = 0; i < fixedModifications.size(); i++) {
            OmssaModification currentmodification = modifs.get(fixedModifications.get(i).intValue());
            String modType = currentmodification.getModName();
            Vector<String> modResidue = currentmodification.getModResidues();
            /* The following cases are not taken into account:
            - modifications at the begining or end of a protein
            - multiple modifications on the same AA or terminus.
             */
            if (currentmodification.getModType() == OmssaModification.MODAA) {
                for (int j = 0; j < decomposedSequence.length; j++) {
                    for (int k = 0; k < modResidue.size(); k++) {
                        if (decomposedSequence[j].compareTo(modResidue.get(k)) == 0) {
                            decomposedSequence[j] = modResidue.get(k) + "<" + modType + ">";
                        }
                    }
                }
            } else if (currentmodification.getModType() == OmssaModification.MODNP) {
                decomposedSequence[0] = decomposedSequence[0] + "<" + modType + ">";
            } else if (currentmodification.getModType() == OmssaModification.MODNPAA && decomposedSequence[0].compareTo(modResidue.get(0)) == 0) {
                decomposedSequence[0] = decomposedSequence[0] + "<" + modType + ">";
            } else if (currentmodification.getModType() == OmssaModification.MODCP) {
                decomposedSequence[decomposedSequence.length - 1] = decomposedSequence[decomposedSequence.length - 1] + "<" + modType + ">";
            } else if (currentmodification.getModType() == OmssaModification.MODCPAA && decomposedSequence[decomposedSequence.length - 1].compareTo(modResidue.get(0)) == 0) {
                decomposedSequence[decomposedSequence.length - 1] = decomposedSequence[decomposedSequence.length - 1] + "<" + modType + ">";
            }
        }

        // Then the variable modifications found in this hit
        List<MSModHit> hitModifs = msHits.MSHits_mods.MSModHit;
        for (int i = 0; i < hitModifs.size(); i++) {
            int mod = hitModifs.get(i).MSModHit_modtype.MSMod;
            String modName = modifs.get(mod).getModName();
            decomposedSequence[hitModifs.get(i).MSModHit_site] += "<" + modName + ">";
        }


        // Concat everything
        String modifiedSequence = "NH2-";
        for (int i = 0; i < decomposedSequence.length; i++) {
            modifiedSequence += decomposedSequence[i];
        }
        modifiedSequence += "-COOH";


        // look for the b and y hits
        boolean[] bHits = new boolean[decomposedSequence.length];
        boolean[] yHits = new boolean[decomposedSequence.length];
        List<MSMZHit> ionHits = msHits.MSHits_mzhits.MSMZHit;
        for (int i = 0; i < ionHits.size(); i++) {
            if (ionHits.get(i).MSMZHit_ion.MSIonType == 1) {
                bHits[ionHits.get(i).MSMZHit_number] = true;
            }
            if (ionHits.get(i).MSMZHit_ion.MSIonType == 4) {
                yHits[ionHits.get(i).MSMZHit_number] = true;
            }
        }

        decomposedSequence[0] = "NH2-" + decomposedSequence[0];
        decomposedSequence[decomposedSequence.length - 1] += "-COOH";

        // Color or underline according to the coverage
        if (bHits[0] && bHits[1]) {
            decomposedSequence[0] = "<u>" + decomposedSequence[0] + "</u>";
        }
        if (bHits[decomposedSequence.length - 1] && bHits[decomposedSequence.length - 2]) {
            decomposedSequence[0] = "<font color=\"red\">" + decomposedSequence[0] + "</font>";
        }
        if (bHits[decomposedSequence.length - 1] && bHits[decomposedSequence.length - 2]) {
            decomposedSequence[decomposedSequence.length - 1] = "<u>" + decomposedSequence[decomposedSequence.length - 1] + "</u>";
        }
        if (bHits[0] && bHits[1]) {
            decomposedSequence[decomposedSequence.length - 1] = "<font color=\"red\">" + decomposedSequence[decomposedSequence.length - 1] + "</font>";
        }
        for (int i = 1; i < decomposedSequence.length - 1; i++) {
            if (bHits[i] && bHits[i - 1]) {
                decomposedSequence[i] = "<u>" + decomposedSequence[i] + "</u>";
            }
            if (yHits[decomposedSequence.length - i - 1] && yHits[decomposedSequence.length - i - 2]) {
                decomposedSequence[i] = "<font color=\"red\">" + decomposedSequence[i] + "</font>";
            }
        }
        String coloredSequence = "<html>";
        for (int i = 0; i < decomposedSequence.length; i++) {
            coloredSequence += decomposedSequence[i];
        }
        modifiedSequence += "</html>";

        // Create label and set text.
        JLabel label = new JLabelImpl(coloredSequence.toString(), getSequence());
        return label;
    }

    public int getBTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(1);
    }

    public int getYTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(4);
    }

    public int getIonTag(int type) {
        int ionTag = 0;
        // Get the Ions found by OMSSA
        List<MSMZHit> ions = msHits.MSHits_mzhits.MSMZHit;
        // Select and sort the ions
        Vector<Boolean> goodIons = new Vector(getSequence().length());
        for (int i = 0; i < getSequence().length(); i++) {
            goodIons.add(i, false);
        }
        for (int i = 0; i < ions.size(); i++) {
            if (ions.get(i).MSMZHit_ion.MSIonType == type) {
                int l = getSequence().length();
                int n = ions.get(i).MSMZHit_number;
                goodIons.set(ions.get(i).MSMZHit_number, true);
            }
        }
        int tempMax = 0;
        for (int i = 0; i < goodIons.size(); i++) {
            if (goodIons.get(i)) {
                tempMax++;
                if (tempMax > ionTag) {
                    ionTag = tempMax;
                }
            } else tempMax = 0;
        }
        return ionTag;
    }

    public int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification) {
        int[] result = new int[3];
        List<MSMZHit> ions = msHits.MSHits_mzhits.MSMZHit;
        for (int i = 0; i < ions.size(); i++) {
            if (ions.get(i).MSMZHit_ion.MSIonType == 1) result[0]++;
            if (ions.get(i).MSMZHit_ion.MSIonType == 4) result[1]++;
            else result[2]++;
        }
        return result;
    }

    public double getExpectancy(double aConfidenceInterval) {
        return msHits.MSHits_evalue;
    }

    public double getTheoMass() {
        return msHits.MSHits_theomass / msResponseScale;
    }

    public double getDeltaMass() {
        return (double) (msHits.MSHits_mass - msHits.MSHits_theomass) / msResponseScale;
    }

    public ArrayList getProteinHits() {
        List<MSPepHit> omssaProteinHits = msHits.MSHits_pephits.MSPepHit;
        ArrayList peptizerProteinHits = new ArrayList();
        for (int i = 0; i < omssaProteinHits.size(); i++) {
            peptizerProteinHits.add(new OmssaProteinHit(omssaProteinHits.get(i)));
        }
        return peptizerProteinHits;
    }

    public String getDatabase(PeptideIdentification aPeptideIdentification) {
        return msSearchSettings.MSSearchSettings_db;
    }

    public HashMap getAnnotation(PeptideIdentification aPeptideIdentification, int id) {
        HashMap lAnnotationsMap = new HashMap();
        List mzHits = msHits.MSHits_mzhits.MSMZHit;
        PeptizerPeak[] peakList = aPeptideIdentification.getSpectrum().getPeakList();


        Vector lAnnotations = new Vector(mzHits.size());
        for (int i = 0; i < mzHits.size(); i++) {
            lAnnotations.add(new OmssaFragmentIon((MSMZHit) mzHits.get(i), getIonPeak((MSMZHit) mzHits.get(i), peakList)));
        }
        lAnnotationsMap.put(iAnnotationType.get(0).getIndex() + "" + SearchEngineEnum.OMSSA.getId() + "" + (id + 1), lAnnotations);
        /*
        lAnnotations.clear();
        for (int i=0 ; i < mzHits.size() ; i++) {
            if (((MSMZHit) mzHits.get(i)).MSMZHit_annotation.MSIonAnnot.MSIonAnnot_suspect != null) {
                if (!((MSMZHit) mzHits.get(i)).MSMZHit_annotation.MSIonAnnot.MSIonAnnot_suspect) {
                    lAnnotations.add(new OmssaFragmentIon((MSMZHit) mzHits.get(i), getIonPeak((MSMZHit) mzHits.get(i), peakList)));
                }
            } else {
                lAnnotations.add(new OmssaFragmentIon((MSMZHit) mzHits.get(i), getIonPeak((MSMZHit) mzHits.get(i), peakList)));
            }
        }
        lAnnotationsMap.put(iAnnotationType.get(1).getIndex() + "" + (id + 1), lAnnotations);
        */
        return lAnnotationsMap;
    }

    private OmssaPeak getIonPeak(MSMZHit ion, PeptizerPeak[] peakList) {
        OmssaPeak peak = new OmssaPeak();
        double distanceMin = (peakList[0].getMZ() - ion.MSMZHit_mz / msResponseScale) * (peakList[0].getMZ() - ion.MSMZHit_mz / msResponseScale);
        double distance;
        for (int i = 0; i < peakList.length; i++) {
            distance = (peakList[i].getMZ() - ion.MSMZHit_mz / msResponseScale) * (peakList[i].getMZ() - ion.MSMZHit_mz / msResponseScale);
            if (distance < distanceMin) {
                peak = new OmssaPeak(peakList[i].getMZ(), peakList[i].getIntensity());
                distanceMin = distance;
            } else if (distance == distanceMin && peakList[i].getIntensity() > peak.getIntensity()) {
                peak = new OmssaPeak(peakList[i].getMZ(), peakList[i].getIntensity());
            }
        }
        return peak;
    }

    private ArrayList<AnnotationType> getOMSSAAnnotationType() {
        return iAnnotationType;
    }

    public double calculateThreshold(double aConfidenceInterval) {
        return -1;
    }

    public boolean scoresAboveThreshold(double anEValue) {
        return (msHits.MSHits_evalue <= anEValue);
    }

    public double calculateThreshold() {
        return -1;
    }

    public boolean scoresAboveThreshold() {
        // Set eValue to the current EValue from the configuration.
        double eValue = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_OMSSA_EVALUE"));
        return scoresAboveThreshold(eValue);
    }

    public double getIonsScore() {
        return -1; // There is no ion score for Omssa
    }

    public double getHomologyThreshold() {
        return -1; // There is no homology threshold for Omssa
    }

    public List<Integer> getFixedModifications() {
        return msSearchSettings.MSSearchSettings_fixed.MSMod;
    }

    /**
     * Private implementation to display a PeptideSequence for the toString() instead of an object reference. (CSV output
     * uses toString!)
     */
    private class JLabelImpl extends JLabel {

        String iName = "";

        /**
         * Creates a <code>JLabel</code> instance with the specified text. The label is aligned against the leading edge of
         * its display area, and centered vertically.
         *
         * @param text The text to be displayed by the label.
         */
        public JLabelImpl(String text, String aName) {
            super(text);
            iName = aName;
        }


        /**
         * Returns a string representation of this component and its values.
         *
         * @return a string representation of this component
         * @since JDK1.0
         */
        @Override
        public String toString() {
            return iName;
        }
    }
}
