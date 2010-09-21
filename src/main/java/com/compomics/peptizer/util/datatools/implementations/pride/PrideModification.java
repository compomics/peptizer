package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 21.07.2010
 * Time: 17:27:20
 * To change this template use File | Settings | File Templates.
 */
public class PrideModification implements PeptizerModification, Serializable {
	// Class specific log4j logger for PrideModification instances.
	 private static Logger logger = Logger.getLogger(PrideModification.class);

    private String name;
    private String accession;
    private double deltaMass;
    private int location;
    private boolean variable;
    private SearchEngineEnum searchEngine;

    public PrideModification(String name, String accession, double deltaMass, int location, boolean variable, SearchEngineEnum searchEngine) {
        this.name = name;
        this.accession = accession;
        this.deltaMass = deltaMass;
        this.location = location;
        this.variable = variable;
        this.searchEngine = searchEngine;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return searchEngine;
    }

    public String getName() {
        return name;
    }

    public String getPrideAccession() {
        return accession;
    }

    public double getDeltaMass() {
        return deltaMass;
    }

    public int getModificationSite() {
        return location;
    }

    public boolean isVariable() {
        return variable;
    }
}
