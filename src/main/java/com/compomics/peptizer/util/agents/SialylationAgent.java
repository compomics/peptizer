package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSHits;

import java.util.List;
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

    public SialylationAgent() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA};
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
                // If an Asn is inside the peptide, look if it carries a demidation modification.
                hasDeamidatedAsparagine = isDeaminated(lPeptideHit, index);
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
     * This Method Checks the modification status of a PeptideHit, the purpose
     *
     * @param aPPh  - PeptizerPeptideHit upon inspection.
     * @param index - Position to inspect
     * @return boolean           - true if peptide deaminated at this position
     */
    private boolean isDeaminated(PeptizerPeptideHit aPPh, int index) {
        boolean identifiedByMascot = aPPh.getAdvocate().getAdvocates().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPPh.getAdvocate().getAdvocates().contains(SearchEngineEnum.OMSSA);

        if (identifiedByMascot) {
            PeptideHit aMPh = (PeptideHit) aPPh.getOriginalPeptideHit(SearchEngineEnum.Mascot);
            String lModificationShortType = aMPh.getModifications()[index].getShortType();
            if (lModificationShortType.equalsIgnoreCase("dam")) {
                return true;
            }
        } else if (identifiedByOMSSA) {
            OmssaPeptideHit anOPh = (OmssaPeptideHit) aPPh;
            MSHits msHits = (MSHits) aPPh.getOriginalPeptideHit(SearchEngineEnum.OMSSA);

            // Look for the id of the deamination
            int id = -1;
            for (int i = 0; i < anOPh.modifs.size(); i++) {
                if (anOPh.modifs.get(i).getModName().compareTo("deamidation of N and Q") == 0) {
                    id = anOPh.modifs.get(i).getModNumber();
                    break;
                }
            }

            // Inspect fixed modifications
            List<Integer> fixedMods = anOPh.getFixedModifications();
            for (int i = 0; i < fixedMods.size(); i++) {
                if (fixedMods.get(i).intValue() == id) {
                    return true;
                }
            }

            // Inspect variable modifications
            for (int i = 0; i < msHits.MSHits_mods.MSModHit.size(); i++) {
                if (msHits.MSHits_mods.MSModHit.get(i).MSModHit_site == index && msHits.MSHits_mods.MSModHit.get(i).MSModHit_modtype.MSMod == id) {
                    return true;
                }
            }
        }
        return false;
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