package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.interfaces.Modification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 21.07.2010
 * Time: 15:56:53
 * To change this template use File | Settings | File Templates.
 */
public class MascotModification implements PeptizerModification, Serializable {

    private Modification modification;
    private int position;

    public MascotModification(Modification modification, int position) {
        this.modification = modification;
        this.position = position;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return SearchEngineEnum.Mascot;
    }

    public String getName() {
        return modification.getType().replaceAll("-", "");
    }

    public String getPrideAccession() {
        return "NA";
    }

    public double getDeltaMass() {
        return modification.getMass();
    }

    public boolean isVariable() {
        return !modification.isFixed();
    }

    public int getModificationSite() {
        return position;
    }
}
