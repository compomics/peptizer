package com.compomics.peptizer.util;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 10:31:32
 */

/**
 * Class description:
 * ------------------
 * This class was developed to save the results of an Agent in a PeptideIdentification instance.
 * The class extends a Hashmap.
 * The AgentReport has defines static final ReportKeys ("RK_*") that serve as keys in this map.
 */
public class ValidationReport implements Serializable {
    // Class specific log4j logger for ValidationReport instances.
    private static Logger logger = Logger.getLogger(ValidationReport.class);

    /**
     * Report on the validation status.<br>
     * False indicates that the identification has not been validated yet. Otherwise true.
     */
    private boolean iValidated = false;

    /**
     * Report on the validation.<br>
     * <b>True</b> indicates that the identification was judged correct and therefor accepted.
     * <b>False</b> indicates that the identification was judged incorrect and therefor not rejected.<br>
     */
    private boolean iResult = false;

    /**
     * The PeptideHitNumber that is judged as correct. Needs to be set!
     * If no correct hit, leave at -1.
     */
    private int iCorrectPeptideHitNumber = -1;

    /**
     * Default the comment
     */
    public static final String DEFAULT_COMMENT = "NA";

    /**
     * Automatically generated comment on the validation.
     */
    private String iAutoComment = DEFAULT_COMMENT;

    /**
     * User comment on the validation.
     */
    private String iUserComment = null;

    /**
     * Constructs an empty <tt>ValidationReport</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public ValidationReport() {
    }


    /**
     * Returns status of validation.
     *
     * @return status of validation.
     */
    public boolean isValidated() {
        return iValidated;
    }

    /**
     * Sets status of validation.
     * Private method only used when validity is set.
     *
     * @param aValidated Class .
     */
    private void setValidated(boolean aValidated) {
        iValidated = aValidated;
    }


    /**
     * Returns result boolean whether identification is accepted or rejected.
     * <b>True</b> indicates that the identification was judged correct and therefor accepted.
     * <b>False</b> indicates that the identification was judged incorrect and therefor not rejected.<br>
     *
     * @return boolean whether identification was accepted or rejected.
     */
    public boolean getResult() {
        return iResult;
    }

    /**
     * Sets result boolean whether identification is accepted or rejected.
     * <b>True</b> indicates that the identification was judged correct and therefor accepted.
     * <b>False</b> indicates that the identification was judged incorrect and therefor not rejected.<br>
     *
     * @param aResult boolean whether identification is accepted or rejected.
     */
    public void setResult(boolean aResult) {
        iResult = aResult;
        setValidated(true);
    }

    /**
     * Returns automated comment on validation.
     *
     * @return comment on validation.
     */
    public String getAutoComment() {
        return iAutoComment;
    }

    /**
     * Sets automated comment on validation.
     *
     * @param aAutoComment String on validation.
     */
    public void setAutoComment(String aAutoComment) {
        iAutoComment = aAutoComment;
    }

    /**
     * Returns user specified comment on validation.
     *
     * @return comment on validation. null if unspecified.
     */
    public String getUserComment() {
        return iUserComment;
    }

    /**
     * Sets user specified comment on validation.
     *
     * @param aUserComment String on validation.
     */
    public void setUserComment(String aUserComment) {
        iUserComment = aUserComment;
    }

    /**
     * Returns correctPeptideHitNumber .
     * Where '1' returns the first rank peptidehit.
     *
     * @return correctPeptideHitNumber.
     */
    public int getCorrectPeptideHitNumber() {
        return iCorrectPeptideHitNumber;
    }

    /**
     * Sets correctPeptideHitNumber. Where '1' marks the first rank peptidehit.
     *
     * @param aCorrectPeptideHitNumber Class .
     */
    public void setCorrectPeptideHitNumber(int aCorrectPeptideHitNumber) {
        iCorrectPeptideHitNumber = aCorrectPeptideHitNumber;
    }

    /**
     * Resets the information in the validationreport.
     * Validation status to false and discard the comment.
     */
    public void reset() {
        setResult(false);
        // Mind that when the validation is false, the result cannot be accessed!
        setValidated(false);
        setAutoComment("NA");
        setUserComment(null);
    }
}
