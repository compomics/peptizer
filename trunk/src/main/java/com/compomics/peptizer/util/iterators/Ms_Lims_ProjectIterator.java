package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Kenny
 * Date: 13-mrt-2008
 * Time: 16:41:25
 * To change this template use File | Settings | File Templates.
 */

/**
 * This Class will iterate all PeptideIdentifications from a given ms_lims project.
 */
public class Ms_Lims_ProjectIterator extends Ms_Lims_Iterator {
    /**
     * The project that must be iterated.
     */
    private long iProjectID;

    /**
     * Iterate over the peptide identifications from a ms_lims project.
     *
     * @param aProjectID long identifying the project.
     */
    public Ms_Lims_ProjectIterator(long aProjectID) {
        iProjectID = aProjectID;

        if (ConnectionManager.getInstance().hasConnection()) {
            construct();
        } else {
            MatLogger.logExceptionalGUIMessage("No database connection was set!!", "No database connection was found in the ConnectionManager during the creation of a Ms_Lims_ProjectIterator. Please create a connection in the GUI mode or use another constructor in command line mode.");
        }
    }

    /**
     * Iterate over the peptide identifications from a ms_lims project.
     *
     * @param aUrl       Url to the ms_lims databse.
     * @param aDriver    SQL driver to connnect to aUrl.
     * @param aUser      username to login to the ms_lims database.
     * @param aPassword  pass to verify the login to the ms_lims database.
     * @param aProjectID long identifying the project.
     */
    public Ms_Lims_ProjectIterator(String aUrl, String aDriver, String aUser, String aPassword, long aProjectID) {

        if (createConnection(aUrl, aDriver, aUser, aPassword)) {
            iProjectID = aProjectID;
            construct();
        }
    }


    /**
     * Iterate over the peptide identifications from a ms_lims project.
     *
     * @param aConnection java.sql.connection instance to an ms_lims database.
     * @param aProjectID  long identifying the project.
     */
    public Ms_Lims_ProjectIterator(Connection aConnection, long aProjectID) {
        ConnectionManager.getInstance().setConnection(aConnection);
        iProjectID = aProjectID;
        construct();
    }


    /**
     * Constructs the ProjectIterator upon construction. A long[] with datfile identifiers will thereby be creaed.
     */
    private void construct() {


        try {
            String lQuery =
                    "Select i.l_datfileid, i.datfile_query, i.identificationid from identification as i, spectrum as s where i.l_spectrumid=s.spectrumid and s.l_projectid=" + iProjectID + " order by i.l_datfileid";

            PreparedStatement ps = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);
            ResultSet rs = ps.executeQuery();

            buildIterationUnits(rs);
            // All user information from the query was transformed into IterationUnit's, the construction is completed.

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Hi I am a ms_lims project peptide identification iterator. I am currently iterating petpide identifcations of project " + iProjectID + ".";
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
            s = "Mascot dat file from ms_lims project " + iProjectID + ".";
        }
        return s;
    }

    public String getGeneralDescription() {
        String s = "";
        if (iProjectID != 0) {
            s = "peptide identification iterator on ms_lims project \'" + iProjectID + "\'.";
        } else {
            s = "ms_lims project peptide identification iterator";
        }
        return s;
    }

}
