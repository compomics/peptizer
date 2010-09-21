package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.pride.PridePeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerModification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-sep-2007
 * Time: 15:30:28
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class SialylationAgent extends Agent {
	// Class specific log4j logger for SialylationAgent instances.
	 private static Logger logger = Logger.getLogger(SialylationAgent.class);

    private final ArrayList<String> prideAccessions = new ArrayList<String>(Arrays.asList("MOD:00137", "MOD:00219", "MOD:00400", "MOD:00565", "MOD:00657", "MOD:00791", "MOD:01293", "MOD:01294", "MOD:01336", "MOD:01337", "MOD:01369", "MOD:01371"));


    public SialylationAgent() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for.
     * <br></br><b>This Agent inspects if the peptide identifcation contains a deamidated Asn residue that was
     * potentially sialylated.</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification.
     *         Where the array of size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0]
     *         gives the inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li>
     *         <li>[n] gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands
     *         for: <ul> <li>+1 if the PeptideIdentification has a deamidated Asn residue.</li> <li>0 if else.</li>
     *         </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {


        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            lTableData = "";
            lARFFData = "";

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            int index = 0;
            boolean hasDeamidatedAsparagine = false;
            String lSequence = lPeptideHit.getSequence();

            // Loop as long as there is an Asn residue in the peptide sequence,
            while ((index = lSequence.indexOf("N", index)) != -1) {
                // If an Asn is inside the peptide, look if it carries a demidation modification.int lNumberOfDeamidations = 0;
                if (lPeptideHit instanceof MascotPeptideHit) {
                    for (PeptizerModification mod : lPeptideHit.getModifications()) {
                        if (mod.getModificationSite() > 0 && mod.getModificationSite() < lPeptideHit.getSequence().length()) {
                            if (mod.getName().equals("deamidation") && lPeptideHit.getSequence().charAt(mod.getModificationSite() - 1) == 'N') {
                                hasDeamidatedAsparagine = true;
                                break;
                            }
                        }
                    }
                } else if (lPeptideHit instanceof OmssaPeptideHit) {
                    for (PeptizerModification mod : lPeptideHit.getModifications()) {
                        if (mod.getName().equals("deamidation of N")) {
                            hasDeamidatedAsparagine = true;
                            break;
                        }
                    }
                } else if (lPeptideHit instanceof PridePeptideHit) {
                    for (PeptizerModification mod : lPeptideHit.getModifications()) {
                        if (mod.getModificationSite() > 0 && mod.getModificationSite() < lPeptideHit.getSequence().length()) {
                            if (prideAccessions.contains(mod.getPrideAccession()) && lPeptideHit.getSequence().charAt(mod.getModificationSite() - 1) == 'N') {
                                hasDeamidatedAsparagine = true;
                                break;
                            }
                        }
                    }
                }
            }

            // Score +1 is the boolean was set to true, Score 0 if else.
            if (hasDeamidatedAsparagine) {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "Deamidated Asn";
                lARFFData = "1";
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = "0";
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
     * Returns a description for the Agent. Use in tooltips and configuration settings.
     * Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for the Sialylation property of the peptide (Ghesquière et al. 2007) . <b>Votes 'Positive_for_selection' if the a deamidated Asn residue is found</b>. Votes 'Neutral_for_selection' if else.</html>";
    }
}
