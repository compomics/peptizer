package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 08.04.2009
 * Time: 10:20:31
 * To change this template use File | Settings | File Templates.
 */
public class OmssaPeak extends PeptizerPeak {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.OMSSA;

    public OmssaPeak() {
    }

    public OmssaPeak(double mz, double abundance) {
        super.iMZ = mz;
        super.iIntensity = abundance;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

}
