package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:26:23
 * To change this template use File | Settings | File Templates.
 */
public class PrideProteinHit implements PeptizerProteinHit, Serializable {

    private String accession;
    private SearchEngineEnum searchEngine;
    private Integer start;
    private Integer end;

    public PrideProteinHit(String anAccession, Integer start, Integer end, SearchEngineEnum aSearchEngine) {
        accession = anAccession;
        searchEngine = aSearchEngine;
        this.start = start;
        this.end = end;
    }

    public String getAccession() {
        return accession;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return searchEngine;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public boolean isSameAs(PrideProteinHit anotherProtein) {
        if (!accession.equals(anotherProtein.getAccession())) {
            return false;
        }
        /*
        if (start != anotherProtein.getStart()) {
            return false;
        }
        if (end != anotherProtein.getEnd()) {
            return false;
        }
        */
        return true;
    }
}
