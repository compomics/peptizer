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
 * Date: 19-sep-2007
 * Time: 11:05:30
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class ModifiedResidueAgent extends Agent {
	// Class specific log4j logger for ModifiedResidueAgent instances.
	 private static Logger logger = Logger.getLogger(ModifiedResidueAgent.class);

    /**
     * The name of the modification that has to be traced.
     */
    public static final String MODIFICATION_NAME = "modified_residue";


    public ModifiedResidueAgent() {
        // Init the general Agent settings.
        initialize(MODIFICATION_NAME);
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for. <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification. Where the array of
     *         size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0] gives the
     *         inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li> <li>[n]
     *         gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands for:
     *         <ul> <li>+1 if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the
     *         PeptideIdentification is a neutral suspect to the Agent's property.</li> <li>-1 if the
     *         PeptideIdentification is opposite to the Agent's property.</li> </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // Localize the property.
        String lModificationName = ((String) this.iProperties.get(MODIFICATION_NAME)).toLowerCase();

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            String lTableData;
            int lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            char lResult = 'x';
            boolean found = false;
            for (PeptizerModification modification : lPeptideHit.getModifications()) {
                if (modification.getModificationSite() == 0) {
                    lResult = 'B';
                } else if (modification.getModificationSite() == lPeptideHit.getSequence().length()) {
                    lResult = 'Z';
                } else {
                    lResult = lPeptideHit.getSequence().toLowerCase().charAt(modification.getModificationSite() - 1);
                }
                if (lModificationName.equals(lResult + "")) {
                    found = true;
                    break;
                }
            }
            if (found) {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "" + lResult;
                lARFFData = 1;
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = 0;
            }

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            // DUMMY implement Agent inspection!

            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lARFFData);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;

    }

    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for a Modification property of the peptide. <b>Votes 'Positive_for_selection' if the PeptideHit is modified ( " + this.iProperties.get(MODIFICATION_NAME) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }


}
