package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.Spectrum;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:26:31
 * To change this template use File | Settings | File Templates.
 */
public class PrideSpectrum implements PeptizerSpectrum, Serializable {

    private Spectrum originalSpectrum;

    private String experiment;

    public PrideSpectrum(Spectrum aSpectrum, String experiment) {
        originalSpectrum = aSpectrum;
        this.experiment = experiment;
    }

    public PeptizerPeak[] getPeakList() {
        Number[] mz = originalSpectrum.getMzNumberArray();
        Number[] intensity = originalSpectrum.getIntentArray();
        PeptizerPeak[] peaks = new PeptizerPeak[mz.length];
        for (int i = 0; i < mz.length; i++) {
            peaks[i] = new PridePeak(mz[i].doubleValue(), intensity[i].doubleValue());
        }
        return peaks;
    }

    public String getName() {
        return experiment + "_" + originalSpectrum.getId();
    }

    public String getChargeString() {
        for (CvParam cvParam : originalSpectrum.getSpectrumDesc().getPrecursorList().getPrecursor().get(0).getIonSelection().getCvParam()) {
            if (cvParam.getName().equals("PSI:1000041")) {
                return cvParam.getValue();
            }
        }
        return "NA";
    }

    public double getMinMZ() {
        return originalSpectrum.getMzNumberArray()[0].doubleValue();
    }

    public double getMaxMZ() {
        return originalSpectrum.getMzNumberArray()[originalSpectrum.getMzNumberArray().length - 1].doubleValue();
    }

    public double getMinIntensity() {
        return originalSpectrum.getIntentArray()[0].doubleValue();
    }

    public double getMaxIntensity() {
        return originalSpectrum.getIntentArray()[originalSpectrum.getIntentArray().length - 1].doubleValue();
    }

    public double getPrecursorMZ() {
        for (CvParam cvParam : originalSpectrum.getSpectrumDesc().getPrecursorList().getPrecursor().get(0).getIonSelection().getCvParam()) {
            if (cvParam.getName().equals("PSI:1000040")) {
                return new Double(cvParam.getValue());
            }
        }
        return -1;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return null;
    }

    public Object getOriginalSpectrum() {
        return originalSpectrum;
    }
}
