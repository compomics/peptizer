package com.compomics.peptizer.util.fileio;

import com.compomics.mslims.db.accessors.Validation;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

            Connection lConnection = ConnectionManager.getInstance().getConnection();
            Integer lL_userid = null;
            String lUserName = "NA";
            try {
                lUserName = lConnection.getMetaData().getUserName();
                String lUserQuery = "SELECT userid from user where name regexp '.*" + lUserName.substring(0, lUserName.indexOf('@')) + ".*'";
                PreparedStatement ps = lConnection.prepareStatement(lUserQuery);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    lL_userid = rs.getInt("userid");
                }
            } catch (SQLException e) {
                MatLogger.logExceptionalEvent("Failed to find the userid for user '" + lUserName + "'.");
                logger.error(e.getMessage(), e);
            }

            // First persist all the selected ids.
            int lNumberOfSpectra = ((SelectedPeptideIdentifications) iData).getNumberOfSpectra();
            for (int i = 0; i < lNumberOfSpectra; i++) {
                PeptideIdentification lPeptideIdentification =
                        ((SelectedPeptideIdentifications) iData).getPeptideIdentification(i);
                String lComment = CommentGenerator.getCommentForSelectiveAgents(lPeptideIdentification, 1);
                ValidationReport lValidationReport = lPeptideIdentification.getValidationReport();
                if (lValidationReport.isValidated()) {
                    lValidationReport.setComment(lComment);
                    this.persistValidation(lConnection, lL_userid, lPeptideIdentification);
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
                        lValidationReport.setComment(lComment);
                        lValidationReport.setResult(true);
                        this.persistValidation(lConnection, lL_userid, lPeptideIdentification);

                    } else if (iUserApproveOption == 2) {
                        lValidationReport.setComment(lComment);
                        lValidationReport.setResult(false);
                        this.persistValidation(lConnection, lL_userid, lPeptideIdentification);
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
                                    lPeptideIdentification.getValidationReport().setComment("AUTO_ACCEPT");
                                    lPeptideIdentification.getValidationReport().setResult(true);
                                    this.persistValidation(lConnection, lL_userid, lPeptideIdentification);
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
     * @param aConnection            Connection to the ms-lims databse.
     * @param aL_userid
     * @param aPeptideIdentification
     */
    private void persistValidation(Connection aConnection, Integer aL_userid, PeptideIdentification aPeptideIdentification) {
        boolean lResult = aPeptideIdentification.getValidationReport().getResult();

        Long lIdentificationid = (Long) aPeptideIdentification.getMetaData(MetaKey.Identification_id);
        try {
            Validation lValidation = Validation.getValidation(lIdentificationid, aConnection);

            if (lValidation == null) {

                // Validation is non existing. create a new entry!
                lValidation = new Validation();
                lValidation.setL_identificationid(lIdentificationid);
                lValidation.setStatus(lResult);
                lValidation.setL_userid(aL_userid);
                lValidation.setComment(aPeptideIdentification.getValidationReport().getComment());
                // store!
                lValidation.persist(aConnection);

                iNumberPersisted++;
                if (lValidation.getStatus()) {
                    iNumberAccepted++;
                } else {
                    iNumberRejected++;
                }

            } else {

                // Validation already exists for this identificationid. Update? is it overwrite
                if (iApplyToall) {
                    if (iOverWriteExistingValidation) {
                        lValidation.setStatus(lResult);
                        lValidation.setL_userid(aL_userid);
                        lValidation.setComment(aPeptideIdentification.getValidationReport().getComment());

                        lValidation.update(aConnection);

                        iNumberUpdated++;
                        if (lValidation.getStatus()) {
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
                        lValidation.setL_userid(aL_userid);
                        lValidation.setComment(aPeptideIdentification.getValidationReport().getComment());
                        lValidation.setStatus(lResult);
                        lValidation.update(aConnection);

                        iNumberUpdated++;
                        if (lValidation.getStatus()) {
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
