package com.compomics.peptizer.gui.dialog;


import com.compomics.peptizer.interfaces.Agent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This dialog pops a information dialog.
 */
public class AgentInfoDialog extends JDialog {


    private Agent iAgent;

    public AgentInfoDialog(Agent aAgent) {
        iAgent = aAgent;

        initUI();
    }

    public final void initUI() {

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel name = new JLabel(iAgent.getDescription());
//        name.setFont(new Font("Serif", Font.BOLD, 13));
        name.setAlignmentX(0.5f);


        JPanel lInfoWrapper = new JPanel();
        lInfoWrapper.setLayout(new BoxLayout(lInfoWrapper, BoxLayout.X_AXIS));
        lInfoWrapper.add(Box.createHorizontalStrut(10));
        lInfoWrapper.add(name);
        lInfoWrapper.add(Box.createHorizontalStrut(10));

        add(lInfoWrapper);
        add(Box.createGlue());

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        close.setAlignmentX(0.5f);
        add(close);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("About Agent '" + iAgent.getName() + "'");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(300, 300);
        setLocation(100, 100);
    }
}
