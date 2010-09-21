package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import org.apache.log4j.Logger;

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
	// Class specific log4j logger for ConfidencePanel instances.
	 private static Logger logger = Logger.getLogger(ConfidencePanel.class);

    private JLabel lblMascot = null;
    private JTextField txtMascot = null;
    private Double iDefaultMascotConfidence;
    private Double iDefaultOMSSAEValueCutOff;
    private Double iDefaultXTandemEValueCutOff;
    private JLabel lblOMSSA = null;
    private JTextField txtOMSSA = null;
    private JLabel lblXTandem = null;
    private JTextField txtXTandem = null;

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
        iDefaultMascotConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        iDefaultOMSSAEValueCutOff = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_OMSSA_EVALUE"));
        iDefaultXTandemEValueCutOff = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_XTANDEM_EVALUE"));

        // Layout
        //BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        super.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 0));

        // Create Labels.
        lblMascot = new JLabel("<html>Mascot Confidence<small> (input alpha value 0.05, results in 95% confidence)</small></html>");
        lblOMSSA = new JLabel("<html>OMSSA maximal E-Value</html>");
        lblXTandem = new JLabel("<html>X!Tandem maximal E-Value</html>");
        // Create TextField.
        txtMascot = new JTextField(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"), 5);
        txtOMSSA = new JTextField(MatConfig.getInstance().getGeneralProperty("DEFAULT_OMSSA_EVALUE"), 5);
        txtXTandem = new JTextField(MatConfig.getInstance().getGeneralProperty("DEFAULT_XTANDEM_EVALUE"), 5);

        txtMascot.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Double lNewConfidence = null;
                try {
                    lNewConfidence = Double.parseDouble(txtMascot.getText());
                    if (lNewConfidence > 0) {
                        MatConfig.getInstance().changeGeneralProperty("DEFAULT_MASCOT_ALPHA", lNewConfidence.toString());
                        txtMascot.setForeground(new Color(0, 200, 0));
                    } else {
                        JOptionPane.showMessageDialog(ConfidencePanel.this, txtMascot.getText() + " is not a valid alpha value for Confidence, <html><Strong>it must be positive!!</Strong></html>\nFor example, please insert alpha where input value 0.05, results in 95% confidence.");
                        updateTextField();
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ConfidencePanel.this, txtMascot.getText() + " is not a valid alpha value for Confidence!!\nFor example, please insert alpha where input value 0.05, results in 95% confidence.");
                    updateTextField();

                }
            }

        });
        txtOMSSA.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Double lNewEValue = null;
                try {
                    lNewEValue = Double.parseDouble(txtOMSSA.getText());
                    if (lNewEValue > 0) {
                        MatConfig.getInstance().changeGeneralProperty("DEFAULT_OMSSA_EVALUE", lNewEValue.toString());
                        txtOMSSA.setForeground(new Color(0, 200, 0));
                    } else {
                        JOptionPane.showMessageDialog(ConfidencePanel.this, "An E-Value must be positive.");
                        updateTextField();
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ConfidencePanel.this, txtMascot.getText() + " is not recognized as valud input.");
                    updateTextField();

                }
            }
        });
        txtXTandem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Double lNewEValue = null;
                try {
                    lNewEValue = Double.parseDouble(txtXTandem.getText());
                    if (lNewEValue > 0) {
                        MatConfig.getInstance().changeGeneralProperty("DEFAULT_XTANDEM_EVALUE", lNewEValue.toString());
                        txtXTandem.setForeground(new Color(0, 200, 0));
                    } else {
                        JOptionPane.showMessageDialog(ConfidencePanel.this, "An E-Value must be positive.");
                        updateTextField();
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ConfidencePanel.this, txtMascot.getText() + " is not recognized as valud input.");
                    updateTextField();

                }
            }
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblMascot);
        textPanel.add(lblOMSSA);
        textPanel.add(lblXTandem);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(txtMascot);
        inputPanel.add(txtOMSSA);
        inputPanel.add(txtXTandem);

        this.add(textPanel);
        this.add(inputPanel);
    }


    /**
     * Update the confidence textfield with the current value in the MatConfig.
     */
    public void updateTextField() {
        txtMascot.setText(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        txtOMSSA.setText(MatConfig.getInstance().getGeneralProperty("DEFAULT_OMSSA_EVALUE"));
        txtXTandem.setText(MatConfig.getInstance().getGeneralProperty("DEFAULT_XTANDEM_EVALUE"));

    }
}
