package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 3-okt-2007
 * Time: 10:27:01
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class BCoverage extends Agent {

    /**
     * The b-ion coverage must be above the given percentage.
     */
    public static final String PERCENTAGE = "percentage";


    public BCoverage() {
        // Init the general Agent settings.
        initialize(PERCENTAGE);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA, SearchEngineEnum.XTandem};
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

        // Localize the Dummy property.
        double lPercentageParameter = Double.parseDouble((String) (this.iProperties.get(PERCENTAGE)));

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            lTableData = "";
            lARFFData = "";

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            int[] lCoverage =
                    lPeptideHit.getSequenceCoverage(aPeptideIdentification);
            BigDecimal BionPercentage =
                    new BigDecimal(((lCoverage[0] + 0.0) / (lPeptideHit.getSequence().length() - 1))).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (BionPercentage.doubleValue() <= lPercentageParameter) {
                lTableData = Double.toString(BionPercentage.doubleValue());
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lARFFData = "1";
            } else {
                lTableData = Double.toString(BionPercentage.doubleValue());
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lARFFData = "0";
            }

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
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for the matched b-ion coverage of the peptide. <b>Votes 'Positive_for_selection' if the b-ion coverage is equal or less then ( " + ((Double.parseDouble((String) this.iProperties.get(PERCENTAGE)) + 0.0) * 100) + "%)</b>. Votes 'Neutral_for_selection' if better.</html>";
    }
}
