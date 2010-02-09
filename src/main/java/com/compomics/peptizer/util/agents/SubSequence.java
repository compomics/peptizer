package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-jun-2007
 * Time: 10:51:40
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class SubSequence extends Agent {

    public static final String SUBSEQUENCE = "Subsequence";

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public SubSequence() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA, SearchEngineEnum.XTandem};
        compatibleSearchEngine = searchEngines;

        // Init the specific Agent settings.
        this.setSequence(MatConfig.getInstance().getAgentProperties(this.getUniqueID()).getProperty("subsequence"));
    }

    /**
     * {@inheritDoc} This Agent inspects the Sequence property of a PeptideIdentification. If iSequence occurs in the
     * first rank PeptideHit. If the sequence is matched, inspection will return 1. If the sequence is not matched,
     * inspection will return 0.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        String lSequence = (String) this.iProperties.get(SUBSEQUENCE);

        // The resulting Inspection score.
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];


        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            //2. Check if iSequence matches.

            // Agent Result.
            // TableRow information.

            int index = -1;
            if ((index = lPeptideHit.getSequence().indexOf(lSequence)) >= 0) {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                iReport.addReport(AgentReport.RK_RESULT, lScore[i]);
                StringBuffer sb = new StringBuffer();
                sb.append(lPeptideHit.getSequence().substring(0, index));
                sb.append('-');
                sb.append(lSequence);
                sb.append('-');
                sb.append(lPeptideHit.getSequence().substring(index + lSequence.length()));
                iReport.addReport(AgentReport.RK_TABLEDATA, sb.toString());
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                iReport.addReport(AgentReport.RK_RESULT, lScore[i]);
                iReport.addReport(AgentReport.RK_TABLEDATA, "NA");
            }

            // Attribute Relation File Format (Just put the Result (1 or 0) if the sequence is matched or not.
            iReport.addReport(AgentReport.RK_ARFF, lScore[i].score);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);

        }
        return lScore;
    }

    /**
     * Sets the partial sequence that the PeptideHit must contain.
     *
     * @param aSequence String partial sequence.
     */
    public void setSequence(String aSequence) {
        aSequence = aSequence.toUpperCase();
        this.iProperties.put(SUBSEQUENCE, aSequence);
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects whether the peptide contains a given subsequence. <b>Votes 'Positive_for_selection' if the peptide contains the subsequence (" + this.iProperties.get(SUBSEQUENCE) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
        return s;
    }
}
