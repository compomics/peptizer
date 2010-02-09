package com.compomics.peptizer.gui.interfaces;

import com.compomics.peptizer.util.PeptideIdentification;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 30-okt-2007
 * Time: 10:05:58
 */

/**
 * Interface description:
 * ------------------
 * This Interface was developed to be implemented by classes that can filter PeptideIdentifications in the TreeView.
 */
public interface TreeFilter {
	/**
	 * Returns a boolean whether a peptideidentification may pass a filter.
	 *
	 * @param aPeptideIdentification PeptideIdentification
	 * @return boolean with status
	 */
	public boolean pass(PeptideIdentification aPeptideIdentification);
}
