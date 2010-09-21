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
 * User: Kenny
 * Date: 13-mrt-2008
 * Time: 15:24:31
 * To change this template use File | Settings | File Templates.
 */


/**
 * Class description: ------------------ This Agent was developed to
 */

public class SingleProteinHit extends Agent {
	// Class specific log4j logger for SingleProteinHit instances.
	 private static Logger logger = Logger.getLogger(SingleProteinHit.class);


    public SingleProteinHit() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property SingleProteinHit has to
     * inspect for.
     * <br></br><b>All implementations must both initiate and append AgentReport iReport!!</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification.
     *         Where the array of size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0]
     *         gives the inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li>
     *         <li>[n] gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands
     *         for: <ul> <li>+1 if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the
     *         PeptideIdentification is a neutral suspect to the Agent's property.</li> <li>-1 if the
     *         PeptideIdentification is opposite to the Agent's property.</li> </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Create a new Agent Report for inspection of the "i"th peptidehit.
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            lTableData = "";
            lARFFData = "";

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            // The logic implementation of SingleProteinHit comes in here.

            if (lPeptideHit.getProteinHits().size() == 1) {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "1 Protein";
                lARFFData = "" + lPeptideHit.getProteinHits().size();
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = lPeptideHit.getProteinHits().size() + " Proteins";
                lARFFData = "" + lPeptideHit.getProteinHits().size();
            }

            // Create a report on the inspection!

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
     * Returns a description for SingleProteinHit. This method will be called by tooltips and configuration settings.
     * Fill in the description of SingleProteinHit. Report on purpose and a minor on actual implementation.
     *
     * @return String description of SingleProteinHit.
     */
    public String getDescription() {
        return "<html>Inspects for the SingleProteinHit property of the peptide. <b>Votes 'Positive_for_selection' if the peptide has a single Protein Hit </b>. Votes 'Neutral_for_selection' if more.</html>";
    }
}
