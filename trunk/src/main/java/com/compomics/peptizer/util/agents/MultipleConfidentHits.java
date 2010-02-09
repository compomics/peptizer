package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-sep-2007
 * Time: 15:41:09
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class MultipleConfidentHits extends Agent {

    /**
     * The delta value above threshold.
     */
    public static final String DELTA = "delta";

    /**
     * This Agent implementation will report for confident peptidehit n if the n+1 peptidehit is above confidence as
     * well.
     */
    public MultipleConfidentHits() {
        // Init the general Agent settings.
        initialize(DELTA);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot };
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

        int lNumberOfConfidentPeptideHits = aPeptideIdentification.getNumberOfConfidentPeptideHits();
        AgentVote[] lScore = new AgentVote[lNumberOfConfidentPeptideHits];

        double lDeltaParameter = Double.parseDouble((String) this.iProperties.get(DELTA));

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData = "";
            String lARFFData = "";

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.


            if ((i + 1) < lNumberOfConfidentPeptideHits) {
                // If there are more confident peptidehits left after this one, score '+1'.
                double lDeltaIonscore =
                        aPeptideIdentification.getPeptideHit(i).getIonsScore() - aPeptideIdentification.getPeptideHit(i + 1).getIonsScore();

                // If (ionscore N - ionscore N-1) is smaller then the parameter threshold, score +1.
                //
                // Example A: (peptidhit 1:71- peptidehit 2:42)=29
                // Parameter delta = 20
                // 29 > 20, score will be 0. Since the first peptidehit is very confident, we don't want to pay a lot of attention to this id.
                //
                // Example B: (peptidhit 1:45- peptidehit 2:39)=6
                // Parameter delta = 20
                // 6 < 20, score will be 1. Since the first peptidehit is not much more confident then the second, we want to pay a lot of attention to this id.
                //
                // NOTE: setting the parameter to 1000 will ALWAYS score +1 if there is another confident hit.

                if (lDeltaIonscore >= lDeltaParameter) {
                    lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                } else {
                    lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                }
            } else {
                // Else score '0'.
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            if (lScore[i].score == 1) {
                iReport.addReport(AgentReport.RK_TABLEDATA, true);
            } else {
                iReport.addReport(AgentReport.RK_TABLEDATA, "NA");
            }

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lScore[i].score);

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
        return "<html>Inspects for if there are more confident identifications from one spectrum. <b>Votes 'Positive_for_selection' if the identification is followed by a lower ranked but confident identification AND the score difference is greater then " + iProperties.get(DELTA) + "</b>. Votes 'Neutral_for_selection' if else.</html>";
    }
}
