package com.compomics.peptizer.util.fileio;

import com.compomics.mslims.db.accessors.Validation;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;

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

    // Statistics intergers.
    private int iNumberAccepted;
    private int iNumberRejected;
    private int iNumberOfSpectra;
    private int iValidationCount;

    public ValidationSaveToMsLims() {
        // Nothing needed to construct this panel!
    }

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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                e.printStackTrace();
            }
            iNumberAccepted = 0;
            iNumberRejected = 0;
            iValidationCount = 0;

            iNumberOfSpectra = ((SelectedPeptideIdentifications) iData).getNumberOfSpectra();
            for (int i = 0; i < iNumberOfSpectra; i++) {
                PeptideIdentification lPeptideIdentification =
                        ((SelectedPeptideIdentifications) iData).getPeptideIdentification(i);
                if (((PeptideIdentification) lPeptideIdentification).isValidated()) {
                    iValidationCount++;
                    // Here we get all of the PeptideIdentifications that were validated.
                    try {
                        boolean lResult = lPeptideIdentification.getValidationReport().getResult();

                        // Allocate the identification id!
                        String lSpectrumFile = lPeptideIdentification.getSpectrum().getName();
                        String lDatfileID = lPeptideIdentification.getMetaData(MetaKey.Datfile_id).toString();

                        ResultSet rs = performSelect(lDatfileID, lSpectrumFile);
                        if (rs.next()) {
                            Validation lValidation = new Validation();
                            lValidation.setL_identificationid((Long) lPeptideIdentification.getMetaData(MetaKey.Identification_id));
                            lValidation.setL_userid(lL_userid);
                            lValidation.setComment(lPeptideIdentification.getValidationReport().getComment());
                            // We expect only a single row in the ResultSet, if the call to next() returns true, there are more identifications which is basically impossible..
                            if (!rs.next()) {
                                if (lResult) {
                                    // Set valid to '2' if the identification was set to true.
                                    lValidation.setStatus(true);
                                    iNumberAccepted++;
                                } else {
                                    // Set valid to '0' if the identification was set to false.
                                    lValidation.setStatus(false);
                                    iNumberRejected++;
                                }
                                lValidation.persist(lConnection);
                            } else {
                                MatLogger.logExceptionalEvent("Multiple identifications found in " + ConnectionManager.getInstance().getConnection().getMetaData().getURL() + " for \'" + lSpectrumFile + "\' in datfile \'" + lDatfileID + "\'!!");
                            }
                        } else {
                            MatLogger.logExceptionalEvent("No identification found in " + ConnectionManager.getInstance().getConnection().getMetaData().getURL() + " for \'" + lSpectrumFile + "\' in datfile \'" + lDatfileID + "\'.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        } else {
            MatLogger.logExceptionalEvent("ValidationSaveToCSV does not yet implements \'" + iData.getClass() + "\' instances!!");
        }
        return lReport;
    }

    /**
     * Performs a SELECT statement by a spectrumfileID and a datfileID to select the PeptideIdentification that was
     * validated.
     *
     * @param aDatfileID    datfile identifier that contains the peptide identification.
     * @param aSpectrumFile spectrumfile identifier as the source of the peptide identification.
     * @return ResultSet of the SELECT statement.
     * @throws SQLException for errors in the SELECT statement.
     */
    private ResultSet performSelect(String aDatfileID, String aSpectrumFile) throws SQLException {
        String lQuery =
                "select * from identification as i, spectrumfile as s where i.l_spectrumfileid=s.spectrumfileid and s.filename=\"" + aSpectrumFile + "\" and i.l_datfileid=" + aDatfileID;
        PreparedStatement lPreparedStatement = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);
        return lPreparedStatement.executeQuery();
    }

    public String getHTMLMessage() throws SQLException {

        // Prepare statistics,
        BigDecimal lRelativeAccepted = new BigDecimal(iNumberAccepted * 100 / iNumberOfSpectra).setScale(2);
        BigDecimal lRelativeRejected = new BigDecimal(iNumberRejected * 100 / iNumberOfSpectra).setScale(2);
        BigDecimal lRelativeNotValidated =
                new BigDecimal((iNumberOfSpectra - (iNumberAccepted + iNumberRejected)) * 100 / iNumberOfSpectra).setScale(2);

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
                "<TR><TH>Absolute<TD> " + iNumberAccepted + " <TD> " + iNumberRejected + " <TD> " + (iNumberOfSpectra - (iNumberAccepted + iNumberRejected)) + " <TD> " + (iNumberOfSpectra) + " \n" +
                "<TR><TH>Relative<TD> " + lRelativeAccepted + " <TD> " + lRelativeRejected + " <TD> " + lRelativeNotValidated + " <TD> " + (lRelativeAccepted.doubleValue() + lRelativeRejected.doubleValue() + lRelativeNotValidated.doubleValue()) + " \n" +
                "</TABLE>");
        sb.append("</HTML>");

        return sb.toString();
    }
}
