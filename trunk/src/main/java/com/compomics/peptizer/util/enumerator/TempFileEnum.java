package com.compomics.peptizer.util.enumerator;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jan 21, 2009
 * Time: 5:48:51 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TempFileEnum {

    NON_CONFIDENT("confident_not"), CONFIDENT_NOT_SELECTED("confident_ns"), CONFIDENT_SELECTED("confident_s");

    public String iName;

    TempFileEnum(String aName) {
        iName = aName;
    }
}
