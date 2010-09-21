package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 16.04.2009
 * Time: 16:49:54
 * To change this template use File | Settings | File Templates.
 */
public class MascotProteinHit implements PeptizerProteinHit {
	// Class specific log4j logger for MascotProteinHit instances.
	 private static Logger logger = Logger.getLogger(MascotProteinHit.class);
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
