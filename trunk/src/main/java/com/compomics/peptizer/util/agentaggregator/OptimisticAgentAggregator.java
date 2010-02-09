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
 * Date: 14-sep-2007
 * Time: 11:34:34
 */

/**
 * Class description: ------------------ This class was developed as an optimistic implementation of the
 * OptimisticAgentAggregator interface.
 */
public class OptimisticAgentAggregator extends AgentAggregator {

    /**
     * String for score property.
     */
    public static final String SCORE = "score";


    /**
     * String for score property.
     */
    public static final String DUMMY = "dummy";

    /**
     * Empty constructor. Gets all active Agents from the AgentFactory. If the allover Agents score is equal or bigger
     * then aScore, the PeptideIdentification is a match.
     */
    public OptimisticAgentAggregator() {
        initialize(new String[]{DUMMY, SCORE});
    }


    /**
     * Matches the PeptideIdentification against the OptimisticAgentAggregator's values by a series of independent
     * Agents.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be matched.
     * @return </dl>Integer if the PeptideIdentifications suits the profile values.<br> <dl> <dt>+1<dd> The
     *         PeptideIdentification is <b>confident</b> and is a <b>true match</b> against the profile. <dt>0<dd> The
     *         PeptideIdentification is <b>confident</b> and is a <b>false match</b> against the profile. <dt>-1<dd> The
     *         PeptideIdentification is <b>not confident</b>. <dt><dd>
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
                // Todo update observer agents on this aggregator.
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
                int lScore = aAgentScores[j][i].score;
                if (lScore == 1) {
                    lSumScores[i] = lSumScores[i] + lScore;
                } else {
                    // Concept of the Optimistic agentaggregator.
                    // Alter neutral votes into non-selecting votes.
                    lSumScores[i] = lSumScores[i] - 1;
                }
            }
        }
        return lSumScores;
    }

    /**
     * Return a String descritiption of the OptimisticAgentAggregator.
     *
     * @return String describing the OptimisticAgentAggregator.
     */
    public String getDescription() {
        return "The inspection score for each confident peptidehit is summed. '0' is considered optimistic by this aggregator and sums '-1' to the final scoring. If the final sum is greater then or equal to " + iProperties.getProperty(SCORE) + " , only then the peptideidentiication will be matched. Plus, if a confident peptidehit with veto set to true returns +1 upon inspection, the peptideidentification is matched as well.\n";
    }

    /**
     * Return a String description with HTML formatting of the OptimisticAgentAggregator.
     *
     * @return String with HTML describing the OptimisticAgentAggregator - for use in GUI!
     */
    public String getHTMLDescription() {
        int lScore = new Integer(iProperties.getProperty(SCORE));
        return "<HTML>\n" +
                "<BODY>\n" +
                "<P><BIG>Optimistic Agent Aggregator explanation.</BIG>\n" +
                "<DL>\n" +
                "\t<DT><b>General</b>\n" +
                "\t<DD>A peptideidentification is a match if the sum from Agent scores is bigger then <b>" + lScore + "</b> or +1 to a single Agent inspection with veto rights.\n" +
                "\t<DT><b>Aggregation</b>\n" +
                "\t<DD>The inspection score for each confident peptidehit is summed. <b>0 is considered optimistic by this aggregator and adds '-1' to the final scoring.</b> <br>If the final sum is greater then or equal to <b>" + lScore + "</b> , only then the peptideidentiication will be matched.<br>Plus,  if a confident peptidehit receives +1 upon inspection from an Agent with veto set to true, the peptideidentification is matched as well.\n" +
                "\t<DT><b>Score property</b>\n" +
                "\t<DD>Define the threshold for the final score build by Agent inspections.\n" +
                "</DL>\n" +
                "</BODY>\n" +
                "</HTML>";
    }
}