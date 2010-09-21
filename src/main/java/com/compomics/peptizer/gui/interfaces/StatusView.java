package com.compomics.peptizer.gui.interfaces;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2008/03/19 16:32:18 $
 */

/**
 * This interface describes the behaviour for any view capabale of presenting a status,
 * status being defined as a true status and an error message.
 *
 * @author Lennart Martens
 */
public interface StatusView {

    /**
     * This method allows the caller to specify the status message
     * that is being displayed.
     *
     * @param aStatus String with the desired status message.
     */
    public abstract void setStatus(String aStatus);

    /**
     * This method allows the caller to specify the error message
     * that is being displayed.
     *
     * @param aError String with the desired error message.
     */
    public abstract void setError(String aError);
}
