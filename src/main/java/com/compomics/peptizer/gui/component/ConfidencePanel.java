package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-aug-2007
 * Time: 14:10:00
 */

/**
 * Class description:
 * ------------------
 * This class was developed to display a simple JPanel with a synchronized textfield that sync the MatConfig instance
 */
public class ConfidencePanel extends JPanel {

    private JLabel lbl1 = null;
    private JTextField txt1 = null;
    private Double iDefaultConfidence;

    /**
     * Emptry constructor.
     * This Panel has a JLabel and JTextField. Changes to the TextField will be in sync with the Confidence property in MatConfig.
     */
    public ConfidencePanel() {
        super();
        constructPanel();
    }

    /**
     * Construct the Panel.
     */
    private void constructPanel() {

        // Save the current confidence before changing settings.
        iDefaultConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA"));

        // Layout
        //BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        super.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 0));

        // Create Label.
        lbl1 = new JLabel("<html>Confidence<small> (input alpha value 0.05, results in 95% confidence)</small></html>");
        // Create TextField.
        txt1 = new JTextField(MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA"), 5);

        txt1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Double lNewConfidence = null;
                try {
                    lNewConfidence = Double.parseDouble(txt1.getText());
                    if (lNewConfidence > 0) {
                        MatConfig.getInstance().changeGeneralProperty("DEFAULT_ALPHA", lNewConfidence.toString());
                        txt1.setForeground(new Color(0, 200, 0));
                    } else {
                        JOptionPane.showMessageDialog(ConfidencePanel.this, txt1.getText() + " is not a valid alpha value for Confidence, <html><Strong>it must be positive!!</Strong></html>\nFor example, please insert alpha where input value 0.05, results in 95% confidence.");
                        updateTextField();
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ConfidencePanel.this, txt1.getText() + " is not a valid alpha value for Confidence!!\nFor example, please insert alpha where input value 0.05, results in 95% confidence.");
                    updateTextField();

                }
            }

        });

        this.add(lbl1);
        this.add(Box.createHorizontalStrut(10));
        this.add(txt1);
    }


    /**
     * Update the confidence textfield with the current value in the MatConfig.
     */
    public void updateTextField() {
        txt1.setText(MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA"));

    }
}
