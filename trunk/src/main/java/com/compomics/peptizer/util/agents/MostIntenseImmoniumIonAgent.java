package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Ion;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 23-jul-2008 Time: 17:31:40 To change this template use File | Settings |
 * File Templates.
 */
public class MostIntenseImmoniumIonAgent extends Agent {
	// Class specific log4j logger for MostIntenseImmoniumIonAgent instances.
	 private static Logger logger = Logger.getLogger(MostIntenseImmoniumIonAgent.class);

    public static final String AMINOACIDS = "aminoacids";
    public static final String ERROR = "error";
    private HashMap<Character, Ion> iImmoniumIons;


    public MostIntenseImmoniumIonAgent() {
        // Init the general Agent settings.
        initialize(new String[]{AMINOACIDS, ERROR});
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;

        // Build a Map with immonium ion masses.
        Double lError = Double.parseDouble(iProperties.getProperty(ERROR));

        iImmoniumIons = new HashMap<Character, Ion>();

        iImmoniumIons.put('H', new Ion(110, IonTypeEnum.immoniumH));
        iImmoniumIons.put('F', new Ion(120, IonTypeEnum.immoniumF));
        iImmoniumIons.put('Y', new Ion(136, IonTypeEnum.immoniumY));
        iImmoniumIons.put('W', new Ion(159, IonTypeEnum.immoniumW));

        iImmoniumIons.put('L', new Ion(86, IonTypeEnum.immoniumL));
        iImmoniumIons.put('I', new Ion(86, IonTypeEnum.immoniumI));
        iImmoniumIons.put('P', new Ion(70, IonTypeEnum.immoniumP));
        /*
        iImmoniumIons.put('M', new Ion(104, IonTypeEnum.immoniumM));
        iImmoniumIons.put('A', new Ion(44, IonTypeEnum.immoniumA));
        iImmoniumIons.put('R', new Ion(129, IonTypeEnum.immoniumR));
        iImmoniumIons.put('N', new Ion(87, IonTypeEnum.immoniumN));
        iImmoniumIons.put('D', new Ion(88, IonTypeEnum.immoniumD));
        iImmoniumIons.put('C', new Ion(76, IonTypeEnum.immoniumC));
        iImmoniumIons.put('E', new Ion(102, IonTypeEnum.immoniumE));
        iImmoniumIons.put('Q', new Ion(101, IonTypeEnum.immoniumQ));
        iImmoniumIons.put('G', new Ion(30, IonTypeEnum.immoniumG));
        iImmoniumIons.put('K', new Ion(101, IonTypeEnum.immoniumK));
        iImmoniumIons.put('S', new Ion(60, IonTypeEnum.immoniumS));
        iImmoniumIons.put('T', new Ion(74, IonTypeEnum.immoniumT));
        iImmoniumIons.put('V', new Ion(72, IonTypeEnum.immoniumV));
        */
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

        Double lError = Double.parseDouble(iProperties.getProperty(ERROR));

        AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lAgentVotes.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            lTableData = "";
            lARFFData = "";

            /**
             * This counter tracks the number of aminoacids in the ImmoniumIon HashMap that are inside the peptide sequence.
             */
            int lImmoniumOptionCounter = 0;

            /**
             * This counter tracks the number of aminoacids found in the spectrum.
             */
            int lImmoniumMatchCounter = 0;

            StringBuffer sb = new StringBuffer();

            char[] lAminoAcids = iProperties.getProperty(AMINOACIDS).toCharArray();

            for (char lAminoAcid : lAminoAcids) {
                if (lPeptideHit.getSequence().indexOf(lAminoAcid) != -1) {
                    Ion ion = iImmoniumIons.get(lAminoAcid);
                    if (ion == null) {
                        String oldAminoAcids = (String) iProperties.getProperty(AMINOACIDS);
                        int index = oldAminoAcids.indexOf(lAminoAcid);
                        String newAminoAcids = oldAminoAcids.substring(0, index);
                        if (oldAminoAcids.length() > index + 1) {
                            newAminoAcids = newAminoAcids.concat(oldAminoAcids.substring(index + 1));
                        }
                        iProperties.setProperty(AMINOACIDS, newAminoAcids);
                        MatLogger.logExceptionalEvent("No immonium ion masses set for aminoacid '" + lAminoAcid + "'.\nRemoved ' " + lAminoAcid + " ' from the list.");
                    } else {
                        lImmoniumOptionCounter++;

                        boolean boolIonMatch =
                                ion.isMatch(aPeptideIdentification.getSpectrum().getPeakList(), lError);

                        if (boolIonMatch) {
                            lImmoniumMatchCounter++;
                            sb.append(lAminoAcid);
                        }
                    }

                }
            }

            lTableData = lImmoniumMatchCounter + "|" + lImmoniumOptionCounter + " " + sb.toString();

            if (lImmoniumOptionCounter == 0) {
                // No possible ions!
                lAgentVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            } else if (lImmoniumMatchCounter == 0) {
                // None of the immonium ions where matched while there was at least a single option. Suspicious!
                lAgentVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else if (lImmoniumMatchCounter == lImmoniumOptionCounter) {
                // All possible immonium ions where found! Not suspicious.
                lAgentVotes[i] = AgentVote.NEGATIVE_FOR_SELECTION;
            } else if (lImmoniumMatchCounter < lImmoniumOptionCounter) {
                // Some but not all immonium ions where matched. Neutral.
                lAgentVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            } else {
                throw new IllegalStateException("There are '" + lImmoniumMatchCounter + "' immonium ions matched out of '" + lImmoniumOptionCounter + "' options. This is an illegalstate for the ImmoniumIon Agent.");
            }

            iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lImmoniumMatchCounter);

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
        return "<html>Inspects for occurence of Immonium ions. <b>Votes 'Positve_for_selection' if none of the immonium ions( " + this.iProperties.get(AMINOACIDS) + " ) are matched.</b>. Is neutral if some are found or no immonium ion generating amino acids were in the sequence and disfavors selection if all are found.</html>";
    }
}
