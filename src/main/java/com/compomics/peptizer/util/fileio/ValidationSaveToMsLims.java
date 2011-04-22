package com.compomics.peptizer.util.fileio;

import com.compomics.mslims.db.accessors.Validation;
import com.compomics.mslims.db.accessors.Validationtype;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.CommentGenerator;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.ValidationReport;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.compomics.peptizer.util.worker.WorkerResult;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by IntelliJ IDEA. User: Kenny Date: 18-mrt-2008 Time: 11:32:32 To change this template use File | Settings |
 * File Templates.
 */
public class ValidationSaveToMsLims extends ValidationSaver {
    // Class specific log4j logger for ValidationSaveToMsLims instances.
    private static Logger logger = Logger.getLogger(ValidationSaveToMsLims.class);

    // Statistics intergers.
    private int iNumberPersisted = 0;
    private int iNumberRejected = 0;
    private int iNumberAccepted = 0;
    private int iNumberUpdated = 0;
    private boolean iOverWriteExistingValidation = false;
    private boolean iApplyToall = false;
    private JComponent iParentComponent = null;
    private Boolean boolUserApproved = false;
    private int iUserApproveOption = -1;
    private String[] iUserOptions = new String[]{"Ignore", "Accept and Save", "Reject and Save"};
    private int iNumberNotValidated;
    private boolean saveConfidentNotSelected;
    private Connection iConn = null;


    /**
     * Finish the ValidationSaveToMsLims by popping up a report to the user.
     */
    public void finish() {
        try {
            // 1. GUI message to user.
            MatLogger.logExceptionalGUIMessage("Save task report", getHTMLMessage());
            // 2. Simple log to statuspanel.
            MatLogger.logNormalEvent("Saved task to " + ConnectionManager.getInstance().getConnection().getMetaData().getURL());

            // reset the counters.
            iNumberAccepted = 0;
            iNumberPersisted = 0;
            iNumberRejected = 0;
            iNumberUpdated = 0;
            iNumberNotValidated = 0;

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void run() {

        // reset former user choices.
        boolUserApproved = false;

        // First fetch the database connection!
        if (!ConnectionManager.getInstance().hasConnection()) {
            MatLogger.logExceptionalEvent("Unable to find a database connection.");
        } else if (iData instanceof SelectedPeptideIdentifications) {

            // First persist all the selected ids.
            int lNumberOfSpectra = ((SelectedPeptideIdentifications) iData).getNumberOfSpectra();
            for (int i = 0; i < lNumberOfSpectra; i++) {
                PeptideIdentification lPeptideIdentification =
                        ((SelectedPeptideIdentifications) iData).getPeptideIdentification(i);

                String lAutoComment = CommentGenerator.getCommentForSelectiveAgents(lPeptideIdentification, 1, false);

                ValidationReport lValidationReport = lPeptideIdentification.getValidationReport();
                lValidationReport.setAutoComment(lAutoComment);

                if (lValidationReport.isValidated()) {
                    this.persistValidation(lPeptideIdentification);
                } else {
                    while (boolUserApproved == false) {

                        iUserApproveOption = JOptionPane.showOptionDialog(
                                iParentComponent,
                                "Do you want to store the selected identifications that you did not validate?",
                                "Unvalidated peptide identifications",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                UIManager.getIcon("OptionPane.questionIcon"),
                                iUserOptions,
                                iUserOptions[0]);
                        boolUserApproved = true;
                    }

                    if (iUserApproveOption == 0) {
                        // do nothing with tis non-validated identification.
                        iNumberNotValidated++;

                    } else if (iUserApproveOption == 1) {
                        lValidationReport.setAutoComment(lAutoComment);
                        lValidationReport.setResult(true);
                        this.persistValidation(lPeptideIdentification);

                    } else if (iUserApproveOption == 2) {
                        lValidationReport.setAutoComment(lAutoComment);
                        lValidationReport.setResult(false);
                        this.persistValidation(lPeptideIdentification);
                    }
                }

            }

            // Second, persist all the confident but not selected ids if needed.

            if (saveConfidentNotSelected) {
                File[] lFiles = TempManager.getInstance().getFiles((SelectedPeptideIdentifications) iData, TempFileEnum.CONFIDENT_NOT_SELECTED);
                if (lFiles != null) {
                    for (int i = 0; i < lFiles.length; i++) {
                        try {
                            File lFile = lFiles[i];
                            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(lFile));
                            Object o = null;
                            // Loop through object input stream.
                            // I know this is messy, though I do not see any method to check EOF on the ObjectInputStream.o
                            while ((o = ois1.readObject()) != null) {
                                if (o instanceof PeptideIdentification && o != null) {
                                    PeptideIdentification lPeptideIdentification = (PeptideIdentification) o;
                                    String lAutoComment = CommentGenerator.getCommentForSelectiveAgents(lPeptideIdentification, 1, false);

                                    lPeptideIdentification.getValidationReport().setAutoComment(lAutoComment);
                                    lPeptideIdentification.getValidationReport().setResult(true);
                                    this.persistValidation(lPeptideIdentification);
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


        } else {
            MatLogger.logExceptionalEvent("ValidationSaveToCSV does not yet implements \'" + iData.getClass() + "\' instances!!");
        }

        if (iObserver != null) {
            iObserver.update(null, WorkerResult.SUCCES);
        }
    }

    /**
     * This method stores the Validation of an PeptideIdentification into the ms-lims connections,.
     *
     * @param aPeptideIdentification
     */
    private void persistValidation(PeptideIdentification aPeptideIdentification) {
        boolean lResult = aPeptideIdentification.getValidationReport().getResult();

        if (iConn == null) {
            iConn = ConnectionManager.getInstance().getConnection();
        }

        Long lIdentificationid = (Long) aPeptideIdentification.getMetaData(MetaKey.Identification_id);
        try {
            Validation lValidation = Validation.getValidation(lIdentificationid, iConn);
            int lValidationtype = parseValidationType(aPeptideIdentification);

            if (lValidation == null) {


                // Validation is non existing. create a new entry!
                lValidation = new Validation();

                lValidation.setL_identificationid(lIdentificationid);

                lValidation.setL_validationtypeid(lValidationtype);
                lValidation.setAuto_comment(aPeptideIdentification.getValidationReport().getAutoComment());
                lValidation.setManual_comment(aPeptideIdentification.getValidationReport().getUserComment());

                // store!
                lValidation.persist(iConn);

                iNumberPersisted++;
                if (lValidationtype > 0) {
                    iNumberAccepted++;
                } else {
                    iNumberRejected++;
                }

            } else {

                // Validation already exists for this identificationid. Update? is it overwrite
                if (iApplyToall) {
                    if (iOverWriteExistingValidation) {
                        lValidation.setL_validationtypeid(lValidationtype);
                        lValidation.setAuto_comment(aPeptideIdentification.getValidationReport().getAutoComment());
                        lValidation.setManual_comment(aPeptideIdentification.getValidationReport().getUserComment());


                        lValidation.update(iConn);

                        iNumberUpdated++;
                        if (lValidationtype > 0) {
                            iNumberAccepted++;
                        } else {
                            iNumberRejected++;
                        }

                    } else {
                        // Keep the old validation.
                    }
                } else {
                    if (iParentComponent != null) {
                        Icon lQuestionIcon = UIManager.getIcon("OptionPane.questionIcon");
                        String[] lOptions = new String[]{"Yes", "Yes to all", "No", "No to all"};
                        int choice = JOptionPane.showOptionDialog(
                                null,
                                "Validation allready exists. Update?",
                                "Option",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                lOptions,
                                lOptions[0]);

                        switch (choice) {
                            case 0:   // YES
                                iOverWriteExistingValidation = true;
                                iApplyToall = false;
                                break;

                            case 1: // YES TO ALL
                                iOverWriteExistingValidation = true;
                                iApplyToall = true;
                                break;

                            case 2: // NO
                                iOverWriteExistingValidation = false;
                                iApplyToall = false;
                                break;

                            case 3:  // NO TO ALL
                                iOverWriteExistingValidation = false;
                                iApplyToall = true;
                                break;
                        }
                    } else {
                        iApplyToall = true;
                        logger.info("No parentcomponent defined. Overwrite existing validation set all to " + iOverWriteExistingValidation);
                    }

                    // Ok, now do we have to do the update?
                    if (iOverWriteExistingValidation) {
                        lValidation.setAuto_comment(aPeptideIdentification.getValidationReport().getAutoComment());
                        lValidation.setManual_comment(aPeptideIdentification.getValidationReport().getUserComment());

                        lValidation.setL_validationtypeid(lValidationtype);
                        lValidation.update(iConn);

                        iNumberUpdated++;
                        if (lValidationtype > 0) {
                            iNumberAccepted++;
                        } else {
                            iNumberRejected++;
                        }

                    } else {
                        // Keep the old validation.
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * This private convenience method parses the validation status (accept/reject) and the validation origin (manual/auto)
     * into an integer typed by the ms-lims database.
     *
     * @param aPeptideIdentification
     * @return
     */
    private int parseValidationType(PeptideIdentification aPeptideIdentification) {
        int lValidationtype;

        if (aPeptideIdentification.isValidated()) {
            // MANUAL!!
            if (aPeptideIdentification.getValidationReport().getResult()) {
                // manual accept!
                lValidationtype = Validationtype.ACCEPT_MANUAL;
            } else {
                // manual reject!
                lValidationtype = Validationtype.REJECT_MANUAL;
            }
        } else {
            // AUTO!
            if (aPeptideIdentification.getValidationReport().getResult()) {
                // auto accept!
                lValidationtype = Validationtype.ACCEPT_AUTO;
            } else {
                // auto reject!
                lValidationtype = Validationtype.REJECT_AUTO;
            }
        }
        return lValidationtype;
    }


    public String getHTMLMessage() throws SQLException {


        // StringBuffer to build the HTML
        StringBuffer sb = new StringBuffer();

        int lTotal = iNumberPersisted + iNumberUpdated;
        int lAccepted = iNumberAccepted;
        int lRejected = iNumberRejected;
        int lNotValidated = iNumberNotValidated;
        int TotalInsert = iNumberPersisted;
        int lTotalUpdate = iNumberUpdated;

        sb.append("<html>\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "</head>\n" +
                "<body>" +
                "<STRONG>Saved " + lTotal + " id's to ms-lims</STRONG>\n" +
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
                "        <TH>Total insert\n" +
                "        <TH>Total update\n" +
                "    <TR>\n" +
                "    <TH>Counter\n" +
                "        <TD>" + lAccepted + "\n" +
                "        <TD>" + lRejected + "\n" +
                "        <TD>" + lNotValidated + "\n" +
                "        <TD>" + TotalInsert + "\n" +
                "        <TD>" + lTotalUpdate + "\n" +
                "</TABLE>");

        sb.append("</HTML>");

        // debug the table html code.
        logger.debug(sb.toString());

        return sb.toString();
    }

    public void setParentComponent(JComponent aParentComponent) {
        iParentComponent = aParentComponent;
    }

    public boolean isSaveConfidentNotSelected() {
        return saveConfidentNotSelected;
    }

    public void setSaveConfidentNotSelected(boolean aSaveConfidentNotSelected) {
        saveConfidentNotSelected = aSaveConfidentNotSelected;
    }
}
