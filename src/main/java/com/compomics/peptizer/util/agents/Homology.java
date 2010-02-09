package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 5-nov-2007
 * Time: 11:44:33
 */

/**
 * Class description: ------------------ This class was developed to use information
 */
public class Homology extends Agent {

    /**
     * The alpha value to estimate the confidence.
     */
    private double iAlpha = -1;

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public Homology() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot };
        compatibleSearchEngine = searchEngines;
    }


    /**
     * {@inheritDoc} This Agent inspects the delta score property of the PeptideIdentifcation. If the PeptideHit scores
     * less then given iDelta units above peptizer confidence threshold, inspection will return 1. If the PeptideHit
     * scores more then given iDelta units above peptizer confidence threshold, inspection will return 0.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        String alphaString = MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA");
        setAlpha(Double.parseDouble(alphaString));

        // The resulting Inspection score.
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // 1. Get the nth confident PeptideHit.
            PeptideHit lPeptideHit = (PeptideHit) aPeptideIdentification.getPeptideHit(i).getOriginalPeptideHit();

            //2. Process homology params.
            boolean isHomologyHigherThenIdentityThreshold = false;
            boolean isHomologyHigherThenIonScore = false;

            // 2.a) Check if the HomologyThreshold is bigger then the IdentityThreshold?
            if (lPeptideHit.getHomologyThreshold() >= lPeptideHit.calculateIdentityThreshold(iAlpha)) {
                isHomologyHigherThenIdentityThreshold = true;
            }

            // 2.b) Check if the HomologyThreshold is bigger the peptidehit's ionscore?
            if (lPeptideHit.getHomologyThreshold() >= lPeptideHit.getIonsScore()) {
                isHomologyHigherThenIonScore = true;
            }

            String lTableData;
            String lARFFData;

            // Assign scores!
            if (isHomologyHigherThenIdentityThreshold && isHomologyHigherThenIonScore) {
                // If delta score is less then given iDelta, Agent
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "homology";
                lARFFData = "1";
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = "0";
            }

            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, Integer.parseInt(lARFFData));

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
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects for homology threshold features. <b>Votes 'Positive_for_selection' if both Identity Threshold AND Ionscore are below Homology. </b>. Votes 'Neutral_for_selection' if else.</html>";
        return s;
    }
}
