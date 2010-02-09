package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.interfaces.IteratorPanel;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;

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

    private JPanel jpanSourceProperties;
    private JComboBox cmbIterators;

    public DataSourcePanel() {
        construct();
    }

    private void construct() {

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createTitledBorder("1. Data Source"));
        setToolTipText("Define the datasource be configuring a PeptideIdentificationIterator.");

        Vector<IteratorPanel> iters = new Vector();
        iters.add(IteratorPanel_Folder.getInstance());
        iters.add(IteratorPanel_File.getInstance());
        iters.add(IteratorPanel_Ms_Lims_Project.getInstance());
        iters.add(IteratorPanel_Ms_Lims_IdentificationIDList.getInstance());
        cmbIterators = new JComboBox(iters);

        cmbIterators.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jpanSourceProperties.remove(0);
                jpanSourceProperties.add((JPanel) cmbIterators.getSelectedItem(), 0);
                jpanSourceProperties.validate();
                DataSourcePanel.this.repaint();
            }
        });

        jpanSourceProperties = new JPanel(new BorderLayout());
        jpanSourceProperties.add((JPanel) cmbIterators.getItemAt(0), 0);

        this.add(cmbIterators);
        this.add(Box.createHorizontalStrut(10));
        this.add(jpanSourceProperties);
        this.add(Box.createHorizontalGlue());


    }

    /**
     * Get the selected PeptideIdentificationIterator.
     *
     * @return
     */
    public PeptideIdentificationIterator getSelectedIterator() {
        return ((IteratorPanel) (cmbIterators.getSelectedItem())).getIterator();
    }

    /**
     * Set the given iterator panel to the combobox.
     *
     * @param aIteratorPanel The iterator panel to be set.
     */
    public void setSelectedIterator(IteratorPanel aIteratorPanel) {
        cmbIterators.setSelectedItem(aIteratorPanel);
    }
}
