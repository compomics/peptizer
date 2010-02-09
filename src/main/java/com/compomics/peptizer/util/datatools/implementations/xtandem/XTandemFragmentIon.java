package com.compomics.peptizer.util.datatools.implementations.xtandem;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 16:30:05
 * To change this template use File | Settings | File Templates.
 */
public class XTandemFragmentIon extends PeptizerFragmentIon implements SpectrumAnnotation {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.XTandem;
    private XTandemPeak iPeak;
    private double iMz;
    private double iIntensity;
    private int iID;
    private int iNumber;
    private double iErrorMargin;
    private Color iColor;

    public XTandemFragmentIon() {

    }

    public XTandemFragmentIon(FragmentIon ion) {
        iMz = ion.getMZ();
        iIntensity = ion.getIntensity();
        iNumber = ion.getNumber();
        iID = ion.getID();
        iErrorMargin = ion.getErrorMargin();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public double getIntensity() {
        return iIntensity;
    }

    public int getNumber() {
        return iNumber;
    }

    public IonTypeEnum getType() {
        if (iID == 1) {
            return IonTypeEnum.b;
        } else if (iID == 4) {
            return IonTypeEnum.y;
        } else {
            return IonTypeEnum.other;
        }

    }

    public double getMZ() {
        return iMz;
    }

    public double getErrorMargin() {
        return iErrorMargin;
    }

    public Color getColor() {
        if (iID == 1) {
            return Color.BLUE;
        }
        if (iID == 4) {
            return Color.BLACK;
        }
        // Default color black
        return Color.BLACK;
    }

    public String getLabel() {
        StringBuffer label = new StringBuffer();
        label.append(getType().toString() + getNumber());
        return label.toString();
    }
}
