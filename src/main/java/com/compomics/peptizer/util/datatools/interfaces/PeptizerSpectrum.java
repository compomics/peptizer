package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;


/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 20.03.2009
 * Time: 17:40:32
 * To change this template use File | Settings | File Templates.
 */
public interface PeptizerSpectrum {
    public PeptizerPeak[] getPeakList();


    public String getName();

    public String getChargeString();

    double getMinMZ();

    double getMaxMZ();

    double getMinIntensity();

    double getMaxIntensity();

    double getPrecursorMZ();

    public SearchEngineEnum getSearchEngineEnum();

    public Object getOriginalSpectrum();
}
