package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 24.03.2009
 * Time: 13:02:46
 * This object will represent a peptide hit, independantly on the search engine used.
 */
public abstract class PeptizerPeptideHit {

    protected Advocate advocate;
    protected HashMap<SearchEngineEnum, Object> originalPeptideHits = new HashMap<SearchEngineEnum, Object>();
    protected ArrayList<AnnotationType> annotationType;
    private ArrayList<PeptizerPeptideHit> fusedHits = new ArrayList<PeptizerPeptideHit>();


    // This methods will be called by agents to work on the original peptide hit 
    public Object getOriginalPeptideHit(SearchEngineEnum aSearchEngineEnum) {
        return originalPeptideHits.get(aSearchEngineEnum);
    }

    public Advocate getAdvocate() {
        return advocate;
    }

    public boolean identifiedBy(SearchEngineEnum aSearchEngine) {
        return advocate.getAdvocatesList().contains(aSearchEngine);
    }

    public PeptizerPeptideHit getPeptidHit(SearchEngineEnum aSearchEngine) {
        for (int i = 0; i < fusedHits.size(); i++) {
            if (fusedHits.get(i).identifiedBy(aSearchEngine)) {
                return fusedHits.get(i);
            }
        }
        return this;
    }

    public void fuse(PeptizerPeptideHit peptideHit) {
        SearchEngineEnum newSearchEngine = peptideHit.getAdvocate().getAdvocatesList().get(0);  // There should be only one at this point
        advocate.addAdvocate(newSearchEngine, peptideHit.getAdvocate().getRank(newSearchEngine));
        originalPeptideHits.put(newSearchEngine, peptideHit.getOriginalPeptideHit(newSearchEngine));
        annotationType.addAll(peptideHit.getAnnotationType());
        fusedHits.add(peptideHit);
        fusedHits.addAll(peptideHit.getFusedHits());
    }

    public ArrayList<PeptizerPeptideHit> getFusedHits() {
        return fusedHits;
    }

    public boolean isSameAs(PeptizerPeptideHit aPeptizerPeptideHit) {
        if (aPeptizerPeptideHit.getSequence().compareTo(this.getSequence()) != 0) {
            return false;
        }
        // Other criteria than the sequence might be implemented here.
        return true;
    }

    public abstract String getSequence();

    public abstract String getModifiedSequence();

    public abstract JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification);

    public abstract int getBTag(PeptideIdentification aPeptideIdentification);

    public abstract int getYTag(PeptideIdentification aPeptideIdentification);

    public abstract double getExpectancy(double aConfidenceInterval);

    public abstract double getTheoMass();

    public abstract double getDeltaMass();

    public abstract ArrayList getProteinHits();

    public abstract String getDatabase(PeptideIdentification aPeptideIdentification);

    public HashMap getAllAnnotation(PeptideIdentification aPeptideIdentification, int id) {
        HashMap annotationMap = new HashMap();
        for (int i = 0; i < fusedHits.size(); i++) {
            PeptizerPeptideHit tempHit = fusedHits.get(i);
            HashMap temp = tempHit.getAllAnnotation(aPeptideIdentification, id);
            annotationMap.putAll(temp);
        }
        annotationMap.putAll(getAnnotation(aPeptideIdentification, id));
        return annotationMap;
    }

    protected abstract HashMap getAnnotation(PeptideIdentification aPeptideIdentification, int id);

    public abstract int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification);

    public abstract double getIonsScore();

    public abstract double getHomologyThreshold();

    public abstract double calculateThreshold(double aConfidenceInterval);

    public abstract boolean scoresAboveThreshold(double aConfidenceInterval);

    public abstract double calculateThreshold();

    public abstract boolean scoresAboveThreshold();

    // If the search results can be annotated in different ways, explain it to Peptizer
    public ArrayList<AnnotationType> getAnnotationType() {
        return annotationType;
    }

}
