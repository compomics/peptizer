package com.compomics.peptizer.util.iterators;

import com.compomics.mslims.db.accessors.Validation;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Interanal class that holds a datfileid, the identification id's and their filenames within that datfile. Implements
 * the Iterator interface.
 */
public class MsLimsIterationUnit implements Iterator {
    // Class specific log4j logger for MsLimsIterationUnit instances.
    private static Logger logger = Logger.getLogger(MsLimsIterationUnit.class);
    // The Datfile identifier of this unit.
    private long iDatfileID;
    // A map of identificationids and corresponding filenames that must be found in this datfile.
    private HashMap<Integer, Long> iQueryNumberToIDMap;

    // A map of identificationids and corresponding validation reports that can be found for the ids.
    private HashMap<Long, Validation> iIdentificationToStatusMap;

    private Iterator iter = null;

    /**
     * Construct a new IterationUnit.
     *
     * @param aDatfileID Datfile identifier for this unit.
     */
    public MsLimsIterationUnit(final long aDatfileID) {
        iDatfileID = aDatfileID;
        iQueryNumberToIDMap = new HashMap<Integer, Long>();
    }

    /**
     * Returns the datfile identifier of this IterationUnit.
     *
     * @return long Datfile identifier.
     */
    public long getDatfileID() {
        return iDatfileID;
    }

    /**
     * Adds a identificationid and filename link to this IterationUnit's map.
     *
     * @param aQueryNumber
     * @param aIdentificationId
     */
    public void add(int aQueryNumber, Long aIdentificationId) {
        iQueryNumberToIDMap.put(aQueryNumber, aIdentificationId);
    }

    /**
     * Returns the identificationid of the given filename.
     */
    public Long getIdentificationId(int aQueryNumber) {
        return iQueryNumberToIDMap.get(aQueryNumber);
    }

    /**
     * Returns the number of identification ids left for this Mascot results file.
     *
     * @return
     */
    public int getNumberOfItems() {
        return iQueryNumberToIDMap.size();
    }

    public boolean hasNext() {
        if (iter == null) {
            iter = iQueryNumberToIDMap.keySet().iterator();
        }
        return iter.hasNext();
    }

    public Integer next() {
        return (Integer) iter.next();
    }

    public void remove() {
        // empty
    }

    public String toString() {
        return "IterationUnit{" +
                "iDatfileID=" + iDatfileID +
                '}';
    }

    /**
     * This method returns a ValidationBean instance for the specified identification id.
     *
     * @param aIdentificationID for the required validation. null if missing.
     * @return
     */
    public Validation getValidationBean(long aIdentificationID) {
        if (iIdentificationToStatusMap == null) {
            // lazy caching.
            iIdentificationToStatusMap = new HashMap<Long, Validation>();
            try {
                // Get all the identification ids.
                Collection<Long> lIdentificationIDs = iQueryNumberToIDMap.values();
                Iterator<Long> lIterator = lIdentificationIDs.iterator();

                // Build the query.
                StringBuilder sb = new StringBuilder();


                while (lIterator.hasNext()) {
                    Long lLong = lIterator.next();
                    sb.append("'");
                    sb.append(lLong);
                    sb.append("',");
                }
                String ids = sb.toString();
                if (ids.length() > 0) {
                    // close fence post, remove the trailing ','.
                    ids = ids.substring(0, ids.length() - 1);
                }

                String lQuery =
                        "Select * from VALIDATION where l_identificationid in (" + ids + ")";

                PreparedStatement ps = null;
                ps = ConnectionManager.getInstance().getConnection().prepareStatement(lQuery);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Validation lValidation = new Validation(rs);
                    iIdentificationToStatusMap.put(lValidation.getL_identificationid(), lValidation);
                }
                // Ok, now persist the resultset into beans.
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return iIdentificationToStatusMap.get(aIdentificationID);
    }
}
