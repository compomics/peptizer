package com.compomics.peptizer.util.worker;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.fileio.TempManager;
import org.apache.log4j.Logger;
import org.divxdede.swing.busy.FutureBusyModel;

import java.awt.*;
import java.io.*;
import java.util.Observer;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 8-mei-2007
 * Time: 10:01:38
 */

/**
 * Class description: ------------------ This class was developed to dirert the interaction between the AgentAggregator
 * and the PeptideIdentificationIterator.
 */
public class MatWorker implements Runnable {

    ;

	// Class specific log4j logger for MatWorker instances.
	 private static Logger logger = Logger.getLogger(MatWorker.class);

    /**
     * The Iterator with PeptideIdentifications.
     */
    private PeptideIdentificationIterator iIter = null;

    /**
     * The AgentAggregator.
     */
    private AgentAggregator iAgentAggregator = null;

    /**
     * The reference Vector wherein results will be stored.
     */
    private SelectedPeptideIdentifications iResults = null;

    /**
     * Integer counters for the AgentAggregator results.
     */
    private Integer iConfidentNotSelected;
    private Integer iNonConfident;
    private Integer iNoIdentification;

    private boolean guiMode;
    private ObjectOutputStream iObectStream_good;
    private ObjectOutputStream iObjectStream_bad;
    private File iFileConfident_oos;
    private File iFileNonConfident_oos;
    private boolean hasOjectStream_good;
    private boolean hasObjectStream_bad;
    private boolean serializationError = false;
    private Frame iPeptizerGUI = null;
    private Observer iObserver;

    private FutureBusyModel iFutureBusyModel = null;

    /**
     * This constructor takes a PeptideIdentificationIterator and an AgentAggregator as parameters.
     *
     * @param aIter       PeptideIdentificationIterator of the MatWorker.
     * @param aAggregator Agregator that will judge the PeptideIdentifications of the Iterator.
     * @param aResults    Reference object wherein the results of the aggregator are stored.
     */
    public MatWorker(PeptideIdentificationIterator aIter, AgentAggregator aAggregator, SelectedPeptideIdentifications aResults) {
        iIter = aIter;
        iAgentAggregator = aAggregator;
        iResults = aResults;
        // If the given progressbar is null, this means this matworker is opertating in command line mode. Disable gui operations!
    }

    public MatWorker(PeptideIdentificationIterator aIter, AgentAggregator aAggregator, SelectedPeptideIdentifications aSelectedPeptideIdentifications, Frame aPeptizerGUI, Observer aObserver) {
        this(aIter, aAggregator, aSelectedPeptideIdentifications);
        iPeptizerGUI = aPeptizerGUI;
        iObserver = aObserver;
    }

    /**
     * Iterate all PeptideIdentifications and pass them one by one to the AgentAggregator to judge them. The
     * PeptideIdentifications that suit the AgentAggregator's values are stored in the reference Vector
     * iResultsReference.
     */
    public void run() {

        // 1. Initiate a PeptideIdentificationIterator, read the source.

        if (iIter == null) {
            // Iterator is null, we cannot continue!
        } else {

            // If ENABLE_TEMP_OBJECT.OUTPUTSTREAM is set to true, the non confident and confident but not selected peptide identifications will be written by an objectstream to a temp file.

            hasOjectStream_good = Boolean.parseBoolean(MatConfig.getInstance().getGeneralProperty("ENABLE_CONFIDENT_NONSELECTED_OBJECT.OUTPUTSTREAM").toString());
            hasObjectStream_bad = Boolean.parseBoolean(MatConfig.getInstance().getGeneralProperty("ENABLE_NONCONFIDENT_OBJECT.OUTPUTSTREAM").toString());

            // Create temp cache files for the ID's that do not require validation (either because they are really good or
            // really bad) to lower memory requirements.

            iConfidentNotSelected = 0;
            iNonConfident = 0;
            iNoIdentification = 0;

            /**
             * Create the Object Outputstreams.
             */
            TempManager lTempFileManager = TempManager.getInstance();
            int lTempFileContentSize = 1000;

            advanceObjectOutputStreams();

            // Iterate all the PeptideIdentifications.
            int iProgressCounter = -2;
            // Counter for the iterator, 1-based.
            int lIteratorCounter = 0;
            while (iIter.hasNext()) {

                if (lIteratorCounter++ % lTempFileContentSize == 0) {
                    advanceObjectOutputStreams();
                }

                if(iFutureBusyModel != null){
                    iFutureBusyModel.setDescription("Processing file " + iIter.getCurrentFileDescription());
                }

                PeptideIdentification lPeptideIdentification = iIter.next();
                // If the PeptideIdentification matches the AgentAggregator.
                AgentAggregationResult lAggregationResult = iAgentAggregator.match(lPeptideIdentification);

                storeAggregation(lAggregationResult, lPeptideIdentification);
            }

            // Flush & close the object outputstreams
            finishObjectOutputStreams();

            iResults.setMeta(SelectedPeptideIdentifications.MK_NUMBER_CONFIDENT, iConfidentNotSelected);
            iResults.setMeta(SelectedPeptideIdentifications.MK_NUMBER_NOT_CONFIDENT, iNonConfident);
            iResults.setMeta(SelectedPeptideIdentifications.MK_NUMBER_NO_IDENTIFICATION, iNoIdentification);

            logger.info("Matched" + iResults.getNumberOfSpectra());
            logger.info("ConfidentNotSelected:" + iConfidentNotSelected);
            logger.info("NotConfident:" + iNonConfident);
            logger.info("NoIdentification:" + iNoIdentification);


            // Send around that the Runner was successfull!
            System.gc();

            iObserver.update(null, WorkerResult.SUCCES);

            if(iPeptizerGUI != null){
                ((PeptizerGUI) iPeptizerGUI).passTask(iResults);
            }
        }
    }


    /**
     * Makes the objectoutputstreams advance to the next temporary file - if they are active!
     */
    private void advanceObjectOutputStreams() {
        try {

            // Only create objectoutputstreams if the general property of this peptizer instance was set to true.
            if (hasOjectStream_good) {
                if (iObectStream_good != null) {
                    iObectStream_good.flush();
                    iObectStream_good.close();
                }
                // Temp file and stream for those that are really good and pass the agent examination.
                iFileConfident_oos = TempManager.getInstance().makeNextTempFile(iResults, TempFileEnum.CONFIDENT_NOT_SELECTED);
                iFileConfident_oos.deleteOnExit();
                iObectStream_good =
                        new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(iFileConfident_oos)));
            }

            if (hasObjectStream_bad) {
                if (iObjectStream_bad != null) {
                    iObjectStream_bad.flush();
                    iObjectStream_bad.close();
                }
                // Temp file and stream for those that are not confident and are therefore ignored.
                iFileNonConfident_oos = TempManager.getInstance().makeNextTempFile(iResults, TempFileEnum.NON_CONFIDENT);
                iFileNonConfident_oos.deleteOnExit();
                iObjectStream_bad =
                        new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(iFileNonConfident_oos)));
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }
    }

    /**
     * Flushes and closes the confident and non-confident object outputstreams that were not selected.
     */
    private void finishObjectOutputStreams() {
        try {
            if (hasOjectStream_good) {
                iObectStream_good.flush();
                iObectStream_good.close();
            }
            if (hasObjectStream_bad) {
                iObjectStream_bad.flush();
                iObjectStream_bad.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Handles the result of the aggregation.
     *
     * @param aAggregation
     * @param aPeptideIdentification
     */
    private void storeAggregation(final AgentAggregationResult aAggregation, final PeptideIdentification aPeptideIdentification) {
        try {
            switch (aAggregation) {

                case MATCH:
                    iResults.addResult(aPeptideIdentification);
                    break;

                case NON_MATCH:
                    if (hasOjectStream_good) {
                        iObectStream_good.writeObject(aPeptideIdentification);
                        iObectStream_good.flush();
                    }
                    iConfidentNotSelected++;
                    break;

                case NON_CONFIDENT:
                    if (hasObjectStream_bad) {
                        iObjectStream_bad.writeObject(aPeptideIdentification);
                        iObjectStream_bad.flush();
                    }
                    iNonConfident++;
                    break;


                case NO_IDENTIFICATION:
                    if (hasObjectStream_bad) {
                        iObjectStream_bad.writeObject(aPeptideIdentification);
                        iObjectStream_bad.flush();
                    }
                    iNoIdentification++;
                    break;
            }
        } catch (Exception e) {
            if (!serializationError) {
                MatLogger.logExceptionalEvent("Serialization failure.\n" + e.getMessage() + "\n" + e.getStackTrace());
                serializationError = true;
            }
        }
    }

    /**
     * This BusyModel serves to keep track of the status of this Task.
     * @param aFutureBusyModel
     */
    public void setFutureBusyModel(FutureBusyModel aFutureBusyModel) {
        iFutureBusyModel = aFutureBusyModel;
    }
}
