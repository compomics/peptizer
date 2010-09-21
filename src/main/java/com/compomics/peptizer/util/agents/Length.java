package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 28-jun-2007
 * Time: 16:22:21
 */

/**
 * Class description: ------------------ This class was developed to inspect on PeptideIdentifications length.
 */
public class Length extends Agent {
	// Class specific log4j logger for Length instances.
	 private static Logger logger = Logger.getLogger(Length.class);

    /**
     * Property tag for length.
     */
    private static final String LENGTH = "length";

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public Length() {
        // Init the Agent settings.
        initialize(LENGTH);
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }


    /**
     * {@inheritDoc} This Agent inspects the length of a PeptideIdentification. If a Peptide is shorter then the Length
     * Property inspect will retrun +1. If a Peptide is longer or equal then the Length Property inspect will retrun 0.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected by the length Agent.
     * @return AgentVote[] with results of inspection. If a Peptide is shorter then the Length Property inspect will
     *         retrun +1. If a Peptide is longer or equal then the Length Property inspect will retrun 0.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // The Agent's length.
        int lLength = Integer.parseInt((String) (this.iProperties.get(LENGTH)));

        /**
         * The returning votes.
         */
        AgentVote[] lVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lVotes.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            int lPeptideLength = lPeptideHit.getSequence().length();


            // Only inspect on the best PeptideHit!
            if (lPeptideLength < lLength) {
                lVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else {
                lVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

            // Build the report!
            // Agent Result.

            iReport.addReport(AgentReport.RK_RESULT, lVotes[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, new Integer(lPeptideLength));

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, new Integer(lPeptideLength));

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lVotes;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects for the length of the peptide. <b>Votes 'Positive_for_selection' if the peptide is smaller then the given length (" + this.iProperties.get(LENGTH) + ")</b>. Votes 'Neutral_for_selection' if more.</html>";
        return s;
    }

}
