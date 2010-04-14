package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.Masses;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.PeptideHitAnnotation;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 3-okt-2007
 * Time: 10:27:01
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class NtermCoverage extends Agent {

    /**
     * The b-ion coverage must be above the given percentage.
     */
    public static final String DISTANCE = "distance";


    public NtermCoverage() {
        // Init the general Agent settings.
        initialize(DISTANCE);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot};
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

        // Localize the Dummy property.
        int lDistanceParameter = Integer.parseInt((String) (this.iProperties.get(DISTANCE)));

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

            // If the peptide is 10 amino acids long, and the Nterm distance is set to 3,
            // then either b1,b2,b3 or y9,y8,y7 must be matched.
            boolean result;

            boolean[] boolMatchB;
            boolean[] boolMatchY;

            int lBcount = 0;
            int lYcount = 0;
            int lFirstMatch = 0;

            boolean identifiedByMascot = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocates().contains(SearchEngineEnum.Mascot);
            boolean identifiedByOMSSA = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocates().contains(SearchEngineEnum.OMSSA);

            if (identifiedByMascot) {
                PeptideHit aMPh = (PeptideHit) lPeptideHit.getOriginalPeptideHit(SearchEngineEnum.Mascot);
                PeptideHitAnnotation lPeptideHitAnnotation =
                        aMPh.getPeptideHitAnnotation((Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section));

                Spectrum lSpectrum = (Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum();
                double lErrorMargin = Double.parseDouble(((Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section)).getITOL());

                // set the b ion indices.
                boolMatchB = new boolean[lDistanceParameter];
                for (int j = 0; j < lDistanceParameter; j++) {
                    boolMatchB[j] = lPeptideHitAnnotation.getBions()[j].isMatch(lSpectrum.getPeakList(), lErrorMargin);
                    if (boolMatchB[j] == false) {
                        boolMatchB[j] = lPeptideHitAnnotation.getBDoubleions()[j].isMatchAboveIntensityThreshold(lSpectrum.getPeakList(), lSpectrum.getMaxIntensity(), 0.02, lErrorMargin);
                    }
                }

                int length = lPeptideHit.getSequence().length();
                // set the y ion indices.

                boolMatchY = new boolean[lDistanceParameter];
                for (int j = 0; j < lDistanceParameter; j++) {
                    int lYIndex = length - 2 - j;
                    boolMatchY[j] = lPeptideHitAnnotation.getYions()[lYIndex].isMatch(lSpectrum.getPeakList(), lErrorMargin);
                    // If no y-ion was found, attempt the double charged yion.
                    if (boolMatchY[j] == false) {
                        boolMatchY[j] = lPeptideHitAnnotation.getYDoubleions()[lYIndex].isMatchAboveIntensityThreshold(lSpectrum.getPeakList(), lSpectrum.getMaxIntensity(), 0.02, lErrorMargin);
                    }
                }
                for (int i1 = 0; i1 < boolMatchB.length; i1++) {
                    boolean b = boolMatchB[i1];
                    if (b) lBcount++;
                }

                for (int i1 = 0; i1 < boolMatchY.length; i1++) {
                    boolean b = boolMatchY[i1];
                    if (b) lYcount++;
                }

                lFirstMatch = 0;
                for (int j = 0; j < boolMatchB.length; j++) {
                    boolean b = boolMatchB[j];
                    boolean y = boolMatchY[j];
                    if (b | y) {
                        lFirstMatch = (j + 1);
                        break;
                    }
                }
            }

            if (lBcount == 0 & lYcount == 0) {
                lTableData = Integer.toString(0);
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lARFFData = "1";
            } else {
                int lSum = lBcount + lYcount;
                BigDecimal lResult = new BigDecimal((0.0 + lSum) / (lDistanceParameter * 2.0) * 100.0).setScale(1, RoundingMode.UP);
                lTableData = "" + lResult.toString() + "% - " + lFirstMatch;
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
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
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "NtermCoverage. Returns the number of fragmentions found proximal (defined by the distance parameter) to the N-terminus.";
    }
}