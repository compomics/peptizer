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
 * User: Kenni
 * Date: 2-jun-2006
 * Time: 11:28:13
 */

/**
 * This class is an extension of the Agent abstract class.
 */
public class Deamidation extends Agent {
	// Class specific log4j logger for Deamidation instances.
	 private static Logger logger = Logger.getLogger(Deamidation.class);

    public static final String COUNT = "count";

    private final ArrayList<String> prideAccessions = new ArrayList<String>(Arrays.asList("MOD:00137", "MOD:00219", "MOD:00400", "MOD:00565", "MOD:00657", "MOD:00791", "MOD:01293", "MOD:01294", "MOD:01336", "MOD:01337", "MOD:01369", "MOD:01371"));

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public Deamidation() {
        // Init the general Agent settings.
        initialize(COUNT);
        compatibleSearchEngine = new SearchEngineEnum[]{};
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
            if (lPeptideHit instanceof MascotPeptideHit) {
                for (PeptizerModification mod : lPeptideHit.getModifications()) {
                    if (mod.getModificationSite() > 0 && mod.getModificationSite() < lPeptideHit.getSequence().length()) {
                        if (mod.getName().toLowerCase().contains("dam") && lPeptideHit.getSequence().charAt(mod.getModificationSite()) == 'G') {
                            lNumberOfDeamidations++;
                        }
                    }
                }
            } else if (lPeptideHit instanceof OmssaPeptideHit) {
                for (PeptizerModification mod : lPeptideHit.getModifications()) {
                    if (mod.getName().equals("deamidation of N and Q")) {
                        lNumberOfDeamidations++;
                    }
                }
            } else if (lPeptideHit instanceof PridePeptideHit) {
                for (PeptizerModification mod : lPeptideHit.getModifications()) {
                    if (prideAccessions.contains(mod.getPrideAccession())) {
                        lNumberOfDeamidations++;
                    }
                }
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
