package com.compomics.peptizer.util.datatools.implementations.xtandem;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.xtandemparser.xtandem.Spectrum;
import de.proteinms.xtandemparser.xtandem.SupportData;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 16:44:18
 * To change this template use File | Settings | File Templates.
 */
public class XTandemSpectrum implements PeptizerSpectrum, Serializable {
	// Class specific log4j logger for XTandemSpectrum instances.
	 private static Logger logger = Logger.getLogger(XTandemSpectrum.class);

    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.XTandem;
    private Spectrum iSpectrum;
    private SupportData iSupportData;

    public XTandemSpectrum() {

    }

    public XTandemSpectrum(Spectrum aSpectrum, SupportData aSupportData) {
        iSpectrum = aSpectrum;
        iSupportData = aSupportData;
    }

    public PeptizerPeak[] getPeakList() {
        ArrayList<Double> mzValues = new ArrayList();
        mzValues = iSupportData.getXValuesFragIonMass2Charge();
        ArrayList<Double> intValues = new ArrayList();
        intValues = iSupportData.getYValuesFragIonMass2Charge();
        XTandemPeak[] peakList = new XTandemPeak[mzValues.size()];
        for (int i = 0; i < peakList.length; i++) {
            peakList[i] = new XTandemPeak(mzValues.get(i), intValues.get(i));
        }
        return peakList;
    }

    public String getName() {
        return iSupportData.getFragIonSpectrumDescription();
    }

    public String getChargeString() {
        return new Integer(iSpectrum.getPrecursorCharge()).toString();
    }

    public double getMinMZ() {
        ArrayList<Double> mzValues = new ArrayList();
        mzValues = iSupportData.getXValuesFragIonMass2Charge();
        double min = Collections.min(mzValues);
        return min;
    }

    public double getMaxMZ() {
        ArrayList<Double> mzValues = new ArrayList();
        mzValues = iSupportData.getXValuesFragIonMass2Charge();
        double max = Collections.max(mzValues);
        return max;
    }

    public double getMinIntensity() {
        ArrayList<Double> intValues = new ArrayList();
        intValues = iSupportData.getYValuesFragIonMass2Charge();
        double min = Collections.min(intValues);
        return min;
    }

    public double getMaxIntensity() {
        ArrayList<Double> intValues = new ArrayList();
        intValues = iSupportData.getYValuesFragIonMass2Charge();
        double max = Collections.max(intValues);
        return max;
    }

    public double getPrecursorMZ() {
        return iSpectrum.getPrecursorMh();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public Spectrum getOriginalSpectrum() {
        return iSpectrum;
    }
}
