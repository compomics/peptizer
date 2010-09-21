package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 27.04.2009
 * Time: 14:23:55
 * To change this template use File | Settings | File Templates.
 */
public interface PeptizerModification {

    public SearchEngineEnum getSearchEngineEnum();

    public String getName();

    public String getPrideAccession();

    public double getDeltaMass();

    public int getModificationSite();

    public boolean isVariable();

}
