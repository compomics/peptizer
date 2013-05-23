package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.dialog.SaveValidationDialog;
import com.compomics.peptizer.gui.interfaces.SaveValidationPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.ValidationSaveToMsLims;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.io.PropertiesManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
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
public class SaveValidationPanel_Ms_Lims extends JPanel implements SaveValidationPanel, Connectable {
    // Class specific log4j logger for SaveValidationPanel_Ms_Lims instances.
    private static Logger logger = Logger.getLogger(SaveValidationPanel_Ms_Lims.class);

    // gui components.
    private JButton btnConnection = null;
    private JLabel lblConnection = null;

    /**
     * The Singleton instance of this Panel.
     */
    private static SaveValidationPanel_Ms_Lims iSingleton = null;

    /**
     * The static instance of the parent SaveValidationDialog.
     */
    private static SaveValidationDialog iDialog;

    /**
     * The Mediator used by the Model.
     */
    private static Mediator iMediator = null;
    private ValidationSaveToMsLims iValidationSaver;
    private JCheckBox chkSaveConfidentNotSelected;

    /**
     * Returns the Singleton instance of SaveValidationPanel_Ms_Lims.
     *
     * @param aDialog
     */
    private SaveValidationPanel_Ms_Lims(SaveValidationDialog aDialog) {
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
    }

    /**
     * {@inheritDoc}
     */
    public static SaveValidationPanel_Ms_Lims getInstance(SaveValidationDialog aDialog) {

        if (iSingleton == null) {
            iSingleton = new SaveValidationPanel_Ms_Lims(aDialog);
        } else {
            // Bizar singleton construction, i know. :)
            // This singleton panel must maintain a coupling to the JDialog to follow the Mediator Combobox.
            // If a Save dialog is launched a second time, this Panel has a pointer to the old JDialog.
            // Therefor, fetch the ActionListener on the ComboBox from the Old Dialog, and place them on the new Dialog & reset the Mediator.
            iDialog.getListeners(ActionListener.class);
            ActionListener[] lActionListeners = iDialog.getComboBoxListeners();
            for (ActionListener lActionListener : lActionListeners) {
                if (lActionListener instanceof MyActionListener) {
                    aDialog.addComboBoxListener(lActionListener);
                }
            }
            iDialog = aDialog;
            iMediator = iDialog.getSelectedMediator();
        }
        return iSingleton;
    }

    /**
     * Construct this SaveValidationPanel_CSV instance.
     */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select a target to save the validation.");


        // Button to construct the connection!
        btnConnection = new JButton();
        btnConnection.setText("Create Connection");
        btnConnection.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Properties lProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");
                JDialog lConnectionDialog = new ConnectionDialog((JFrame) SwingUtilities.getRoot(lblConnection).getParent(),
                        SaveValidationPanel_Ms_Lims.this,
                        "Establish DB connnection for Peptizer",
                        lProperties
                );
                lConnectionDialog.setVisible(true);
            }
        });
        btnConnection.setMnemonic(KeyEvent.VK_N);


        // JLabel for the project id
        lblConnection = new JLabel();

        if (ConnectionManager.getInstance().hasConnection()) {
            try {
                lblConnection.setText(ConnectionManager.getInstance().getConnection().getMetaData().getURL());
                lblConnection.setForeground(new Color(0, 200, 0));
                btnConnection.setVisible(false);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            btnConnection.setVisible(true);
            lblConnection.setText("No ms_lims connection.");
        }

        chkSaveConfidentNotSelected = new JCheckBox();
        chkSaveConfidentNotSelected.setSelected(true);
        chkSaveConfidentNotSelected.setText("Auto-accept confident not-selected identifications?");

        // Components initiation && GUI construction.

        // Put target on the Top panel.
        JPanel jpanTop = new JPanel();
        BoxLayout lBoxLayout2 = new BoxLayout(jpanTop, BoxLayout.LINE_AXIS);
        jpanTop.setLayout(lBoxLayout2);
        jpanTop.add(btnConnection);
        jpanTop.add(Box.createHorizontalStrut(10));
        jpanTop.add(lblConnection);
        jpanTop.add(Box.createHorizontalGlue());
        jpanTop.setBorder(BorderFactory.createTitledBorder("Connection details"));

        JPanel jpanOption = new JPanel();
        jpanOption.add(chkSaveConfidentNotSelected, BorderLayout.WEST);

        JPanel jpanMain = new JPanel();
        jpanMain.add(jpanTop, BorderLayout.CENTER);
        jpanMain.add(jpanOption, BorderLayout.SOUTH);

        this.add(jpanMain);
        this.add(Box.createVerticalGlue());

        this.validate();
    }


    /**
     * Returns an instance to save selected identifications and validation.
     *
     * @return ValidationSaver to save validation of selected identifications.
     */
    public ValidationSaver getNewValidationSaver() {
        if (ConnectionManager.getInstance().hasConnection()) {
            iValidationSaver = null;
            try {
                DefaultProgressBar lProgress = new DefaultProgressBar((JFrame) SwingUtilities.getRoot(iMediator), "Updating validation results to " + ConnectionManager.getInstance().getConnection().getMetaData().getURL() + " .", 0, 1);
                iValidationSaver = new ValidationSaveToMsLims();
                iValidationSaver.setData(iDialog.getSelectedMediator().getSelectedPeptideIdentifications());
                iValidationSaver.setSaveConfidentNotSelected(chkSaveConfidentNotSelected.isSelected());
                iValidationSaver.setParentComponent(this);

            } catch (SQLException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }
            return iValidationSaver;
        } else {
            JOptionPane.showMessageDialog(this.getParent(), "Unable to find a database connection.!!", "Please create a new database connection by the main menu first!", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }


    /**
     * Get the validationSaver of the Current panel. Use the getNewValidationSaver() for a new Build.
     *
     * @return
     */
    public ValidationSaver getActiveValidationSaver() {
        if (iValidationSaver == null) {
            return getNewValidationSaver();
        } else {
            return iValidationSaver;
        }
    }


    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String toString() {
        return "Save to ms_lims";
    }


    /**
     * Connectable signature.
     * This panel can load a JDialog wherein a database connection is created.
     * The Connection will then be passed from the Dialog into Peptizer by this method.
     *
     * @param aConnection is the Connection that was created in the Dialog.
     * @param aDBName     is the name of the Connection that was established.
     */
    public void passConnection(final Connection aConnection, final String aDBName) {
        if (aConnection != null) {
            ConnectionManager.getInstance().setConnection(aConnection);
            lblConnection.setText(aDBName);
            lblConnection.setForeground(new Color(0, 200, 0));
        }
    }

    /**
     * The ActionListener is place on the combobox of the parent SaveValidationDialog.
     * Whenever it's selection is changed, make sure the Mediator used by the TableModel follows the selection by this Listener!
     */
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            iMediator = iDialog.getSelectedMediator();
        }
    }
}
