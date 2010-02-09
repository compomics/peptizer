package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.dialog.SaveValidationDialog;
import com.compomics.peptizer.gui.interfaces.SaveValidationPanel;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.compomics.peptizer.util.fileio.FileManager;
import com.compomics.peptizer.util.fileio.TempManager;
import com.compomics.peptizer.util.fileio.ValidationSaveToCSV;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 22:35:59
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class SaveValidationPanel_CSV extends JPanel implements SaveValidationPanel {

    /**
     * The Singleton instance of this Panel.
     */
    private static SaveValidationPanel_CSV iSingleton = null;

    /**
     * The static instance of the parent SaveValidationDialog.
     */
    private static SaveValidationDialog iDialog;

    /**
     * The HashMap couples the TableRow ID's with a boolean corresponding if they will be used in the csv output.
     * Keys: AbstractTableRows
     * Values: Boolean (csv output inclusive - True or False)
     */
    private static HashMap iTableRows = null;


    /**
     * The Mediator used by the Model.
     */
    private static Mediator iMediator = null;
    private JTextField txtCSV = null;
    private JButton btnCSV = null;
    private JCheckBox chkComment = null;
    private static JCheckBox chkConfident = null;
    private static JCheckBox chkNonConfident = null;
    private JCheckBox chkNonPrimary = null;

    private static JTable iTable = null;
    private File iCSV = null;


    /**
     * Returns the Singleton instance of SaveValidationPanel_CSV.
     *
     * @param aDialog
     */
    private SaveValidationPanel_CSV(SaveValidationDialog aDialog) {
        // Super constructor.
        super();

        // Set the super dialog.
        iDialog = aDialog;

        // Make sure the Table is using the correct Mediator.
        // If the combobox changes it's selection, make sure necesairy actions are performed!
        ActionListener listener = new MyActionListener();
        iDialog.addComboBoxListener(listener);

        // Get Selected Mediator.
        iMediator = iDialog.getSelectedMediator();
        // Construct JPanel
        construct();

        // Try to load parameters.
        String s = null;
        if ((s = MatConfig.getInstance().getGeneralProperty("SAVEVALIDATION_CSV")) != null) {
            setCSV(new File(s));
        }
    }

    /**
     * {@inheritDoc}
     */
    public static SaveValidationPanel_CSV getInstance(SaveValidationDialog aDialog) {

        if (iSingleton == null) {
            iSingleton = new SaveValidationPanel_CSV(aDialog);
        } else {
            // Bizar singleton construction, i know. :)
            // This singleton panel must maintain a coupling to the JDialog to follow the Mediator Combobox.
            // If a Save dialog is launched a second time, this Panel has a pointer to the old JDialog.
            // Therefor, fetch the ActionListener on the ComboBox from the Old Dialog, and place them on the new Dialog & reset the Mediator.
            ActionListener[] lActionListeners = iDialog.getComboBoxListeners();
            for (ActionListener lActionListener : lActionListeners) {
                if (lActionListener instanceof MyActionListener) {
                    aDialog.addComboBoxListener(lActionListener);
                }
            }
            iDialog = aDialog;
            iMediator = iDialog.getSelectedMediator();
            rebuildOutput();
        }
        return iSingleton;
    }

    /**
     * Construct this SaveValidationPanel_CSV instance.
     */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select a target to save the validation.");

        // Components initiation

        // JTextField
        txtCSV = new JTextField();
        txtCSV.setFont(txtCSV.getFont().deriveFont(11.0F));
        txtCSV.setBorder(BorderFactory.createEmptyBorder());
        txtCSV.setEditable(false);
        txtCSV.setText("/");

        // JButton
        btnCSV = new JButton();
        btnCSV.setText("Browse");
        btnCSV.setMnemonic(KeyEvent.VK_B);
        btnCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                csvSelection();
            }
        });

        // Checkbox to include non-confident peptideidentifciations.
        chkComment = new JCheckBox("Include validation comments");
        chkComment.setSelected(true);

        // Checkbox to include confident not-matched peptideidentifciations.
        chkConfident = new JCheckBox("Include confident identifications that did not match the profile");
        chkConfident.setSelected(false);
        chkConfident.setEnabled(false);

        // Checkbox to include non-confident peptideidentifciations.
        chkNonConfident = new JCheckBox("Include non confident identifications");
        chkNonConfident.setSelected(false);
        chkNonConfident.setEnabled(false);

        // Checkbox to include non primary rank confident hits
        chkNonPrimary = new JCheckBox("Include non primary ranked hits");
        chkNonPrimary.setSelected(false);

        updateSelectionBox();

        // Table.

        iTable = new JTable();
        iTable.setModel(new CSVOutputTableModel());
        iTable.setRowHeight(20);
        iTable.setCellSelectionEnabled(false);
        iTable.setRowSelectionAllowed(true);
        iTable.setColumnSelectionAllowed(false);
        // This enable's the JScrollpane again for some reason!
        iTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        iTable.setDefaultRenderer(String.class, new CSVOutputTableCellRenderer());

        // Put target on the Top panel.
        JPanel jpanMiddle = new JPanel();
        jpanMiddle.setLayout(new BoxLayout(jpanMiddle, BoxLayout.PAGE_AXIS));
        jpanMiddle.add(chkComment);
        jpanMiddle.add(Box.createHorizontalStrut(10));
        jpanMiddle.add(chkConfident);
        jpanMiddle.add(Box.createHorizontalStrut(10));
        jpanMiddle.add(chkNonConfident);
        jpanMiddle.add(Box.createHorizontalStrut(10));
        jpanMiddle.add(chkNonPrimary);
        jpanMiddle.add(Box.createHorizontalGlue());
        jpanMiddle.setBorder(BorderFactory.createTitledBorder("Options"));

        // Put target on the Top panel.
        JPanel jpanBottom = new JPanel();
        jpanBottom.setLayout(new BoxLayout(jpanBottom, BoxLayout.LINE_AXIS));
        jpanBottom.add(txtCSV);
        jpanBottom.add(Box.createHorizontalStrut(10));
        jpanBottom.add(btnCSV);
        jpanBottom.add(Box.createHorizontalGlue());
        jpanBottom.setBorder(BorderFactory.createTitledBorder("Target"));

        JScrollPane scroll1 = new JScrollPane(iTable);
        scroll1.setBorder(BorderFactory.createTitledBorder("Content"));


        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.PAGE_AXIS));
        jpanMain.add(scroll1);
        jpanMain.add(Box.createRigidArea(new Dimension(iTable.getSize().width, 20)));
        jpanMain.add(jpanMiddle);
        jpanMain.add(Box.createRigidArea(new Dimension(iTable.getSize().width, 20)));
        jpanMain.add(jpanBottom);

        this.add(jpanMain);
        this.validate();

    }

    /**
     * Select CSV file.
     */
    private void csvSelection() {
        if (FileManager.getInstance().selectTXTOutput(this)) {
            iCSV = (FileManager.getInstance().getTXTOutput());
            try {
                txtCSV.setText(iCSV.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns an instance to save selected identifications and validation.
     *
     * @return ValidationSaver to save validation of selected identifications.
     */
    public ValidationSaver getValidationSaver() {
        if (iCSV != null) {
            ArrayList lOutputRows = new ArrayList();
            for (Object o : iTableRows.keySet()) {
                AbstractTableRow row = (AbstractTableRow) o;
                if ((Boolean) iTableRows.get(row)) {
                    lOutputRows.add(row);
                }
            }
            DefaultProgressBar lProgress = new DefaultProgressBar((JFrame) SwingUtilities.getRoot(iMediator), "Writing csv results file into " + iCSV + " .", 0, 1);
            ValidationSaveToCSV lValidationSaver = new ValidationSaveToCSV(iCSV, lOutputRows, lProgress);
            if (lValidationSaver instanceof ValidationSaveToCSV) {
                ((ValidationSaveToCSV) lValidationSaver).setComments(chkComment.isSelected());
            }
            lValidationSaver.setIncludeConfidentNotSelected(chkConfident.isSelected());
            lValidationSaver.setIncludeNonConfident(chkNonConfident.isSelected());
            lValidationSaver.setIncludeNonPrimary(chkNonPrimary.isSelected());


            return lValidationSaver;
        } else {
            JOptionPane.showMessageDialog(this.getParent(), "A csv file must be selected first!!", "Validation saver to CSV failed..", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Set the file we want to save our validation.
     *
     * @param aCSV File to save our results..
     */
    public void setCSV(File aCSV) {
        iCSV = aCSV;
        try {
            txtCSV.setText(iCSV.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String toString() {
        return "Save to CSV";
    }

    /**
     * If the Mediator changes in selection, the table can change as well. In that case, rebuild the iOutput HashMap.
     * Save the properties that were allready set!
     */
    private static void rebuildOutput() {
        // This will be the new output map.
        HashMap lProperties = new HashMap();

        int lNumberOfRows = iTable.getRowCount();
        for (int i = 0; i < lNumberOfRows; i++) {
            // Localise the tablerow.
            AbstractTableRow lRow = iMediator.getTableRow(i);

            // If the current Map allready contains a boolean for the tablerow, re-use it!
            if (iTableRows.get(lRow) != null) {
                Boolean aValue = (Boolean) iTableRows.get(lRow);
                lProperties.put(lRow, aValue);
            } else {
                // Else place it's activity status.
                lProperties.put(lRow, lRow.isActive());
            }
        }
        // Update the selection boxes.
        updateSelectionBox();

        // Set the field iTablerows to the reconstructed lProperties.
        iTableRows = lProperties;
    }

    private static void updateSelectionBox() {
        TempManager lTempFileManager = TempManager.getInstance();

        boolean hasObjectStream_good = lTempFileManager.getNumberOfFiles(iMediator.getSelectedPeptideIdentifications(), TempFileEnum.CONFIDENT_NOT_SELECTED) > 0;
        boolean hasObjectStream_bad = lTempFileManager.getNumberOfFiles(iMediator.getSelectedPeptideIdentifications(), TempFileEnum.NON_CONFIDENT) > 0;

        if (hasObjectStream_good) {
            chkConfident.setEnabled(true);
            chkConfident.setSelected(true);
        } else {
            chkConfident.setEnabled(false);
            chkConfident.setSelected(false);
        }

        if (hasObjectStream_bad) {
            chkNonConfident.setEnabled(true);
            chkNonConfident.setSelected(false);
        } else {
            chkNonConfident.setEnabled(false);
            chkNonConfident.setSelected(false);
        }
    }

    /**
     * The ActionListener is place on the combobox of the parent SaveValidationDialog.
     * Whenever it's selection is changed, make sure the Mediator used by the TableModel follows the selection by this Listener!
     */
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            iMediator = iDialog.getSelectedMediator();
            rebuildOutput();
            iTable.validate();
            iTable.updateUI();
            iTable.repaint();
        }
    }

    /**
     * This TableModel serves a dynamic selection of the CSV output.
     * The model accesses a Mediator and finds all visible AbstractTableRows that the user originally could see in the GUI.
     * These rows are now listed with a boolean whether to use this row for output or not.
     */
    private class CSVOutputTableModel implements TableModel {
        /**
         * The TableModel for a dynamic CSV output of the Mediator results.
         * This Model reflects the Mediator's Table.
         */
        public CSVOutputTableModel() {
            // Create the output map.
            int lNumberOfRows = getRowCount();
            iTableRows = new HashMap();
            for (int i = 0; i < lNumberOfRows; i++) {
                Object aKey = iMediator.getTableRow(i);
                Boolean aValue = iMediator.getTableRow(i).isActive();
                iTableRows.put(aKey, aValue);
            }
        }


        /**
         * Returns the number of rows in the model. A
         * <code>JTable</code> uses this method to determine how many rows it
         * should display.  This method should be quick, as it
         * is called frequently during rendering.
         *
         * @return the number of rows in the model
         * @see #getColumnCount
         */
        public int getRowCount() {
            return iMediator.getNumberOfVisibleTableRows();
        }

        /**
         * Returns the number of columns in the model. A
         * <code>JTable</code> uses this method to determine how many columns it
         * should create and display by default.
         *
         * @return the number of columns in the model
         * @see #getRowCount
         */
        public int getColumnCount() {
            return 2;
        }

        /**
         * Returns the name of the column at <code>columnIndex</code>.  This is used
         * to initialize the table's column header name.  Note: this name does
         * not need to be unique; two columns in a table can have the same name.
         *
         * @param columnIndex the index of the column
         * @return the name of the column
         */
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Property";
            } else if (columnIndex == 1) {
                return "Output";
            } else {
                return "NA!";
            }
        }

        /**
         * Returns the most specific superclass for all the cell values
         * in the column.  This is used by the <code>JTable</code> to set up a
         * default renderer and editor for the column.
         *
         * @param columnIndex the index of the column
         * @return the common ancestor class of the object values in the model.
         */
        public Class<?> getColumnClass(int columnIndex) {
            Class c = null;
            try {
                c = getValueAt(0, columnIndex).getClass();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        /**
         * Returns true if the cell at <code>rowIndex</code> and
         * <code>columnIndex</code>
         * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
         * change the value of that cell.
         *
         * @param rowIndex    the row whose value to be queried
         * @param columnIndex the column whose value to be queried
         * @return true if the cell is editable
         * @see #setValueAt
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            } else {
                return true;
            }
        }

        /**
         * Returns the value for the cell at <code>columnIndex</code> and
         * <code>rowIndex</code>.
         *
         * @param rowIndex    the row whose value is to be queried
         * @param columnIndex the column whose value is to be queried
         * @return the value Object at the specified cell
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object o = null;

            AbstractTableRow row = iMediator.getTableRow(rowIndex);
            if (columnIndex == 0) {
                o = row.getName();
            } else if (columnIndex == 1) {
                o = iTableRows.get(row);
            }

            return o;
        }


        /**
         * Sets the value in the cell at <code>columnIndex</code> and
         * <code>rowIndex</code> to <code>aValue</code>.
         *
         * @param aValue      the new value
         * @param rowIndex    the row whose value is to be changed
         * @param columnIndex the column whose value is to be changed
         * @see #getValueAt
         * @see #isCellEditable
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // Activity column,
            if (columnIndex == 1) {
                // Set the value (Boolean for column 1) to iTableRows.
                iTableRows.put(iMediator.getTableRow(rowIndex), aValue);
            }
        }

        /**
         * Adds a listener to the list that is notified each time a change
         * to the data model occurs.
         *
         * @param l the TableModelListener
         */
        public void addTableModelListener(TableModelListener l) {
            // No implementation.
        }

        /**
         * Removes a listener from the list that is notified each time a
         * change to the data model occurs.
         *
         * @param l the TableModelListener
         */
        public void removeTableModelListener(TableModelListener l) {
            // No implementation.
        }

    }

    /**
     * This CellRenderer serves padding for the Table.
     */
    private class CSVOutputTableCellRenderer extends DefaultTableCellRenderer {

        /**
         * JLabel used for rendering.
         */
        JLabel lbl = null;

        /**
         * Empty constructor.
         * The renderer makes overall use of the DefaultTableCellRenderer plus setting an empty padding border.
         */
        public CSVOutputTableCellRenderer() {
            super();
        }


        // implements javax.swing.table.TableCellRenderer
        /**
         * Returns the default table cell renderer.
         *
         * @param table      the <code>JTable</code>
         * @param value      the value to assign to the cell at
         *                   <code>[row, column]</code>
         * @param isSelected true if cell is selected
         * @param hasFocus   true if cell has focus
         * @param row        the row of the cell to render
         * @param column     the column of the cell to render
         * @return the default table cell renderer
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // set padding - you should provide caching of compound
            // borders and Insets instance for performace reasons...
            lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setBorder(new CompoundBorder(new EmptyBorder(new Insets(1, 4, 1, 4)), lbl.getBorder()));
            return lbl;
        }
    }
}
