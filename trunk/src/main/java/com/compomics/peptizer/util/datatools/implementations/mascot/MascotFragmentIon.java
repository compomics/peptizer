package com.compomics.peptizer.util.datatools.implementations.mascot;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 22.04.2009
 * Time: 10:30:41
 * To change this template use File | Settings | File Templates.
 */
public class MascotFragmentIon extends PeptizerFragmentIon implements SpectrumAnnotation {
	// Class specific log4j logger for MascotFragmentIon instances.
	 private static Logger logger = Logger.getLogger(MascotFragmentIon.class);
    private FragmentIon iFragmentIon;
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.Mascot;

    public MascotFragmentIon() {

    }

    public MascotFragmentIon(FragmentIon aFragmention) {
        iFragmentIon = aFragmention;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public double getIntensity() {
        return iFragmentIon.getIntensity();
    }

    public int getNumber() {
        return iFragmentIon.getNumber();
    }

    public IonTypeEnum getType() {
        switch (iFragmentIon.getID()) {
            // singly charged Yion
            case FragmentIon.Y_ION:
                return IonTypeEnum.y;
            // singly charged Bion
            case FragmentIon.B_ION:
                return IonTypeEnum.b;
            // double charged Yion
            case FragmentIon.Y_DOUBLE_ION:
                return IonTypeEnum.y;
            // double charged Bion
            case FragmentIon.B_DOUBLE_ION:
                return IonTypeEnum.b;
        }

        return IonTypeEnum.other;
    }

    // Methods used for the display of the annotations on the spectrum

    public double getMZ() {
        return iFragmentIon.getMZ();
    }

    public double getErrorMargin() {
        return iFragmentIon.getErrorMargin();
    }

    public Color getColor() {
        return iFragmentIon.getColor();
    }

    public String getLabel() {
        return iFragmentIon.getLabel();
    }
}
