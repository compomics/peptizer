package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 09.04.2009
 * Time: 11:52:08
 * To change this template use File | Settings | File Templates.
 */
public interface ParsingType {
    public String toString();

    public Object getParsingType();

    public SearchEngineEnum getSearchEngineEnum();
}
