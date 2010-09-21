package com.compomics.peptizer.util.agents.spectrum;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerFragmentIon;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Apr 20, 2009
 * Time: 5:04:59 PM
 * <p/>
 * This Agent inspects the average relative intensity of the spectrum.
 * If low, this means the spectrum has not fragmented very well.
 */
public class RelativeIntensityAgent extends Agent {
	// Class specific log4j logger for RelativeIntensityAgent instances.
	 private static Logger logger = Logger.getLogger(RelativeIntensityAgent.class);

    public static final String INTENSITY_THRESHOLD = "threshold";

    public RelativeIntensityAgent() {
        // Init the general Agent settings.
        initialize(INTENSITY_THRESHOLD);
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
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {
        AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        // Localize the Dummy property.
        double lThreshold = Double.parseDouble((String) (this.iProperties.get(INTENSITY_THRESHOLD)));


        PeptizerSpectrum lSpectrum = aPeptideIdentification.getSpectrum();
        PeptizerPeak[] lPeaks = lSpectrum.getPeakList();
        double lMaxIntensity = lSpectrum.getMaxIntensity();

        double lTotalSpectrumIntensity = 0.0;
        for (int i = 0; i < lPeaks.length; i++) {
            PeptizerPeak lPeak = lPeaks[i];
            lTotalSpectrumIntensity += lPeak.getIntensity();
        }

        for (int i = 0; i < lAgentVotes.length; i++) {


            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            SearchEngineEnum searchEngineUsed = SearchEngineEnum.Mascot;
            String lTableData;
            String lARFFData;
            lTableData = "";
            lARFFData = "";


            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            HashMap lPeptideHitAnnotation;
            ArrayList<AnnotationType> lAnnotationType = lPeptideHit.getAnnotationType();

            HashMap<String, Vector<PeptizerFragmentIon>> annotationMap = lPeptideHit.getAllAnnotation(aPeptideIdentification, 0);
            String key = 0 + "" + lPeptideHit.getAdvocate().getAdvocatesList().get(0).getId() + "" + 1 + "";
            Vector v = annotationMap.get(key);
            double intMax = lSpectrum.getMaxIntensity();

            DescriptiveStatistics lStatistics = new DescriptiveStatistics();

            if (v != null) {
                for (int j = 0; j < v.size(); j++) {
                    PeptizerFragmentIon lFragmentIon = (PeptizerFragmentIon) v.elementAt(j);
                    lStatistics.addValue(lFragmentIon.getIntensity() / intMax);
                }
            }

            // double lIntensityRatio =
            //        new BigDecimal(lIdentifiedIonIntensity / lTotalSpectrumIntensity).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            double lRelativeIntensityMean = lStatistics.getMean();
            double lRelativeIntensitySD = lStatistics.getStandardDeviation();


            lTableData = lRelativeIntensityMean + "-" + lRelativeIntensitySD + "(" + searchEngineUsed.getInitial() + ")";
            lARFFData = lTableData;

            if (lRelativeIntensityMean <= lThreshold) {
                lAgentVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else {
                lAgentVotes[i] = AgentVote.NEGATIVE_FOR_SELECTION;
            }

            iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lARFFData);

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
        return "<html>Inspects the fragmentation spectrum for the relative intensity of the identified fragmentions is above a given threshold. ( " + this.iProperties.get(INTENSITY_THRESHOLD) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }


}
