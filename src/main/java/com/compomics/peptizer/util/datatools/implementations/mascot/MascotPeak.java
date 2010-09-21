package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.mascot.Peak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 07.04.2009
 * Time: 16:58:02
 * To change this template use File | Settings | File Templates.
 */
public class MascotPeak extends PeptizerPeak {
	// Class specific log4j logger for MascotPeak instances.
	 private static Logger logger = Logger.getLogger(MascotPeak.class);

    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.Mascot;

    public MascotPeak(Peak iPeak) {
        super.iMZ = iPeak.getMZ();
        super.iIntensity = iPeak.getIntensity();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }
}
