package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 30-okt-2007
 * Time: 10:09:46
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class ValidationTreeFilter implements TreeFilter {
	// Class specific log4j logger for ValidationTreeFilter instances.
	 private static Logger logger = Logger.getLogger(ValidationTreeFilter.class);
    /**
     * Returns a boolean whether a peptideidentification is allowed to pass the filter.
     *
     * @param aPeptideIdentification PeptideIdentification
     * @return boolean with status
     */
    public boolean pass(PeptideIdentification aPeptideIdentification) {
        return !aPeptideIdentification.getValidationReport().isValidated();
    }
}
