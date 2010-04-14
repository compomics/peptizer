package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.interfaces.Modification;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSHits;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Kenni
 * Date: 2-jun-2006
 * Time: 11:28:13
 */

/**
 * This class is an extension of the Agent abstract class.
 */
public class Deamidation extends Agent {

    public static final String COUNT = "count";

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public Deamidation() {
        // Init the general Agent settings.
        initialize(COUNT);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * {@inheritDoc} This Agent inspects the Deamidation property of a PeptideIdentification.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // This inspect score will stay '0' if there are no deamidations found in
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];


        for (int i = 0; i < lScore.length; i++) {

            // 1. Get the peptidehit that was requested by the arguments.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // 2. Get the number of deamidations.
            int lNumberOfDeamidations = 0;
            boolean identifiedByMascot = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocates().contains(SearchEngineEnum.Mascot);
            boolean identifiedByOMSSA = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocates().contains(SearchEngineEnum.OMSSA);

            if (identifiedByMascot) {
                lNumberOfDeamidations = getNumberOfDeamidations((MascotPeptideHit) lPeptideHit);
            } else if (identifiedByOMSSA) {
                lNumberOfDeamidations = getNumberOfDeamidations((OmssaPeptideHit) lPeptideHit);
            }

            // 3. Parse the number of deamidations into a inspect score,
            lScore[i] = parseNumberOfDeamidations(lNumberOfDeamidations);

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            // Result
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);
            // TableData
            if (lNumberOfDeamidations > 0) {
                if (lNumberOfDeamidations == 1) {
                    iReport.addReport(AgentReport.RK_TABLEDATA, lNumberOfDeamidations + " dam");
                } else {
                    iReport.addReport(AgentReport.RK_TABLEDATA, lNumberOfDeamidations + " dam's");
                }
            } else {
                iReport.addReport(AgentReport.RK_TABLEDATA, "NA");
            }

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lNumberOfDeamidations);

            aPeptideIdentification.addAgentReport((i + 1), getUniqueID(), iReport);
        }

        return lScore;
    }

    /**
     * This method calculates the number of deamidations in the sequence of a Mascot peptidehit.
     *
     * @param aMPH PeptideHit to be inspected.
     * @return int Number of deamidations.
     */
    private int getNumberOfDeamidations(MascotPeptideHit aMPH) {
        int lCount = 0;
        PeptideHit aPH = (PeptideHit) aMPH.getOriginalPeptideHit();
        // Check first if the PeptideHit has any modifications!
        if (aPH.getModifications() != null) {
            for (int i = 0; i < aPH.getModifications().length; i++) {
                // Check out each 'not null' modification in the array for deamidations, if there is one, raise the counter.
                Modification lModification = aPH.getModifications()[i];
                if (lModification != null) {
                    if (lModification.getShortType().equals("Dam")) {
                        // Deamidations do occur when followed by Glycine.
                        if (aPH.getSequence().charAt(i) != 'G') {
                            lCount = lCount + 1;
                        }
                    }
                }
            }
        }

        return lCount;
    }

    /**
     * This method calculates the number of deamidations in the sequence of an Omssa peptidehit.
     *
     * @param anOPH PeptideHit to be inspected.
     * @return int Number of deamidations.
     */
    private int getNumberOfDeamidations(OmssaPeptideHit anOPH) {
        int lCount = 0;
        // Get the id of deamination
        int id = -1;
        Vector<String> modResidues = new Vector();
        for (int i = 0; i < anOPH.modifs.size(); i++) {
            if (anOPH.modifs.get(i).getModName().compareTo("deamidation of N and Q") == 0) {
                id = anOPH.modifs.get(i).getModType().intValue();
                modResidues = anOPH.modifs.get(i).getModResidues();
                break;
            }
        }

        // inspect fixed modifications
        String[] decomposedSequence = anOPH.decomposeSequence(anOPH.getSequence());
        for (int i = 0; i < anOPH.getFixedModifications().size(); i++) {
            if (anOPH.getFixedModifications().get(i).intValue() == id) {
                for (int j = 0; j < decomposedSequence.length - 1; j++) {
                    for (int k = 0; k < modResidues.size(); k++) {
                        // if we have the concerned residue not followed by a G then increase
                        if (decomposedSequence[j].compareTo(modResidues.get(k)) == 0 && decomposedSequence[j + 1].compareTo("G") != 0) {
                            lCount++;
                        }
                    }
                }
            }
        }

        // inspect variable modifications
        MSHits aPH = (MSHits) anOPH.getOriginalPeptideHit(SearchEngineEnum.OMSSA);
        for (int i = 0; i < aPH.MSHits_mods.MSModHit.size(); i++) {
            // if we have the concerned modification not followed by a G then increase
            if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_modtype.MSMod == id) {
                if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_site < decomposedSequence.length) {
                    if (decomposedSequence[aPH.MSHits_mods.MSModHit.get(i).MSModHit_site + 1].compareTo("G") != 0) {
                        lCount++;
                    }
                }
            }
        }

        return lCount;
    }

    /**
     * This method parses the inspect score based on the number of deamidations of the PeptideHit.
     *
     * @param aNumberOfDeamidations The number of deamidations.
     * @return int     inspect score
     */
    private AgentVote parseNumberOfDeamidations(int aNumberOfDeamidations) {
        AgentVote result;
        int count = Integer.parseInt((String) iProperties.get(COUNT));
        // If there are two or more deamidations, inspect score will be '+1'.
        if (aNumberOfDeamidations >= count) {
            result = AgentVote.POSITIVE_FOR_SELECTION;
        } else {
            result = AgentVote.NEUTRAL_FOR_SELECTION;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "<html>Inspects for deamidation modifications that are not folowed by a Glycine amino acid. <b>Votes 'Positive_for_selection' if " + COUNT + " or more deamidations.</b> 0 if else.</html>";
    }
}