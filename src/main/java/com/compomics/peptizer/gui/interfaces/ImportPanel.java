package com.compomics.peptizer.gui.interfaces;

import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-jun-2007
 * Time: 23:14:16
 */

/**
 * Interface description: ------------------ This Interface was developed to have a uniform signature for some method's
 * required by IteratorPanel's.
 */
public interface ImportPanel {

    /**
     * Loads identifications in the fileFactory which will preprocess them.
     */
    public void loadIdentifications(DefaultProgressBar progressBar);


}
