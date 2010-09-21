package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Apr 14, 2009
 * Time: 1:48:43 PM
 * <p/>
 * This class inspects the Peptide identification for its Protein's context.
 * The Agent retrieves the Protein Sequence from Uniprot and then reports whether its an Nterminal, Cterminal or internal peptide.
 */
public class ProteinContextAgent extends Agent {
	// Class specific log4j logger for ProteinContextAgent instances.
	 private static Logger logger = Logger.getLogger(ProteinContextAgent.class);

    private HashMap<String, String> iProteins = new HashMap<String, String>();


    public ProteinContextAgent() {
        // Init the general Agent settings.
        initialize();
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    public AgentVote[] inspect(final PeptideIdentification aPeptideIdentification) {

        /**
         * The returning votes.
         */
        AgentVote[] lVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lVotes.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // Get the accession.

            String lAccession = ((ProteinHit) lPeptideHit.getProteinHits().get(0)).getAccession();

            String lProteinSequence = iProteins.get(lAccession);

            // If null, it means the sequence was not yet cached from the uniprot site.
            if (lProteinSequence == null) {
                try {
                    UniprotSequenceRetriever retriever = new UniprotSequenceRetriever(lAccession);
                    lProteinSequence = retriever.getSequence();
                    iProteins.put(lAccession, lProteinSequence);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                }

            }

            int index = lProteinSequence.indexOf(lPeptideHit.getSequence());


            String lTableData = "";
            String lArffData = "";
            if (index == 0 || index == 1) { // Initiator methionine!
                lVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "N";
            } else if (index == (lProteinSequence.length() - lPeptideHit.getSequence().length())) { // Protein length minus peptide length gives index of a Cterminal peptide.
                lVotes[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = "C";
            } else if (index == -1) {
                lVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
            } else {
                lVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "I";
            }

            // Build the report!
            // Agent Result.

            iReport.addReport(AgentReport.RK_RESULT, lVotes[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, new Integer(lArffData));

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lVotes;
    }

    public String getDescription() {
        return "The Agent inspects whether the Peptide is an N-term, C-term or internal peptide based on the first ProteinHit and the current Uniprot sequence.";  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * This class can retrieve the protein sequence for an UniProt protien accession
     */
    private class UniprotSequenceRetriever {

        /**
         * The protein sequence
         */
        private String iSequence = null;
        /**
         * The number of times the sequence retrieving was retried
         */
        private int iRetry = 0;

        /**
         * Constructor
         *
         * @param aUniprotAccession Protein accession
         * @throws Exception
         */
        public UniprotSequenceRetriever(String aUniprotAccession) throws Exception {
            iSequence = readSequenceUrl("http://www.uniprot.org/uniprot/" + aUniprotAccession + ".fasta");
        }

        /**
         * This method reads a url a tries to extrect the protein sequence
         *
         * @param aUrl String with the url
         * @return String with the protein sequence
         * @throws Exception
         */
        public String readSequenceUrl(String aUrl) throws Exception {
            String sequence = "";

            URL myURL = new URL(aUrl);
            StringBuilder input = new StringBuilder();
            HttpURLConnection c = (HttpURLConnection) myURL.openConnection();
            BufferedInputStream in = new BufferedInputStream(c.getInputStream());
            Reader r = new InputStreamReader(in);

            int i;
            while ((i = r.read()) != -1) {
                input.append((char) i);
            }

            String inputString = input.toString();

            String[] lLines = inputString.split("\n");
            for (int j = 1; j < lLines.length; j++) {
                sequence = sequence + lLines[j];
            }
            if (sequence.length() == 0) {
                if (iRetry < 5) {
                    iRetry = iRetry + 1;
                    sequence = readSequenceUrl(aUrl);
                } else {
                    sequence = null;
                }

            }
            return sequence;
        }

        /**
         * Getter for the protein sequence
         *
         * @return String with protein sequence
         */
        public String getSequence() {
            return iSequence;
        }

    }
}
