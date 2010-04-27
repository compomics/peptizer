package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-jun-2007
 * Time: 11:39:38
 */

/**
 * Class description: ------------------ This agent was developed to inspect on the difference between Mascot's ionscore
 * and threshold.
 */
public class DeltaScore extends Agent {


    /**
     * The alpha value to estimate the confidence.
     */
    private double iAlpha = -1;

    /**
     * The delta value above threshold.
     */
    public static final String DELTA = "delta";

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public DeltaScore() {
        // Init the general Agent settings.
        initialize(DELTA);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot}; // There is no ion score for Omssa
        compatibleSearchEngine = searchEngines;
    }


    /**
     * {@inheritDoc} This Agent inspects the delta score property of the PeptideIdentifcation. If the PeptideHit scores
     * less then given iDelta units above peptizer confidence threshold, inspection will return 1. If the PeptideHit
     * scores more then given iDelta units above peptizer confidence threshold, inspection will return 0.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        String alphaString = MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA");
        setAlpha(Double.parseDouble(alphaString));

        double lDelta = Double.parseDouble((String) this.iProperties.get(DELTA));

        // The resulting Inspection score.
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            boolean identifiedByMascot = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);

            if (identifiedByMascot) {
                // 1. Get the nth confident PeptideHit.
                PeptideHit lPeptideHit = (PeptideHit) aPeptideIdentification.getPeptideHit(i).getOriginalPeptideHit(SearchEngineEnum.Mascot);
                //2. Process delta score.
                double lDeltaScore = lPeptideHit.getIonsScore() - lPeptideHit.calculateIdentityThreshold(iAlpha);
                // Assign scores!
                if (lDeltaScore <= lDelta) {
                    // If delta score is less then given iDelta, Agent
                    lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                } else {
                    lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                }

                // Build the report!
                // Agent Result.
                iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

                // TableRow information.
                iReport.addReport(AgentReport.RK_TABLEDATA, (new BigDecimal(lDeltaScore)).setScale(2, BigDecimal.ROUND_HALF_DOWN));

                // Attribute Relation File Format
                iReport.addReport(AgentReport.RK_ARFF, lDeltaScore);

            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                // Agent Result.
                iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

                // TableRow information.
                iReport.addReport(AgentReport.RK_TABLEDATA, (new BigDecimal(0)).setScale(2, BigDecimal.ROUND_HALF_DOWN));

                // Attribute Relation File Format
                iReport.addReport(AgentReport.RK_ARFF, 0.0);
            }


            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;
    }

    /**
     * Sets the alpha value to estimate the confidence.
     *
     * @param aAlpha Alpha value to
     */
    public void setAlpha(double aAlpha) {
        iAlpha = aAlpha;
    }

    /**
     * Sets the delta value above threshold
     */
    public void setDelta(double aDelta) {
        this.iProperties.put(DELTA, aDelta);
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects for score units above threshold. <b>Votes 'Positive_for_selection' if identity score minus confidence threshold is less then delta score (" + this.iProperties.get(DELTA) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
        return s;
    }
}
