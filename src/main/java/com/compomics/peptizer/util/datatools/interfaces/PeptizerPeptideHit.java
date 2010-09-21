package com.compomics.peptizer.util.datatools.interfaces;

import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 24.03.2009
 * Time: 13:02:46
 * This object will represent a peptide hit, independantly on the search engine used.
 */
public abstract class PeptizerPeptideHit {
	// Class specific log4j logger for PeptizerPeptideHit instances.
	 private static Logger logger = Logger.getLogger(PeptizerPeptideHit.class);

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
        ArrayList<Integer> newLocations = aPeptizerPeptideHit.getModificationsLocations();
        ArrayList<Integer> thisLocations = this.getModificationsLocations();
        if (newLocations.size() != thisLocations.size()) {
            return false;
        }
        for (int i = 0; i < thisLocations.size(); i++) {
            if (newLocations.get(i) != thisLocations.get(i)) {
                return false;
            }
        }
        // Other criteria might be implemented here.
        return true;
    }

    public abstract String getSequence();

    protected String[] decomposeSequence() {
        String sequence = getSequence();
        String[] decomposedSequence = new String[sequence.length()];
        for (int i = 0; i < sequence.length(); i++) {
            Character a = new Character(sequence.charAt(i));
            decomposedSequence[i] = a.toString();
        }
        return decomposedSequence;
    }

    public String getModifiedSequence() {

        String[] decomposedSequence = decomposeSequence();

        String nTerm = "NH2";
        String cTerm = "COOH";
        for (PeptizerModification mod : getModifications()) {
            if (mod.getModificationSite() == 0) {
                nTerm += "<" + mod.getName() + ">";
            } else if (mod.getModificationSite() == decomposedSequence.length) {
                cTerm = "<" + mod.getName() + ">" + cTerm;
            } else {
                decomposedSequence[mod.getModificationSite() - 1] += "<" + mod.getName() + ">";
            }
        }

        // Concat everything
        String modifiedSequence = nTerm + "-";
        for (String component : decomposedSequence) {
            modifiedSequence += component;
        }
        modifiedSequence += "-" + cTerm;
        return modifiedSequence;

    }

    public ArrayList<Integer> getModificationsLocations() {
        ArrayList<Integer> locations = new ArrayList<Integer>();
        for (PeptizerModification mod : getModifications()) {
            locations.add(mod.getModificationSite());
        }
        return locations;
    }

    public JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification) {

        HashMap<String, Vector<PeptizerFragmentIon>> annotationMap = getAllAnnotation(aPeptideIdentification, 0);
        String key = 0 + "" + getAdvocate().getAdvocatesList().get(0).getId() + "" + 1 + "";
        Vector<PeptizerFragmentIon> fragmentIons = annotationMap.get(key);
        String sequence = getSequence();
        int length = sequence.length();
        // Create Y and B boolean arrays.
        boolean[] yIons = new boolean[length];
        boolean[] bIons = new boolean[length];
        // Fill out arrays.
        for (PeptizerFragmentIon ion : fragmentIons) {
            switch (ion.getType()) {
                case y:
                case yH2O:
                case yNH3:
                    yIons[ion.getNumber() - 1] = true;
                    if (yIons.length == ion.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case b:
                case bH2O:
                case bNH3:
                    bIons[ion.getNumber() - 1] = true;
                    if (bIons.length == ion.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;
                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply add formatting.
        String[] modifiedAA = decomposeSequence();
        StringBuffer formattedSequence = new StringBuffer("<html>");
        // Cycle the amino acids (using b-ions indexing here).
        for (int i = 0; i < bIons.length; i++) {
            boolean italic = false;
            boolean bold = false;
            // First and last one only have 50% coverage anyway
            if (i == 0) {
                if (bIons[i]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    if (yIons[yIons.length - (i + 3)]) {
                        bold = true;
                    }
                }
            } else if (i == (length - 1)) {
                if (bIons[i] && bIons[i - 1]) {
                    if (bIons[i - 2]) {
                        italic = true;
                    }
                }
                if (yIons[yIons.length - (i + 1)]) {
                    bold = true;
                }
            } else {
                // Aha, two ions needed here.
                if (bIons[i] && bIons[i - 1]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    bold = true;
                }
            }
            // Actually add the next char.
            formattedSequence.append(
                    (italic ? "<u>" : "") +
                            (bold ? "<font color=\"red\">" : "") +
                            modifiedAA[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;") +
                            (italic ? "</u>" : "") +
                            (bold ? "</font>" : "")
            );
        }
        // Finalize HTML'ized label text.
        formattedSequence.append("</html>");

        // Create label and set text.
        JLabel label = new JLabelImpl(formattedSequence.toString(), sequence);
        return label;

    }

    public abstract int getBTag(PeptideIdentification aPeptideIdentification);

    public abstract int getYTag(PeptideIdentification aPeptideIdentification);

    public abstract Double getTheoMass();

    public abstract Double getDeltaMass();

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

    public abstract ArrayList<PeptizerModification> getModifications();

    protected abstract HashMap<String, Vector<PeptizerFragmentIon>> getAnnotation(PeptideIdentification aPeptideIdentification, int id);

    public abstract int[] getSequenceCoverage(PeptideIdentification aPeptideIdentification);

    public abstract Double getIonsScore();              // Search engine dependant - depreciated

    public abstract Double getHomologyThreshold();    // Search engine dependant - depreciated

    public abstract Double calculateThreshold(double aConfidenceInterval);    // Search engine dependant - depreciated

    public abstract boolean scoresAboveThreshold(double aConfidenceInterval);   // Search engine dependant - depreciated

    public abstract Double getExpectancy(double aConfidenceInterval); // Search engine dependant - to be used carefully

    public abstract Double calculateThreshold();  // Search engine dependant - depreciated

    public boolean validatedByOneAdvocate() {
        // We perform a fuzzy logic "or" to see if a peptide is confident. More peptides could be rescued.
        for (int i = 0; i < fusedHits.size(); i++) {
            if (fusedHits.get(i).scoresAboveThreshold()) {
                return true;
            }
        }
        return scoresAboveThreshold();
    }

    protected abstract boolean scoresAboveThreshold();

    // If the search results can be annotated in different ways, explain it to Peptizer
    public ArrayList<AnnotationType> getAnnotationType() {
        return annotationType;
    }


    /**
     * Private implementation to display a PeptideSequence for the toString() instead of an object reference. (CSV output
     * uses toString!)
     */
    private class JLabelImpl extends JLabel {

        String iName = "";

        /**
         * Creates a <code>JLabel</code> instance with the specified text. The label is aligned against the leading edge of
         * its display area, and centered vertically.
         *
         * @param text The text to be displayed by the label.
         */
        public JLabelImpl(String text, String aName) {
            super(text);
            iName = aName;
        }


        /**
         * Returns a string representation of this component and its values.
         *
         * @return a string representation of this component
         * @since JDK1.0
         */
        @Override
        public String toString() {
            return iName;
        }
    }
}
