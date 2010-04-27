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

    public static final String INTENSITY_THRESHOLD = "threshold";

    public RelativeIntensityAgent() {
        // Init the general Agent settings.
        initialize(INTENSITY_THRESHOLD);
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA, SearchEngineEnum.XTandem};
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
        double lThreshold = Double.parseDouble((String) (this.iProperties.get(INTENSITY_THRESHOLD)));

        AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        PeptizerSpectrum lSpectrum = aPeptideIdentification.getSpectrum();
        PeptizerPeak[] lPeaks = lSpectrum.getPeakList();
        double lMaxIntensity = lSpectrum.getMaxIntensity();

        double lTotalSpectrumIntensity = 0.0;
        for (int i = 0; i < lPeaks.length; i++) {
            PeptizerPeak lPeak = lPeaks[i];
            lTotalSpectrumIntensity = lTotalSpectrumIntensity + lPeak.getIntensity();
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

            Vector v = new Vector();
            boolean identifiedByMascot = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
            boolean identifiedByOMSSA = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);
            boolean identifiedByXTandem = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.XTandem);

            if (identifiedByMascot) {
                searchEngineUsed = SearchEngineEnum.Mascot;
                // We will analyze the mascot assigned peaks or the fused ones according to fuse. I guess this could be search engine independant.
                boolean fused = false;
                if (fused) {
                    for (int j = 0; j < lAnnotationType.size(); j++) {
                        if (lAnnotationType.get(j).getSearchEngine() == SearchEngineEnum.Mascot && lAnnotationType.get(j).getIndex() == 1) {
                            lPeptideHitAnnotation = lPeptideHit.getAllAnnotation(aPeptideIdentification, i);
                            v = (Vector) lPeptideHitAnnotation.get(lAnnotationType.get(j).getIndex() + "" + SearchEngineEnum.Mascot.getId() + "" + i);
                            break;
                        }
                    }
                } else {
                    for (int j = 0; j < lAnnotationType.size(); j++) {
                        if (lAnnotationType.get(j).getSearchEngine() == SearchEngineEnum.Mascot && lAnnotationType.get(j).getIndex() == 0) {
                            lPeptideHitAnnotation = lPeptideHit.getAllAnnotation(aPeptideIdentification, i);
                            v = (Vector) lPeptideHitAnnotation.get(lAnnotationType.get(j).getIndex() + "" + SearchEngineEnum.Mascot.getId() + "" + i);
                            break;
                        }
                    }
                }
            } else if (identifiedByOMSSA) {
                searchEngineUsed = SearchEngineEnum.OMSSA;
                // There is only one annotation type here.
                for (int j = 0; j < lAnnotationType.size(); j++) {
                    if (lAnnotationType.get(j).getSearchEngine() == SearchEngineEnum.OMSSA) {
                        lPeptideHitAnnotation = lPeptideHit.getAllAnnotation(aPeptideIdentification, i);
                        v = (Vector) lPeptideHitAnnotation.get(lAnnotationType.get(j).getIndex() + "" + SearchEngineEnum.OMSSA.getId() + "" + i);
                        break;
                    }
                }
            } else if (identifiedByXTandem) {
                searchEngineUsed = SearchEngineEnum.XTandem;
                // There is only one annotation type here.
                for (int j = 0; j < lAnnotationType.size(); j++) {
                    if (lAnnotationType.get(j).getSearchEngine() == SearchEngineEnum.XTandem) {
                        lPeptideHitAnnotation = lPeptideHit.getAllAnnotation(aPeptideIdentification, i);
                        v = (Vector) lPeptideHitAnnotation.get(lAnnotationType.get(j).getIndex() + "" + SearchEngineEnum.XTandem.getId() + "" + i);
                        break;
                    }
                }
            }

            DescriptiveStatistics lStatistics = new DescriptiveStatistics();

            if (v != null) {
                for (int j = 0; j < v.size(); j++) {
                    PeptizerFragmentIon lFragmentIon = (PeptizerFragmentIon) v.elementAt(j);
                    lStatistics.addValue(lFragmentIon.getIntensity() / lSpectrum.getMaxIntensity());
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
