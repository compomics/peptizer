package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.*;

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
    private MSSearchSettings msSearchSettings;
    private int msResponseScale;
    private ArrayList<PeptizerModification> modifications = new ArrayList<PeptizerModification>();

    /**
     * The final annotation types available.
     */
    private final ArrayList<AnnotationType> iAnnotationType = createAnnotationType();

    public OmssaPeptideHit(MSHits msHits, HashMap modifs, MSSearchSettings msSearchSettings, int msResponseScale, int rank) {
        originalPeptideHits.put(SearchEngineEnum.OMSSA, msHits);
        advocate = new Advocate(SearchEngineEnum.OMSSA, rank);
        annotationType = createAnnotationType();
        this.msHits = msHits;
        this.msSearchSettings = msSearchSettings;
        this.msResponseScale = msResponseScale;
        importModifications(modifs);
    }

    private void importModifications(HashMap<Integer, de.proteinms.omxparser.util.OmssaModification> modifs) {

        /* The following cases are not taken into account:
       - fixed modifications
       - modifications at the begining or end of a protein
       - multiple modifications on the same AA or terminus.
        */

        int modType, index;
        String name;
        ArrayList<String> modResidues;
        double deltaMass;

        // inspect variable modifications
        for (MSModHit msModHit : msHits.MSHits_mods.MSModHit) {
            index = msModHit.MSModHit_modtype.MSMod;
            modType = modifs.get(index).getModType();
            modResidues = new ArrayList<String>(modifs.get(index).getModResidues());
            name = modifs.get(index).getModName();
            deltaMass = modifs.get(index).getModMonoMass();
            modifications.add(new OmssaModification(modType, modResidues, name, msModHit.MSModHit_site + 1, deltaMass, true));
        }
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

    public Double getExpectancy(double aConfidenceInterval) {
        return msHits.MSHits_evalue;
    }

    public Double getTheoMass() {
        return (double) msHits.MSHits_theomass / msResponseScale;
    }

    public Double getDeltaMass() {
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

    public Double calculateThreshold(double aConfidenceInterval) {
        return null;
    }

    public boolean scoresAboveThreshold(double anEValue) {
        return (msHits.MSHits_evalue <= anEValue);
    }

    public Double calculateThreshold() {
        return null;
    }

    public boolean scoresAboveThreshold() {
        // Set eValue to the current EValue from the configuration.
        double eValue = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_OMSSA_EVALUE"));
        return scoresAboveThreshold(eValue);
    }

    public Double getIonsScore() {
        return null; // There is no ion score for Omssa
    }

    public Double getHomologyThreshold() {
        return null; // There is no homology threshold for Omssa
    }

    public List<Integer> getFixedModifications() {
        return msSearchSettings.MSSearchSettings_fixed.MSMod;
    }

    @Override
    public ArrayList<PeptizerModification> getModifications() {
        return modifications;
    }

}
