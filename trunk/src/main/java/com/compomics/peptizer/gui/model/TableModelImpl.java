package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.model.tablerowimpl.AgentTableRowImpl;
import com.compomics.peptizer.gui.view.TableView;
import com.compomics.peptizer.util.PeptideIdentification;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:16:11
 */

/**
 * Class description:
 * ------------------
 * This class was developed to customize the TableModel of the TableView.
 */
public class TableModelImpl implements TableModel {

	/**
	 * The parent TableView.
	 */
	private TableView iTableView;

	/**
	 * This constructor takes a TableView as a single parameter.
	 *
	 * @param aTableView TableView parent.
	 */
	public TableModelImpl(TableView aTableView) {
		this.iTableView = aTableView;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getColumnCount() {
		// The number of columns equals the number of confident peptidehits
		// plus a describing column on the left.
		int lColumnCount = 1;
		if (iTableView.getTableID() != null) {
			lColumnCount = lColumnCount + iTableView.getTableID().getNumberOfConfidentPeptideHits();
		}
		return lColumnCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRowCount() {
		// The number of rows equals the number of describing parameters.
		return iTableView.getNumberOfVisibleRows();
	}

	/**
	 * {@inheritDoc}
	 * <br> <i>Always false.</i>
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return false;

	}

	/**
	 * {@inheritDoc}
	 */
	public Class getColumnClass(int columnIndex) {
		Class c = null;
		c = this.getValueAt(1, columnIndex).getClass();
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object o = null;
		// 1. Populate first describing column.
		if (columnIndex == 0) {
			o = ((AbstractTableRow) iTableView.getTableRow(rowIndex)).getName();
		}
		// 2. Populate the PeptideHit columns.
		if ((columnIndex > 0)) {
			// 2a. Get this row's AbstractTableRow.
			AbstractTableRow ar = (AbstractTableRow) iTableView.getTableRow(rowIndex);
			// 2b. Get this collumn's PeptideIdentification.
			PeptideIdentification p = (PeptideIdentification) iTableView.getTableID();
			// 2c. Set the returning value. columnindex equals the peptidehitnumber!
			o = ar.getData(p, columnIndex);
		}
		return o;
	}

	/**
	 * {@inheritDoc}
	 * <br> <i>Not implemented.</i>
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnName(int columnIndex) {
		// Handled by TableHeaderCellRendererImpl.
		return "";
	}

	/**
	 * {@inheritDoc}
	 * <br> <i>Not implemented.</i>
	 */
	public void addTableModelListener(TableModelListener l) {

	}

	/**
	 * {@inheritDoc}
	 * <br> <i>Not implemented.</i>
	 */
	public void removeTableModelListener(TableModelListener l) {

	}

	/**
	 * Returns the importance of a cell value.
	 * Returns true when an Agent returned +1 on this cell value.
	 *
	 * @param rowIndex	int row of the cell.
	 * @param columnIndex int column of the cell.
	 * @return boolean with importance on this cell value.
	 */
	public boolean isImportant(int rowIndex, int columnIndex) {
		boolean bool = false;
		if (columnIndex == 0) {
			// keep false;
		} else if ((columnIndex > 0)) {
			// 2. Populate the PeptideHit columns.
			// 2a. Get this row's AbstractTableRow.
			AbstractTableRow ar = (AbstractTableRow) iTableView.getTableRow(rowIndex);
			// Only Agent's can be important,
			if (ar instanceof AgentTableRowImpl) {
				// 2b. Get this collumn's PeptideIdentification.
				PeptideIdentification p = (PeptideIdentification) iTableView.getTableID();
				// 2c. Set the returning value. columnindex equals the peptidehitnumber!

			} else {
				// keep false;
			}
		}
		return bool;
	}
}
