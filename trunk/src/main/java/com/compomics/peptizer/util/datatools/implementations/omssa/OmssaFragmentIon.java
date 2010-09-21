package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import de.proteinms.omxparser.util.MSMZHit;
import org.apache.log4j.Logger;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 22.04.2009
 * Time: 10:30:27
 * To change this template use File | Settings | File Templates.
 */
public class OmssaFragmentIon extends PeptizerFragmentIon implements SpectrumAnnotation {
	// Class specific log4j logger for OmssaFragmentIon instances.
	 private static Logger logger = Logger.getLogger(OmssaFragmentIon.class);
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.OMSSA;
    private MSMZHit msMZHit;
    private OmssaPeak peak;

    public OmssaFragmentIon() {

    }

    public OmssaFragmentIon(MSMZHit msMZHit, OmssaPeak peak) {
        this.msMZHit = msMZHit;
        this.peak = peak;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public double getIntensity() {
        return peak.getIntensity();
    }

    public int getNumber() {
        return msMZHit.MSMZHit_number + 1;
    }

    public IonTypeEnum getType() {
        switch (msMZHit.MSMZHit_ion.MSIonType) {
            // Bion
            case 1:
                return IonTypeEnum.b;
            // Yion
            case 4:
                return IonTypeEnum.y;
        }
        return IonTypeEnum.other;
    }

    // Methods used for the display of the annotations on the spectrum

    public double getMZ() {
        return peak.getMZ();
    }

    public double getErrorMargin() {
        return 1; // Parameter not found in the omssa parser
    }

    public Color getColor() {
        switch (msMZHit.MSMZHit_ion.MSIonType) {
            // A ion
            case 0: {
                switch (msMZHit.MSMZHit_charge) {
                    case 1:
                        return new Color(153, 0, 0);
                    case 2:
                        return new Color(0, 139, 0);
                }
                return new Color(153, 0, 0);
            }
            // B ion
            case 1:
                return new Color(0, 0, 255);
            // C ion
            case 2:
                return new Color(188, 0, 255);
            // X ion
            case 3:
                return new Color(78, 200, 0);
            // Y ion
            case 4:
                return new Color(0, 0, 0);
            // Z ion
            case 5: {
                switch (msMZHit.MSMZHit_charge) {
                    case 1:
                        return new Color(255, 140, 0);
                    case 2:
                        return new Color(64, 179, 0);
                }
                return new Color(255, 140, 0);
            }
            // Parent ion
            case 6:
                return Color.red;
            // Internal ion
            case 7:
                return Color.red;
            // Immonium ion
            case 8:
                return Color.gray;
            // Unknown ion
            case 9:
                return new Color(150, 150, 150);
        }
        return new Color(150, 150, 150);
    }

    public String getLabel() {
        StringBuffer label = new StringBuffer();
        label.append(getType().toString() + getNumber());
        return label.toString();
    }
}
