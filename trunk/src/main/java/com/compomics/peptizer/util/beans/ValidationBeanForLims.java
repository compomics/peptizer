package com.compomics.peptizer.util.beans;

/**
 * This bean is a container for Validation statuses to the database
 */
public class ValidationBeanForLims {
    /**
     * boolean for the status of the current identification.
     */
    private boolean iStatus;
    private String iComment;

    /**
     * ms-lims validationid
     */
    private long iValidationID;

    /**
     * ms-lims identificationid
     */
    private long iIdentificationID;

    public ValidationBeanForLims(boolean aStatus, String aComment, long aValidationID, long aIdentificationID) {
        iStatus = aStatus;
        iComment = aComment;
        iValidationID = aValidationID;
        iIdentificationID = aIdentificationID;
    }

    public boolean isValid() {
        return iStatus;
    }

    public String getComment() {
        return iComment;
    }
}
