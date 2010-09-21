package com.compomics.peptizer.util.datatools.implementations.omssa;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 21.07.2010
 * Time: 16:10:07
 * To change this template use File | Settings | File Templates.
 */
public class OmssaModification implements PeptizerModification, Serializable {
	// Class specific log4j logger for OmssaModification instances.
	 private static Logger logger = Logger.getLogger(OmssaModification.class);
    /**
     * modification at particular amino acids
     */
    public static final int MODAA = 0;
    /**
     * modification at the N terminus of a protein
     */
    public static final int MODN = 1;
    /**
     * modification at the N terminus of a protein
     */
    public static final int MODNAA = 2;
    /**
     * modification at the C terminus of a protein
     */
    public static final int MODC = 3;
    /**
     * modification at the C terminus of a protein at particular amino acids
     */
    public static final int MODCAA = 4;
    /**
     * modification at the N terminus of a peptide
     */
    public static final int MODNP = 5;
    /**
     * modification at the N terminus of a peptide at particular amino acids
     */
    public static final int MODNPAA = 6;
    /**
     * modification at the C terminus of a peptide
     */
    public static final int MODCP = 7;
    /**
     * modification at the C terminus of a peptide at particular amino acids
     */
    public static final int MODCPAA = 8;
    /**
     * the max number of modification types
     */
    public static final int MODMAX = 9;

    private String name;
    private double deltaMass;
    private boolean variable;
    private int type;
    private int site;
    private ArrayList<String> modifiedResidues;

    public OmssaModification(int type, ArrayList<String> modifiedResidues, String name, int site, double deltaMass, boolean variable) {
        this.type = type;
        this.modifiedResidues = modifiedResidues;
        this.name = name;
        this.deltaMass = deltaMass;
        this.variable = variable;
    }

    public int getType() {
        return type;
    }

    public ArrayList<String> getModifiedResidues() {
        return modifiedResidues;
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return SearchEngineEnum.OMSSA;
    }

    public String getName() {
        return name;
    }

    public String getPrideAccession() {
        return "NA";
    }

    public double getDeltaMass() {
        return deltaMass;
    }

    public boolean isVariable() {
        return variable;
    }

    public int getModificationSite() {
        return site;
    }
}
