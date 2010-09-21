/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jun 29, 2009
 * Time: 2:46:52 PM

 This class
 */
package com.compomics.peptizer.util.iterators;

import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import org.apache.log4j.Logger;

public class CurrentMascotDatfile {
	// Class specific log4j logger for CurrentMascotDatfile instances.
	 private static Logger logger = Logger.getLogger(CurrentMascotDatfile.class);
    /**
     * The current MascotDatfile instance.
     */
    private static CurrentMascotDatfile ourInstance = new CurrentMascotDatfile();
    private MascotDatfileInf iMascotDatfile = null;
    /**
     * This Object identifies the current MascotDatfile instance.
     */
    private Object iIdentifier;

    public static CurrentMascotDatfile getInstance() {
        return ourInstance;
    }

    private CurrentMascotDatfile() {
        // Empty.
    }

    /**
     * Security check.
     *
     * @param aIdentifier for the MascotDatfile.
     * @return If the identifier does not match, then return null. Else return the 'current' Datfile.
     */
    public MascotDatfileInf getCurrentMascotDatfile(Object aIdentifier) {
        if (aIdentifier.equals(iIdentifier)) {
            return iMascotDatfile;
        } else {
            throw new IllegalArgumentException("The argument identifier '" + aIdentifier + "' does not match " +
                    "the CurrentMascotDatfile Identifier '" + iIdentifier + "'!!");
        }
    }

    /**
     * Set the current MascotDatfile instance.
     *
     * @param aMascotDatfile MascotDatfile that is being iterated at the moment.
     * @param aIdentifier    Object to identify current datfile.
     *                       If HDD, the filename is used.
     *                       If ms_lims, the datfile_id is used. Note the datfile_id is MetaInformation on a PeptideIdentification.
     */
    public void setCurrentMascotDatfile(MascotDatfileInf aMascotDatfile, Object aIdentifier) {
        iMascotDatfile = aMascotDatfile;
        iIdentifier = aIdentifier;
    }
}
