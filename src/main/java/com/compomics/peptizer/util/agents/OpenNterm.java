package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-okt-2007
 * Time: 17:58:12
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class OpenNterm extends Agent {
	// Class specific log4j logger for OpenNterm instances.
	 private static Logger logger = Logger.getLogger(OpenNterm.class);


    public OpenNterm() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for.
     * <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
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

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData = "";
            String lARFFData = "";

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            String modificationType = getModificationType(lPeptideHit);


            if (modificationType == null) {
                // A. No modification at Nterminus
                lTableData = "Open";
                lARFFData = "1";
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else {
                // B. Modification at the Nterminus. Score 0.
                lTableData = modificationType;
                lARFFData = "0";
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

            // Build the report!
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
     * This Method Checks if there is a modification on the n-term and returns its name.
     *
     * @param aPH - PeptizerPeptideHit upon inspection.
     * @return String           - The modification type found.
     */

    private String getModificationType(PeptizerPeptideHit aPH) {
        for (PeptizerModification mod : aPH.getModifications()) {
            if (mod.getModificationSite() == 0) {
                return mod.getName();
            }
        }
        return null;
    }

    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings.
     * Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for the NTerminal modification status of the peptide. <b>Votes 'Positive_for_selection' if the peptide has an unmodified Nterminus.</b>. Votes 'Neutral_for_selection' if modified.</html>";
    }
}
