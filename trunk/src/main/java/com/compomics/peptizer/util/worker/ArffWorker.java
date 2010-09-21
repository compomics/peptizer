package com.compomics.peptizer.util.worker;

import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.util.sun.SwingWorker;
import org.apache.log4j.Logger;

import java.io.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-jun-2007
 * Time: 11:49:10
 */

/**
 * Class description: ------------------ This worker iterates over a PeptideIdentificationIterator && offers each of
 * them to an Aggregator. The result of the Aggregator and each Agent is written to a Arff file for import into a neural
 * network.
 */
public class ArffWorker extends SwingWorker {
	// Class specific log4j logger for ArffWorker instances.
	 private static Logger logger = Logger.getLogger(ArffWorker.class);

    /**
     * The Iterator with PeptideIdentifications.
     */
    private PeptideIdentificationIterator iIter = null;

    /**
     * The AgentAggregator.
     */
    private AgentAggregator iAgentAggregator = null;

    /**
     * The File for results output.
     */
    private File iTargetFile = null;

    /**
     * The BufferedWriter for output.
     */
    private BufferedWriter iBufferedWriter = null;

    /**
     * The progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * The Agent's references that are in use.
     */
    private String[] iAgentUniqueIDs = null;

    private boolean iDetailOutputType;

    /**
     * This constructor takes a PeptideIdentificationIterator and an AgentAggregator as parameters.
     *
     * @param aIter       PeptideIdentificationIterator of the MatWorker.
     * @param aAggregator Agregator that will judge the PeptideIdentifications of the Iterator.
     * @param aTargetFile File for results output.
     * @param aProgress   Progressbar gui component.
     */
    public ArffWorker(PeptideIdentificationIterator aIter, AgentAggregator aAggregator, File aTargetFile, DefaultProgressBar aProgress, boolean aDetailOutputType) {
        iIter = aIter;
        iAgentAggregator = aAggregator;
        iTargetFile = aTargetFile;
        iProgress = aProgress;
        iDetailOutputType = aDetailOutputType;
    }

    /**
     * Iterate all PeptideIdentifications and pass them one by one to the AgentAggregator to judge them. The
     * PeptideIdentifications that suit the AgentAggregator's values are stored in the reference Vector
     * iResultsReference.
     */
    public Object construct() {

        // 0. Create the BufferedWriter for output.
        try {
            iBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iTargetFile)));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

        // 1. Set Agent identifiers,
        Object[] lAgents = iAgentAggregator.getAgentsCollection().toArray();
        iAgentUniqueIDs = new String[lAgents.length];
        for (int i = 0; i < lAgents.length; i++) {
            Agent lAgent = (Agent) lAgents[i];
            iAgentUniqueIDs[i] = lAgent.getUniqueID();
        }
        int lConfidentMatch = 0;
        int lConfidentNOMatch = 0;
        int lNonConfident = 0;

        try {

            // 2. Print header.
            printHeader();

            // 3. Iterate all the PeptideIdentifications and print the Data.

            while (iIter.hasNext()) {
                PeptideIdentification lPeptideIdentification = (PeptideIdentification) iIter.next();
                // If the PeptideIdentification matches the AgentAggregator.
                AgentAggregationResult lAggregation = iAgentAggregator.match(lPeptideIdentification);

                if (lAggregation == AgentAggregationResult.MATCH) {
                    lConfidentMatch++;
                } else if (lAggregation == AgentAggregationResult.NON_MATCH) {
                    lConfidentNOMatch++;
                } else if (lAggregation == AgentAggregationResult.NON_CONFIDENT) {
                    lNonConfident++;
                }

                this.printData(lAggregation, lPeptideIdentification);
                iProgress.setValue(iIter.estimateSize() - iIter.estimateToDo());
            }

            // Set a message to PeptizerGUI.
            StringBuffer iMessage = new StringBuffer();
            iMessage.append("Profiling on " + iAgentUniqueIDs.length + " information points completed!");
            iMessage.append("\n");
            iMessage.append("\t-" + lConfidentMatch + " confident matched identifications\n" +
                    "\t-" + lConfidentNOMatch + " confident NOT matched identifications\n" +
                    "\t-" + lNonConfident + " Non confident ");
            iMessage.append("\n");
            iMessage.append("Results written to " + iTargetFile.getCanonicalFile() + "!");

            ((PeptizerGUI) iProgress.getOwner()).setStatus(iMessage.toString());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                iBufferedWriter.flush();
                iBufferedWriter.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * Prints the Header of the arff file to the BufferedWriter.
     */
    private void printHeader() throws IOException {
        if (iBufferedWriter != null) {
            iBufferedWriter.write("@RELATION" + " " + "MatARFF");
            iBufferedWriter.newLine();
            iBufferedWriter.newLine();

            for (int i = 0; i < iAgentUniqueIDs.length; i++) {
                String lAgentUniqueID = iAgentUniqueIDs[i];
                String lName = AgentFactory.getInstance().getAgent(lAgentUniqueID).getName();
                iBufferedWriter.write("@ATTRIBUTE" + " " + lName.replaceAll(" ", "") + " " + "INTEGER");
                iBufferedWriter.newLine();
            }

            iBufferedWriter.newLine();
            iBufferedWriter.write("@ATTRIBUTE class {Confident_Match,Confident_NOMatch,NOT_Confident}");
            iBufferedWriter.newLine();
            iBufferedWriter.write("@DATA");
            iBufferedWriter.newLine();
            iBufferedWriter.flush();
        }
    }

    /**
     * Prints a new Data line to the BufferedWriter.
     *
     * @param aAggregationResult     result of the aggregator on the PeptideIdentification
     * @param aPeptideIdentification PeptideIdentification that subjected to the Agents.
     */
    private void printData(AgentAggregationResult aAggregationResult, PeptideIdentification aPeptideIdentification) throws IOException {
        if (aPeptideIdentification.getNumberOfConfidentPeptideHits() > 0) {
            StringBuffer lStringBuffer = new StringBuffer();

            String lGroup = "";
            switch (aAggregationResult) {
                case MATCH:
                    lGroup = "Confident_Match";
                    break;
                case NON_CONFIDENT:
                    lGroup = "Confident_NONMatch";
                    break;
                case NON_MATCH:
                    lGroup = "NOT_Confident";
                    break;
            }

            for (int i = 0; i < iAgentUniqueIDs.length; i++) {
                String lAgentUniqueID = iAgentUniqueIDs[i];
                if (iDetailOutputType) {
                    lStringBuffer.append(aPeptideIdentification.getAgentReport(1, lAgentUniqueID).get(AgentReport.RK_ARFF));
                } else {
                    lStringBuffer.append(aPeptideIdentification.getAgentReport(1, lAgentUniqueID).get(AgentReport.RK_RESULT));
                }
                lStringBuffer.append(",");
            }
            lStringBuffer.append(lGroup);
            iBufferedWriter.write(lStringBuffer.toString());
            iBufferedWriter.newLine();
            iBufferedWriter.flush();
        }
    }
}
