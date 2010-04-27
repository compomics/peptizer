package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.interfaces.Modification;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSHits;
import de.proteinms.omxparser.util.MSModHit;

import java.util.List;
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


    public OpenNterm() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA};
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
            String modificationType = null;
            modificationType = getModificationType(lPeptideHit);


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
     * This Method Checks if there is a modification on the n-term and returns its type.
     *
     * @param aPH - PeptizerPeptideHit upon inspection.
     * @return String           - The modification type found.
     */

    private String getModificationType(PeptizerPeptideHit aPH) {
        String modificationType = null;
        boolean identifiedByMascot = aPH.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPH.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);

        if (identifiedByMascot) {
            Modification lModification = ((PeptideHit) aPH.getOriginalPeptideHit(SearchEngineEnum.Mascot)).getModifications()[0];
            if (lModification != null) {
                modificationType = lModification.getShortType();
            }
        } else if (identifiedByOMSSA) {
            OmssaPeptideHit anOPh = (OmssaPeptideHit) aPH;

            // see if there is a fixed modification :
            // Type 1 : modn
            // Type 2 : modnaa
            // Type 5 : modnp
            // Type 6 : modnpaa
            // Other types : if the residue is concerned
            String nTerm = anOPh.decomposeSequence(anOPh.getSequence())[0];
            List<Integer> ids = anOPh.getFixedModifications();
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < anOPh.modifs.size(); j++) {
                    if (anOPh.modifs.get(j).getModNumber() == ids.get(j).intValue()) {
                        if (anOPh.modifs.get(j).getModType() == 1 || anOPh.modifs.get(j).getModType() == 2 || anOPh.modifs.get(j).getModType() == 5 || anOPh.modifs.get(j).getModType() == 6) {
                            return anOPh.modifs.get(j).getModName();
                        } else {
                            for (int k = 0; k < anOPh.modifs.get(j).getModResidues().size(); k++) {
                                if (anOPh.modifs.get(j).getModResidues().get(k).compareTo(nTerm) == 0) {
                                    return anOPh.modifs.get(j).getModName();
                                }
                            }
                        }
                        break;
                    }
                }
            }

            // see if there is a variable modification
            List<MSModHit> modifications = ((MSHits) anOPh.getOriginalPeptideHit(SearchEngineEnum.OMSSA)).MSHits_mods.MSModHit;
            for (int j = 0; j < modifications.size(); j++)
                if (modifications.get(j).MSModHit_site == 0) {
                    modificationType = modifications.get(j).MSModHit_modtype.toString();
                }
        }
        return modificationType;
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
