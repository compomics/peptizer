package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.CommentGenerator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.ValidationReport;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.compomics.peptizer.util.worker.WorkerResult;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 14:59:39
 */

/**
 * Class description: ------------------ This class was developed to save the results of manual validation into a comma
 * separated file.
 */
public class ValidationSaveToCSV extends ValidationSaver {
    // Class specific log4j logger for ValidationSaveToCSV instances.
    private static Logger logger = Logger.getLogger(ValidationSaveToCSV.class);
    /**
     * This object holds the data that must be written into the csv file.
     * Can be a SelectedPeptideIdentifications or an ArrayList with PeptideIdentifications.
     * TODO- Make a "Saveable interface for these objects!
     * TODO- Improssible for the moment (Nov '07 since we do not want to change the version of these classes untill publication of peptizer.
     */


    /**
     * The target csv file.
     */
    private File iFile = null;

    /**
     * The target BufferedWriter.
     */
    private BufferedWriter iBufferedWriter = null;

    /**
     * Separator that will be used in the CSV output.
     */
    private String iSeparator = "\t";

    /**
     * The List of PeptideIdentifications that needs to be selected.
     */
    private ArrayList iPeptideIdentifications = null;

    /**
     * This String saves the Headers of the output for user reporting.
     */
    private String iHeaderList = null;

    /**
     * The count Number of Accepted PeptideIdentifications.
     */
    private int iNumberAccepted;
    /**
     * The count Number of Rejected PeptideIdentifications.
     */
    private int iNumberRejected;
    /**
     * The count Number of non validated PeptideIdentifications.
     */
    private int iNumberNotValidated;

    /**
     * If they are stored, this int counts the number of confident not selected id's.
     */
    private int iNumberConfidentNotSelected = 0;

    /**
     * If they are stored, this int counts the number of non-confident id's.
     */
    private int iNumberNonConfident = 0;

    /**
     * The HashMap couples the TableRow ID's with a boolean corresponding if they will be used in the csv output. Keys:
     * AbstractTableRows Values: Boolean (csv output inclusive - True or False)
     */
    private ArrayList iTableRows;

    /**
     * This Boolean defines the inclusion of comments to the csv file. Default set to false.
     */
    private boolean iComments = false;

    /**
     * This Boolean defines whether to include Not Selected though confident PeptideIdentifications if availlable.
     */
    private boolean iIncludeConfidentNotSelected = false;

    /**
     * This Boolean defines whether to include non confident PeptideIdentifications if availlable.
     */
    private boolean iIncludeNonConfident = false;

    private boolean iIncludeNonPrimary;


    /**
     * This constructor takes a single target File as argument. A BufferedWriter is created on this file. Be sure to
     * close it afterwards.
     *
     * @param aFile      that will be used for csv output.
     * @param aTableRows ArrayList with AbstractTableRows whose getData() method will be used to print the csv file.
     */
    public ValidationSaveToCSV(File aFile, ArrayList aTableRows) {
        iFile = aFile;
        try {
            if (iFile.exists() == false) {
                iFile.createNewFile();
            }
            iTableRows = aTableRows;
            iBufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(iFile)));
        } catch (FileNotFoundException e) {
            MatLogger.logExceptionalEvent("Error while opening CSV output!!\n" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * This constructor takes a single target File as argument. A BufferedWriter is created on this file. Be sure to
     * close it afterwards.
     *
     * @param aFile      that will be used for csv output.
     * @param aTableRows ArrayList with AbstractTableRows whose getData() method will be used to print the csv file.
     * @param aProgress  The Default progressbar for GUI operation.
     */
    public ValidationSaveToCSV(File aFile, ArrayList aTableRows, DefaultProgressBar aProgress) {
        this(aFile, aTableRows);
    }


    /**
     * Save a selection of PeptideIdentifications.
     * If the SelectedPeptideIdentifications has File Handles to NonSelected Confident and NonConfident
     * PeptideIdentifications these can be included as well by setting the relevant booleans
     * iIncludeConfidentNotSelected and iIncludeNonConfident. DEFAULT:false
     *
     * @param aSelectedPeptideIdentifications
     *         SelectedPeptideIdentifications contains a list of PeptideIdentifications.
     */
    private void save(SelectedPeptideIdentifications aSelectedPeptideIdentifications) {
        // Always save the SelectedPeptideIdentifications.
        this.save(aSelectedPeptideIdentifications.getSelectedPeptideIdentificationList());

        // Include Confident non matched peptideidentifications??
        // DEFAULT FALSE
        if (iIncludeConfidentNotSelected) {

            File[] lFiles = TempManager.getInstance().getFiles(aSelectedPeptideIdentifications, TempFileEnum.CONFIDENT_NOT_SELECTED);

            if (lFiles != null) {
                for (int i = 0; i < lFiles.length; i++) {
                    try {
                        File lFile = lFiles[i];
                        ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(lFile));
                        Object o = null;
                        // Loop through object input stream.
                        // I know this is messy, though I do not see any method to check EOF on the ObjectInputStream.o
                        while ((o = ois1.readObject()) != null) {
                            if (o instanceof PeptideIdentification) {
                                PeptideIdentification lPeptideIdentification = (PeptideIdentification) o;
                                lPeptideIdentification.getValidationReport().setAutoComment("CONFIDENT_NOTSELECTED");
                                savePeptideIdentification(lPeptideIdentification);
                                iNumberConfidentNotSelected++;
                            }
                        }
                    } catch (EOFException eof) {
                        // The end of the file is reached, go to the next file ..
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    } catch (ClassNotFoundException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }

        }

        // Include Non Confident Peptideidentifications??
        // DEFAULT FALSE
        if (iIncludeNonConfident) {
            File[] lFiles = TempManager.getInstance().getFiles(aSelectedPeptideIdentifications, TempFileEnum.NON_CONFIDENT);
            if (lFiles != null) {
                for (int i = 0; i < lFiles.length; i++) {
                    try {
                        File lFile = lFiles[i];
                        ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(lFile));
                        Object o = null;
                        // Loop through object input stream.
                        // I know this is messy, though I do not see any method to check EOF on the ObjectInputStream.o
                        while ((o = ois1.readObject()) != null) {
                            if (o instanceof PeptideIdentification) {
                                saveNonConfidentPeptideIdentification((PeptideIdentification) o);
                                iNumberNonConfident++;
                            }
                        }
                    } catch (EOFException eof) {
                        // The end of the file is reached, go to the next file ..
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    } catch (ClassNotFoundException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }

        }
    }

    /**
     * Save a List of PeptideIdentifications.
     *
     * @param aPeptideIdentifications PeptideIdentifications in an ArrayList.
     */
    private void save(ArrayList aPeptideIdentifications) {
        // This is always used when a csv is written. Set of the HTML of the AgentTableRows.
        if (iTableRows.size() != 0) {
            ((AbstractTableRow) iTableRows.get(0)).setHTML(false);
        }

        this.iPeptideIdentifications = aPeptideIdentifications;
        // Set counters.
        iNumberAccepted = 0;
        iNumberNotValidated = 0;
        iNumberRejected = 0;


        try {
            // 1. Initiate the headers.
            initHeader();

            // 2. IDENTIFICATIONS.
            PeptideIdentification lPeptideIdentification = null;
            int lCount = 1;
            for (Object o : aPeptideIdentifications) {
                lPeptideIdentification = (PeptideIdentification) o;
                // a) save every peptide identification.
                savePeptideIdentification(lPeptideIdentification);

                // b) For report statistics.
                boolean isValidated = lPeptideIdentification.getValidationReport().isValidated();
                if (isValidated) {
                    if (lPeptideIdentification.getValidationReport().getResult()) {
                        iNumberAccepted++;
                    } else {
                        iNumberRejected++;
                    }
                } else {
                    iNumberNotValidated++;
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void initHeader() throws IOException {
        /// 1. HEADERS
        // A. Fixed print of certain values.
        StringWriter lStringWriter = new StringWriter();
        lStringWriter.write("Spectrum" + iSeparator);
        lStringWriter.write("IsValidated" + iSeparator);
        lStringWriter.write("IsAccepted" + iSeparator);
        lStringWriter.write("PeptideHitNumber" + iSeparator);

        // B. Print all selected tablerows.
        for (int i = 0; i < iTableRows.size(); i++) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iTableRows.get(i);
            lStringWriter.write(lAbstractTableRow.getName() + iSeparator);
        }

        if (iComments) {
            // C. Validation Comments.
            lStringWriter.write("Automatic Comment" + iSeparator);
            lStringWriter.write("User Comment"); // No separator in the end.
        }

        iHeaderList = lStringWriter.toString();
        lStringWriter.close();

        iBufferedWriter.write(iHeaderList);
        iBufferedWriter.newLine();
        iBufferedWriter.flush();
    }

    /**
     * Writes a PeptideIdentification to the active bufferedWriter and using fixed values and the Tablerows.
     *
     * @param aPeptideIdentification PeptideIdentification to save.
     * @throws IOException Exception while writing.
     */
    public void savePeptideIdentification(PeptideIdentification aPeptideIdentification) throws IOException {

        // first, set the autocomment to each PeptideIdentification.
        String lAutoComment = CommentGenerator.getCommentForSelectiveAgents(aPeptideIdentification, 1, false);

        ValidationReport lValidationReport = aPeptideIdentification.getValidationReport();
        lValidationReport.setAutoComment(lAutoComment);


        // A. Fixed print of certain values.
        int lLoopCount;
        if (isIncludeNonPrimary()) {
            // If include nonPrimary is set to true, confident peptidehits must be included as well, count the number of confident hits.
            lLoopCount = aPeptideIdentification.getNumberOfConfidentPeptideHits();
        } else {
            // If include nonPrimary is set to false, a single pass through the for loop if there are any peptide hits for this spectrum.
            if (aPeptideIdentification.getNumberOfPeptideHits() > 0) {
                lLoopCount = 1;
            } else {
                lLoopCount = 0;
            }
        }

        int lPeptideHitNumber;

        for (int i = 0; i < lLoopCount; i++) {
            if (isIncludeNonPrimary()) {
                // Get peptidehit i for the number of confident hits.
                lPeptideHitNumber = i + 1;
            } else {
                // Else get the correct peptidehit number.
                lPeptideHitNumber = aPeptideIdentification.getValidationReport().getCorrectPeptideHitNumber();
                if (lPeptideHitNumber == -1) {
                    // Non validated hits are set to '-1', return the best ranked peptidehit for these.
                    lPeptideHitNumber = 1;
                }
            }

            // 1. Spectrumfile
            iBufferedWriter.write(aPeptideIdentification.getSpectrum().getName() + iSeparator);

            // 2. Inspected boolean
            boolean isValidated = aPeptideIdentification.getValidationReport().isValidated();
            iBufferedWriter.write(Boolean.toString(isValidated) + iSeparator);

            // 3. Validity boolean
            if (isValidated) {
                iBufferedWriter.write(aPeptideIdentification.getValidationReport().getResult() + iSeparator);
            } else {
                iBufferedWriter.write(Boolean.toString(false) + iSeparator);
            }

            // 4. PeptidehitNumber information
            // CorrectPeptideHitNumber is as displayed. '1' is the first ranked peptidehit.

            iBufferedWriter.write(lPeptideHitNumber + iSeparator);


            for (Object iTableRow : iTableRows) {
                AbstractTableRow lAbstractTableRow = (AbstractTableRow) iTableRow;
                iBufferedWriter.write(lAbstractTableRow.getData(aPeptideIdentification, lPeptideHitNumber) + iSeparator);
            }

            if (iComments) {
                // 10. Validation Comment
                iBufferedWriter.write(aPeptideIdentification.getValidationReport().getAutoComment().replaceAll("\n", "*").replaceAll("=", ""));
                iBufferedWriter.write(iSeparator);
                if(aPeptideIdentification.getValidationReport().getUserComment() != null){
                    iBufferedWriter.write(aPeptideIdentification.getValidationReport().getUserComment().replaceAll("\n", " ").replaceAll("=", ""));
                }
            }

            iBufferedWriter.newLine();

            iBufferedWriter.flush();
        }

    }

    /**
     * Write a non Confident Identification to the current Writer. Distinct method because the Agent's did not inspect
     * on these so there are no AgentReports for these.
     *
     * @param aPeptideIdentification non confident peptideidentification
     * @throws IOException Exception while writing to the output.
     */
    private void saveNonConfidentPeptideIdentification(PeptideIdentification aPeptideIdentification) throws IOException {

        // A. Fixed print of certain values.

        // 1. Spectrumfile
        iBufferedWriter.write(aPeptideIdentification.getSpectrum().getName() + iSeparator);
        // 2. Inspected boolean
        boolean isValidated = aPeptideIdentification.getValidationReport().isValidated();

        iBufferedWriter.write(Boolean.toString(isValidated) + iSeparator);
        // 3. Validity boolean
        if (isValidated) {
            iBufferedWriter.write(aPeptideIdentification.getValidationReport().getResult() + iSeparator);
        } else {
            iBufferedWriter.write(Boolean.toString(false) + iSeparator);
        }
        // 4. PeptidehitNumber information
        // CorrectPeptideHitNumber is as displayed. '1' is the first ranked peptidehit.

        int lPeptideHitNumber = aPeptideIdentification.getValidationReport().getCorrectPeptideHitNumber();
        iBufferedWriter.write(lPeptideHitNumber + iSeparator);

        // B. Print all selected tablerows.
        // If lPeptideHitNumber was -1 for an unvalidated hit, set to 1 to output the best rank peptidehit anyway.
        if (lPeptideHitNumber == -1) {
            lPeptideHitNumber = 1;
        }

        for (Object iTableRow : iTableRows) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iTableRow;
            if (aPeptideIdentification.getPeptideHits() != null) {
                iBufferedWriter.write(lAbstractTableRow.getData(aPeptideIdentification, lPeptideHitNumber) + iSeparator);
            } else {
                iBufferedWriter.write("NA" + iSeparator);
            }
        }

        if (iComments) {
            // 10. Validation Comment
            iBufferedWriter.write("NOT_CONFIDENT"); // No Separator in the end.
        }

        iBufferedWriter.newLine();
        iBufferedWriter.flush();

    }


    /**
     * Sets the String separator to delimit data in the csv file.
     *
     * @param aSeparator String separator.
     */
    public void setSeparator(String aSeparator) {
        iSeparator = aSeparator;
    }

    /**
     * Sets the boolean whether to include comments in the csv output or not.
     *
     * @param aComments boolean to include comments.
     */
    public void setComments(boolean aComments) {
        iComments = aComments;
    }

    /**
     * {@inheritDoc} Finish the ValidationToCSV by closing the stream and present a report to the user.
     */
    public synchronized void finish() {
        // 1. Close the stream.
        closeWriter();

        // 2. Turn on the HTML formatting of the TableRows.
        if (iTableRows.size() != 0) {
            ((AbstractTableRow) iTableRows.get(0)).setHTML(true);
        }

// 1. GUI message to user.
        MatLogger.logExceptionalGUIMessage("Save task report", getHTMLMessage());
        // 2. Simple log to statuspanel.
        MatLogger.logNormalEvent("Saved task to " + iFile.getAbsolutePath());


        // 3. GUI message to user.
//        MatLogger.logExceptionalGUIMessage("Save task report", getHTMLMessage());

        // 4. Simple log to statuspanel.
//        MatLogger.logNormalEvent("Saved task to " + iFile.getPath());
    }


    public void closeWriter() {
        if (iBufferedWriter != null) {
            try {
                iBufferedWriter.flush();
                iBufferedWriter.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Construct a report message in HTML formatting.
     *
     * @return HTML formatted report on the CSV save.
     */
    private String getHTMLMessage() {

        // StringBuffer to build the HTML
        StringBuffer sb = new StringBuffer();
        // Header

        int lTotal = iNumberAccepted + iNumberRejected + iNumberNotValidated + iNumberNonConfident + iNumberConfidentNotSelected;
        sb.append("<html>\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "</head>\n" +
                "<body>" +
                "<STRONG>Saved " + lTotal + " id's to CSV file</STRONG>\n" +
                "<TABLE\n" +
                "       CELLSPACING=10\n" +
                "       CELLPADDING=10\n" +
                "       >\n" +
                "    <CAPTION><EM>Validation counts</EM></CAPTION>\n" +
                "    <TR>\n" +
                "        <TH>\n" +
                "        <TH>Accepted\n" +
                "        <TH>Rejected\n" +
                "        <TH>Not Validated\n" +
                "        <TH>Confident (not shown)\n" +
                "        <TH>Non-Confident (not shown)\n" +
                "    <TR>\n" +
                "    <TH>Counter\n" +
                "        <TD>" + iNumberAccepted + "\n" +
                "        <TD>" + iNumberRejected + "\n" +
                "        <TD>" + iNumberNotValidated + "\n" +
                "        <TD>" + iNumberConfidentNotSelected + "\n" +
                "        <TD>" + iNumberNonConfident + "\n" +
                "</TABLE>");

        sb.append("</HTML>");


        return sb.toString();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    public boolean isIncludeConfidentNotSelected() {
        return iIncludeConfidentNotSelected;
    }

    public void setIncludeConfidentNotSelected(boolean aIncludeConfidentNotSelected) {
        iIncludeConfidentNotSelected = aIncludeConfidentNotSelected;
    }

    public boolean isIncludeNonConfident() {
        return iIncludeNonConfident;
    }

    public void setIncludeNonConfident(boolean aIncludeNonConfident) {
        iIncludeNonConfident = aIncludeNonConfident;
    }

    public void setIncludeNonPrimary(boolean aIncludeNonPrimary) {
        iIncludeNonPrimary = aIncludeNonPrimary;
    }

    public boolean isIncludeNonPrimary() {
        return iIncludeNonPrimary;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public void run() {
        if (iData == null) {
            MatLogger.logExceptionalEvent("Data not set!" +
                    "\nPlace an data object containing PeptideIdentifications by the setData() method before launching the worker!!");
        } else {
            // For temporary ease, just show an indeterminate progressbar.
            // Check the data origin.
            if (iData instanceof SelectedPeptideIdentifications) {
                // 1. SelectedPeptideIdentifications!
                // Call the save method for SelectedPeptideIdentifications.
                this.save((SelectedPeptideIdentifications) iData);
            } else if (iData instanceof ArrayList) {
                // 2. ArrayList!
                // Call the save method for ArrayList with PeptideIdentifications.
                this.save((ArrayList) iData);
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\nResults written:\n\t" + (iNumberAccepted + iNumberNotValidated + iNumberRejected) + " selected by profile\n")
                .append(isIncludeConfidentNotSelected() ? "\t" + iNumberConfidentNotSelected + " not selected by profile but confident\n" : "")
                .append(isIncludeNonConfident() ? "\t" + iNumberNonConfident + " not confident" : "");
        MatLogger.logNormalEvent(sb.toString());

        if (iObserver != null) {
            iObserver.update(null, WorkerResult.SUCCES);
        }
    }
}
