package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.dialog.SaveValidationDialog;
import com.compomics.peptizer.gui.interfaces.SaveValidationPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.compomics.peptizer.util.fileio.FileManager;
import com.compomics.peptizer.util.fileio.TempManager;
import com.compomics.peptizer.util.fileio.ValidationSaveToPDF;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 22:35:59
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class SaveValidationPanel_PDF extends JPanel implements SaveValidationPanel {
	// Class specific log4j logger for SaveValidationPanel_PDF instances.
	 private static Logger logger = Logger.getLogger(SaveValidationPanel_PDF.class);

    /**
     * The Singleton instance of this Panel.
     */
    private static SaveValidationPanel_PDF iSingleton = null;

    /**
     * The static instance of the parent SaveValidationDialog.
     */
    private static SaveValidationDialog iDialog;

    /**
     * The Mediator used by the Model.
     */
    private static Mediator iMediator = null;
    private JTextField txtPDF = null;
    private JButton btnPDF = null;
    private JCheckBox chkComment = null;
    private static JCheckBox chkConfident = null;

    private File iOutput = null;


    /**
     * Returns the Singleton instance of SaveValidationPanel_PDF.
     *
     * @param aDialog
     */
    private SaveValidationPanel_PDF(SaveValidationDialog aDialog) {
        // Super constructor.
        super();

        // Set the super dialog.
        iDialog = aDialog;

        // Make sure the Table is using the correct Mediator.
        // If the combobox changes it's selection, make sure necesairy actions are performed!
        ActionListener listener = new MyActionListener();
        iDialog.addComboBoxListener(listener);

        // Get Selected Mediator.
        iMediator = iDialog.getSelectedMediator();
        // Construct JPanel
        construct();

        // Try to load parameters.
        String s = null;
        if ((s = MatConfig.getInstance().getGeneralProperty("SAVEVALIDATION_PDF")) != null) {
            setOutput(new File(s));
        }
    }

    /**
     * {@inheritDoc}
     */
    public static SaveValidationPanel_PDF getInstance(SaveValidationDialog aDialog) {

        if (iSingleton == null) {
            iSingleton = new SaveValidationPanel_PDF(aDialog);
        } else {
            // Bizar singleton construction, i know. :)
            // This singleton panel must maintain a coupling to the JDialog to follow the Mediator Combobox.
            // If a Save dialog is launched a second time, this Panel has a pointer to the old JDialog.
            // Therefor, fetch the ActionListener on the ComboBox from the Old Dialog, and place them on the new Dialog & reset the Mediator.
            ActionListener[] lActionListeners = iDialog.getComboBoxListeners();
            for (ActionListener lActionListener : lActionListeners) {
                if (lActionListener instanceof MyActionListener) {
                    aDialog.addComboBoxListener(lActionListener);
                }
            }
            iDialog = aDialog;
            iMediator = iDialog.getSelectedMediator();
            rebuildOutput();
        }
        return iSingleton;
    }

    /**
     * Construct this SaveValidationPanel_PDF instance.
     */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select a target to save the validation.");

        // Components initiation

        // JTextField
        txtPDF = new JTextField();
        txtPDF.setFont(txtPDF.getFont().deriveFont(11.0F));
        txtPDF.setBorder(BorderFactory.createEmptyBorder());
        txtPDF.setEditable(false);
        txtPDF.setText("/");

        // JButton
        btnPDF = new JButton();
        btnPDF.setText("Browse");
        btnPDF.setMnemonic(KeyEvent.VK_B);
        btnPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pdfSelection();
            }
        });

        // Checkbox to include non-confident peptideidentifciations.
        chkComment = new JCheckBox("Include validation comments");
        chkComment.setSelected(true);

        // Checkbox to include confident not-matched peptideidentifciations.
        chkConfident = new JCheckBox("Include confident identifications that did not match the profile");
        chkConfident.setSelected(false);
        chkConfident.setEnabled(false);

        updateSelectionBox();
        // Put target on the Top panel.
        JPanel jpanMiddle = new JPanel();
        jpanMiddle.setLayout(new BoxLayout(jpanMiddle, BoxLayout.PAGE_AXIS));
        jpanMiddle.add(chkComment);
        jpanMiddle.add(Box.createHorizontalStrut(10));
        jpanMiddle.add(chkConfident);
        jpanMiddle.add(Box.createHorizontalGlue());
        jpanMiddle.setBorder(BorderFactory.createTitledBorder("Options"));

        // Put target on the Top panel.
        JPanel jpanBottom = new JPanel();
        jpanBottom.setLayout(new BoxLayout(jpanBottom, BoxLayout.LINE_AXIS));
        jpanBottom.add(txtPDF);
        jpanBottom.add(Box.createHorizontalStrut(10));
        jpanBottom.add(btnPDF);
        jpanBottom.add(Box.createHorizontalGlue());
        jpanBottom.setBorder(BorderFactory.createTitledBorder("Target"));

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.PAGE_AXIS));
        jpanMain.add(jpanMiddle);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanBottom);

        this.add(jpanMain);
        this.validate();

    }

    /**
     * Select PDF file.
     */
    private void pdfSelection() {
        if (FileManager.getInstance().selectPDFOutput(this)) {
            iOutput = (FileManager.getInstance().getPDFOutput());
            try {
                txtPDF.setText(iOutput.getCanonicalPath());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Returns an instance to save selected identifications and validation.
     *
     * @return ValidationSaver to save validation of selected identifications.
     */
    public ValidationSaver getValidationSaver() {
        if (iOutput != null) {
            DefaultProgressBar lProgress = new DefaultProgressBar((JFrame) SwingUtilities.getRoot(iMediator), "Writing pdf results file into " + iOutput + " .", 0, 1);
            ValidationSaveToPDF lValidationSaver = new ValidationSaveToPDF(iOutput);
            if (lValidationSaver instanceof ValidationSaveToPDF) {
                lValidationSaver.setComments(chkComment.isSelected());
            }
            lValidationSaver.setIncludeConfidentNotSelected(chkConfident.isSelected());

            return lValidationSaver;
        } else {
            JOptionPane.showMessageDialog(this.getParent(), "A pdf file must be selected first!!", "Validation saver to PDF failed..", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Set the file we want to save our validation.
     *
     * @param aOutput File to save our results..
     */
    public void setOutput(File aOutput) {
        iOutput = aOutput;
        try {
            txtPDF.setText(iOutput.getCanonicalPath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String toString() {
        return "Save to PDF";
    }

    /**
     * If the Mediator changes in selection, the table can change as well. In that case, rebuild the iOutput HashMap.
     * Save the properties that were allready set!
     */
    private static void rebuildOutput() {
        // This will be the new output map.
        HashMap lProperties = new HashMap();
        // Update the selection boxes.
        updateSelectionBox();
    }

    private static void updateSelectionBox() {
        TempManager lTempFileManager = TempManager.getInstance();

        boolean hasObjectStream_good = lTempFileManager.getNumberOfFiles(iMediator.getSelectedPeptideIdentifications(), TempFileEnum.CONFIDENT_NOT_SELECTED) > 0;
        boolean hasObjectStream_bad = lTempFileManager.getNumberOfFiles(iMediator.getSelectedPeptideIdentifications(), TempFileEnum.NON_CONFIDENT) > 0;

        if (hasObjectStream_good) {
            chkConfident.setEnabled(true);
            chkConfident.setSelected(true);
        } else {
            chkConfident.setEnabled(false);
            chkConfident.setSelected(false);
        }
    }

    /**
     * The ActionListener is place on the combobox of the parent SaveValidationDialog.
     * Whenever it's selection is changed, make sure the Mediator used by the TableModel follows the selection by this Listener!
     */
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            iMediator = iDialog.getSelectedMediator();
            rebuildOutput();
        }
    }
}
