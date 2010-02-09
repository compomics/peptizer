package com.compomics.peptizer.util.enumerator;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 20.03.2009
 * Time: 14:28:58
 * To change this template use File | Settings | File Templates.
 */
public enum SearchEngineEnum {
    Mascot, OMSSA, XTandem;

    public String getName() {
        if (this == Mascot) {
            return "Mascot";
        } else if (this == OMSSA) {
            return "OMSSA";
        } else if (this == XTandem) {
            return "XTandem";
        } else return null;
    }
}
