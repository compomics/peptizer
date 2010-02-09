package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.peptizer.util.datatools.interfaces.ParsingType;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 09.04.2009
 * Time: 11:54:23
 * To change this template use File | Settings | File Templates.
 */
public class MascotParsingType implements ParsingType {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.Mascot;
    private MascotDatfileType iMascotDatfileType = MascotDatfileType.INDEX; // default is MEMORY
    private String iName;

    public MascotParsingType() {

    }

    public MascotParsingType(MascotDatfileType aMascotDatfileType) {
        this.iMascotDatfileType = aMascotDatfileType;
        this.iName = iMascotDatfileType.toString();
    }

    public MascotDatfileType getParsingType() {
        return iMascotDatfileType;
    }

    public void setParsingType(MascotDatfileType aType) {
        this.iMascotDatfileType = aType;
    }

    public String toString() {
        return iName;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }
}
