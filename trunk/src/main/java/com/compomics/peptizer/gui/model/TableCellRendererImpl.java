package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TableColor;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 2-aug-2007
 * Time: 12:13:22
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class TableCellRendererImpl implements TableCellRenderer {

    // The Table makes use of 4 colors.
    // a dark and light shade for alternating rows
    // and a selected and non-selected color folowing the selection of columns.
    private TableColor iTableColor;


    /**
     * This JLabel will be dynamically modified according the content of the cell.
     */
    private JLabel lbl = null;


    public TableCellRendererImpl() {
        lbl = new JLabel();
        iTableColor = new DefaultTableColorImpl();
    }

    /**
     * Returns the component used for drawing the cell.  This method is
     * used to configure the renderer appropriately before drawing.
     *
     * @param table      the <code>JTable</code> that is asking the
     *                   renderer to draw; can be <code>null</code>
     * @param value      the value of the cell to be rendered.  It is
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
     * @param row        the row index of the cell being drawn.  When
     *                   drawing the header, the value of
     *                   <code>row</code> is -1
     * @param column     the column index of the cell being drawn
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof JLabel) {
            lbl = (JLabel) value;
            lbl.setOpaque(true);

            // 1. Set color.
            setColor(isSelected, row, column);
            // 2. Set bold
            setBold(column);
        } else {

            lbl.setOpaque(true);

            // 1. Set color.
            setColor(isSelected, row, column);
            // 2. Set bold
            setBold(column);
            // 3. Set text to JLabel
            lbl.setText(value.toString());
        }

        return lbl;
    }

    /**
     * Sets the color of the JLabel According it's selection status and rownumber.
     *
     * @param aSelected boolean on the selection.
     * @param aRow      int rownumber.
     */
    private void setColor(boolean aSelected, int aRow, int aColumn) {
        // Detail columns > 0
        if (aColumn > 0) {
            // Selection Column
            if (aSelected) {
                if (aRow % 2 == 0) {
                    // Equal Selected rows (0,2,4,6,..)
                    lbl.setBackground(iTableColor.getSelectedLight());
                } else {
                    // Unequal Selected rows (1,3,5,7,..)
                    lbl.setBackground(iTableColor.getSelectedDark());
                }
            } else {
                // Non Selected Column.
                if (aRow % 2 == 0) {
                    // Equal Non-Selected rows (0,2,4,6,..)
                    lbl.setBackground(iTableColor.getNonSelectedLight());
                } else {
                    // Unequal Non-Selected rows (1,3,5,7,..)
                    lbl.setBackground(iTableColor.getNonSelectedDark());
                }
            }
            // rowHeader column == 0
        } else {
            if (aRow % 2 == 0) {
                // Equal Selected rows (0,2,4,6,..)
                lbl.setBackground(iTableColor.getHeaderLight());
            } else {
                // Unequal Selected rows (1,3,5,7,..)
                lbl.setBackground(iTableColor.getHeaderDark());
            }
        }
    }

    void setBold(int column) {
        Font font;
        if (column == 0)
            font = new Font(lbl.getFont().getFamily(), Font.BOLD, lbl.getFont().getSize());
        else
            font = new Font(lbl.getFont().getFamily(), 0, lbl.getFont().getSize());
        lbl.setFont(font);
    } // End setBold function

    /**
     * Set the TableColor for the Table.
     *
     * @param aTableColor TableColor implementing Object.
     */
    public void setTableColor(TableColor aTableColor) {
        iTableColor = aTableColor;
    }
}
