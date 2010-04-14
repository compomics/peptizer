package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSSpectrum;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 18:09:38
 * To change this template use File | Settings | File Templates.
 */
public class OmssaSpectrum implements PeptizerSpectrum, Serializable {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.OMSSA;
    private int msResponseScale;
    private MSSpectrum aSpectrum;

    public OmssaSpectrum() {

    }

    public OmssaSpectrum(int aScale) {
        this.msResponseScale = aScale;
    }

    public void setMSSpectrum(MSSpectrum aSpectrum) {
        this.aSpectrum = aSpectrum;
    }


    public PeptizerPeak[] getPeakList() {
        OmssaPeak[] aPeakList = new OmssaPeak[aSpectrum.MSSpectrum_mz.MSSpectrum_mz_E.size()];
        for (int i = 0; i < aPeakList.length; i++) {
            aPeakList[i] = new OmssaPeak((double) aSpectrum.MSSpectrum_mz.MSSpectrum_mz_E.get(i) / msResponseScale, aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(i));
        }
        return aPeakList;
    }

    public String getName() {
        return aSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0);
    }

    public String getChargeString() {
        return aSpectrum.MSSpectrum_charge.MSSpectrum_charge_E.toString();
    }

    public double getMinMZ() {
        return (double) aSpectrum.MSSpectrum_mz.MSSpectrum_mz_E.get(0) / msResponseScale;
    }

    public double getMaxMZ() {
        return (double) aSpectrum.MSSpectrum_mz.MSSpectrum_mz_E.get(aSpectrum.MSSpectrum_mz.MSSpectrum_mz_E.size() - 1) / msResponseScale;
    }

    public double getMinIntensity() {
        Integer minimum = aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(0);
        for (int i = 0; i < aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.size(); i++) {
            if (aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(i) < minimum) {
                minimum = aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(i);
            }
        }
        return minimum.doubleValue();
    }

    public double getMaxIntensity() {
        Integer maximum = aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(0);
        for (int i = 0; i < aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.size(); i++) {
            if (aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(i) > maximum) {
                maximum = aSpectrum.MSSpectrum_abundance.MSSpectrum_abundance_E.get(i);
            }
        }
        return maximum.doubleValue();
    }

    public double getPrecursorMZ() {
        return (double) aSpectrum.MSSpectrum_precursormz / msResponseScale;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public MSSpectrum getOriginalSpectrum() {
        return aSpectrum;
    }
}
