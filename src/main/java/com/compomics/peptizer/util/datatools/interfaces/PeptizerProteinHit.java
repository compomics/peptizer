package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 16.04.2009
 * Time: 16:45:16
 * To change this template use File | Settings | File Templates.
 */
public interface PeptizerProteinHit {

    public String getAccession();
    public SearchEngineEnum getSearchEngineEnum();
    public int getStart();
    public int getEnd();
}
