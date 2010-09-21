package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 22.04.2009
 * Time: 10:20:20
 * To change this template use File | Settings | File Templates.
 */
public abstract class PeptizerFragmentIon implements SpectrumAnnotation {
	// Class specific log4j logger for PeptizerFragmentIon instances.
	 private static Logger logger = Logger.getLogger(PeptizerFragmentIon.class);

    abstract public SearchEngineEnum getSearchEngineEnum();

    abstract public double getIntensity();

    abstract public int getNumber();

    abstract public IonTypeEnum getType();

    // Methods used for the display of the annotations on the spectrum
    abstract public double getMZ();

    abstract public double getErrorMargin();

    abstract public Color getColor();

    abstract public String getLabel();

}
