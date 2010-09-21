package com.compomics.peptizer.util.datatools.implementations.pride;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.jaxb.model.CvParam;
import uk.ac.ebi.pride.jaxb.model.FragmentIon;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 19.07.2010
 * Time: 13:25:49
 * To change this template use File | Settings | File Templates.
 */
public class PrideFragmentIon extends PeptizerFragmentIon implements SpectrumAnnotation, Serializable {
	// Class specific log4j logger for PrideFragmentIon instances.
	 private static Logger logger = Logger.getLogger(PrideFragmentIon.class);

    private FragmentIon originalFragmentIon;
    private SearchEngineEnum searchEngine;
    private double intensity;
    private double mz;
    private int number;
    private IonTypeEnum ionType;
    private double errorMargin;


    public PrideFragmentIon(FragmentIon fragmentIon, SearchEngineEnum searchEngine) {

        this.searchEngine = searchEngine;
        this.originalFragmentIon = fragmentIon;

        for (CvParam cvParam : originalFragmentIon.getCvParam()) {
            if (cvParam.getAccession().equals("PRIDE:0000189")) {
                intensity = new Double(cvParam.getValue());
            } else if (cvParam.getAccession().equals("PRIDE:0000190")) {
                errorMargin = new Double(cvParam.getValue());
            } else if (cvParam.getAccession().equals("PRIDE:0000188")) {
                mz = new Double(cvParam.getValue());

            } else if (cvParam.getAccession().equals("PRIDE:0000233")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.a;
            } else if (cvParam.getAccession().equals("PRIDE:0000234")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.aH2O;
            } else if (cvParam.getAccession().equals("PRIDE:0000235")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.aNH3;
            } else if (cvParam.getAccession().equals("PRIDE:0000194")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.b;
            } else if (cvParam.getAccession().equals("PRIDE:0000196")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.bH2O;
            } else if (cvParam.getAccession().equals("PRIDE:0000195")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.bNH3;
            } else if (cvParam.getAccession().equals("PRIDE:0000236")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.c;
            } else if (cvParam.getAccession().equals("PRIDE:0000227")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.x;
            } else if (cvParam.getAccession().equals("PRIDE:0000193")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.y;
            } else if (cvParam.getAccession().equals("PRIDE:0000197")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.yH2O;
            } else if (cvParam.getAccession().equals("PRIDE:0000198")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.yNH3;
            } else if (cvParam.getAccession().equals("PRIDE:0000230")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.z;
            } else if (cvParam.getAccession().equals("PRIDE:0000263")) {
                ionType = IonTypeEnum.MH;
            } else if (cvParam.getAccession().equals("PRIDE:0000261")) {
                ionType = IonTypeEnum.MHH2O;
            } else if (cvParam.getAccession().equals("PRIDE:0000262")) {
                ionType = IonTypeEnum.MHNH3;
            } else if (cvParam.getAccession().equals("PRIDE:0000239")) {
                ionType = IonTypeEnum.immonium;
            } else if (cvParam.getAccession().equals("PRIDE:0000240")) {
                ionType = IonTypeEnum.immoniumA;
            } else if (cvParam.getAccession().equals("PRIDE:0000241")) {
                ionType = IonTypeEnum.immoniumC;
            } else if (cvParam.getAccession().equals("PRIDE:0000242")) {
                ionType = IonTypeEnum.immoniumD;
            } else if (cvParam.getAccession().equals("PRIDE:0000243")) {
                ionType = IonTypeEnum.immoniumE;
            } else if (cvParam.getAccession().equals("PRIDE:0000244")) {
                ionType = IonTypeEnum.immoniumF;
            } else if (cvParam.getAccession().equals("PRIDE:0000245")) {
                ionType = IonTypeEnum.immoniumG;
            } else if (cvParam.getAccession().equals("PRIDE:0000246")) {
                ionType = IonTypeEnum.immoniumH;
            } else if (cvParam.getAccession().equals("PRIDE:0000247")) {
                ionType = IonTypeEnum.immoniumI;
            } else if (cvParam.getAccession().equals("PRIDE:0000248")) {
                ionType = IonTypeEnum.immoniumK;
            } else if (cvParam.getAccession().equals("PRIDE:0000249")) {
                ionType = IonTypeEnum.immoniumL;
            } else if (cvParam.getAccession().equals("PRIDE:0000250")) {
                ionType = IonTypeEnum.immoniumM;
            } else if (cvParam.getAccession().equals("PRIDE:0000251")) {
                ionType = IonTypeEnum.immoniumN;
            } else if (cvParam.getAccession().equals("PRIDE:0000252")) {
                ionType = IonTypeEnum.immoniumP;
            } else if (cvParam.getAccession().equals("PRIDE:0000253")) {
                ionType = IonTypeEnum.immoniumQ;
            } else if (cvParam.getAccession().equals("PRIDE:0000254")) {
                ionType = IonTypeEnum.immoniumR;
            } else if (cvParam.getAccession().equals("PRIDE:0000255")) {
                ionType = IonTypeEnum.immoniumS;
            } else if (cvParam.getAccession().equals("PRIDE:0000256")) {
                ionType = IonTypeEnum.immoniumT;
            } else if (cvParam.getAccession().equals("PRIDE:0000257")) {
                ionType = IonTypeEnum.immoniumV;
            } else if (cvParam.getAccession().equals("PRIDE:0000258")) {
                ionType = IonTypeEnum.immoniumW;
            } else if (cvParam.getAccession().equals("PRIDE:0000259")) {
                ionType = IonTypeEnum.immoniumY;
            } else if (cvParam.getAccession().contains("ion")) {
                number = new Integer(cvParam.getValue());
                ionType = IonTypeEnum.other;
            }
        }
    }


    @Override
    public SearchEngineEnum getSearchEngineEnum() {
        return searchEngine;
    }

    @Override
    public double getIntensity() {
        return intensity;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public IonTypeEnum getType() {
        return ionType;
    }

    @Override
    public double getMZ() {
        return mz;
    }

    @Override
    public double getErrorMargin() {
        return errorMargin;
    }

    @Override

    public Color getColor() {
        switch (ionType) {
            // A ion
            case a:
            case aH2O:
            case aNH3:
                return new Color(153, 0, 0);
            // B ion
            case b:
            case bH2O:
            case bNH3:
                return new Color(0, 0, 255);
            // C ion
            case c:
                return new Color(188, 0, 255);
            // X ion
            case x:
                return new Color(78, 200, 0);
            // Y ion
            case y:
            case yH2O:
            case yNH3:
                return new Color(0, 0, 0);
            // Z ion
            case z:
                return new Color(255, 140, 0);
            // Parent ion
            case MH:
                return Color.red;
            // Immonium ion
            case immonium:
                return Color.gray;
            // Unknown ion
            default:
                return new Color(150, 150, 150);
        }
    }

    @Override
    public String getLabel() {
        return ionType.getName() + number;
    }
}
