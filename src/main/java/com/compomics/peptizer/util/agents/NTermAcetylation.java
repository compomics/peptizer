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

/**
 * Created by IntelliJ IDEA. User: kenny Date: 28-sep-2006 Time: 14:49:25
 */
public class NTermAcetylation extends Agent {

    /**
     * This empty private constructor can only be accessed from a static method getInstance().
     */
    public NTermAcetylation() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * {@inheritDoc} This Agent inspects the Deamidation property of a PeptideIdentification.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {
        // This inspect score will stay '0' if there are no deamidations found in
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < aPeptideIdentification.getNumberOfConfidentPeptideHits(); i++) {
            // 1. Get peptidehit i that was requested by the arguments.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // 2.Check if there is an N-terminal acetylation.
            if (getAcetylationStatus(lPeptideHit)) {
                lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION;
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            if (lScore[i].score < 0) {
                iReport.addReport(AgentReport.RK_TABLEDATA, "N-term Ace");
            } else {
                iReport.addReport(AgentReport.RK_TABLEDATA, "NA");
            }

            // Attribute Relation File Format (Just put the Result (1 or 0) if the sequence has an NTerm Acetylation or not.
            iReport.addReport(AgentReport.RK_ARFF, lScore[i]);

            aPeptideIdentification.addAgentReport((i + 1), getUniqueID(), iReport);
        }
        return lScore;
    }

    /**
     * This Method Checks the N-terminal acetylation status of a PeptideHit, the purpose
     *
     * @param aPh - PeptideHit
     * @return boolean - true if <aPH> has an N-terminal acetylation.
     */
    private boolean getAcetylationStatus(PeptizerPeptideHit aPh) {

        boolean identifiedByMascot = aPh.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPh.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);

        if (identifiedByMascot) {
            PeptideHit aMPh = (PeptideHit) aPh.getOriginalPeptideHit(SearchEngineEnum.Mascot);
            // N-terminal residue is [0] in the Modification array.
            Modification lMod = aMPh.getModifications()[0];
            // If lMod exists, check if it is an acetylation.
            if (lMod != null) {
                if (lMod.getShortType().indexOf("Ac") >= 0) {
                    return true;
                }
            }
        } else if (identifiedByOMSSA) {
            OmssaPeptideHit anOPh = (OmssaPeptideHit) aPh;

            // id of this modification
            int id = 10;

            // inspect if this modification was found in the fixed modifications
            for (int i = 0; i < anOPh.getFixedModifications().size(); i++) {
                if (anOPh.getFixedModifications().get(i).intValue() == id) {
                    return true;
                }
            }

            // inspect if this modification was found in the variable modifications
            MSHits aPH = (MSHits) anOPh.getOriginalPeptideHit(SearchEngineEnum.OMSSA);
            for (int i = 0; i < aPH.MSHits_mods.MSModHit.size(); i++) {
                if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_modtype.MSMod == id) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects for acetylation (Ace) modification on the NTerminus. <b>Votes 'Negative_for_selection' if the NTerminus contains an Acetylation (Ace/Ace3D) as expected in COFRADIC </b>. 0 if there is no NTerminal Acetylation.</html>";
        return s;
    }
}