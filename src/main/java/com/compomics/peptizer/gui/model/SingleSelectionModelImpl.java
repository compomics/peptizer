package com.compomics.peptizer.gui.model;


import com.compomics.peptizer.gui.view.TabbedView;

import javax.swing.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:12:15
 */

/**
 * Class description:
 * ------------------
 * This class was developed to customize the SingleSelectionModel of the TabbedView.
 */
public class SingleSelectionModelImpl extends DefaultSingleSelectionModel {

	/**
	 * The parent TabbedView.
	 */
	TabbedView iTabbedView;

	/**
	 * This constructor takes a TabbedView instance as a single argument.
	 *
	 * @param aTabbedView TabbedView parent.
	 */
	public SingleSelectionModelImpl(TabbedView aTabbedView) {
		super();
		iTabbedView = aTabbedView;
	}

	private int index = -1;

	// implements javax.swing.SingleSelectionModel
	public int getSelectedIndex() {
		return index;
	}

	// implements javax.swing.SingleSelectionModel
	public void setSelectedIndex(int index) {
		if (this.index != index) {
			this.index = index;
			fireStateChanged();
		}
		// Index is set, notion mediator!
		// Needed a SingleSelectionModelImpl to update a selectionChange at all times.
		// If a Tab is removed and the index remains identical, the Table needs to be updated nevertheles!!
		iTabbedView.selectionChanged(this.index);
	}

	// implements javax.swing.SingleSelectionModel
	public void clearSelection() {
		setSelectedIndex(-1);
	}

	// implements javax.swing.SingleSelectionModel
	public boolean isSelected() {
		boolean ret = false;
		if (getSelectedIndex() != -1) {
			ret = true;
		}
		return ret;
	}
}
