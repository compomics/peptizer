package com.compomics.peptizer.gui.listener;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.dialog.AgentFilterDialog;
import com.compomics.peptizer.gui.model.AgentTreeFilter;
import com.compomics.peptizer.util.AgentFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class is an actionlistener which launches a new AgentFilterDialog.
 */
public class AddAgentFilterActionListener implements ActionListener {


    /**
     * Reference to the iPeptizerGUI.
     */
    PeptizerGUI iPeptizerGUI = null;


    /**
     * Construct a new ActionListener that will operate on the specified PeptizerGUI.
     *
     * @param aParent
     */
    public AddAgentFilterActionListener(PeptizerGUI aParent) {
        iPeptizerGUI = aParent;
    }


    public void actionPerformed(ActionEvent e) {
        ArrayList lAgents = new ArrayList();
        new AgentFilterDialog(iPeptizerGUI, "Select Agent's to filter the Tree.", AgentFactory.getInstance().getAllAgents(), lAgents);
        if (lAgents.size() > 0) {
            AgentTreeFilter lFilter = new AgentTreeFilter(lAgents);
            ((Mediator) iPeptizerGUI.getTabs()[iPeptizerGUI.getSelectedTabIndex()]).setFilter(lFilter);
        }
    }
}
