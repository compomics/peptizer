package com.compomics.peptizer.util.datatools;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.awt.*;

import static java.lang.Math.abs;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 26.04.2009
 * Time: 18:15:37
 * To change this template use File | Settings | File Templates.
 */
public class Ion extends PeptizerFragmentIon {
	// Class specific log4j logger for Ion instances.
	 private static Logger logger = Logger.getLogger(Ion.class);

    private double mz;
    private double intensity;
    private int number;
    private IonTypeEnum type;
    private SearchEngineEnum iSearchEngineEnum;

    public Ion() {

    }

    public Ion(double mz, IonTypeEnum type) {
        this.mz = mz;
        this.type = type;
    }

    public Ion(double mz, double intensity, IonTypeEnum type, int number, SearchEngineEnum aSearchEngineEnum) {
        this.mz = mz;
        this.intensity = intensity;
        this.type = type;
        this.number = number;
        iSearchEngineEnum = aSearchEngineEnum;
    }

    public Ion(double mz, IonTypeEnum type, int number, SearchEngineEnum aSearchEngineEnum) {
        this.mz = mz;
        this.intensity = -1;
        this.type = type;
        this.number = number;
        iSearchEngineEnum = aSearchEngineEnum;
    }

    public double getMZ() {
        return mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public int getNumber() {
        return number;
    }

    public IonTypeEnum getType() {
        return type;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public boolean isMatch(PeptizerPeak[] peakList, double errorMargin) {
        boolean match = false;
        for (int i = 0; i < peakList.length; i++) {
            if (abs(peakList[i].getMZ() - this.getMZ()) <= errorMargin) {
                match = true;
            }
        }
        return match;
    }

    // Methods used for the display of the annotations on the spectrum - The display should use the ion from the parser and not this one

    public double getErrorMargin() {
        return 0;
    }

    public Color getColor() {
        return Color.black;
    }

    public String getLabel() {
        return "#";
    }
}
