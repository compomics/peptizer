package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 31-jul-2008 Time: 11:39:01 To change this template use File | Settings |
 * File Templates.
 */
public class Ms_Lims_IdentificationIDIterator extends Ms_Lims_Iterator {
	// Class specific log4j logger for Ms_Lims_IdentificationIDIterator instances.
	 private static Logger logger = Logger.getLogger(Ms_Lims_IdentificationIDIterator.class);

    private ArrayList<Long> iIdentificationIDs;

    public Ms_Lims_IdentificationIDIterator(final ArrayList<Long> aIdentificationIDs) {
        iIdentificationIDs = aIdentificationIDs;
        if (ConnectionManager.getInstance().hasConnection()) {
            construct();
        } else {
            MatLogger.logExceptionalGUIMessage("No database connection was set!!", "No database connection was found in the ConnectionManager during the creation of a Ms_Lims_ProjectIterator. Please create a connection in the GUI mode or use another constructor in command line mode.");
        }
    }


    /**
     * Iterate over the peptide identifications from a ms_lims project.
     *
     * @param aUrl      Url to the ms_lims databse.
     * @param aDriver   SQL driver to connnect to aUrl.
     * @param aUser     username to login to the ms_lims database.
     * @param aPassword pass to verify the login to the ms_lims database.
     */
    public Ms_Lims_IdentificationIDIterator(String aUrl, String aDriver, String aUser, String aPassword, final ArrayList<Long> aIdentificationIDs) {

        if (createConnection(aUrl, aDriver, aUser, aPassword)) {
            iIdentificationIDs = aIdentificationIDs;
            construct();
        }
    }


    /**
     * Constructs the ProjectIterator upon construction. A long[] with datfile identifiers will thereby be creaed.
     */
    private void construct() {

        try {
            // Prepare the query, select all identified peptides from the given project and group by datfileid.
            // Thereby fetching all datfiles that contain peptide identifications of this project.

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < iIdentificationIDs.size(); i++) {
                Long lLong = iIdentificationIDs.get(i);
                sb.append("'");
                sb.append(lLong);
                sb.append("'");
                // close fence post.
                if ((i + 1) < iIdentificationIDs.size()) {
                    sb.append(", ");
                }
            }

            String lQuery =
                    "Select i.l_datfileid, i.datfile_query, i.identificationid from identification as i, spectrum as s where i.l_spectrumid=s.spectrumid and i.identificationid in (" + sb.toString() + ") order by i.l_datfileid";
            // Test query - returns 6 rows with 5 distinct datfiles. (34, 309, 310, 2498 and 23068)
            // lQuery = "Select i.l_datfileid, s.filename, i.identificationid from identification as i, spectrumfile as s where i.l_spectrumfileid=s.spectrumfileid and i.identificationid in ('5134','5139','5145','513', '51344', '513454') order by l_datfileid";

            PreparedStatement ps = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);
            ResultSet rs = ps.executeQuery();

            buildIterationUnits(rs);
            // All user information from the query was transformed into IterationUnit's, the construction is completed.

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String getCurrentFileDescription() {
        String s = "";
        if (iMascotDatfile != null) {
            s = iMascotDatfile.getFileName();
        } else {
            s = iIdentificationIDs.size() + " identification ids from " + iIterationUnits.size() + "datfiles.";
        }
        return s;
    }

    public String getGeneralDescription() {
        String s = "";
        if (iIterationUnits != null && iIdentificationIDs != null) {
            s =
                    "peptide identification id iterator on \'" + iIdentificationIDs.size() + " peptide identifications from " + iIterationUnits.size() + " mascot result files.";
        } else {
            s = "ms_lims project peptide identification iterator";
        }
        return s;
    }

}
