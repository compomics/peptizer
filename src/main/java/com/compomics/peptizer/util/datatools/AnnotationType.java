package com.compomics.peptizer.util.datatools;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 21.04.2009
 * Time: 18:06:01
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationType implements Serializable {
	// Class specific log4j logger for AnnotationType instances.
	 private static Logger logger = Logger.getLogger(AnnotationType.class);
    private String name;
    private int index;
    private SearchEngineEnum searchEngine;

    public AnnotationType() {

    }

    public AnnotationType(String aName, int anIndex, SearchEngineEnum searchEngine) {
        name = aName;
        index = anIndex;
        this.searchEngine = searchEngine;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public SearchEngineEnum getSearchEngine() {
        return searchEngine;
    }

}
