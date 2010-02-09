package com.compomics.peptizer.util;

import com.compomics.peptizer.util.enumerator.AgentVote;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jan 21, 2009
 * Time: 2:52:47 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class was developped to generate comments for peptide identifications.
 */
public class CommentGenerator {

    /**
     * empty constructor.
     */
    public CommentGenerator() {
    }

    /**
     * Returns a String commenting on the selective Agents of the PeptideIdentication's nth peptidehit.
     *
     * @param aPeptideIdentification PeptideIdentication to generate the comment.
     * @param aPeptideHit            Peptidehitnumber to generate the commment for. '1' returns the first peptidehit.
     * @return String comment on the PeptideIdentication .
     */
    public static String getCommentForSelectiveAgents(PeptideIdentification aPeptideIdentification, int aPeptideHit) {
        List aAgentReports = aPeptideIdentification.getAgentReports(aPeptideHit);
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < aAgentReports.size(); i++) {
            AgentReport lAgentReport = (AgentReport) aAgentReports.get(i);
            if (lAgentReport.getReport(AgentReport.RK_RESULT) == AgentVote.POSITIVE_FOR_SELECTION) {
                sb.append(AgentFactory.getInstance().getAgent(lAgentReport.getAgentID()) + " - " + lAgentReport.getReport(AgentReport.RK_TABLEDATA));
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
