package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.Peak;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 16:49:28
 * To change this template use File | Settings | File Templates.
 */
public class MascotSpectrum implements PeptizerSpectrum, Serializable {
    private SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.Mascot;
    private Spectrum aSpectrum;

    public MascotSpectrum() {

    }

    public MascotSpectrum(Spectrum aSpectrum) {
        this.aSpectrum=aSpectrum;
    }

    public void setSpectrum(Spectrum aSpectrum) {
        this.aSpectrum = aSpectrum;
    }

    public PeptizerPeak[] getPeakList() {
        Peak[] mascotPeakList = aSpectrum.getPeakList();
        MascotPeak[] peptizerPeakList =  new MascotPeak[mascotPeakList.length];
        for (int i = 0; i < peptizerPeakList.length; i++) {
            peptizerPeakList[i]= new MascotPeak(mascotPeakList[i]);
        }
        return peptizerPeakList;
    }

    public String getFilename() {
        return aSpectrum.getFilename();
    }

    public String getChargeString() {
        return aSpectrum.getChargeString();
    }

    public double getMinMZ() {
        return aSpectrum.getMinMZ();
    }

    public double getMaxMZ() {
        return aSpectrum.getMaxMZ();
    }

    public double getMinIntensity() {
        return aSpectrum.getMinIntensity() ;
    }

    public double getMaxIntensity() {
        return aSpectrum.getMaxIntensity();
    }

    public double getPrecursorMZ() {
        return aSpectrum.getPrecursorMZ();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public Spectrum getOriginalSpectrum() {
        return aSpectrum;
    }
}
