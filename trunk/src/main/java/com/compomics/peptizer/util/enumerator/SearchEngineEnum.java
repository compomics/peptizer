package com.compomics.peptizer.util.enumerator;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 20.03.2009
 * Time: 14:28:58
 * To change this template use File | Settings | File Templates.
 */
public enum SearchEngineEnum implements Serializable {
    Mascot, OMSSA, XTandem, Sequest, unknown;

    public String getName() {
        switch (this) {
            case Mascot:
                return "Mascot";
            case OMSSA:
                return "OMSSA";
            case XTandem:
                return "XTandem";
            default:
                return "unknown";
        }
    }

    public int getId() {
        switch (this) {
            case Mascot:
                return 0;
            case OMSSA:
                return 1;
            case XTandem:
                return 2;
            case Sequest:
                return 3;
            default:
                return 4;
        }
    }

    public String getInitial() {
        return "" + getName().charAt(0);
    }
}
