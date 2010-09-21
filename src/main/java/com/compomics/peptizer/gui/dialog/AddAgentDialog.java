package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.interfaces.Updateable;
import com.compomics.peptizer.util.AgentFactory;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jan 7, 2009
 * Time: 10:01:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddAgentDialog extends JDialog {
	// Class specific log4j logger for AddAgentDialog instances.
	 private static Logger logger = Logger.getLogger(AddAgentDialog.class);

    private JPanel jpanMain;

    private JRadioButton rdbClasspath;
    private JRadioButton rdbAllAgents;

    private JTextField txtClasspath;
    private JTextField txtName;
    private JList listAllAgents;
    private JButton btnCancel;
    private JButton btnOK;

    private HashMap iAgentList;
    private Updateable iUpdateable;

    public AddAgentDialog(JFrame aFrame, Updateable aUpdateable) throws HeadlessException {
        super(aFrame, "Add Agent to the table");
        iUpdateable = aUpdateable;
        constructScreen();
        setListeners();


        // Pack and go.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
    }

    private void constructScreen() {
        // The list with all Agents comes at the top.
        // Only display those not allready in the table!
        rdbAllAgents = new JRadioButton("All Agents");
        rdbAllAgents.setToolTipText("All the Agents as listed in the 'agent_complete.xml' configuration file.");
        rdbAllAgents.setSelected(true);


        // Build the list from unused AgentID's.
        iAgentList = AgentFactory.getInstance().getUnusedAgents();
        listAllAgents = new JList(iAgentList.keySet().toArray());

        listAllAgents.setCellRenderer(new ListCellRenderer() {

            JLabel lbl = new JLabel();
            Color selected_color = new Color(36, 96, 183);
            Color nonselected_color = Color.darkGray;

            public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                lbl.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

                if (iAgentList.containsKey(value)) {
                    String s = ((Properties) iAgentList.get(value)).get("name").toString();
                    lbl.setText(s);
                } else {
                    lbl.setText(value.toString());
                }
                if (isSelected) {
                    lbl.setForeground(selected_color);
                } else {
                    lbl.setForeground(nonselected_color);
                }
                return lbl;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        JPanel jpanAllAgents = new JPanel(new FlowLayout(FlowLayout.LEADING));
        jpanAllAgents.add(rdbAllAgents);
        jpanAllAgents.add(listAllAgents);

        // Next comes the direct input by classpath.
        rdbClasspath = new JRadioButton("Agent from classpath");
        rdbClasspath.setToolTipText("Add the Agent by dynamic loading from the classpath. ex: 'com.compomics.peptizer.util.agents.Deltascore");
        txtClasspath = new JTextField("enter class reference ..");
        txtName = new JTextField("enter name ..");
        txtClasspath.setFont(txtClasspath.getFont().deriveFont(11f));
        txtClasspath.setColumns(30);
        txtName.setFont(txtClasspath.getFont());
        txtName.setColumns(20);
        JPanel jpanClasspath = new JPanel(new FlowLayout(FlowLayout.LEADING));
        jpanClasspath.add(rdbClasspath);
        jpanClasspath.add(txtName);
        jpanClasspath.add(txtClasspath);

        // apply the txtfield border to the list!
        listAllAgents.setBorder(txtClasspath.getBorder());

        // bind the radiobuttons.
        ButtonGroup group = new ButtonGroup();
        group.add(rdbAllAgents);
        group.add(rdbClasspath);

        // Final OK and Cancel button.
        btnOK = new JButton("Add");
        btnCancel = new JButton("Cancel");
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        jpanButtons.add(btnOK);
        jpanButtons.add(btnCancel);

        // Put it all together.
        jpanMain = new JPanel();
        BoxLayout lBoxLayout = new BoxLayout(jpanMain, BoxLayout.PAGE_AXIS);
        jpanMain.setLayout(lBoxLayout);
        jpanMain.add(jpanAllAgents);
        jpanMain.add(jpanClasspath);
        jpanMain.add(jpanButtons);

    }

    private void setListeners() {
        // Disable & enable changes upon radiobutton actions.
        rdbAllAgents.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                listAllAgents.setEnabled(true);

                txtClasspath.setEnabled(false);
                txtName.setEnabled(false);
            }
        });

        rdbClasspath.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                txtClasspath.setEnabled(true);
                txtName.setEnabled(true);

                listAllAgents.setEnabled(false);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelPressed();
            }
        });

        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                okPressed();
            }
        });
    }

    private void cancelPressed() {
        this.setVisible(false);
        dispose();
    }

    private void okPressed() {
        // Get the selected AgentID's.
        ArrayList<String> list = new ArrayList<String>();

        if (rdbClasspath.isSelected()) {
            String lAgentID = txtClasspath.getText();

            Properties prop = new Properties();
            prop.put("name", txtName.getText());
            prop.put("veto", false);
            prop.put("active", true);

            iAgentList.put(lAgentID, prop);

            list.add(lAgentID);

        } else {
            Object[] lSelectedAgents = listAllAgents.getSelectedValues();

            for (int i = 0; i < lSelectedAgents.length; i++) {
                Object o = lSelectedAgents[i];
                list.add(o.toString());
            }
        }

        // Ok, all the class references ar ein the 'list' object now.
        Iterator iter = list.iterator();
        MatConfig config = MatConfig.getInstance();
        while (iter.hasNext()) {
            String lClassReference = (String) iter.next();
            Properties prop = (Properties) iAgentList.get(lClassReference);
            config.addAgent(lClassReference, prop);
        }
        AgentFactory.reset();
        iUpdateable.update();
        cancelPressed();
    }


}
