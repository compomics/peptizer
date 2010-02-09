package com.compomics.peptizer.util.agentaggregator;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.peptizer.util.enumerator.AgentVote;

import java.util.Iterator;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-mei-2007
 * Time: 15:13:52
 */

/**
 * Class description: ------------------ This class was developed as a simple  implementation of the AgentAggregator
 * interface.
 */
public class SimpleAgentAggregator extends AgentAggregator {

    /**
     * String for score property.
     */
    public static final String SCORE = "score";


    /**
     * Empty constructor. Gets all active Agents from the AgentFactory. If the allover Agents score is equal or bigger
     * then aScore, the PeptideIdentification is a match.
     */
    public SimpleAgentAggregator() {
        initialize(SCORE);
    }

    /**
     * {@inheritDoc}
     */
    public AgentAggregationResult match(PeptideIdentification aPeptideIdentification) {

        boolean lConfident = false;
        boolean lMatch = false;

        boolean lVetoCalled = false;
        int lAgentCount = -1;
        int lNumberOfConfidentPeptideHits = aPeptideIdentification.getNumberOfConfidentPeptideHits();
        int lNumberOfAgents = iAgentsCollection.size();
        AgentVote[][] results = new AgentVote[lNumberOfAgents][lNumberOfConfidentPeptideHits];


        if (lNumberOfConfidentPeptideHits > 0) {
            // There are confident peptidehits.
            lConfident = true;
            // first loop with i, first dimension looping the Agents.
            for (Iterator lIterator = iAgentsCollection.iterator(); lIterator.hasNext();) {
                Agent lAgent = (Agent) lIterator.next();
                lAgentCount++;

                // TODO update observer agents on this aggregator.
                results[lAgentCount] = lAgent.inspectIfPossible(aPeptideIdentification);
                if (results != null) {
                    for (int i = 0; i < results[lAgentCount].length; i++) {
                        AgentVote lResult = results[lAgentCount][i];
                        if (lResult == AgentVote.POSITIVE_FOR_SELECTION && lAgent.hasVeto()) {
                            lVetoCalled = true;
                        }
                    }
                }
            }
        }

        // Return true if Veto was called.
        if (lVetoCalled) {
            lMatch = true;
        } else {
            // Else count all the results, return true if 'iScore' or more agents match the
            int[] lSumScore = sum(results);

            // Check if any of the sum's surpasses the iScore. Match if so!
            for (int i = 0; i < lSumScore.length; i++) {
                if (lSumScore[i] >= new Integer(iProperties.getProperty(SCORE))) {
                    lMatch = true;
                    break;
                }
            }
        }

        // Return the int result according the triplet separation.
        if (lConfident) {
            if (lMatch) {
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
     * Returns the sum of an int[][] in an int[]. ex: [Hit1] -> [0][1][0][1] [Hit2] -> [1][1][1][1] will return [2][4]
     *
     * @param aAgentScores 2D int array.
     * @return int Array with sums of the second dimension.
     */
    private int[] sum(AgentVote[][] aAgentScores) {
        int[] lSumScores = new int[aAgentScores[0].length];
        for (int i = 0; i < lSumScores.length; i++) {
            lSumScores[i] = 0;
        }

        for (int i = 0; i < lSumScores.length; i++) {
            for (int j = 0; j < aAgentScores.length; j++) {
                lSumScores[i] = lSumScores[i] + aAgentScores[j][i].score;
            }
        }
        return lSumScores;
    }

    /**
     * Return a String descritiption of the AgentAggregator.
     *
     * @return String describing the AgentAggregator.
     */
    public String getDescription() {
        return "Simple AgentAggregator description. The inspection results (+1, 0 or -1) for each confident peptidehit are summed." +
                "If the sum of a peptideidentification is at least " + (iProperties.getProperty(SCORE)) +
                "or +1 to a single Agent with veto rights, a peptideidentification is matched.";
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
                "<P><BIG>Simple Agent Aggregator explanation.</BIG>\n" +
                "<DL>\n" +
                "\t<DT><b>General</b>\n" +
                "\t<DD>A peptideidentification is a match if at least <b>" + lScore + "</b> Agents return +1 upon inspection of a confident peptidehit<br>or +1 to a single Agent inspection with veto rights.\n" +
                "\t<DT><b>Aggregation</b>\n" +
                "\t<DD>The inspection results for each confident peptidehit are summed. <br>If a sum is greater then or equal to <b>" + lScore + "</b> , only then the peptideidentiication will be matched.<br>Plus, if a confident peptidehit receives +1 upon inspection from an Agent with veto set to true, the peptideidentification is matched as well.\n" +
                "\t<DT><b>Score property</b>\n" +
                "\t<DD>Define the number of Agents that must result in a positive inspection.\n" +
                "</DL>\n" +
                "</BODY>\n" +
                "</HTML>";
    }
}
