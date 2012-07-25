package com.compomics.peptizer.util;

import com.compomics.peptizer.util.enumerator.AgentVote;
import org.apache.log4j.Logger;

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
    // Class specific log4j logger for CommentGenerator instances.
    private static Logger logger = Logger.getLogger(CommentGenerator.class);
    private static String iLineFeedReplacement = "*";

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
        if(aPeptideIdentification.getValidationReport().getAutoComment() != null){
            if (!aPeptideIdentification.getValidationReport().getAutoComment().equals(ValidationReport.DEFAULT_COMMENT)) {
            // add user specified comment!
                sb.append(aPeptideIdentification.getValidationReport().getAutoComment());
                sb.append("/n");
            }
        }
        for (Object aAgentReport : aAgentReports) {
            AgentReport lAgentReport = (AgentReport) aAgentReport;
            if (lAgentReport.getReport(AgentReport.RK_RESULT) == AgentVote.POSITIVE_FOR_SELECTION) {
                sb.append(AgentFactory.getInstance().getAgent(lAgentReport.getAgentID()) + " - " + lAgentReport.getReport(AgentReport.RK_TABLEDATA));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a String commenting on the selective Agents of the PeptideIdentication's nth peptidehit.
     *
     * @param aPeptideIdentification PeptideIdentication to generate the comment.
     * @param aPeptideHit            Peptidehitnumber to generate the commment for. '1' returns the first peptidehit.
     * @param aLineFeeds             Keeps 'line separators' if TRUE. Replaces linefeeds with '*' if set to FALSE.
     * @return String comment on the PeptideIdentication .
     */
    public static String getCommentForSelectiveAgents(PeptideIdentification aPeptideIdentification, int aPeptideHit, boolean aLineFeeds) {
        String lComment = getCommentForSelectiveAgents(aPeptideIdentification, aPeptideHit);
        String linesep = System.getProperty("line.separator");
        lComment = lComment.replaceAll(linesep, iLineFeedReplacement);

        return lComment;
    }
}
