package com.compomics.peptizer.util.datatools;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 21.04.2009
 * Time: 18:06:01
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationType implements Serializable {
    private String name;
    private int index;

    public AnnotationType() {

    }
    public AnnotationType(String aName, int anIndex) {
        name = aName;
        index = anIndex;
    }

    public int getIndex() {
        return index;
    }
    public String getName() {
        return name;
    }

}
