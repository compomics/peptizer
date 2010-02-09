package com.compomics.peptizer.interfaces;

import com.compomics.peptizer.util.PeptideIdentification;

import java.util.Iterator;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-mei-2007
 * Time: 14:33:01
 */

/**
 * Interface description: ------------------ This Interface was developed to be implemented by classes that bring
 * PeptideIdentification instances to the AgentAggregator.
 */
public interface PeptideIdentificationIterator extends Iterator {

    /**
     * Returns the next PeptideIdentification.
     *
     * @return the next PeptideIdentification. null if no more left.
     */
    public abstract Object next();

    /**
     * Returns true if more PeptideIdentifications are left.
     *
     * @return true if more PeptideIdentifications are left<br></br>false otherwise.
     */
    public abstract boolean hasNext();

    /**
     * Not implemented.
     */
    public abstract void remove();

    /**
     * Returns an <b>estimate</b> of the iteration size to measure progress of the worker.
     *
     * @return estimation of the iteration size.
     */
    public abstract int estimateSize();

    /**
     * Returns an <b>estimate</b> of the work left of the iteration to measure progress of the worker.
     *
     * @return
     */
    public abstract int estimateToDo();

    /**
     * Returns a String description of the current activity of the Iterator.
     *
     * @return String description of the Iterator.
     */
    public String getCurrentFileDescription();

    /**
     * Returns a String description of the general function of the Iterator.
     *
     * @return String description of the Iterator.
     */
    public String getGeneralDescription();

}
