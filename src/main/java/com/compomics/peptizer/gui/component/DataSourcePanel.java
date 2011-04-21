package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.interfaces.ImportPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 30-jul-2008 Time: 15:17:45 To change this template use File | Settings |
 * File Templates.
 */
public class DataSourcePanel extends JPanel {
    // Class specific log4j logger for DataSourcePanel instances.
    private static Logger logger = Logger.getLogger(DataSourcePanel.class);

    private JPanel jpanSourceProperties;
    private JComboBox cmbIterators;

    public DataSourcePanel() {
        construct();
    }

    private void construct() {

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createTitledBorder("1. Data Source"));
        setToolTipText("Define the datasource be configuring a PeptideIdentificationIterator.");

        Vector<ImportPanel> iters = new Vector();
        iters.add(ImportPanel_File.getInstance());
        iters.add(ImportPanel_Ms_Lims_Project.getInstance());
        iters.add(ImportPanel_Ms_Lims_IdentificationIDList.getInstance());
        cmbIterators = new JComboBox(iters);

        cmbIterators.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jpanSourceProperties.remove(0);
                jpanSourceProperties.add((JPanel) cmbIterators.getSelectedItem(), 0);
                jpanSourceProperties.validate();
//                DataSourcePanel.this.validate();
//                DataSourcePanel.this.repaint();
                DataSourcePanel.this.getParent().validate();
                DataSourcePanel.this.getParent().repaint();
            }
        });

        jpanSourceProperties = new JPanel(new BorderLayout());
        jpanSourceProperties.add((JPanel) cmbIterators.getItemAt(0), 0);

        JPanel jpanCmbWrapperPanel = new JPanel();
        jpanCmbWrapperPanel.setLayout(new BoxLayout(jpanCmbWrapperPanel, BoxLayout.Y_AXIS));
        jpanCmbWrapperPanel.add(cmbIterators);
        jpanCmbWrapperPanel.add(Box.createVerticalGlue());
        Box lHorizontalBox = Box.createHorizontalBox();

        this.add(jpanCmbWrapperPanel);
        this.add(Box.createHorizontalStrut(10));
        this.add(jpanSourceProperties);
        this.add(Box.createHorizontalGlue());


    }

    /**
     * Get the selected import type.
     *
     * @return
     */
    public ImportPanel getSelectedImport() {
        return (ImportPanel) (cmbIterators.getSelectedItem());
    }

    /**
     * Set the given iterator panel to the combobox.
     *
     * @param anImportPanel The panel to be set.
     */
    public void setSelectedIterator(ImportPanel anImportPanel) {
        cmbIterators.setSelectedItem(anImportPanel);
    }
}
