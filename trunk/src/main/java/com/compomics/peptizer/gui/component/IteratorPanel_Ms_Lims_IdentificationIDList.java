package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.interfaces.IteratorPanel;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.iterators.Ms_Lims_IdentificationIDIterator;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.util.interfaces.Connectable;

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

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 14-mrt-2008 Time: 13:45:55 To change this template use File | Settings |
 * File Templates.
 */
public class IteratorPanel_Ms_Lims_IdentificationIDList extends JPanel implements IteratorPanel, Connectable {

    /**
     * Singleton instance of the JPanel.
     */
    private static IteratorPanel_Ms_Lims_IdentificationIDList iSingleton = null;

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
    private IteratorPanel_Ms_Lims_IdentificationIDList() {
        super();
        construct();
    }

    /**
     * Returns the Singleton instance of IteratorPanel_Ms_Lims_IdentificationIDList.
     *
     * @return IteratorPanel_Ms_Lims_IdentificationIDList instance.
     */
    public static IteratorPanel_Ms_Lims_IdentificationIDList getInstance() {
        if (iSingleton == null) {
            iSingleton = new IteratorPanel_Ms_Lims_IdentificationIDList();
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
                                IteratorPanel_Ms_Lims_IdentificationIDList.this,
                                "Establish DB connnection for Peptizer",
                                MatConfig.getInstance().getGeneralProperties().getProperty("CONNECTION_PROPERTIES")
                        );
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
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
     * Construct the PeptideIdentificationIterator based on the parameters set by the GUI.
     *
     * @return PeptideIdentificationIterator instance. Can be null if
     */
    public PeptideIdentificationIterator getIterator() {

        Ms_Lims_IdentificationIDIterator lPeptideIdentificationIterator = null;

        // First call the control method if all parameters are complete.
        try {
            if (controlParameters()) {
                lPeptideIdentificationIterator = new Ms_Lims_IdentificationIDIterator(iIdentificationIDs);
            }
        } catch (SQLException e) {
            MatLogger.logExceptionalGUIMessage("SQLException thrown while creating the Iterator.", e.toString());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return lPeptideIdentificationIterator;
    }


    private void setConnectionInformation() {
        if (ConnectionManager.getInstance().hasConnection()) {
            try {
                String url = ConnectionManager.getInstance().getConnection().getMetaData().getURL();

                lblConnection.setText(url.substring(url.lastIndexOf(":") + 1));
                lblConnection.setForeground(new Color(0, 200, 0));

            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
