package com.compomics.peptizer.util.iterators;

import org.apache.log4j.Logger;

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
}
