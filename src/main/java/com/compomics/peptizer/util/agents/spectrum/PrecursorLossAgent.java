package com.compomics.peptizer.util.agents.spectrum;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Apr 21, 2009
 * Time: 4:29:10 PM
 * <p/>
 * This class
 */
public class PrecursorLossAgent extends Agent {

    public static final String PRECURSOR_LOSS = "precursor_loss";

    public PrecursorLossAgent() {
        // Init the general Agent settings.
        initialize(PRECURSOR_LOSS);
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

        double lPrecursorLoss = Double.parseDouble((String) (this.iProperties.get(PRECURSOR_LOSS)));

        double intensity = match(aPeptideIdentification, lPrecursorLoss);

        // Make Agent Report!
        AgentVote lVote;
        iReport = new AgentReport(getUniqueID());

        double lIntensityRatio;
        String lTableData;
        String lARFFData;


        if (intensity != -1) {
            // If matched, store a relative intensity ratio for the precursor loss.
            lIntensityRatio = new BigDecimal(intensity / aPeptideIdentification.getSpectrum().getMaxIntensity()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            lTableData = "" + lIntensityRatio;
            lARFFData = "" + lIntensityRatio;
            lVote = AgentVote.POSITIVE_FOR_SELECTION;
        } else {
            lIntensityRatio = 0;
            lTableData = "" + lIntensityRatio;
            lARFFData = "" + lIntensityRatio;
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

    private double match(PeptideIdentification aPeptideIdentification, double lPrecursorLoss) {

        boolean identifiedByMascot = aPeptideIdentification.getAdvocate().getAdvocates().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPeptideIdentification.getAdvocate().getAdvocates().contains(SearchEngineEnum.OMSSA);

        if (identifiedByMascot) {
            double lPrecursor = aPeptideIdentification.getSpectrum().getPrecursorMZ();
            double lCharge = Double.parseDouble(aPeptideIdentification.getSpectrum().getChargeString().substring(0, 1));
            double lPrecursorWithLoss = lPrecursor - (lPrecursorLoss / lCharge);
            double lTolerance = Double.parseDouble(((Parameters) (aPeptideIdentification.getMetaData(MetaKey.Parameter_section))).getITOL());
            // Create a new 'in-silico' fragmention and try to match it in the spectrum.
            FragmentIon lFragmentIon = new FragmentIonImpl(lPrecursorWithLoss, lTolerance, FragmentIon.PRECURSOR_LOSS, 1, "Prec_ " + lPrecursorLoss + "_loss");

            if (lFragmentIon.isMatch(((Spectrum) aPeptideIdentification.getSpectrum().getOriginalSpectrum()).getPeakList(), lTolerance)) {
                return lFragmentIon.getIntensity();
            } else {
                return -1;
            }
        } else if (identifiedByOMSSA) {
            // ITOL in OMSSA ?
        }
        return -1;
    }

    /**
     * Returns a description for the Agent. Note that html tags are used to stress properties. Use in tooltips and
     * configuration settings. Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the DummyAgent.
     */
    public String getDescription() {
        return "<html>Inspects for the dummy property of the peptide. <b>Votes 'Positive_for_selection' if the dummy property is fullfilled ( " + this.iProperties.get(PRECURSOR_LOSS) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }
}
