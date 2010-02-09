package com.compomics.peptizer.util.agentaggregator;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.peptizer.util.enumerator.AgentVote;

import java.util.ArrayList;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-mei-2007
 * Time: 15:13:52
 */

/**
 * Class description: ------------------ This class was developed as an aggregator that only regards the profile of the
 * best peptidehit. For the rest it works identical as the SimpleAgentAggregator.
 */
public class BestHitAggregator extends AgentAggregator {

    /**
     * String for score property.
     */
    public static final String SCORE = "score";


    /**
     * Empty constructor. Gets all active Agents from the AgentFactory. If the allover Agents score is equal or bigger
     * then aScore, the PeptideIdentification is a match.
     */
    public BestHitAggregator() {
        // The singleton MatConfig returns Properties of this Class by the unique class reference String.
        initialize(SCORE);
    }

    /**
     * {@inheritDoc}
     */
    public AgentAggregationResult match(PeptideIdentification aPeptideIdentification) {

        boolean boolConfident = false;
        boolean boolMatch = false;
        boolean boolVetoWasCalled = false;

        Integer lThresholdScore = new Integer(iProperties.getProperty(SCORE));

        int counter = -1;
        ArrayList<AgentVote> results = new ArrayList<AgentVote>(iAgentsCollection.size());

        if (aPeptideIdentification.getNumberOfConfidentPeptideHits() > 0) {
            // There are confident peptidehits.
            boolConfident = true;
            // first loop with i, first dimension looping the Agents.

            for (Agent lAgent : iAgentsCollection) {

                counter++;

                AgentVote[] lVotes = lAgent.inspectIfPossible(aPeptideIdentification);

                if (lVotes != null) {
                    AgentVote lVote = lVotes[0];
                    if (!lAgent.isInforming()) {
                        results.add(lVote);
                    }

                    if (lVote == AgentVote.POSITIVE_FOR_SELECTION && lAgent.hasVeto()) {
                        boolVetoWasCalled = true;
                    }
                }
            }

            // Return true if Veto was called.
            if (boolVetoWasCalled) {
                boolMatch = true;
            } else {
                // Else count all the results, return true if 'iScore' or more agents match the
                int lSumScore = sumVotes(results);

                // Check if any of the sum's surpasses the iScore. Match if so!
                if (lSumScore >= lThresholdScore) {
                    boolMatch = true;
                }
            }

        }
        // Return the int result according the triplet separation.
        if (boolConfident) {
            if (boolMatch) {
                // Confident && Match
                return AgentAggregationResult.MATCH;
            } else {
                // Confident && NO Match
                return AgentAggregationResult.NON_MATCH;
            }
        } else {
            if (aPeptideIdentification.getNumberOfPeptideHits() > 0) {
                // Peptide identifications, but none Confident.
                return AgentAggregationResult.NON_CONFIDENT;
            } else {
                // No Peptide identifciations for this spectrum.
                return AgentAggregationResult.NO_IDENTIFICATION;
            }
        }
    }

    /**
     * Returns the sum of an int[]. ex: [Hit1] -> [0][1][0][1][-1] will return [1]
     *
     * @param aAgentVotes ArrayList with AgentVotes.
     * @return int Array with sums of the second dimension.
     */
    private int sumVotes(ArrayList<AgentVote> aAgentVotes) {
        int lSumOfScores = 0;
        for (AgentVote aAgentScore : aAgentVotes) {
            lSumOfScores = lSumOfScores + aAgentScore.score;
        }
        return lSumOfScores;
    }

    /**
     * Return a String descritiption of the AgentAggregator.
     *
     * @return String describing the AgentAggregator.
     */
    public String getDescription() {
        return "Best Hit AgentAggregator description. The inspection results (+1, 0 or -1) for the best confident peptidehit are summed." +
                "If the sum of AgentScores for the best peptidehit is at least " + (iProperties.getProperty(SCORE)) +
                "or +1 of a single Agent with veto rights, the peptideidentification is matched.";
    }

    /**
     * Return a String description with HTML formatting of the Aggregator.
     *
     * @return String with HTML describing the Aggregator - for use in GUI!
     */
    public String getHTMLDescription() {
        int lScore = new Integer(iProperties.getProperty(SCORE));

        return "<HTML>\n" +
                "<BODY>\n" +
                "<P><BIG>Best Hit AgentAggregator explanation.</BIG>\n" +
                "<DL>\n" +
                "\t<DT><b>General</b>\n" +
                "\t<DD>A peptideidentification is a match if at least <b>" + lScore + "</b> Agents return +1 upon inspection of the best peptidehit<br>or +1 to a single Agent inspection with veto rights.\n" +
                "\t<DT><b>Aggregation</b>\n" +
                "\t<DD>The inspection is performed only on the most confident peptidehit are summed. <br>If a sum is greater then or equal to <b>" + lScore + "</b> , only then the peptideidentiication will be matched.<br>Plus, if the best peptidehit receives +1 upon inspection from an Agent with veto set to true, the peptideidentification is matched as well.\n" +
                "\t<DT><b>Score property</b>\n" +
                "\t<DD>Define the number of Agents that must result in a positive inspection.\n" +
                "</DL>\n" +
                "</BODY>\n" +
                "</HTML>";
    }
}