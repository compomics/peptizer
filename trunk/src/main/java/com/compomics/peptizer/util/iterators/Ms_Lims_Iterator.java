package com.compomics.peptizer.util.iterators;

import com.compomics.mslims.db.accessors.Datfile;
import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.mascotdatfile.util.mascot.factory.MascotDatfileFactory;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

/**
 * This abstract class shares common code for Iterators that fetch peptide identifications from an ms_lims database.
 * Only the construct method wherein the MsLimsIterationUnits are created must be implemented besides some reporting
 * classes such as the toString method.
 */
public abstract class Ms_Lims_Iterator implements PeptideIdentificationIterator {

    public final static String MK_IDENTIFICATION_ID = "IDENTIFICATIONID";

    /**
     * An arraylist with the iterationunits that must be fetched by this iterator.
     */
    protected ArrayList<MsLimsIterationUnit> iIterationUnits;

    /**
     * The currect iterationunit as an instance field.
     */
    private MsLimsIterationUnit iCurrentIterationUnit;

    /**
     * The index of the currect iterationunit.
     */
    private int iIterationUnitIndex = 0;

    /**
     * The current MascotDatfileInf implementation.
     */
    protected MascotDatfileInf iMascotDatfile;

    /**
     * The MascotDatfile parsing type to be used.
     */
    private MascotDatfileType iMascotDatfileType = MascotDatfileType.INDEX;

    /**
     * {@inheritDoc}
     */
    public Object next() {

        if (iCurrentIterationUnit.hasNext()) {
            // Get the next filename for the current IterationUnit.
            int lQueryNumber = iCurrentIterationUnit.next();

            if (lQueryNumber <= 0) {
                MatLogger.logExceptionalEvent("Could not find the Spectrum for query '" + lQueryNumber + "' in '" + iMascotDatfile.getFileName() + "'");
                if (this.hasMoreIterationUnits()) {
                    moveToNextIterationUnit();
                    return this.next();
                } else {
                    return null;
                }

            } else {

                // Get the spectrum and peptidehits for this peptide hit.
                MascotSpectrum lSpectrum = new MascotSpectrum((Spectrum) iMascotDatfile.getQuery(lQueryNumber));
                Vector lPeptideHits = null;
                for (int i = 0; i < iMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber).size(); i++) {
                    if (lPeptideHits == null) {
                        lPeptideHits = new Vector<MascotPeptideHit>();
                    }
                    lPeptideHits.add(new MascotPeptideHit((PeptideHit) iMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber).get(i)));
                }

                // Construct a PeptideIdentification instance.
                PeptideIdentification lPeptideIdentification = new PeptideIdentification(lSpectrum, lPeptideHits, SearchEngineEnum.Mascot);

                // Add meta info.
                lPeptideIdentification.addMetaData(MetaKey.Masses_section, iMascotDatfile.getMasses());
                lPeptideIdentification.addMetaData(MetaKey.Parameter_section, iMascotDatfile.getParametersSection());
                lPeptideIdentification.addMetaData(MetaKey.Identification_id, iCurrentIterationUnit.getIdentificationId(lQueryNumber));
                lPeptideIdentification.addMetaData(MetaKey.Datfile_id, iCurrentIterationUnit.getDatfileID());

                return lPeptideIdentification;
            }

        } else if (this.hasMoreIterationUnits()) {
            moveToNextIterationUnit();
            return this.next();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        if (iCurrentIterationUnit == null) {
            return moveToNextIterationUnit();
        } else {
            return iCurrentIterationUnit.hasNext() || this.hasMoreIterationUnits();
        }
    }

    public void remove() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc} The number of files in the folder in this implementation.
     */
    public int estimateSize() {
        if (iIterationUnits != null) {
            return iIterationUnits.size();
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc} The number of files left in the folder in this implementation.
     */
    public int estimateToDo() {
        if (iIterationUnits != null) {
            if (iIterationUnitIndex != iIterationUnits.size()) {
                return (iIterationUnits.size() - iIterationUnitIndex + 1);
            } else {
                return estimateSize();
            }
        } else {
            return 0;
        }
    }

    /**
     * Moves to the next file of the folder.
     *
     * @return boolean true if succesfull, false if failure.
     */
    private boolean moveToNextIterationUnit() {
        boolean result = false;
        if (iIterationUnitIndex < iIterationUnits.size()) {
            // Set the next file.
            try {

                // This iterator buffers datfiles from ms_lims!!
                String lQuery = "Select * from datfile where datfileid=?";
                PreparedStatement ps = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);
                ps.setLong(1, iIterationUnits.get(iIterationUnitIndex).getDatfileID());
                ResultSet rs = ps.executeQuery();

                // Create a datfile table accessor by the query.
                rs.next();
                Datfile lDatfileAccessor = new Datfile(rs);

                // Create a new MascotDatfile instance by the bufferedreader of the Datfile tableaccessor.
                iMascotDatfile =
                        MascotDatfileFactory.create(lDatfileAccessor.getBufferedReader(), lDatfileAccessor.getFilename(), iMascotDatfileType);

                CurrentMascotDatfile.getInstance().setCurrentMascotDatfile(iMascotDatfile, lDatfileAccessor.getDatfileid());


                /* 090110
                * This boolean indicates whether or not the Query filenames must be transformed.
                * When Mascot Distiller performs the searches out of the raw data (Parameter:Filename ends with '.raw')
                * we loose control on the filename. As such, ms_lims filenames can no longer be mapped onto id's in the mascot result files.
                * If set to true, this boolean will transform the distiller filename into a shorter sensible filename
                * as used in ms_lims.
                */


                iCurrentIterationUnit = iIterationUnits.get(iIterationUnitIndex);

                ps.close();

                // Set the current DatfileIterator to new MascotDatfile.
                System.out.println("LOG: MOVED TO " + (iIterationUnitIndex + 1) + " " + iIterationUnits.get(iIterationUnitIndex));

                // Raise the index!
                iIterationUnitIndex = iIterationUnitIndex + 1;
                // The move to next file method succeeded!
                result = true;

            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // The move to next file method did not succeed, no more files left!
            result = false;
        }
        return result;
    }

    /**
     * Returns if there are more IterationUnits left.
     *
     * @return true if there are more files, false otherwise.
     */
    private boolean hasMoreIterationUnits() {
        return iIterationUnitIndex < iIterationUnits.size();
    }

    /**
     * Returns the IterationUnit index.
     *
     * @return int count of the IterationUnits that have been indexed.
     */
    public int getFileCountIndex() {
        return iIterationUnitIndex;
    }

    /**
     * Getter for property 'mascotDatfileType'.
     *
     * @return Value for property 'mascotDatfileType'.
     */
    public MascotDatfileType getMascotDatfileType() {
        return iMascotDatfileType;
    }

    /**
     * Setter for property 'mascotDatfileType'.
     *
     * @param aMascotDatfileType Value to set for property 'mascotDatfileType'.
     */
    public void setMascotDatfileType(final MascotDatfileType aMascotDatfileType) {
        iMascotDatfileType = aMascotDatfileType;
    }


    /**
     * The connection to the ms_lims database can also be passes by arguments.
     *
     * @param aUrl
     * @param aDriver
     * @param aUser
     * @param aPassword
     * @return boolean reporting the succes of creation of the database connection.
     */
    protected boolean createConnection(final String aUrl, final String aDriver, final String aUser, final String aPassword) {
        String lErrorString = null;
        String lDriver = aDriver;
        String lUrl = aUrl;
        String lUser = aUser;
        String lPassword = aPassword;

        Connection lConnection = null;
        try {
            Driver d = (Driver) Class.forName(lDriver).newInstance();
            Properties lProps = new Properties();
            if (lUser != null) {
                lProps.put("user", lUser);
            }
            if (lPassword != null) {
                lProps.put("password", lPassword);
            }
            lConnection = d.connect(lUrl, lProps);
            if (lConnection == null) {
                lErrorString =
                        "Could not connect to the database. Either your driver is incorrect for this database, or your URL is malformed.";
            }
        } catch (ClassNotFoundException cnfe) {
            lErrorString = "Driver class was not found! (" + cnfe.getMessage() + ")";
        } catch (IllegalAccessException iae) {
            lErrorString = "Could not access default constructor on driver class! (" + iae.getMessage() + ")";
        } catch (InstantiationException ie) {
            lErrorString = "Could not create instance of driver class! (" + ie.getMessage() + ")";
        } catch (SQLException sqle) {
            lErrorString = "Database refused connection! (" + sqle.getMessage() + ")";
        }

        if (lErrorString != null) {
            // Status is 'no go'!
            System.err.println("Unable to make the connection to '" + lUrl + "' using '" + lDriver + "'!\n" + lErrorString + "\n");
            return false;
        } else {
            // All was fine, set connection and continue.
            System.out.println("Connection to '" + lUrl + "' established!");
            ConnectionManager.getInstance().setConnection(lConnection);
            return true;
        }
    }

    /**
     * Build the iteration units for an ms_lims iterator.
     *
     * @param aRs ResultSet with three columns <br><br><b>1.</b> datfileid<br><b>2.</b> identificationid<br><b>3.</b>
     *            MS/MS spectrum filename
     * @throws SQLException
     */
    protected void buildIterationUnits(final ResultSet aRs) throws SQLException {
        /**
         * This collection will hold different IterationUnit objects.
         */
        iIterationUnits = null;

        // Initialize.
        long lDatfileID = -1l;
        long lIdentificationid = -1l;
        int lQueryNumber = -1;
        MsLimsIterationUnit unit = null;

        // While more identificationid's are returning from the query ..
        while (aRs.next()) {
            // Get the values from this row.
            lDatfileID = aRs.getLong(1);
            lQueryNumber = aRs.getInt(2);
            lIdentificationid = aRs.getLong(3);

            // First row,
            if (iIterationUnits == null) {
                iIterationUnits = new ArrayList<MsLimsIterationUnit>();
                unit = new MsLimsIterationUnit(lDatfileID);
            }

            // If the datfileid of this row is different then the datfileid of the current IterationUnit,
            // then a new IterationUnit must be created.

            if (unit.getDatfileID() != lDatfileID) {
                // Store the previous unit in the instance list.
                iIterationUnits.add(unit);
                // Create a new unit.
                unit = new MsLimsIterationUnit(lDatfileID);
            }

            // Always add the the identificationid and filename of this row to the current unit.
            unit.add(lQueryNumber, lIdentificationid);

        }

        if (iIterationUnits == null) {
            // Resultset was empty!
            MatLogger.logExceptionalGUIMessage("Iteration failed.", "No peptide identifications were retrieved from the selected datasource.");
        } else {
            iIterationUnits.add(unit);
        }
        // Close fence post, Add the last unit as well!
    }
}