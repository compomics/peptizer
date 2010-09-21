package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 17:05:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class PeptizerPeak {
	// Class specific log4j logger for PeptizerPeak instances.
	 private static Logger logger = Logger.getLogger(PeptizerPeak.class);
    protected double iMZ;
    protected double iIntensity;

    public PeptizerPeak(double iMZ, double iIntensity) {
        this.iMZ = iMZ;
        this.iIntensity = iIntensity;
    }

    public PeptizerPeak() {
    }

    public double getMZ() {
        return iMZ;
    }

    public void setMZ(double iMZ) {
        this.iMZ = iMZ;
    }

    public double getIntensity() {
        return iIntensity;
    }

    public void setIntensity(double iIntensity) {
        this.iIntensity = iIntensity;
    }

    public abstract SearchEngineEnum getSearchEngineEnum();
}
