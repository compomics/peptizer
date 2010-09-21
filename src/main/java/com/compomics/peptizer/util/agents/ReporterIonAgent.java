package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 18-jun-2008
 * Time: 15:30:28
 */

/**
 * Class description: ------------------ This class was developed to inspect for deviating reporter ion intensities.
 */
public class ReporterIonAgent extends Agent {
	// Class specific log4j logger for ReporterIonAgent instances.
	 private static Logger logger = Logger.getLogger(ReporterIonAgent.class);

    /**
     * PARAMETERS ---------- String identifiers for the parameters in the agent.xml configuration file.
     */

    // Mass over charge parameter for the first reporter ion.
    public static final String MASS_1 = "reporter_mz_1";

    // Mass over charge parameter for the second reporter ion.
    public static final String MASS_2 = "reporter_mz_2";

    // Fold ratio parameter between the two reporter ions you consider as deviating.
    public static final String RATIO = "ratio";

    // Error tolerance for matching the expected reporter
    // ion mass over charge values to a fragmention in the MS/MS spectrum.
    public static final String ERROR = "error";

    /**
     * CONSTRUCTOR ----------- Construct a new instance of the ReporterIonAgent.
     */
    public ReporterIonAgent() {
        // a. Sets general properties shared among all Agents.
        initialize(new String[]{MASS_1, MASS_2, RATIO, ERROR});
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * INSPECTION ---------- The inspection is the core of an Agent since this logic leads to the Agent's vote. This
     * method returns an array of AgentVote objects, reflecting this Agent's idea whether to select or not to select the
     * peptide hypothesis. All Agent Implementations must also create and store AgentReport for each peptide
     * hypothesis.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] as a vote upon inspection for each the confident peptide hypothesises. AgentVotes[0] gives
     *         the inspection result on PeptideHit 1 AgentVotes[1] gives the inspection result on PeptideHit 2
     *         AgentVotes[n] gives the inspection result on PeptideHit n+1 Where the different AgentVotes can be: a vote
     *         approving the selection of the peptide hypothesis. a vote indifferent to the selection. a vote objecting
     *         to select the peptide hypothesis.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // A. PREPARING THE VARIABLES
        //***************************

        // 1. The reporter ion masses.
        double lReporterMass_1 = Double.parseDouble((String) (this.iProperties.get(MASS_1)));
        double lReporterMass_2 = Double.parseDouble((String) (this.iProperties.get(MASS_2)));

        // 2. The fold ratio threshold.
        double lRatio = Double.parseDouble((String) (this.iProperties.get(RATIO)));

        // 3. The error tolerance.
        double lError = Double.parseDouble((String) (this.iProperties.get(ERROR)));

        // 4. Reserves an array with AgentVotes for each confident peptide hypothesis.
        AgentVote[] lAgentVotes =
                new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        // Since this inspection is dependent on the MS/MS spectrum,
        // it will result in the same vote for each peptide hypothesis.
        // Therefore, a single inspection is reused for each peptide hypothesis.

        // 5. Initiate an AgentReport serving as a report for this inspection.
        iReport = new AgentReport(getUniqueID());

        // 6. Local variable to store the result shown in the information table.
        String lResultForTable = "";
        // 7. Local variable to store the result written in the arff file.
        String lResultForArff = "";

        // 8. Local variables for matching reporter ion 1 in the MS/MS spectrum.
        boolean lReporter_1_match = false;
        double lReporter_1_intensity = 0;

        // 9. Local variables for matching reporter ion 2 in the MS/MS spectrum.
        boolean lReporter_2_match = false;
        double lReporter_2_intensity = 0;

        // B. THE ACTUAL INSPECTION
        //***************************************

        // 1. Gets the MS/MS spectrum from the PeptideIdentification object
        // that was given as a parameter to the inspect() method.
        PeptizerSpectrum lSpectrum = aPeptideIdentification.getSpectrum();

        // 2. Gets the peaklist from this MS/MS spectrum.
        PeptizerPeak[] lPeaks = lSpectrum.getPeakList();

        // 3. Iterates over all peaks through a for loop.
        for (int i = 0; i < lPeaks.length; i++) {
            PeptizerPeak lPeak = lPeaks[i];
            double lDelta_1 = lPeak.getMZ() - lReporterMass_1;
            double lDelta_2 = lPeak.getMZ() - lReporterMass_2;

            // 3i) If absolute value of the mass diference of this fragmention and the expected mass
            // of reporter ion 1 is less then the defined error tolerance.
            // Then there is a match!
            if (Math.abs(lDelta_1) < lError) {
                lReporter_1_match = true;
                lReporter_1_intensity = lPeak.getIntensity();
            }

            // 3ii) Same idea for reporter ion 2.
            if (Math.abs(lDelta_2) < lError) {
                lReporter_2_match = true;
                lReporter_2_intensity = lPeak.getIntensity();
            }

            // 3iii) For performance reasons: if both peaks were matched then the for loop can be exited.
            if (lReporter_1_match && lReporter_2_match) {
                break;
            }
        }

        // 4. Checks the intensity ratio.
        double lUpperBoundary;
        double lLowerBoundary;

        // First an upper and a lower boundary must be defined for the Reporter Ions intensity ratio.
        // If the experimental ratio between Reporter Ion 1 and Reporter Ion 2 is
        //  more then 1.5 or less then 0.66, then the two samples deviate by a factor of 1.5.
        //
        // If the user defined the factor as larger then 1, the upper boundary for an
        // deviating ratio is given by 1 diveded by that factor.
        // The lower boundary for an deviating ratio is given by 1 multiplied by that factor.
        // Example:
        // If (Ratio=1.5)
        // Then lower boundary = 1.5 and upper boundary = 0.66
        if (lRatio > 1) {
            lUpperBoundary = 1 * lRatio;
            lLowerBoundary = 1 / lRatio;
        } else {
            // If the user defined the facotr as smaller then 1, then it is the other way round.
            lUpperBoundary = 1 / lRatio;
            lLowerBoundary = 1 * lRatio;
        }

        // 5. Local variable for the intesity ratio between Reporter Ion 1 and Reporter Ion 2.
        double lExperimentalRatio;
        // Local boolean for the upcomming function to store whether the ratio between the Reporter Ions
        // deviates more then the expected ratio.
        boolean lDeviatingRatio;

        // 6. Checks the intensities of the reporter ions!

        // 6i) If this condition is true, then one of the reporter ions was not found!
        // These are not selected as these are probably unlabeled peptides.
        if (!lReporter_1_match || !lReporter_2_match) {
            lDeviatingRatio = false;
            lExperimentalRatio = 0;
            // 6ii) Else both the reporter ions were found. Lets inspect their ratio.
        } else {
            lExperimentalRatio = lReporter_1_intensity / lReporter_2_intensity;

            // The ExperimentalRatio between reporter ion 1 an reporter ion 2
            // is either less then the lower boundary,
            // or either more then the upper boundar.
            // In both cases, the reporter ion intesity is deviating for both samples:
            if (lLowerBoundary > lExperimentalRatio || lUpperBoundary < lExperimentalRatio) {
                // A. The Agent inspection resulted in deviating reporter ion intensities as
                // their ratio was outside one of the boundaries.
                lDeviatingRatio = true;
            } else {
                // B. Else the Agent inspection resulted in non deviating reporter ion intensities as
                // their ratio was within the lower and upper boundary.
                lDeviatingRatio = false;
            }
        }

        // C. MAKING THE INSPECTION REPORTS AND COMMITTING THE VOTES
        //**********************************************************

        // 1. In all cases, store the experimental ratio between the
        // two reporter ions as a value to display in the information table.

        // A BigDecimal rounds a double at 2 decimals.
        BigDecimal lRoundedExperimentalRatio = null;
        lRoundedExperimentalRatio = new BigDecimal(lExperimentalRatio).setScale(2, BigDecimal.ROUND_HALF_UP);
        lResultForTable = lRoundedExperimentalRatio.toString();

        // 2i. Deviating reporter ion intesity ratio, this Agent suggests to select the peptide hypothesis!
        if (lDeviatingRatio) {
            lResultForArff = "1";
            for (int i = 0; i < lAgentVotes.length; i++) {
                lAgentVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
            }
            // 2ii. Non Deviating reporter ion intensity ratio, this Agent is neutral to select the peptide hypothesis!
        } else {
            lResultForArff = "0";
            for (int i = 0; i < lAgentVotes.length; i++) {
                lAgentVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }
        }

        // 3. Creates an Agentreport for this inspection.
        iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[0]);
        iReport.addReport(AgentReport.RK_TABLEDATA, lResultForTable);
        iReport.addReport(AgentReport.RK_ARFF, lResultForArff);

        // 4. Stores the report on the PeptideIdentification object.
        for (int i = 0; i < lAgentVotes.length; i++) {
            aPeptideIdentification.addAgentReport((i + 1), this.getUniqueID(), iReport);
        }

        // 5. Returns the AgentVotes in the end of the inspection.
        return lAgentVotes;
    }

    /**
     * Returns a description for the Agent. Note that html tags are used to stress properties. Use in tooltips and
     * configuration settings. Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the ReporterIonAgent.
     */
    public String getDescription() {
        return "<html>Inspects for the deviating reporter ion intensities." +
                "<b>Votes 'Positive_for_selection' when two reporter ions ( " + this.iProperties.get(MASS_1) +
                " , " + this.iProperties.get(MASS_2) + ") have a more then " + this.iProperties.get(RATIO) +
                " fold intesity ratio.";
    }
}
