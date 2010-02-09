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

import java.util.Vector;
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

    /**
     * The name of the modification that has to be traced.
     */
    public static final String MODIFICATION_NAME = "modified_residue";


    public ModifiedResidueAgent() {
        // Init the general Agent settings.
        initialize(MODIFICATION_NAME);
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

        // Localize the Dummy property.
        String lModificationName = ((String) this.iProperties.get(MODIFICATION_NAME)).toLowerCase();

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            String lTableData;
            int lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            char lResult;
            if ((lResult = isModified(lPeptideHit, lModificationName)) != 'x') {
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

    /**
     * This Method Checks the modification status of a PeptideHit, the purpose
     *
     * @param aPPh              - PeptideHit upon inspection.
     * @param aModificationName String for
     * @return boolean - true if peptidehit contains the Modification as described by aModificationName.
     */
    private char isModified(PeptizerPeptideHit aPPh, String aModificationName) {
        // Method for Mascot PeptideHits
        if (aPPh.getSearchEngineEnum() == SearchEngineEnum.Mascot) {
            PeptideHit aPh = (PeptideHit) aPPh.getOriginalPeptideHit();
            boolean lModified = false;
            char lResidue = 'x';
            int lCount = 0;
            while (!lModified && lCount < (aPh.getSequence().length() + 2)) {
                Modification lMod = aPh.getModifications()[lCount];
                // If lMod exists, check if it is an acetylation.
                if (lMod != null) {
                    if (lMod.getShortType().toLowerCase().indexOf(aModificationName) >= 0) {
                        lModified = true;
                        if (lCount == 0) {
                            lResidue = 'B';
                        } else if (lCount == aPh.getSequence().length() + 1) {
                            lResidue = 'Z';
                        } else {
                            lResidue = aPh.getSequence().charAt(lCount - 1);
                        }
                    }
                }
                lCount++;
            }
            return lResidue;
            // Method for OmssaPeptideHits
        } else if (aPPh.getSearchEngineEnum() == SearchEngineEnum.OMSSA) {
            OmssaPeptideHit anOPH = (OmssaPeptideHit) aPPh;
            char lResidue = 'x';
            // Get the id of the modification
            int id = -1;
            Vector<String> modResidues = new Vector();
            for (int i = 0; i < anOPH.modifs.size(); i++) {
                for (int j = 0; j < anOPH.modifs.get(i).getModResidues().size(); j++) {
                    if (anOPH.modifs.get(i).getModName().compareTo("acetylation of protein n-term") == 0) {
                        id = anOPH.modifs.get(i).getModType().intValue();
                        modResidues = anOPH.modifs.get(i).getModResidues();
                        break;
                    }
                }
            }
            // inspect fixed modifications
            String[] decomposedSequence = anOPH.decomposeSequence(anOPH.getSequence());
            for (int j = 0; j < decomposedSequence.length - 1; j++) {
                for (int k = 0; k < modResidues.size(); k++) {
                    // if we have the concerned residue return the modified residue
                    if (decomposedSequence[j].compareTo(modResidues.get(k)) == 0) {
                        if (j == 0) {
                            lResidue = 'B';
                        } else if (j == decomposedSequence.length - 1) {
                            lResidue = 'Z';
                        } else {
                            lResidue = decomposedSequence[j].charAt(0);
                        }
                    }
                }
            }

            // inspect variable modifications
            MSHits aPH = (MSHits) anOPH.getOriginalPeptideHit();
            for (int i = 0; i < aPH.MSHits_mods.MSModHit.size(); i++) {
                // if we have the concerned modification return the modified residue
                if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_modtype.MSMod == id) {
                    if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_site == 0) {
                        lResidue = 'B';
                    } else if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_site == decomposedSequence.length - 1) {
                        lResidue = 'Z';
                    } else {
                        lResidue = decomposedSequence[aPH.MSHits_mods.MSModHit.get(i).MSModHit_site].charAt(0);
                    }
                }
            }
        }

        return 'x';
    }
}