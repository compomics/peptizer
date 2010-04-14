package com.compomics.peptizer.gui.dialog;


import com.compomics.peptizer.gui.component.BooleanPanel;
import com.compomics.peptizer.interfaces.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
public class AgentFilterDialog extends JDialog {

    private JButton btnOK = null;
    private JButton btnCancel = null;

    // The properties.
    Properties iProperties = null;
    Agent[] iAgents = null;
    boolean[] iValues = null;

    // Reference Variable to store the result.
    private ArrayList iResult;

    /**
     * Constructor for a properties dialog. Takes
     *
     * @param aParent JFrame that is the parent of this JDialog.
     * @param aTitle  String with the title for this dialog
     * @param aAgents Array with Agents.
     */
    public AgentFilterDialog(JFrame aParent, String aTitle, Agent[] aAgents, ArrayList aResult) {
        super(aParent, aTitle, true);
        iAgents = aAgents;
        iValues = new boolean[iAgents.length];
        iResult = aResult;
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

        // Construct the screen with the created Strings.
        this.constructScreen();
        validate();
        pack();

        setResizable(false);
        setVisible(true);
    }


    /**
     * This method will initialize and lay-out all components.
     */
    private void constructScreen() {
        // 1. set String[][]

        JPanel jpanTop = new JPanel();
        jpanTop.setBorder(BorderFactory.createTitledBorder("Parameter Dialog"));

        BooleanPanel jpanBoolean = new BooleanPanel(iAgents, iValues);
        jpanTop.add(jpanBoolean);

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

        for (int i = 0; i < iAgents.length; i++) {
            Agent lAgent = iAgents[i];
            // If the boolean was set to true, include in the list.
            if (iValues[i]) {
                iResult.add(lAgent);
            }
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