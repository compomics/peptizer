package com.compomics.peptizer.util.datatools.implementations.xtandem;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 16:28:56
 * To change this template use File | Settings | File Templates.
 */
public class XTandemPeak extends PeptizerPeak {
	// Class specific log4j logger for XTandemPeak instances.
	 private static Logger logger = Logger.getLogger(XTandemPeak.class);
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.XTandem;

    public XTandemPeak() {
    }

    public XTandemPeak(double mz, double abundance) {
        super.iMZ = mz;
        super.iIntensity = abundance;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

}
