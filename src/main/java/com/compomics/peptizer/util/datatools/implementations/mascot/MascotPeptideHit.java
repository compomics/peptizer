package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.*;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 16:34:22
 * To change this template use File | Settings | File Templates.
 */
public class MascotPeptideHit extends PeptizerPeptideHit implements Serializable {

    private PeptideHit iPeptideHit;


    public MascotPeptideHit(PeptideHit aPeptideHit, int rank) {
        originalPeptideHits.put(SearchEngineEnum.Mascot, aPeptideHit);
        advocate = new Advocate(SearchEngineEnum.Mascot, rank);
        annotationType = createAnnotationType();
        iPeptideHit = aPeptideHit;
    }

    public String getSequence() {
        return iPeptideHit.getSequence();
    }

    public String getModifiedSequence() {
        return iPeptideHit.getModifiedSequence();
    }

    private ArrayList<AnnotationType> createAnnotationType() {
        ArrayList<AnnotationType> result = new ArrayList();
        AnnotationType mascot = new AnnotationType("Mascot", 0, SearchEngineEnum.Mascot);
        AnnotationType fuse = new AnnotationType("Fuse", 1, SearchEngineEnum.Mascot);
        result.add(mascot);
        result.add(fuse);
        return result;
    }

    public JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification) {
        // Get peptidehitannotation object.
        PeptideHitAnnotation pha =
                iPeptideHit.getPeptideHitAnnotation((Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section));
        // Match ions.
        Vector ions = pha.getMatchedIonsByMascot(((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(), iPeptideHit.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = iPeptideHit.getSequence();
        int length = sequence.length();
        // Create Y and B boolean arrays.
        boolean[] yIons = new boolean[length];
        boolean[] bIons = new boolean[length];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {
                case FragmentIon.Y_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.Y_DOUBLE_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_DOUBLE_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;
                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply add formatting.
        String[] modifiedAA = iPeptideHit.getModifiedSequenceComponents();
        StringBuffer formattedSequence = new StringBuffer("<html>");
        // Cycle the amino acids (using b-ions indexing here).
        for (int i = 0; i < bIons.length; i++) {
            boolean italic = false;
            boolean bold = false;
            // First and last one only have 50% coverage anyway
            if (i == 0) {
                if (bIons[i]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    if (yIons[yIons.length - (i + 3)]) {
                        bold = true;
                    }
                }
            } else if (i == (length - 1)) {
                if (bIons[i] && bIons[i - 1]) {
                    if (bIons[i - 2]) {
                        italic = true;
                    }
                }
                if (yIons[yIons.length - (i + 1)]) {
                    bold = true;
                }
            } else {
                // Aha, two ions needed here.
                if (bIons[i] && bIons[i - 1]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    bold = true;
                }
            }
            // Actually add the next char.
            formattedSequence.append(
                    (italic ? "<u>" : "") +
                            (bold ? "<font color=\"red\">" : "") +
                            modifiedAA[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;") +
                            (italic ? "</u>" : "") +
                            (bold ? "</font>" : "")
            );
        }
        // Finalize HTML'ized label text.
        formattedSequence.append("</html>");

        // Create label and set text.
        JLabel label = new JLabelImpl(formattedSequence.toString(), sequence);
        return label;
    }

    public Object getOriginalPeptideHit() {
        return iPeptideHit;
    }

    public int getBTag(PeptideIdentification aPeptideIdentification) {
        // Get peptidehitannotation object.
        PeptideHitAnnotation pha =
                iPeptideHit.getPeptideHitAnnotation((Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section));
        // Match Mascot ions.
        Vector ions = pha.getMatchedIonsByMascot(((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(), iPeptideHit.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = iPeptideHit.getSequence();
        int length = sequence.length();
        // Create B boolean array.
        boolean[] bIons = new boolean[length - 1];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {

                case FragmentIon.B_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    break;

                case FragmentIon.B_DOUBLE_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    break;
                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply count!
        int lLength = 0;
        int lMaxLength = 0;
        int lMaxIndex = 0;
        for (int i = 0; i < bIons.length; i++) {
            if (bIons[i]) {
                lLength++;
                if (lLength > lMaxLength) {
                    lMaxLength = lLength;
                    lMaxIndex = i - (lLength - 1);
                }
            } else {
                lLength = 0;
            }
        }
        //result = sequence.substring(lMaxIndex, lMaxIndex+lMaxLength);
        return lMaxLength;
    }

    public int getYTag(PeptideIdentification aPeptideIdentification) {
        // Get peptidehitannotation object.
        PeptideHitAnnotation pha =
                iPeptideHit.getPeptideHitAnnotation((Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section));
        // Match Mascot ions.
        Vector ions = pha.getMatchedIonsByMascot(((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(), iPeptideHit.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = iPeptideHit.getSequence();
        int length = sequence.length();
        // Create Y boolean array.
        boolean[] yIons = new boolean[length - 1];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {

                case FragmentIon.Y_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    break;

                case FragmentIon.Y_DOUBLE_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    break;
                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply count!
        int lLength = 0;
        int lMaxLength = 0;
        int lMaxIndex = 0;
        for (int i = 0; i < yIons.length; i++) {
            if (yIons[i]) {
                lLength++;
                if (lLength > lMaxLength) {
                    lMaxLength = lLength;
                    lMaxIndex = i - (lLength - 1);
                }
            } else {
                lLength = 0;
            }
        }
        //result = sequence.substring(lMaxIndex, lMaxIndex+lMaxLength);
        return lMaxLength;
    }

    public double getExpectancy(double aConfidenceInterval) {
        return iPeptideHit.getExpectancy(aConfidenceInterval);
    }

    public double getTheoMass() {
        return iPeptideHit.getPeptideMr();
    }

    public double getDeltaMass() {
        return iPeptideHit.getDeltaMass();
    }

    public String getDatabase(PeptideIdentification aPeptideIdentification) {
        return ((Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section)).getDatabase();
    }

    public ArrayList getProteinHits() {
        ArrayList mascotProteinHits = iPeptideHit.getProteinHits();
        ArrayList peptizerProteinHits = new ArrayList();
        for (int i = 0; i < mascotProteinHits.size(); i++) {
            peptizerProteinHits.add(new MascotProteinHit((ProteinHit) mascotProteinHits.get(i)));
        }
        return peptizerProteinHits;
    }

    public int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification) {
        return iPeptideHit.getPeptideHitAnnotation((Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section))
                .getMascotIonCoverage(((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(), iPeptideHit.getPeaksUsedFromIons1());
    }

    public HashMap getAnnotation(PeptideIdentification aPeptideIdentification, int id) {
        HashMap lAnnotationsMap = new HashMap();
        // Local variables.
        Vector mAnnotations = null;
        PeptideHitAnnotation lPeptideHitAnnotation = null;

        lPeptideHitAnnotation = iPeptideHit.getPeptideHitAnnotation(
                (Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section),
                (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section),
                aPeptideIdentification.getSpectrum().getPrecursorMZ(),
                aPeptideIdentification.getSpectrum().getChargeString());

        // MascotMatched Ions
        mAnnotations = lPeptideHitAnnotation.getMatchedIonsByMascot(
                ((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(),
                iPeptideHit.getPeaksUsedFromIons1());
        int mlength = 0;
        if (mAnnotations != null) {
            mlength = mAnnotations.size();
        }
        Vector pAnnotations = new Vector(mlength);
        for (int i = 0; i < mlength; i++) {
            pAnnotations.add(i, new MascotFragmentIon((FragmentIon) mAnnotations.get(i)));
        }

        lAnnotationsMap.put(annotationType.get(0).getIndex() + "" + SearchEngineEnum.Mascot.getId() + "" + (id + 1), pAnnotations);

        /*
        // B/Y ions
        lAnnotations = lPeptideHitAnnotation.getMatchedBYions(aPeptideIdentification.getSpectrum().getPeakList());
        lAnnotationsMap.put(RDB_MASCOT + "" + (i + 1), lAnnotations);
        */

        // FusedMatched Ions
        double lIntensityPercentage =
                Double.parseDouble(MatConfig.getInstance().getGeneralProperty("FUSED_INTENSITY_PERCENTAGE"));
        mAnnotations = lPeptideHitAnnotation.getFusedMatchedIons(
                ((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(),
                iPeptideHit.getPeaksUsedFromIons1(),
                aPeptideIdentification.getSpectrum().getMaxIntensity(),
                lIntensityPercentage);
        mlength = 0;
        if (mAnnotations != null) {
            mlength = mAnnotations.size();
        }
        pAnnotations = new Vector(mlength);
        for (int i = 0; i < mlength; i++) {
            pAnnotations.add(i, new MascotFragmentIon((FragmentIon) mAnnotations.get(i)));
        }
        lAnnotationsMap.put(annotationType.get(1).getIndex() + "" + SearchEngineEnum.Mascot.getId() + "" + (id + 1), pAnnotations);
        // Returns the HashMap with annotation.
        return lAnnotationsMap;
    }

    public double calculateThreshold(double aConfidenceInterval) {
        return iPeptideHit.calculateIdentityThreshold(aConfidenceInterval);
    }

    public boolean scoresAboveThreshold(double aConfidenceInterval) {
        return iPeptideHit.scoresAboveIdentityThreshold(aConfidenceInterval);
    }

    public double calculateThreshold() {
        // Set iAlpha to the current Alpha from the configuration.
        double iAlpha = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        return calculateThreshold(iAlpha);
    }

    public boolean scoresAboveThreshold() {
        // Set iAlpha to the current Alpha from the configuration.
        double iAlpha = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        return scoresAboveThreshold(iAlpha);
    }

    public double getIonsScore() {
        return iPeptideHit.getIonsScore();
    }

    public double getHomologyThreshold() {
        return iPeptideHit.getHomologyThreshold();
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
