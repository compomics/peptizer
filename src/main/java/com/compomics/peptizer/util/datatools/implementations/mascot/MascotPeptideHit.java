package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.interfaces.Modification;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.*;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

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
	// Class specific log4j logger for MascotPeptideHit instances.
	 private static Logger logger = Logger.getLogger(MascotPeptideHit.class);

    private PeptideHit iPeptideHit;
    private ArrayList<PeptizerModification> modifications;

    public MascotPeptideHit(PeptideHit aPeptideHit, int rank) {
        iPeptideHit = aPeptideHit;
        originalPeptideHits.put(SearchEngineEnum.Mascot, aPeptideHit);
        advocate = new Advocate(SearchEngineEnum.Mascot, rank);
        annotationType = createAnnotationType();
        importModifications();
    }

    private void importModifications() {
        modifications = new ArrayList<PeptizerModification>();
        for (int i = 0; i < iPeptideHit.getModifications().length; i++) {
            Modification mod = iPeptideHit.getModifications()[i];
            if (mod != null) {
                modifications.add(new MascotModification(mod, i));
            }
        }
    }

    public String getSequence() {
        return iPeptideHit.getSequence();
    }


    private ArrayList<AnnotationType> createAnnotationType() {
        ArrayList<AnnotationType> result = new ArrayList();
        AnnotationType mascot = new AnnotationType("Mascot", 0, SearchEngineEnum.Mascot);
        AnnotationType fuse = new AnnotationType("Fuse", 1, SearchEngineEnum.Mascot);
        result.add(mascot);
        result.add(fuse);
        return result;
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

    public Double getExpectancy(double aConfidenceInterval) {
        return iPeptideHit.getExpectancy(aConfidenceInterval);
    }

    public Double getTheoMass() {
        return iPeptideHit.getPeptideMr();
    }

    public Double getDeltaMass() {
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

    public Double calculateThreshold(double aConfidenceInterval) {
        return iPeptideHit.calculateIdentityThreshold(aConfidenceInterval);
    }

    public boolean scoresAboveThreshold(double aConfidenceInterval) {
        return iPeptideHit.scoresAboveIdentityThreshold(aConfidenceInterval);
    }

    public Double calculateThreshold() {
        // Set iAlpha to the current Alpha from the configuration.
        double iAlpha = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        return calculateThreshold(iAlpha);
    }

    public boolean scoresAboveThreshold() {
        // Set iAlpha to the current Alpha from the configuration.
        double iAlpha = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        return scoresAboveThreshold(iAlpha);
    }

    public Double getIonsScore() {
        return iPeptideHit.getIonsScore();
    }

    public Double getHomologyThreshold() {
        return iPeptideHit.getHomologyThreshold();
    }

    @Override
    public ArrayList<PeptizerModification> getModifications() {
        return modifications;
    }

}
