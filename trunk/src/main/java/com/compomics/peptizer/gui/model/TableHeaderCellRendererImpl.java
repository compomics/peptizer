package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.view.TableView;
import com.compomics.peptizer.util.PeptideIdentification;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-aug-2007
 * Time: 15:47:20
 */

/**
 * Class description:
 * ------------------
 * This class was developed to allow colored Table Headers according to their Validation state.
 */
public class TableHeaderCellRendererImpl implements TableCellRenderer {

	/**
	 * The Label used as a dynamic component.
	 */
	JLabel lbl;

	/**
	 * TableView owns the PeptideIdentification instance information
	 * needed for coloring the headers.
	 */
	private TableView iTableView;

	/**
	 * Default color of the JLabel.
	 */
	private Color iDefaultBackground;

	/**
	 * This constructor takes a TableView as a single argument.
	 *
	 * @param aTableView TableView instance to be aware of the current PeptideIdentification in the Table.
	 */
	public TableHeaderCellRendererImpl(TableView aTableView) {
		lbl = new JLabel();
		lbl.setBorder(BorderFactory.createLineBorder(Color.black));
		lbl.setHorizontalAlignment(JLabel.HORIZONTAL);
		iDefaultBackground = lbl.getBackground();
		iTableView = aTableView;

	}

	/**
	 * Returns the component used for drawing the cell.  This method is
	 * used to configure the renderer appropriately before drawing.
	 *
	 * @param table	  the <code>JTable</code> that is asking the
	 *                   renderer to draw; can be <code>null</code>
	 * @param value	  the value of the cell to be rendered.  It is
	 *                   up to the specific renderer to interpret
	 *                   and draw the value.  For example, if
	 *                   <code>value</code>
	 *                   is the string "true", it could be rendered as a
	 *                   string or it could be rendered as a check
	 *                   box that is checked.  <code>null</code> is a
	 *                   valid value
	 * @param isSelected true if the cell is to be rendered with the
	 *                   selection highlighted; otherwise false
	 * @param hasFocus   if true, render cell appropriately.  For
	 *                   example, put a special border on the cell, if
	 *                   the cell can be edited, render in the color used
	 *                   to indicate editing
	 * @param row		the row index of the cell being drawn.  When
	 *                   drawing the header, the value of
	 *                   <code>row</code> is -1
	 * @param column	 the column index of the cell being drawn
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (column > 0) {
			PeptideIdentification lPeptideIdentification = iTableView.getTableID();
			if (!lPeptideIdentification.isValidated()) {
				// A. If nothing is validated, stay with the defaults.
				setDefault();
			} else if (lPeptideIdentification.getValidationReport().getResult()) {
				// B. Identification is accepted, get the correct peptidehitnumber.
				if (lPeptideIdentification.getValidationReport().getCorrectPeptideHitNumber() == column) {
					// B.1.This column is the accepted PeptideHit!
					markAccept();
				} else {
					// B.2.This column is the indirect false PeptideHit!
					markIndirectReject();
				}
			} else {
				// C. Identification is rejected.
				markReject();
			}
		} else if (column == 0) {
			// Column with RowHeaders!
			setDefault();
			lbl.setText("Property");
			setBold(true);
		}

		// All options have been iterated and this instance has been modified according to it's content.
		// Now return,
		return lbl;
	}

	/**
	 * Mark the Label as rejected.
	 */
	private void markReject() {
		lbl.setForeground(Color.red);
		lbl.setText("Rejected");
		setBold(true);
	}

	/**
	 * Mark the Label as indirect rejected. Meaning that another peptideHit was accepted for this spectrum.
	 */
	private void markIndirectReject() {
		lbl.setForeground(Color.gray);
		lbl.setText("Rejected");
		setBold(true);
	}

	/**
	 * Mark the Label as accepted.
	 */
	private void markAccept() {
		lbl.setForeground(new Color(75, 175, 0));
		lbl.setText("Accepted");
		setBold(true);
	}

	/**
	 * Reset the DefaultBackground of the Label.
	 */
	private void setDefault() {
		lbl.setForeground(Color.black);
		lbl.setText("");
		setBold(false);
	}

	/**
	 * Set the bold type of the label.
	 *
	 * @param bold boolean.
	 */
	void setBold(boolean bold) {
		Font font;
		if (bold)
			font = new Font(lbl.getFont().getFamily(), Font.BOLD, lbl.getFont().getSize());
		else
			font = new Font(lbl.getFont().getFamily(), 0, lbl.getFont().getSize());
		lbl.setFont(font);
	}
}