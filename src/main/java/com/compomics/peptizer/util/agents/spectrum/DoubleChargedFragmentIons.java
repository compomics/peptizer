package com.compomics.peptizer.util.agents.spectrum;

import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.Masses;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.PeptideHitAnnotation;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Jun 7, 2010
 * Time: 10:50:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleChargedFragmentIons  extends Agent {

    public static final String INTENSITY = "intensity";

    public DoubleChargedFragmentIons() {
        // Init the general Agent settings.
        initialize(INTENSITY);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * INSPECTION ---------- The inspection is the core of an Agent since this logic leads to the Agent's vote. This
     * method returns an array of AgentVote objects, reflecting this Agent's idea whether to select or not to select the
     * peptide hypothesis. All Agent Implementations must also create and store AgentReport for each peptide
     * hypothesis.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] as a vote upon inspection for each the confident peptide hypothesises. <ul> <li>AgentVotes[0]
     *         gives the inspection result on PeptideHit 1</li> <li>AgentVotes[1] gives the inspection result on
     *         PeptideHit 2</li> <li>AgentVotes[n] gives the inspection result on PeptideHit n+1</li> </ul> Where the
     *         different AgentVotes can be: <ul> <li>a vote approving the selection of the peptide hypothesis.</li>
     *         <li>a vote indifferent to the selection.</li> <li>a vote objecting to select the peptide hypothesis.</li>
     *         </ul>
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {
        // Localize the Dummy property.

        AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        double lIntensityThreshold = Double.parseDouble((String) (this.iProperties.get(INTENSITY)));

        double[] lInspectionResults = findMaxDoubleChargedIon(aPeptideIdentification);
        double lRelativeDoubleChargedIntensity = lInspectionResults[0];
        double lMatchDoubleChargeCounter = lInspectionResults[1];
        double lRelatviveSummedDoubleChargedIntensity = lInspectionResults[2];

        // Make Agent Report!
        AgentVote lVote;
        iReport = new AgentReport(getUniqueID());

        String lTableData;
        String lARFFData;


        String lReport = "" + lRelativeDoubleChargedIntensity + "\t" + lMatchDoubleChargeCounter + "\t" + lRelatviveSummedDoubleChargedIntensity;
        if (lRelativeDoubleChargedIntensity < lIntensityThreshold) {
            // If matched, store a relative lPrecursorLossRelativeIntensity ratio for the precursor loss.
            lTableData = lReport;
            lARFFData = lReport;
            lVote = AgentVote.POSITIVE_FOR_SELECTION;
        } else {
            lTableData = lReport;
            lARFFData = lReport;
            lVote = AgentVote.NEGATIVE_FOR_SELECTION;
        }

        for (int i = 0; i < lAgentVotes.length; i++) {
            lAgentVotes[i] = lVote;
            iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[i]);
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);
            iReport.addReport(AgentReport.RK_ARFF, lARFFData);
            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lAgentVotes;

    }

    /**
     * Describe the double charged ions in this peptide identification.
     * @param aPeptideIdentification
     * @return double[] with [0]the max relative intensity, [1] the number of matched double charged ions, [2] the relative summed intensity.
     */
    private double[] findMaxDoubleChargedIon(PeptideIdentification aPeptideIdentification) {

        boolean identifiedByMascot = aPeptideIdentification.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPeptideIdentification.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);


        if (identifiedByMascot) {
            // Get the tolerance used in the Mascot Searches.
            double lTolerance = Double.parseDouble(((Parameters) (aPeptideIdentification.getMetaData(MetaKey.Parameter_section))).getITOL());
            // Get the Spectrum of the identifications.
            Spectrum lSpectrum = (Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum();
            // Get the peptidehit and create an array with double charged b and y ions.
            PeptideHit peptidehit = (PeptideHit) aPeptideIdentification.getBestPeptideHit().getOriginalPeptideHit(SearchEngineEnum.Mascot);
            PeptideHitAnnotation lPeptideHitAnnotation = peptidehit.getPeptideHitAnnotation((Masses) (aPeptideIdentification.getMetaData(MetaKey.Masses_section)), (Parameters) (aPeptideIdentification.getMetaData(MetaKey.Parameter_section)));
            ArrayList lDoubleChargedIons = new ArrayList();
            lDoubleChargedIons.addAll(Arrays.asList(lPeptideHitAnnotation.getBDoubleions()));
            lDoubleChargedIons.addAll(Arrays.asList(lPeptideHitAnnotation.getYDoubleions()));

            // Track the most intense match.
            double lMaxDoubleChargedIntensity = 0.0;
            double lSummedIntensityOfDoubleChargedIons = 0.0;
            double lMatchDoubleChargeCounter = 0.0;

            for (Iterator iterator = lDoubleChargedIons.iterator(); iterator.hasNext();) {
                FragmentIonImpl fragmentIon = (FragmentIonImpl) iterator.next();
                if (fragmentIon.isMatch(lSpectrum.getPeakList(), lTolerance)) {
                    double lIntensity = fragmentIon.getIntensity();
                    lMatchDoubleChargeCounter++;
                    lSummedIntensityOfDoubleChargedIons = lSummedIntensityOfDoubleChargedIons + lIntensity;
                    if(lIntensity > lMaxDoubleChargedIntensity){
                        lMaxDoubleChargedIntensity = lIntensity;
                    }
                }
            }

            // Get the highest intensity in the spectrum.
            double lMaxSpectrumIntensity = lSpectrum.getMaxIntensity();
            double lRelativeDoubleChargedIntensity = (lMaxDoubleChargedIntensity/lMaxSpectrumIntensity*100)/100; // round to 2 digits.
            double lRelatviveSummedDoubleChargedIntensity = (lSummedIntensityOfDoubleChargedIons/lMaxSpectrumIntensity*100)/100;


            // Return the relative intensity of the most intenste double charged fragment ion in the MS/MS spectrum.
            return new double[]{lRelativeDoubleChargedIntensity, lMatchDoubleChargeCounter, lRelatviveSummedDoubleChargedIntensity};


        } else if (identifiedByOMSSA) {
            // no implementation.
        }
        return null;
    }

    /**
     * Returns a description for the Agent. Note that html tags are used to stress properties. Use in tooltips and
     * configuration settings. Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the DummyAgent.
     */
    public String getDescription() {
        return "<html>Inspects for double charged fragmentionds. <b>Votes 'Positive_for_selection' if the maximum relative intensity of a double charged fragement ions is less then( " + this.iProperties.get(INTENSITY) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }
}
