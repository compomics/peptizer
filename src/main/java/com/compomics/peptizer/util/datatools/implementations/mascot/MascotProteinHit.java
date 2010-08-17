package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 16.04.2009
 * Time: 16:49:54
 * To change this template use File | Settings | File Templates.
 */
public class MascotProteinHit implements PeptizerProteinHit {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.Mascot;
    private ProteinHit iProteinHit;

    public MascotProteinHit() {

    }

    public MascotProteinHit(ProteinHit aProteinHit) {
        iProteinHit = aProteinHit;
    }

    public String getAccession() {
        return iProteinHit.getAccession();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public Integer getStart() {
        return iProteinHit.getStart();
    }

    public Integer getEnd() {
        return iProteinHit.getStop();
    }
}
