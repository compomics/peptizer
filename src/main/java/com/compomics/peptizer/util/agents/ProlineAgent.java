package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 19-sep-2007
 * Time: 14:34:09
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class ProlineAgent extends Agent {
	// Class specific log4j logger for ProlineAgent instances.
	 private static Logger logger = Logger.getLogger(ProlineAgent.class);

    /**
     * This Property defines the relative intensity of the Proline peak compared to the highest peak in the
     * spectrum.
     */
    public static final String INTENSITY = "intensity";
    private double iIntensity = 0.0;

    public ProlineAgent() {
        // Init the general Agent settings.
        initialize(INTENSITY);
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for. <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification. Where the array of
     *         size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0] gives the
     *         inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li> <li>[n]
     *         gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands for:
     *         <ul> <li>+1 if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the
     *         PeptideIdentification is a neutral suspect to the Agent's property.</li> <li>-1 if the
     *         PeptideIdentification is opposite to the Agent's property.</li> </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        if (iIntensity != Double.parseDouble((String) this.iProperties.get(INTENSITY))) {
            iIntensity = Double.parseDouble((String) this.iProperties.get(INTENSITY));
        }

        // Localize the Dummy property.
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {
            // Create output for this peptidehit.
            StringBuffer sb = new StringBuffer();

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            boolean result = false;
            boolean lContainsProline = false;
            int index = 0;

            while ((index =
                    lPeptideHit.getSequence().indexOf("P", index + 1)) > 0) { // NTerminal Proline residue is of no matter.
                lContainsProline = true;
                // Documentation example:
                // Peptide with sequence PEPTIDE, we want to look for the b2 and y5.
                // index will be 2 so we want to check the relevance of b(index) and y(length-index)
                boolean boolB = getBoolB(lPeptideHit, aPeptideIdentification, index);
                boolean boolY = getBoolY(lPeptideHit, aPeptideIdentification, index);
                if (boolB || boolY) {
                    result = true;
                    if (boolB) {
                        sb.append("b").append(index);
                        if (boolY) {
                            sb.append(" - ");
                        }
                    }
                    if (boolY) {
                        sb.append("y").append(lPeptideHit.getSequence().length() - index);
                    }
                }
            }

            if (result) {
                // Score negative if Proline peak is found! This is as expected!
                lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION;
                lTableData = sb.toString();
                lARFFData = Integer.toString(-1);
            } else if (lContainsProline) {
                // Score +1 if no intense Proline peak is found in a Proline peptide!
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "No Proline!";
                lARFFData = Integer.toString(1);
            } else {
                // Score 0 if neutral. Ex, no Proline in the peptide.
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = Integer.toString(0);
            }

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            // DUMMY implement Agent inspection!

            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, new Integer(lARFFData));

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;

    }

    /**
     * This Method Checks the relevance of b(index) or y(index)
     *
     * @param aPPh - PeptizerPeptideHit upon inspection.
     * @return boolean          - true if proline B ion peak is found
     */
    private boolean getBoolB(PeptizerPeptideHit aPPh, PeptideIdentification aPeptideIdentification, int index) {
        double intMax = aPeptideIdentification.getSpectrum().getMaxIntensity();
        HashMap<String, Vector<PeptizerFragmentIon>> annotationMap = aPPh.getAllAnnotation(aPeptideIdentification, 0);
        String key = 0 + "" + aPPh.getAdvocate().getAdvocatesList().get(0).getId() + "" + 1 + "";
        for (PeptizerFragmentIon ion : annotationMap.get(key)) {
            if (ion.getType() == IonTypeEnum.b
                    || ion.getType() == IonTypeEnum.bH2O
                    || ion.getType() == IonTypeEnum.bNH3) {
                if (ion.getIntensity() >= iIntensity * intMax && ion.getNumber() == index) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This Method Checks the relevance of b(index) or y(index)
     *
     * @param aPPh - PeptizerPeptideHit upon inspection.
     * @return boolean          - true if proline B ion peak is found
     */
    private boolean getBoolY(PeptizerPeptideHit aPPh, PeptideIdentification aPeptideIdentification, int index) {
        double intMax = aPeptideIdentification.getSpectrum().getMaxIntensity();
        HashMap<String, Vector<PeptizerFragmentIon>> annotationMap = aPPh.getAllAnnotation(aPeptideIdentification, 0);
        String key = 0 + "" + aPPh.getAdvocate().getAdvocatesList().get(0).getId() + "" + 1 + "";
        for (PeptizerFragmentIon ion : annotationMap.get(key)) {
            if (ion.getType() == IonTypeEnum.y
                    || ion.getType() == IonTypeEnum.yH2O
                    || ion.getType() == IonTypeEnum.yNH3) {
                if (ion.getIntensity() >= iIntensity * intMax && ion.getNumber() == aPPh.getSequence().length() - index) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for the Proline fragmentation properties. <b>Votes 'Positive_for_selection' if the peptide contains a Pro and no relative intense (max intensity x " + this.iProperties.get(INTENSITY) + ") b or y ion Nterminal of the Pro is found.</b>. Votes 'Neutral_for_selection' if no Proline. Votes 'Negative_for_selection' if an intens fragment ion NTerminal to the Pro is found.</html>";
    }
}
