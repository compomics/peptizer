package com.compomics.peptizer.gui.listener;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is a
 */
public class DisableAgentFilterActionListener implements ActionListener {
    private PeptizerGUI iPeptizerGUI = null;

    public DisableAgentFilterActionListener(PeptizerGUI aPeptizerGUI) {
        iPeptizerGUI = aPeptizerGUI;
    }

    public void actionPerformed(ActionEvent aActionEvent) {
        ((Mediator) iPeptizerGUI.getTabs()[iPeptizerGUI.getSelectedTabIndex()]).disableFilter();
    }
}
