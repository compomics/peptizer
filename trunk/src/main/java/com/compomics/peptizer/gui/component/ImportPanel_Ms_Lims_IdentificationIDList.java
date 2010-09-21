package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.util.datatools.IdentificationFactory;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.io.PropertiesManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 14-mrt-2008 Time: 13:45:55 To change this template use File | Settings |
 * File Templates.
 */
public class ImportPanel_Ms_Lims_IdentificationIDList extends JPanel implements ImportPanel, Connectable {
	// Class specific log4j logger for ImportPanel_Ms_Lims_IdentificationIDList instances.
	 private static Logger logger = Logger.getLogger(ImportPanel_Ms_Lims_IdentificationIDList.class);

    /**
     * Singleton instance of the JPanel.
     */
    private static ImportPanel_Ms_Lims_IdentificationIDList iSingleton = null;

    /**
     * GUI components.
     */
    private JLabel lblConnection;
    private JButton btnConnection;
    private JTextArea txaIdentificationIDs = null;

    /**
     * Instance ArrayList with the ms_lims IdentificationID's that will be iterated.
     */
    private ArrayList<Long> iIdentificationIDs;
    private JScrollPane iScrollText;

    /**
     * Private constructor for the singleton construction.
     */
    private ImportPanel_Ms_Lims_IdentificationIDList() {
        super();
        construct();
    }

    /**
     * Returns the Singleton instance of IteratorPanel_Ms_Lims_IdentificationIDList.
     *
     * @return IteratorPanel_Ms_Lims_IdentificationIDList instance.
     */
    public static ImportPanel_Ms_Lims_IdentificationIDList getInstance() {
        if (iSingleton == null) {
            iSingleton = new ImportPanel_Ms_Lims_IdentificationIDList();
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
                Properties lProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.PEPTIZER, "peptizer.properties");
                JDialog lConnectionDialog =
                        new ConnectionDialog(null,
                                ImportPanel_Ms_Lims_IdentificationIDList.this,
                                "Establish DB connnection for Peptizer",lProperties);
                lConnectionDialog.setVisible(true);
            }
        });

        // TextArea for the Identification id's.

        txaIdentificationIDs = new JTextArea(4, 12);
        txaIdentificationIDs.setDragEnabled(true);


        DocumentListener myListener = new DocumentListener() {
            public void insertUpdate(final DocumentEvent e) {
                changedUpdate(e);
            }

            public void removeUpdate(final DocumentEvent e) {
                changedUpdate(e);
            }

            public void changedUpdate(final DocumentEvent e) {
                String content;
                boolean error = false;
                String line = "";
                try {
                    content = txaIdentificationIDs.getText();

                    if (content.equals("")) {
                        iIdentificationIDs = new ArrayList<Long>();
                    } else {
                        BufferedReader br = new BufferedReader(new StringReader(content));
                        iIdentificationIDs = new ArrayList<Long>();
                        while ((line = br.readLine()) != null) {
                            iIdentificationIDs.add(Long.parseLong(line));
                        }
                    }
                    updateScrollPaneBorder();

                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);  //To change body of catch statement use File | Settings | File Templates.
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(txaIdentificationIDs, "'" + line + "' cannot be an identificationid.", "Incorrect input!", JOptionPane.ERROR_MESSAGE);
                }
                ;
            }

        };
        txaIdentificationIDs.getDocument().addDocumentListener(myListener);

        iScrollText =
                new JScrollPane(txaIdentificationIDs, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        iScrollText.setBorder(BorderFactory.createTitledBorder("Identificationid list"));
        iScrollText.setToolTipText("Enter ms_lims identification id's corresponding to peptide identifications that must be iterated. <html><b>1 id per line</b><html>");


        // Set layout.
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Insert a ms_lims project identifier and make a connection.");

        // Put components on the panel.
        this.add(btnConnection);
        this.add(Box.createHorizontalStrut(10));
        this.add(lblConnection);
        this.add(Box.createHorizontalStrut(10));
        this.add(this.iScrollText);
        this.add(Box.createHorizontalGlue());
    }

    private void updateScrollPaneBorder() {
        iScrollText.setBorder(BorderFactory.createTitledBorder("Identificationid list (" + iIdentificationIDs.size() + " items)"));
        iScrollText.repaint();
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

        // Check if we have a database connection!
        if (!ConnectionManager.getInstance().hasConnection()) {
            result = false;
            JOptionPane.showMessageDialog(this, "No DB Connection found!!\nPlease create a new database connection first.");
        }

        // Check if the connection can perform a query!
        else if (!ConnectionManager.getInstance().testConnection()) {
            result = false;
            JOptionPane.showMessageDialog(this, "Testing of the datbase connection failed!! ");
        }

        // Check if the ArrayList with identification id's was allready constructed.
        else if (iIdentificationIDs == null) {
            result = false;
            JOptionPane.showMessageDialog(this, "No identification id's set!!\nPlease enter one or a few identification id's corresponding to peptide identifications of your ms_lims database.");
        }
        // Check if some identification id's are given.
        else if (iIdentificationIDs.size() == 0) {
            result = false;
            JOptionPane.showMessageDialog(this, "No identification id's set!!\nPlease enter one or a few identification id's corresponding to peptide identifications of your ms_lims database.");
        }

        // All should be fine now to execute the queries on this connection!
        return result;
    }


    public ArrayList<Long> getIdentificationIDs() {
        return iIdentificationIDs;
    }

    public void setIdentificationIDs(final ArrayList<Long> aIdentificationIDs) {
        StringWriter sw = new StringWriter();
        for (Long aIdentificationID : aIdentificationIDs) {
            sw.write(aIdentificationID.toString() + "\n");
        }
        sw.flush();
        txaIdentificationIDs.setText(sw.toString());
    }


    /**
     * loads identifications based on the parameters set by the GUI.
     */
    public void loadIdentifications(DefaultProgressBar progressBar) {
        // We should not need any progressbar here

        // First call the control method if all parameters are complete.
        try {
            if (controlParameters()) {
                IdentificationFactory.getInstance().load(iIdentificationIDs);
            }
        } catch (SQLException e) {
            MatLogger.logExceptionalGUIMessage("SQLException thrown while creating the Iterator.", e.toString());
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

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
        }
    }


    public String toString() {
        return "Ms_lims IdentificationId iterator";
    }
}
