package com.compomics.peptizer.gui.interfaces;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
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
public interface IteratorPanel {

    /**
     * Returns a PeptideIdentification Iterator.
     *
     * @return Iterator PeptideIdentificationIterator of the IteratorPanel. If all parameters are set correctly, otherwise
     *         returns null.
     */
    public PeptideIdentificationIterator getIterator();


}
