package com.compomics.peptizer.gui.listener;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.model.PeptideSequenceTreeFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is a
 */
public class AddSequenceFilterActionListener implements ActionListener {

    /**
     * Reference to the iPeptizerGUI.
     */
    PeptizerGUI iPeptizerGUI = null;


    /**
     * Construct a new ActionListener that will operate on the specified PeptizerGUI.
     *
     * @param aParent
     */
    public AddSequenceFilterActionListener(PeptizerGUI aParent) {
        iPeptizerGUI = aParent;
    }

    /**
     * This method will open a input Dialog on the PeptizerGUI.
     * If a valid peptide sequence is specified by the user, then an new SequenceFilter will be applied to the identification tree.
     *
     * @param aActionEvent
     */
    public void actionPerformed(ActionEvent aActionEvent) {

        // Set the filter.
        Mediator lMediator = (Mediator) iPeptizerGUI.getTabs()[iPeptizerGUI.getSelectedTabIndex()];

        if (lMediator.isFiltered()) {
            lMediator.disableFilter();
        } else {

            String lResult = getInput();
            // Create a sequence filter for the Tree.
            PeptideSequenceTreeFilter lFilter = new PeptideSequenceTreeFilter();
            lFilter.setSequence(lResult);
            // Set the filter.
            lMediator.setFilter(lFilter);
        }
    }

    /**
     * Convenience method to show the input dialog.
     *
     * @return
     */
    private String getInput() {
        return JOptionPane.showInputDialog(iPeptizerGUI, "Peptide sequence:", "Specify the peptide sequence filter for the identification Tree", JOptionPane.QUESTION_MESSAGE);
    }
}
