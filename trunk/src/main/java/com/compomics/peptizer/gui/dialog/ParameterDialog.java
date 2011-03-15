package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.component.JLabelAndComponentPanel;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 24-jun-2007
 * Time: 19:36:31
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class ParameterDialog extends JDialog {
	// Class specific log4j logger for ParameterDialog instances.
	 private static Logger logger = Logger.getLogger(ParameterDialog.class);

    private JComponent[] compFields = null;
    private JLabel[] lblFields = null;

    private JButton btnOK = null;
    private JButton btnCancel = null;

    // The properties.
    Properties iProperties = null;
    String[][] iPropertiesArray = null;

    /**
     * Constructor for a properties dialog. Takes
     *
     * @param aParent     JFrame that is the parent of this JDialog.
     * @param aTitle      String with the title for this dialog
     * @param aProperties Properties of the Agent.
     */
    public ParameterDialog(JFrame aParent, String aTitle, Properties aProperties) {
        super(aParent, aTitle, true);
        iProperties = aProperties;
        this.showParameterDialog();
    }

    /**
     * This method actually shows the ConnectionDialog.
     * It takes care of the GUI related stuff.
     */
    private void showParameterDialog() {
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                cancelTriggered();
            }
        });

        if (getParent().getLocation().getX() <= 0 || getParent().getLocation().getY() <= 0) {
            this.setLocation(150, 150);
        } else {
            this.setLocation((int) getParent().getLocation().getX() + 150, (int) getParent().getLocation().getY() + 150);
        }

        // First read the Properties into intermediate Strings.
        this.createStrings();
        // Construct the screen with the created Strings.
        this.constructScreen();
        validate();
        pack();

        doOptimalResize();

        setResizable(false);
        setVisible(true);
    }

    /**
     * Construct String[] according to the number of properties.
     */
    private void createStrings() {
        iPropertiesArray = new String[iProperties.size()][2];
        Iterator iter = iProperties.keySet().iterator();
        int lCount = 0;
        while (iter.hasNext()) {
            String aKey = (String) iter.next();
            iPropertiesArray[lCount][0] = aKey;
            iPropertiesArray[lCount][1] = (String) iProperties.get(aKey);
            lCount++;
        }
    }

    private void doOptimalResize() {

        int lLabelWidth = 0;
        int lTextFieldWidth = 0;

        for (int i = 0; i < iPropertiesArray.length; i++) {

            // Define width.
            if (lLabelWidth < lblFields[i].getSize().width) {
                lLabelWidth = lblFields[i].getSize().width;
            }

            if (lTextFieldWidth < compFields[i].getSize().width) {
                lTextFieldWidth = compFields[i].getSize().width;
            }
        }

        for (int i = 0; i < iPropertiesArray.length; i++) {
            lblFields[i].setPreferredSize(new Dimension(lLabelWidth, lblFields[i].getSize().height));
            compFields[i].setPreferredSize(new Dimension(lTextFieldWidth, compFields[i].getSize().height));
        }
    }

    /**
     * This method will initialize and lay-out all components.
     */
    private void constructScreen() {
        // 1. set String[][]

        compFields = new JComponent[iProperties.size()];
        lblFields = new JLabel[iProperties.size()];

        for (int i = 0; i < iPropertiesArray.length; i++) {
            String[] lStrings = iPropertiesArray[i];
            String aKey = lStrings[0];
            String aValue = lStrings[1].toUpperCase();

            JLabel lblField = new JLabel(aKey);
            JComponent lComponent;
            if(aValue.equals("TRUE") || aValue.equals("FALSE")){
                lComponent = new JCheckBox();
                ((JCheckBox)lComponent).setSelected(Boolean.parseBoolean(aValue));
            }else{
                lComponent = new JTextField();
                ((JTextField)lComponent).setText(aValue);
            }

            lComponent.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been typed.
                 * This event occurs when a key press is followed by a key release.
                 */
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        okTriggered();
                    }
                }
            });

            lComponent.setPreferredSize(new Dimension((lComponent.getFontMetrics(lComponent.getFont()).stringWidth(aValue) + 50), 25));

            compFields[i] = lComponent;

            lblFields[i] = lblField;
        }

        JLabelAndComponentPanel jpanTop = new JLabelAndComponentPanel(lblFields, compFields);
        jpanTop.setBorder(BorderFactory.createTitledBorder(""));

        btnOK = new JButton("Ok");
        //btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okTriggered();
            }
        });
        btnOK.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    okTriggered();
                }
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelTriggered();
            }
        });
        btnCancel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    cancelTriggered();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));

        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnOK);
        jpanButtons.add(Box.createRigidArea(new Dimension(15, btnOK.getHeight())));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));

        JPanel jpanTotal = new JPanel();
        jpanTotal.setLayout(new BoxLayout(jpanTotal, BoxLayout.Y_AXIS));
        jpanTotal.add(jpanTop);
        jpanTotal.add(Box.createRigidArea(new Dimension(jpanTop.getWidth(), 10)));
        jpanTotal.add(jpanButtons);

        this.getContentPane().add(jpanTotal, BorderLayout.CENTER);
    }

    /**
     * This method is called when the user attempts to connect.
     */
    private void okTriggered() {
        for (int i = 0; i < lblFields.length; i++) {
            String aKey = lblFields[i].getText();
            String aValue;

            if(compFields[i] instanceof JCheckBox){
                aValue = String.valueOf(((JCheckBox)compFields[i]).isSelected());
            }else{
                aValue = String.valueOf(((JTextField)compFields[i]).getText());

            }

            if (aValue.equals("")) {
                JOptionPane.showMessageDialog(this, aKey + " needs to be specified!", "Unspecified parameter!", JOptionPane.ERROR_MESSAGE);
                compFields[i].requestFocus();
                return;
            }

            iPropertiesArray[i][0] = aKey;
            iPropertiesArray[i][1] = aValue.toUpperCase();
        }
        // String[][] is reset with correct values, now set to Properties.
        for (int i = 0; i < iPropertiesArray.length; i++) {
            String[] lProperty = iPropertiesArray[i];
            iProperties.put(lProperty[0], lProperty[1]);
        }
        cancelTriggered();
    }

    /**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {
        this.setVisible(false);
        this.dispose();
    }
}


