package com.compomics.peptizer.util.fileio;

import com.compomics.mslims.db.accessors.Validation;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.CommentGenerator;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
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
    private boolean iOverWriteExistingValidation = false;
    private boolean iApplyToall = false;
    private JComponent iParentComponent = null;


    /**
     * Finish the ValidationSaveToMsLims by popping up a report to the user.
     */
    public void finish() {
        try {
            // 1. GUI message to user.
            MatLogger.logExceptionalGUIMessage("Save task report", getHTMLMessage());
            // 2. Simple log to statuspanel.
            MatLogger.logNormalEvent("Saved task to " + ConnectionManager.getInstance().getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public Object construct() {

        String lReport = "";

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
                lPeptideIdentification.getValidationReport().setComment(lComment);
                this.persistValidation(lConnection, lL_userid, lPeptideIdentification);
            }

            // Second, persist all the confident but not selected ids.
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


        } else {
            MatLogger.logExceptionalEvent("ValidationSaveToCSV does not yet implements \'" + iData.getClass() + "\' instances!!");
        }
        return lReport;
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

            } else {

                // Validation already exists for this identificationid. Update? is it overwrite
                if (iApplyToall) {
                    if (iOverWriteExistingValidation) {
                        lValidation.setStatus(lResult);
                        lValidation.setL_userid(aL_userid);
                        lValidation.setComment(aPeptideIdentification.getValidationReport().getComment());

                        lValidation.update(aConnection);
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
                    } else {
                        // Keep the old validation.
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
        doCounts(lResult);
    }

    /**
     * This method counts the number of accepted and rejected peptide identifications.
     *
     * @param aResult
     */
    private void doCounts(boolean aResult) {
        if (iOverWriteExistingValidation) {
            iNumberPersisted++;
            if (aResult) {
                iNumberAccepted++;
            } else {
                iNumberRejected++;
            }
        }
    }


    public String getHTMLMessage() throws SQLException {

        // Prepare statistics,

        BigDecimal lRelativeAccepted;
        BigDecimal lRelativeRejected;
        if(iNumberPersisted != 0){
            lRelativeAccepted = new BigDecimal(iNumberAccepted * 100 / iNumberPersisted).setScale(2);
            lRelativeRejected = new BigDecimal(iNumberRejected * 100 / iNumberPersisted).setScale(2);
        }else{
            lRelativeAccepted = new BigDecimal(0);
            lRelativeRejected = new BigDecimal(0);
        }

        // StringBuffer to build the HTML
        StringBuffer sb = new StringBuffer();
        // Header
        sb.append(
                "<HTML>" +
                        "<STRONG>" +
                        " Saved " + (iNumberAccepted + iNumberRejected) + " id's to " + ConnectionManager.getInstance().getConnection().getMetaData().getURL() +
                        " </STRONG>");

        // Statistics table
        sb.append("<TABLE  border=\"1\"\n" +
                "          summary=\"This table gives some statistics on the validation.\"\n " +
                "          CELLSPACING=2\n" +
                "          CELLPADDING=2>\n" +
                "<CAPTION><EM>Statistics on validation</EM></CAPTION>\n" +
                "<TR>\t<TH rowspan=\"2\">\n" +
                "\t<TH colspan=\"2\">Validated\n" +
                "\t<TH rowspan=\"2\">Not Validated\n" +
                "\t<TH rowspan=\"2\">Total\n" +
                "<TR><TH>Accepted<TH>Rejected\n" +
                "<TR><TH>Absolute<TD> " + iNumberAccepted + " <TD> " + iNumberRejected + " <TD> " + iNumberRejected + " \n" +
                "<TR><TH>Relative<TD> " + lRelativeAccepted + " <TD> " + lRelativeRejected + " <TD> " + " \n" +
                "</TABLE>");
        sb.append("</HTML>");

        return sb.toString();
    }

    public void setParentComponent(JComponent aParentComponent) {
        iParentComponent = aParentComponent;
    }
}
