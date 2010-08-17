package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSPepHit;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 16.04.2009
 * Time: 16:45:54
 * To change this template use File | Settings | File Templates.
 */
public class OmssaProteinHit implements PeptizerProteinHit {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.OMSSA;
    private MSPepHit msPepHit;

    public OmssaProteinHit() {

    }

    public OmssaProteinHit(MSPepHit aMSPepHit) {
        msPepHit = aMSPepHit;
    }

    public String getAccession() {
        return getProteinAccession(msPepHit.MSPepHit_defline);
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public Integer getStart() {
        return msPepHit.MSPepHit_start;
    }

    public Integer getEnd() {
        return msPepHit.MSPepHit_stop;
    }

    private String getProteinAccession(String description) {
        int start = description.indexOf("|");
        int end = description.indexOf("|", ++start);
        return description.substring(start, end);
    }
}
