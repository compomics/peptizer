package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:26:01
 * To change this template use File | Settings | File Templates.
 */
public class PridePeak extends PeptizerPeak implements Serializable {

    public PridePeak() {
    }

    public PridePeak(double mz, double abundance) {
        super.iMZ = mz;
        super.iIntensity = abundance;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return null;
    }
}
