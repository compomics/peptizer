package com.compomics.peptizer.gui.component;

import com.compomics.mslimsdb.accessors.Project;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.interfaces.IteratorPanel;
import com.compomics.peptizer.gui.renderer.ProjectListRenderer;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.iterators.Ms_Lims_ProjectIterator;
import com.compomics.util.interfaces.Connectable;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 14-mrt-2008 Time: 13:45:55 To change this template use File | Settings |
 * File Templates.
 */
public class IteratorPanel_Ms_Lims_Project extends JPanel implements IteratorPanel, Connectable {
	// Class specific log4j logger for IteratorPanel_Ms_Lims_Project instances.
	 private static Logger logger = Logger.getLogger(IteratorPanel_Ms_Lims_Project.class);

    /**
     * Singleton instance of the JPanel.
     */
    private static IteratorPanel_Ms_Lims_Project iSingleton = null;

    /**
     * GUI components.
     */
    private JLabel lblConnection = null;
    private JButton btnConnection = null;
    private JLabelOrComboboxPanel jpanProjects = null;

    /**
     * Instance field with the ms_lims project that will be iterated.
     */
    private long iProjectID = -1;

    /**
     * Private constructor for the singleton construction.
     */
    private IteratorPanel_Ms_Lims_Project() {
        super();
        construct();
    }

    /**
     * Returns the Singleton instance of IteratorPanel_Ms_Lims_Project.
     *
     * @return IteratorPanel_Ms_Lims_Project instance.
     */
    public static IteratorPanel_Ms_Lims_Project getInstance() {
        if (iSingleton == null) {
            iSingleton = new IteratorPanel_Ms_Lims_Project();
        }
        return iSingleton;
    }

    /**
     * Construct the JPanel.
     */
    private void construct() {

        // Lable for the connection.
        lblConnection = new JLabel();
        lblConnection.setFont(lblConnection.getFont().deriveFont(11.0F));
        setConnectionInformation();

        // Button for the connection.
        btnConnection = new JButton();
        btnConnection.setText("Create Connection");
        btnConnection.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JDialog lConnectionDialog =
                        new ConnectionDialog((JFrame) SwingUtilities.getRoot(lblConnection).getParent(),
                                IteratorPanel_Ms_Lims_Project.this,
                                "Establish DB connnection for Peptizer",
                                MatConfig.getInstance().getGeneralProperties().getProperty("CONNECTION_PROPERTIES")
                        );
                lConnectionDialog.setVisible(true);
            }
        });

        // Panel for project selection.
        // This panel shows a label if no connection availlable, while it shows a project JCombobox if there is a Connection.
        jpanProjects = new JLabelOrComboboxPanel();
        jpanProjects.setMaximumSize(new Dimension(100, 100));

        // Set layout.
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Insert a ms_lims project identifier and make a connection.");

        // Put components on the panel.
        this.add(btnConnection);
        this.add(Box.createHorizontalStrut(10));
        this.add(lblConnection);
        this.add(Box.createHorizontalStrut(10));
        this.add(jpanProjects);
        this.add(Box.createHorizontalGlue());
        setConnectionInformation();
        repaint();
    }


    /**
     * Construct the PeptideIdentificationIterator based on the parameters set by the GUI.
     *
     * @return PeptideIdentificationIterator instance. Can be null if
     */
    public PeptideIdentificationIterator getIterator() {

        Ms_Lims_ProjectIterator lPeptideIdentificationIterator = null;

        // First call the control method if all parameters are complete.
        try {
            if (controlParameters()) {
                lPeptideIdentificationIterator = new Ms_Lims_ProjectIterator(iProjectID);
                /*if(lPeptideIdentificationIterator.getIdentificationIDCount() < 1){
                        lPeptideIdentificationIterator = null;
                    }*/
            }
        } catch (SQLException e) {
            MatLogger.logExceptionalGUIMessage("SQLException thrown while creating the Iterator.", e.toString());
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

        return lPeptideIdentificationIterator;
    }

    /**
     * Controls whether all parameters were set correctly in the GUI to construct a PeptideIdentificationIterator.
     *
     * @return boolean whether a PeptideIdentificationIterator can be created from these settings.
     * @throws java.sql.SQLException while testing the database connection.
     */
    public boolean controlParameters() throws SQLException {
        // If all goes fine, this will stay true.
        boolean result = true;

        // 1) Check if we have a database connection!
        if (!ConnectionManager.getInstance().hasConnection()) {
            result = false;
            JOptionPane.showMessageDialog(this, "No DB Connection found!!\nPlease create a new database connection first.");
        }
        // 3) Check if a project identifier was set.
        else if (iProjectID == -1) {
            result = false;
            JOptionPane.showMessageDialog(this, "No Project Identifier set!!\nPlease enter a valid project identifier in the text box first.");
        }
        // 3) Check if the connection can perform a query!
        else if (!ConnectionManager.getInstance().testConnection()) {
            result = false;
            JOptionPane.showMessageDialog(this, "Testing of the datbase connection failed!! ");
        }

        // All should be fine now to execute the queries on this connection!
        return result;
    }

    private void setConnectionInformation() {
        if (ConnectionManager.getInstance().hasConnection()) {
            try {
                String url = ConnectionManager.getInstance().getConnection().getMetaData().getURL();

                lblConnection.setText(url.substring(url.lastIndexOf(":") + 1));
                lblConnection.setForeground(new Color(0, 200, 0));

            } catch (SQLException e) {
                logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            lblConnection.setText("No ms_lims connection.");
        }
    }

    /**
     * Connectable signature. This panel can load a JDialog wherein a database connection is created. The Connection
     * will then be passed from the Dialog into Peptizer by this method.
     *
     * @param aConnection is the Connection that was created in the Dialog.
     * @param aDBName     is the name of the Connection that was established.
     */
    public void passConnection(final Connection aConnection, final String aDBName) {
        if (aConnection != null) {
            ConnectionManager.getInstance().setConnection(aConnection);
            lblConnection.setText(aDBName);
            lblConnection.setForeground(new Color(0, 200, 0));
            jpanProjects.update();
        }
    }


    public long getProjectID() {
        return iProjectID;
    }

    public void setProjectID(final long aProjectID) {
        iProjectID = aProjectID;
    }


    public String toString() {
        return "Ms_lims project iterator";
    }

    /**
     * This private class is used to either show a JLabel with text or a JComboBox with projects respectively if there
     * is or there is no Connection availlable.
     */
    private class JLabelOrComboboxPanel extends JPanel {
        // Gui components.
        private JLabel lbl1 = null;
        private JComboBox cmb1;

        /**
         * Constructor.
         */
        public JLabelOrComboboxPanel() {
            super();
            lbl1 = new JLabel("No projects!");
            update();
        }


        /**
         * Call whenever this panel must be updated.
         */
        public void update() {
            if (ConnectionManager.getInstance().hasConnection()) {
                try {
                    // Only create a new project combobox,
                    // If JCombobox was not created yet, or if a new Connection was created in meanwhile.						if((cmb1 == null)|| (lbl1.getText() == ConnectionManager.getInstance().getConnection().getMetaData().getURL())){
                    cmb1 = new JComboBox(Project.getAllProjects(ConnectionManager.getInstance().getConnection()));
                    cmb1.setMaximumRowCount(5);
                    cmb1.setMaximumSize(new Dimension(50, 10));
                    // todo failing connection!
                    iProjectID = ((Project) cmb1.getSelectedItem()).getProjectid();
                    cmb1.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            // If the combobox changes, update the projectID!
                            iProjectID = ((Project) cmb1.getSelectedItem()).getProjectid();
                        }
                    });
                    ListCellRenderer renderer = new ProjectListRenderer();
                    cmb1.setRenderer(renderer);
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }

                this.removeAll();
                this.add(cmb1);
            } else {
                this.removeAll();
                this.add(lbl1);
            }
        }

    }
}
