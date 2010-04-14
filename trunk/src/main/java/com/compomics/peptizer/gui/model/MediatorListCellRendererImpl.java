package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;

import javax.swing.*;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 6-aug-2007
 * Time: 14:58:29
 */

/**
 * Class description:
 * ------------------
 * This class was developed for Rendering Mediators in a List. (ex. List distinct Mediators in a ComboBox)
 */
public class MediatorListCellRendererImpl extends DefaultListCellRenderer {

    private JLabel lbl = null;
    private PeptizerGUI iPeptizerGUI = null;

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     * @see javax.swing.JList
     * @see javax.swing.ListSelectionModel
     * @see javax.swing.ListModel
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        int lTaskTabIndex = iPeptizerGUI.getSelectedTabIndex();
        for (int i = 0; i < iPeptizerGUI.getNumberOfTabs(); i++) {
            if (iPeptizerGUI.getTabs()[i] == value) {
                lTaskTabIndex = i;
                break;
            }
        }

        int lNumberOfSpectra = ((Mediator) value).getSelectedPeptideIdentifications().getNumberOfSpectra();
        int lNumberOfValidatedSpectra = ((Mediator) value).getSelectedPeptideIdentifications().getNumberOfValidatedSpectra();


        StringBuffer result = new StringBuffer();
        result.append("Task " + (lTaskTabIndex + 1) + " : ");
        result.append(lNumberOfValidatedSpectra).append(" out of ").append(+lNumberOfSpectra).append(" validated (").append(lNumberOfValidatedSpectra * 100 / lNumberOfSpectra).append("%).");
        lbl.setText(result.toString());

        return lbl;
    }


    /**
     * This constructor takes PeptizerGUI as a single argument.
     * The tab numbering of PeptizerGUI is used for representing the Mediators in the combobox.
     */
    public MediatorListCellRendererImpl(PeptizerGUI aPeptizerGui) {
        super();
        iPeptizerGUI = aPeptizerGui;
    }
}
