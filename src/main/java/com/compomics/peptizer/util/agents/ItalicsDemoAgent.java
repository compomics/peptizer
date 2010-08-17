package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-aug-2007
 * Time: 11:10:16
 */

/**
 * Class description: ------------------ This class was developed to demo the Italics font if an Agent returns -1. It
 * simply controls whether the Identification scores below 95% confidence.
 */
public class ItalicsDemoAgent extends Agent {

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public ItalicsDemoAgent() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;

    }


    /**
     * {@inheritDoc} This Demo Agent serves to display Italics Font in the Table!
     *
     * @param aPeptideIdentification PeptideIdentification to be inspected.
     * @return Integer result If a Peptide is below 95% confidence, the Agent will retrun +1. If a Peptide is above 95%
     *         confidence, the Agent will return -1. (And must be displayed in italics)
     */

    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];
        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            if (lPeptideHit.validatedByOneAdvocate()) {
                lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION;
            } else {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
            }

            // Build the report!
            // Agent Result.

            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            if (lScore[i].score == -1) {
                iReport.addReport(AgentReport.RK_TABLEDATA, "More then 95% Confident");
            } else {
                iReport.addReport(AgentReport.RK_TABLEDATA, "Not confident!");
            }

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lScore[i]);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects for the confidence of the peptide. <b>Votes 'Positive_for_selection' if the peptide less then 95% confident.</b>. Votes 'Negative_for_selection' if the peptide is confident.</html>";
        return s;
    }


}
