package com.compomics.peptizer.gui.component;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * This class is a panel for a label and a checkbox.
 */
public class JLabelAndComponentPanel extends JPanel {
    // Class specific log4j logger for JLabelAndTextFieldPanel instances.
    private static Logger logger = Logger.getLogger(JLabelAndComponentPanel.class);

    /** This class implements a JPanel that lays out a set of JLabel and a JTextField
     * next to each other. <br />
     * Correct usage of this class is calling the empty constructor
     * and adding label-textfield pairs via the corresponding 'add' method. <br />
     * Do not call anything else unless you really want to break the functionality!
     *
     * @author Lennart Martens
     */
    /**
     * Constructor which allows the specification of the labels and textfields
     * to lay out.
     *
     * @param aLabels     JLabel[] with the labels.
     * @param aComponents JCheckBox[] with the checkboxes.
     */
    public JLabelAndComponentPanel(JLabel[] aLabels, JComponent[] aComponents) {
        super();
        if (aLabels.length != aComponents.length) {
            throw new IllegalArgumentException("Unequal amounts of labels (" + aLabels.length + ") and checkboxes (" + aComponents.length + ")!");
        } else {
            this.setLayout(new GridBagLayout());
            for (int i = 0; i < aLabels.length; i++) {
                JPanel jpl = new JPanel();
                jpl.setLayout(new BoxLayout(jpl, BoxLayout.X_AXIS));
                jpl.add(Box.createRigidArea(new Dimension(10, aLabels[i].getHeight())));
                jpl.add(aLabels[i]);
                jpl.add(Box.createHorizontalGlue());

                GridBagConstraints gbcL = new GridBagConstraints();
                gbcL.gridx = 0;
                gbcL.gridy = i;
                gbcL.gridwidth = 1;
                gbcL.gridheight = 1;
                gbcL.anchor = GridBagConstraints.WEST;

                JPanel jpt = new JPanel();
                jpt.add(new JLabel(" : "));
                jpt.add(aComponents[i]);

                GridBagConstraints gbcT = new GridBagConstraints();
                gbcT.gridx = 1;
                gbcT.gridy = i;
                gbcT.gridwidth = GridBagConstraints.REMAINDER;
                gbcT.gridheight = 1;

                this.add(jpl, gbcL);
                this.add(jpt, gbcT);
            }
        }
    }
}