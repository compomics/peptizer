package com.compomics.peptizer.gui.view;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.model.*;
import com.compomics.peptizer.util.PeptideIdentification;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:06:15
 */

/**
 * Class description:
 * ------------------
 * This class is the Table View-Controller.
 * It extends a JTable which is a Swing JComponent.
 * A fixed set of AbstractTableRows extract information of a variable PeptideIdentification.
 * The PeptideIdentification is controlled by the Tree.
 */
public class TableView extends JTable {

    /**
     * The parent super-controller.
     */
    private Mediator iMediator;

    /**
     * The active PeptideIdentification that populates the table.
     */
    private PeptideIdentification iPeptideIdentification;

    /**
     * The manager of the tablerows.
     */
    private TableRowManager iTableRowManager;


    /**
     * This constructor takes the parent Mediator as a single argument.
     *
     * @param aMediator Super controller.
     */

    public TableView(Mediator aMediator) {

        // JTable empty constructor.
        super();
        this.iMediator = aMediator;
        iTableRowManager = new TableRowManager(aMediator.getActiveAgents());
        // Set a new custom TableModel.
        TableModel lTableModel = new TableModelImpl(this);
        this.setDefaultRenderer(Object.class, new TableCellRendererImpl());
        this.setModel(lTableModel);

        // Set columnselection for the table.
        this.setColumnSelectionAllowed(true);
        this.setRowSelectionAllowed(false);
        this.getTableHeader().setResizingAllowed(true);
        this.getTableHeader().setDefaultRenderer(new TableHeaderCellRendererImpl(this));

        this.validate();
        doOptimalHeaderResize();
    }


    /**
     * Does optimal resize for the Row Header column.
     */
    private void doOptimalHeaderResize() {

        TableColumn lColumn = this.getColumnModel().getColumn(0);
        int lSpacer = 10;

        int lRowCount = this.getRowCount();
        int lMaxLength = 0;
        int lMaxRow = -1;
        for (int i = 0; i < lRowCount; i++) {
            int lRowLength = ((String) this.getValueAt(i, 0)).length();
            if (lMaxLength < lRowLength) {
                lMaxLength = lRowLength;
                lMaxRow = i;
            }
        }
        int lColumnWidth;
        lColumnWidth = this.getFontMetrics(this.getFont()).stringWidth((String) this.getValueAt(lMaxRow, 0));

        lColumn.setMinWidth(lColumnWidth + 30);
        lColumn.setMaxWidth(lColumnWidth + 50);

    }


    /**
     * Prepares the renderer by querying the data model for the
     * value and selection state
     * of the cell at <code>row</code>, <code>column</code>.
     * Returns the component (may be a <code>Component</code>
     * or a <code>JComponent</code>) under the event location.
     * <p/>
     * <b>Note:</b>
     * Throughout the table package, the internal implementations always
     * use this method to prepare renderers so that this default behavior
     * can be safely overridden by a subclass.
     *
     * @param renderer the <code>TableCellRenderer</code> to prepare
     * @param row      the row of the cell to render, where 0 is the first row
     * @param column   the column of the cell to render,
     *                 where 0 is the first column
     * @return the <code>Component</code> under the event location
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (c instanceof JComponent) {
            ((JComponent) c).setToolTipText(getTableRow(row).getDescription());
        }

        return c;
    }


    /**
     * Sets the PeptideIdentification of the Table.
     *
     * @param aPeptideIdentification PeptideIdentification of the Table. null clears the table.
     */
    public void setTableID(PeptideIdentification aPeptideIdentification) {
        iPeptideIdentification = aPeptideIdentification;

        // Valid PeptideIdentification,
        if (iPeptideIdentification != null) {
            if (this.columnModel.getColumnCount() > 1) {
                int lColumnCount = columnModel.getColumnCount();
                for (int i = 1; i < lColumnCount; i++) {
                    this.columnModel.removeColumn(columnModel.getColumn(lColumnCount - i));
                }
            }
            int lNumberOfConfidentPeptideHits = iPeptideIdentification.getNumberOfConfidentPeptideHits();
            if (lNumberOfConfidentPeptideHits == 1) {
                this.columnModel.addColumn(new TableColumn(1));

            } else if (lNumberOfConfidentPeptideHits > 1) {
                for (int i = 0; i < lNumberOfConfidentPeptideHits; i++) {
                    this.columnModel.addColumn(new TableColumn(i + 1));
                }
            }
            // If Identification is already validated, put the selection on the validated peptidehit.
            if (iPeptideIdentification.isValidated()) {
                int lIndex = iPeptideIdentification.getValidationReport().getCorrectPeptideHitNumber();
                if (lIndex != -1) {
                    this.setColumnSelectionInterval(lIndex, lIndex);
                } else {
                    this.setColumnSelectionInterval(1, 1);
                }
                iMediator.columnSelected();
            } else {
                // Else, select the first peptidehit.
                this.setColumnSelectionInterval(1, 1);
            }

        } else {
            // null argument, table has to be cleared. Remove all colums from the datamodel except the Row Headers (first column).
            if (this.columnModel.getColumnCount() > 1) {
                int lColumnCount = columnModel.getColumnCount();
                for (int i = 1; i < lColumnCount; i++) {
                    this.columnModel.removeColumn(columnModel.getColumn(lColumnCount - i));
                }
            }
        }
    }

    /**
     * Returns the PeptideIdentification of the Table.
     *
     * @return The identifications of the Table.
     */
    public PeptideIdentification getTableID() {
        return iPeptideIdentification;
    }

    /**
     * Returns the number of visible rows.
     *
     * @return int Number of visible rows.
     */
    public int getNumberOfVisibleRows() {
        return iTableRowManager.getNumberOfVisibleRows();
    }

    /**
     * Returns the row at aRowIndex.
     *
     * @param aRowIndex index of the TableRow.
     * @return The AbstractTableRow at aRowIndex.
     */
    public AbstractTableRow getTableRow(int aRowIndex) {
        return iTableRowManager.getTableRow(aRowIndex);
	}
}