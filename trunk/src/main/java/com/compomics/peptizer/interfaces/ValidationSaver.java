package com.compomics.peptizer.interfaces;

import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.util.sun.SwingWorker;
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
public abstract class ValidationSaver extends SwingWorker {

    /**
     * Object that will be saved.
     * This is prefered to be a SelectedPeptideIdentifications or ArrayList instance.
     */
    protected Object iData = null;

    /**
     * Optional progressbar of the ValidationSaver.
     */
    protected DefaultProgressBar iProgress = null;

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
     * Set the visible status of the Progressbar.
     *
     * @param aVisible
     */
    public void setProgressVisible(boolean aVisible) {
        if (iProgress != null) {
            iProgress.setVisible(aVisible);
        }
    }

}
