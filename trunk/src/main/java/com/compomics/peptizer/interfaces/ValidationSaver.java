package com.compomics.peptizer.interfaces;

import org.apache.log4j.Logger;

import java.util.Observer;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 15:10:32
 */

/**
 * Interface description:
 * ------------------
 * This Interface was developed to be implemented by IO objects that can save a group of PeptideIdentifications.
 */
public abstract class ValidationSaver implements Runnable {
	// Class specific log4j logger for ValidationSaver instances.
	 private static Logger logger = Logger.getLogger(ValidationSaver.class);

    /**
     * Object that will be saved.
     * This is prefered to be a SelectedPeptideIdentifications or ArrayList instance.
     */
    protected Object iData = null;

    /**
     * allow one object to observe the saver.
     */
    public Observer iObserver = null;


    /**
     * Set the Data that must be written to the csv.
     * This should be an ArrayList with PeptideIdentifications or a SelectedPeptideidentifications instance.
     *
     * @param aData Object that contains peptideidentifications.
     */
    public void setData(Object aData) {
        iData = aData;
    }

    /**
     * Finish open connections and streams of the ValidationSaver.
     */
    public abstract void finish();

    /**
     * Set one observer to the Saver instance.
     * @param aObserver
     */
    public void setObserver(Observer aObserver) {
        iObserver = aObserver;
    }
}
