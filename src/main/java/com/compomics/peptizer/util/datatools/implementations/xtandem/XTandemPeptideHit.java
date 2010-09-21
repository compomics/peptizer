package com.compomics.peptizer.util.datatools.implementations.xtandem;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.XTandemFile;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 18:10:13
 * To change this template use File | Settings | File Templates.
 */
public class XTandemPeptideHit extends PeptizerPeptideHit implements Serializable {
	// Class specific log4j logger for XTandemPeptideHit instances.
	 private static Logger logger = Logger.getLogger(XTandemPeptideHit.class);
    /**
     * The original Peptide(hit)
     */
    private Peptide iPeptide;
    private XTandemFile iXTandemFile;
    /**
     * The final annotation types available.
     */
    private final ArrayList<AnnotationType> iAnnotationType = createAnnotationType();

    private String[] iModArray;

    public XTandemPeptideHit(Peptide aPeptide, XTandemFile aXTandemFile, int rank) {
        originalPeptideHits.put(SearchEngineEnum.XTandem, aPeptide);
        advocate = new Advocate(SearchEngineEnum.XTandem, rank);
        iPeptide = aPeptide;
        iXTandemFile = aXTandemFile;
    }

    private ArrayList<AnnotationType> createAnnotationType() {
        ArrayList<AnnotationType> result = new ArrayList();
        AnnotationType all = new AnnotationType("X!Tandem", 0, SearchEngineEnum.XTandem);
        result.add(all);
        return result;
    }

    public String getSequence() {
        return iPeptide.getDomainSequence();
    }

    public ArrayList<Integer> getModificationsLocations() {
        // To be implemented once the parser retrives modifications properly
        return new ArrayList<Integer>();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] decomposeSequence(String sequence) {
        String shortsequence = sequence.substring(4, (sequence.length() - 5));


        String[] decomposedSequence = new String[shortsequence.length()];
        for (int i = 0; i < shortsequence.length(); i++) {
            Character a = new Character(shortsequence.charAt(i));
            decomposedSequence[i] = a.toString();
        }
        return decomposedSequence;
    }

    public String getModificationName(int modPos) {
        return iModArray[modPos];
    }

    public String getModifiedSequence() {
        String modifiedSequence = "";
        String sequence = iPeptide.getDomainSequence();
        String[] modifications = new String[sequence.length()];
        for (int i = 0; i < modifications.length; i++) {
            modifications[i] = "";
        }
        int spectrumNumber = iPeptide.getSpectrumNumber();

        // cycle through all the modifications and extract the modification type if possible
        for (int i = 0; i < modifications.length; i++) {
            // add the amino acid itself to the sequence
            modifiedSequence += sequence.substring(i, i + 1);

            if (!modifications[i].equalsIgnoreCase("")) {
                // have to check for multiple modifications on one
                // residue
                String[] residueMods = modifications[i].split(">");

                for (int j = 0; j < residueMods.length; j++) {
                    String currentMod = residueMods[j] + ">";
                    modifiedSequence += currentMod;
                }
            }
        }

        // Add head and tail of the peptide
        String head = "NH2-";
        modifiedSequence = head + modifiedSequence;
        modifiedSequence += "-COOH";

        return modifiedSequence;
    }

    public JLabel getColoredModifiedSequence(PeptideIdentification aPeptideIdentification) {
        String[] decomposedSequence = decomposeSequence(getModifiedSequence());

        // look for the b and y hits
        Vector<FragmentIon[]> vector = iXTandemFile.getFragmentIonsForPeptide(iPeptide);
        FragmentIon[] bIons = vector.get(0);
        FragmentIon[] yIons = vector.get(1);

        boolean[] bHits = new boolean[decomposedSequence.length];
        boolean[] yHits = new boolean[decomposedSequence.length];

        if (bIons != null) {
            for (int i = 0; i < bIons.length; i++) {
                int index = bIons[i].getNumber() - 1;
                bHits[index] = true;
            }
        }

        if (yIons != null) {
            for (int i = 0; i < yIons.length; i++) {
                int index = yIons[i].getNumber() - 1;
                yHits[index] = true;
            }
        }


        decomposedSequence[0] = "NH2-" + decomposedSequence[0];
        decomposedSequence[decomposedSequence.length - 1] += "-COOH";

        // Color or underline according to the coverage
        if (bHits[0] && bHits[1]) {
            decomposedSequence[0] = "<u>" + decomposedSequence[0] + "</u>";
        }
        if (bHits[decomposedSequence.length - 1] && bHits[decomposedSequence.length - 2]) {
            decomposedSequence[0] = "<font color=\"red\">" + decomposedSequence[0] + "</font>";
        }
        if (bHits[decomposedSequence.length - 1] && bHits[decomposedSequence.length - 2]) {
            decomposedSequence[decomposedSequence.length - 1] = "<u>" + decomposedSequence[decomposedSequence.length - 1] + "</u>";
        }
        if (bHits[0] && bHits[1]) {
            decomposedSequence[decomposedSequence.length - 1] = "<font color=\"red\">" + decomposedSequence[decomposedSequence.length - 1] + "</font>";
        }
        for (int i = 1; i < decomposedSequence.length - 1; i++) {
            if (bHits[i] && bHits[i - 1]) {
                decomposedSequence[i] = "<u>" + decomposedSequence[i] + "</u>";
            }
            if (yHits[decomposedSequence.length - i - 1] && yHits[decomposedSequence.length - i - 2]) {
                decomposedSequence[i] = "<font color=\"red\">" + decomposedSequence[i] + "</font>";
            }
        }
        String coloredSequence = "<html>";
        for (int i = 0; i < decomposedSequence.length; i++) {
            coloredSequence += decomposedSequence[i];
        }
        //modifiedSequence += "</html>";

        // Create label and set text.
        JLabel label = new JLabelImpl(coloredSequence.toString(), getSequence());
        return label;
    }

    public int getBTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(1);
    }

    public int getYTag(PeptideIdentification aPeptideIdentification) {
        return getIonTag(4);
    }

    public int getIonTag(int type) {
        int ionTag = 0;

        Vector<FragmentIon[]> vector = iXTandemFile.getFragmentIonsForPeptide(iPeptide);
        FragmentIon[] ions = null;

        // b ions
        if (type == 1) {
            ions = vector.get(0);
        }
        // y ions
        if (type == 4) {
            ions = vector.get(1);
        }

        // Select and sort the ions
        Vector<Boolean> goodIons = new Vector(getSequence().length());
        for (int i = 0; i < getSequence().length(); i++) {
            goodIons.add(i, false);
        }

        for (int i = 0; i < ions.length - 1; i++) {

            goodIons.set(ions[i].getNumber(), true);
        }
        int tempMax = 0;
        for (int i = 0; i < goodIons.size(); i++) {
            if (goodIons.get(i)) {
                tempMax++;
                if (tempMax > ionTag) {
                    ionTag = tempMax;
                }
            } else tempMax = 0;
        }
        return ionTag;
    }

    public Double getExpectancy(double aConvidence) {
        return iPeptide.getDomainExpect();
    }

    public Double getTheoMass() {
        return iPeptide.getDomainMh();
    }

    public Double getDeltaMass() {
        return iPeptide.getDomainDeltaMh();
    }

    public ArrayList getProteinHits() {
        ArrayList peptizerProteinHits = new ArrayList();
        int index = iPeptide.getDomainID().indexOf(".");
        String proteinID = iPeptide.getDomainID().substring(0, index + 2);
        XTandemProteinHit proteinHit = new XTandemProteinHit(iXTandemFile.getProteinMap().getProtein(proteinID), iPeptide);
        peptizerProteinHits.add(proteinHit);
        return peptizerProteinHits;
    }

    public String getDatabase(PeptideIdentification aPeptideIdentification) {
        return null;
    }

    public HashMap getAnnotation(PeptideIdentification pepIdent, int id) {
        HashMap lAnnotationsMap = new HashMap();
        // Vector for all the annotations
        Vector allAnnotations = new Vector();

        // Get the b and y ions
        Vector IonVector = iXTandemFile.getFragmentIonsForPeptide(iPeptide);
        FragmentIon[] bIons = (FragmentIon[]) IonVector.get(0);
        FragmentIon[] yIons = (FragmentIon[]) IonVector.get(1);
        for (int i = 0; i < bIons.length - 1; i++) {
            allAnnotations.add(new XTandemFragmentIon(bIons[i]));
        }
        for (int i = 0; i < yIons.length - 1; i++) {
            allAnnotations.add(new XTandemFragmentIon(yIons[i]));
        }

        lAnnotationsMap.put(iAnnotationType.get(0).getIndex() + "" + SearchEngineEnum.XTandem.getId() + "" + (id + 1), allAnnotations);
        return lAnnotationsMap;
    }

    public int[] getSequenceCoverage(PeptideIdentification pepIdent) {
        int[] coverage = new int[3];
        Vector ions = iXTandemFile.getFragmentIonsForPeptide(iPeptide);
        FragmentIon[] bIons = (FragmentIon[]) ions.get(0);
        for (FragmentIon bIon : bIons) {
            coverage[0]++;
        }
        FragmentIon[] yIons = (FragmentIon[]) ions.get(1);
        for (FragmentIon yIon : yIons) {
            coverage[1]++;
        }
        return coverage;
    }

    public Double getIonsScore() {
        return null;   // No ions score in X!Tandem
    }

    public Double getHomologyThreshold() {
        return null;   //No homology threshold in X!Tandem found
    }

    public Double calculateThreshold(double aConfidenceInterval) {
        return null;  //No confidence interval in X!Tandem found
    }

    public boolean scoresAboveThreshold(double anEValue) {
        return (iPeptide.getDomainExpect() <= anEValue);
    }

    public Double calculateThreshold() {
        return null;  //No confidence interval in X!Tandem found
    }

    public boolean scoresAboveThreshold() {
        // Set eValue to the current EValue from the configuration.
        double eValue = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_XTANDEM_EVALUE"));
        return scoresAboveThreshold(eValue);
    }

    public ArrayList<AnnotationType> getAnnotationType() {
        return iAnnotationType;
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
         * That's the toString()-method of the JLabelImpl
         *
         * @return iName
         */
        public String toString() {
            return iName;
        }
    }

    @Override
    public ArrayList<PeptizerModification> getModifications() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
