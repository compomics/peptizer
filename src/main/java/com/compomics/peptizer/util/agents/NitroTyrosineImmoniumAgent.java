package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Ion;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-sep-2007
 * Time: 15:30:28
 */

/**
 * Class description: --------
 * This class was developed to inspect for mass tolerance error (Da) (experimental vs theory).
 */
public class NitroTyrosineImmoniumAgent extends Agent {

    /**
     * Identifies the allowed mass tolerance.
     */
    public static final String TOLERANCE = "tolerance";

    /**
     * These immomiun ions are to be expected from nitrotyrosin peptides.
     * (Sarver et al.
     * Analysis of peptides and proteins containing nitrotyrosine by matrix-assisted laser desorption/ionization mass spectrometry.
     * Journal of the American Society for Mass Spectrometry (2001) vol. 12 (4) pp. 439-48)
     */
    public static Ion[] IONS = new Ion[]{
            new Ion(181.06, IonTypeEnum.immonium),
            new Ion(165.60, IonTypeEnum.immonium),
            new Ion(151.4, IonTypeEnum.immonium),
            new Ion(149.3, IonTypeEnum.immonium)
    };


    public NitroTyrosineImmoniumAgent() {
        // Init the general Agent settings.
        initialize(new String[]{TOLERANCE});
        SearchEngineEnum[] searchEngines = {};
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
     *         <p/>
     *         Votes positive for selection if none of the immonium ions where seen.
     *         Votes negative for selection if else.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        double lTolerance = Double.parseDouble((String) (this.iProperties.get(TOLERANCE)));

        AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        PeptizerSpectrum lSpectrum = aPeptideIdentification.getSpectrum();
        boolean ionMatch = false;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < IONS.length; i++) {
            Ion lFragmentIon = IONS[i];
            if (lFragmentIon.isMatch(lSpectrum.getPeakList(), lTolerance)) {
                ionMatch = true;
                sb.append(lFragmentIon.getType() + "-");
            }
        }

        String lTableComment = sb.toString();
        if (lTableComment.length() > 0) {
            // Remove the trailing '-' character.
            lTableComment = lTableComment.substring(0, lTableComment.length() - 1);
        }


        for (int i = 0; i < lAgentVotes.length; i++) {
            int lArffComment;

            if (ionMatch == true) {
                lAgentVotes[i] = AgentVote.NEGATIVE_FOR_SELECTION;
                lArffComment = 0;
            } else {
                lAgentVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lArffComment = 1;
            }

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // Build the report!
            // Agent Result.

            iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableComment);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lArffComment);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lAgentVotes;

    }

    /**
     * Returns a description for the Agent. Note that html tags are used to stress properties. Use in tooltips and
     * configuration settings. Fill in an agent description. Report on purpose and a minor on actual implementation.
     *
     * @return String description of the DummyAgent.
     */
    public String getDescription() {
        return "<html>Inspects for the mass error (Da) of the peptide. <b>Votes 'Positive_for_selection' if the mass error is greater then the allowed tolerance ( " + this.iProperties.get(TOLERANCE) + ")</b>. Votes 'Neutral_for_selection' if less.</html>";
    }
}