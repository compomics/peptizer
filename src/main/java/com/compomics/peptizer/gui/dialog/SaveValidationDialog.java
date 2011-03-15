package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.component.SaveValidationPanel_CSV;
import com.compomics.peptizer.gui.component.SaveValidationPanel_Ms_Lims;
import com.compomics.peptizer.gui.component.SaveValidationPanel_PDF;
import com.compomics.peptizer.gui.interfaces.SaveValidationPanel;
import com.compomics.peptizer.gui.listener.SaveActionListener;
import com.compomics.peptizer.gui.model.MediatorListCellRendererImpl;
import com.compomics.peptizer.interfaces.ValidationSaver;
import org.apache.log4j.Logger;
import org.divxdede.swing.busy.JBusyComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 14:50:11
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class SaveValidationDialog extends JDialog {
    // Class specific log4j logger for SaveValidationDialog instances.
    private static Logger logger = Logger.getLogger(SaveValidationDialog.class);
    /**
     * The main frame.
     */
    PeptizerGUI iPeptizerGUI = null;


    // GUI components.
    private JComboBox cmbSavers;
    private JComboBox cmbMediators;
    private JButton btnSave;
    private JButton btnCancel;

    private JBusyComponent<JPanel> busyComponent = null;

    private JPanel jpanTargetProperties;
    private JPanel jpanTarget;


    /**
     * Create a new dialog to save the validation.
     *
     * @param aPeptizerGUI parent JFrame for the dialog.
     */
    public SaveValidationDialog(PeptizerGUI aPeptizerGUI) {
        super(aPeptizerGUI, "Save Task");
        iPeptizerGUI = aPeptizerGUI;
        construct();
        this.setVisible(true);
    }

    /**
     * Construct the dialog.
     */
    private void construct() {

        // 0. Dialog settings
        this.setLocation(iPeptizerGUI.getLocation().x + 50, iPeptizerGUI.getLocation().y + 100);

        // 1. The source.
        cmbMediators = new JComboBox();
        cmbMediators.setModel(new DefaultComboBoxModel(iPeptizerGUI.getTabs()));
        cmbMediators.setRenderer(new MediatorListCellRendererImpl(iPeptizerGUI));

        JPanel jpanMediators = new JPanel();
        jpanMediators.setLayout(new BoxLayout(jpanMediators, BoxLayout.LINE_AXIS));
        jpanMediators.setBorder(BorderFactory.createTitledBorder("1. Select the task"));
        jpanMediators.setToolTipText("Select the task to be saved.");
        jpanMediators.add(cmbMediators);
        jpanMediators.add(Box.createHorizontalGlue());

        // Select showing tab in advance.
        int lTaskIndex = iPeptizerGUI.getSelectedTabIndex();
        cmbMediators.setSelectedIndex(lTaskIndex);

        // 2. The target. Manually control the number of possible targets.
        Vector lSavers = new Vector();
        lSavers.add(SaveValidationPanel_CSV.getInstance(this));
        lSavers.add(SaveValidationPanel_PDF.getInstance(this));
        lSavers.add(SaveValidationPanel_Ms_Lims.getInstance(this));
        cmbSavers = new JComboBox(lSavers);


        cmbSavers.setMaximumSize(new Dimension(500, cmbSavers.getPreferredSize().height));

        jpanTargetProperties = new JPanel(new BorderLayout());
        jpanTargetProperties.add((JPanel) cmbSavers.getItemAt(0), 0);

        JPanel jpanCombo = new JPanel();
        jpanCombo.setLayout(new BoxLayout(jpanCombo, BoxLayout.PAGE_AXIS));
        jpanCombo.add(cmbSavers);
        jpanCombo.add(Box.createVerticalGlue());

        jpanTarget = new JPanel();
        jpanTarget.setLayout(new BoxLayout(jpanTarget, BoxLayout.LINE_AXIS));
        jpanTarget.setBorder(BorderFactory.createTitledBorder("2. Save to"));
        jpanTarget.setToolTipText("Define the target to save the selected task.");

        jpanTarget.add(jpanCombo);
        jpanTarget.add(Box.createHorizontalStrut(10));
        jpanTarget.add(jpanTargetProperties);
        jpanTarget.add(Box.createHorizontalGlue());

        // 4. Go!
        btnSave = new JButton("Save");
        btnSave.setMnemonic(KeyEvent.VK_S);

        btnCancel = new JButton("Cancel");


        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.LINE_AXIS));
        jpanButtons.add(Box.createGlue());
        jpanButtons.add(btnSave);
        jpanButtons.add(Box.createHorizontalStrut(10));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));

        JPanel jpanContent = new JPanel();
        jpanContent.setLayout(new BoxLayout(jpanContent, BoxLayout.PAGE_AXIS));
        jpanContent.add(Box.createVerticalStrut(5));
        jpanContent.add(jpanMediators);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanTarget);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanButtons);
        jpanContent.add(Box.createVerticalStrut(5));

        JPanel jpanHorizontalSpace = new JPanel();
        jpanHorizontalSpace.setLayout(new BoxLayout(jpanHorizontalSpace, BoxLayout.LINE_AXIS));
        jpanHorizontalSpace.add(Box.createHorizontalStrut(5));
        jpanHorizontalSpace.add(jpanContent);
        jpanHorizontalSpace.add(Box.createHorizontalStrut(5));

        busyComponent = new JBusyComponent<JPanel>(jpanHorizontalSpace);

        this.add(busyComponent);
        this.pack();

        this.jpanTarget.setMaximumSize(new Dimension(3000, jpanTarget.getSize().height));
        int lNewWidth = iPeptizerGUI.getSize().width - (new Double(iPeptizerGUI.getSize().width * 0.60)).intValue();
        setSize(new Dimension(lNewWidth, this.getSize().height));

        // Finally, add the listeners to the components.
        setListeners();
    }

    /**
     * Set listeners to the components of the save dialog.
     */
    private void setListeners() {
        btnSave.addActionListener(new SaveActionListener(this, busyComponent));

        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SaveValidationDialog.this.cancelPressed();
            }
        });

        cmbSavers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jpanTargetProperties.remove(0);
                jpanTargetProperties.add((JPanel) cmbSavers.getSelectedItem(), 0);
                jpanTargetProperties.validate();
                jpanTarget.repaint();
            }
        });
    }

    /**
     * Cancel the Task creation.
     */
    private void cancelPressed() {
        this.dispose();
    }


    /**
     * Returns the currently selected Mediator.
     *
     * @return Mediator as selected in the dialog.
     */
    public Mediator getSelectedMediator() {
        return (Mediator) cmbMediators.getSelectedItem();
    }

    /**
     * Add a ActionListener when the Mediator combobox fires an event.
     *
     * @param lActionListener
     */
    public void addComboBoxListener(ActionListener lActionListener) {
        cmbMediators.addActionListener(lActionListener);
    }

    /**
     * Returns the ActionListeners on the Mediator combobox.
     *
     * @return ActionListener[] listeners on the Mediator combobox.
     */
    public ActionListener[] getComboBoxListeners() {
        return cmbMediators.getListeners(ActionListener.class);
    }

    /**
     * Returns the active ValidationSaver of the Dialog.
     *
     * @return
     */
    public ValidationSaver getValidationSaver() {
        return ((SaveValidationPanel) cmbSavers.getSelectedItem()).getValidationSaver();
    }

    /**
     * Returns the active Collection of selected PeptideIdentifications from the Mediator.
     *
     * @return
     */
    public SelectedPeptideIdentifications getSelectedPeptideIdentifications() {
        return ((Mediator) cmbMediators.getSelectedItem()).getSelectedPeptideIdentifications();
    }

    /**
     * Set the last save status of the SaveDialog for this Mediator.
     *
     * @param b
     */
    public void setChangedSinceLastSave(boolean b) {
        ((Mediator) cmbMediators.getSelectedItem()).setChangedSinceLastSave(b);
    }
}
