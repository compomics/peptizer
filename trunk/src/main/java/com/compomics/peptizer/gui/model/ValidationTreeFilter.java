package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.util.PeptideIdentification;
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
