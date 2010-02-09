package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.AnnotationType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 24.03.2009
 * Time: 13:02:46
 * To change this template use File | Settings | File Templates.
 */
public interface PeptizerPeptideHit {

    public String getSequence();
    public String getModifiedSequence();
    public JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification);
    public SearchEngineEnum getSearchEngineEnum();
    public Object getOriginalPeptideHit();
    public int getBTag(PeptideIdentification aPeptideIdentification);
    public int getYTag(PeptideIdentification aPeptideIdentification);
    public double getExpectancy(double aConfidenceInterval);
    public double getTheoMass();
    public double getDeltaMass();
    public ArrayList getProteinHits();
    public String getDatabase(PeptideIdentification aPeptideIdentification);
    public HashMap getAnnotation(PeptideIdentification aPeptideIdentification, int id);
    public int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification);
    public double getIonsScore();
    public double getHomologyThreshold();
    public double calculateThreshold(double aConfidenceInterval); 
    public boolean scoresAboveThreshold(double aConfidenceInterval);
    // If the search results can be annotated in different ways, explain it to Peptizer 
    public ArrayList<AnnotationType> getAnnotationType();
    
}
