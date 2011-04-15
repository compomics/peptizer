package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.util.CommentGenerator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.ValidationReport;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 3-aug-2007
 * Time: 13:45:49
 */

/**
 * Class description:
 * ------------------
 * This class was developed to offer a more complex Input dialog for commenting on validation.
 */
public class CommentValidationDialog extends JDialog {
    // Class specific log4j logger for CommentValidationDialog instances.
    private static Logger logger = Logger.getLogger(CommentValidationDialog.class);

    /**
     * The JLabel that will display information on the input.
     */
    private JLabel txtInformation = null;

    /**
     * The textArea that can receive input.
     */
    private JTextArea txtAutoInput = null;

    /**
     * The textArea that can user receive input.
     */
    private JTextArea txtUserInput = null;

    /**
     * This button can be pressed to confirm the input and exit the dialog.
     */
    private JButton btnOK = null;

    /**
     * This button can be pressed to discard the input and exit the dialog.
     */
    private JButton btnCancel = null;

    /**
     * The PeptideIdentification on which we comment.
     */
    private PeptideIdentification iPeptideIdentification = null;

    /**
     * The peptidehit number that is being validated.
     * 1 means the best ranked peptidehit, first peptide in the table.
     * Default to -1 which is also used when the spectrum is rejected.
     */
    private int iCorrectPeptidehitNumber = -1;

    /**
     * The Boolean whether this dialog accepts an identification.
     * If true, accept formatting.
     * If false, reject formatting.
     */
    private Boolean iBoolAccept = null;

    /**
     * boolean that tracks if this identification has a Accept/Reject status.
     */
    private boolean iValidated;

    /**
     * Boolean that tracks if these comments need to be editable.
     */
    private boolean iEditable;

    /**
     * The Mediator instance.
     */
    private Mediator iMediator;

    /**
     * This constructor mimics the constructor on the superclass and allows
     * specification of the parent JFrame as well as the title for the dialog.
     * Note that about dialog is always modal!
     *
     * @param aTitle      String with the title for this dialog.
     * @param aMediator   Mediator
     * @param aBoolAccept boolean whether this dialog serves for accepting a PeptideIdentification.
     *                    When true, this means we are accepting. Otherwise when false, this means we are rejecting and the dialog formatting will adapt to this boolean.
     */
    public CommentValidationDialog(String aTitle, Mediator aMediator, boolean aBoolAccept, boolean aValidated) {
        this(aTitle, aMediator, aBoolAccept, aValidated, true);
    }

    /**
     * This constructor mimics the constructor on the superclass and allows
     * specification of the parent JFrame as well as the title for the dialog.
     * Note that about dialog is always modal!
     *
     * @param aTitle      String with the title for this dialog.
     * @param aMediator   Mediator
     * @param aBoolAccept boolean whether this dialog serves for accepting a PeptideIdentification.
     *                    When true, this means we are accepting. Otherwise when false, this means we are rejecting and the dialog formatting will adapt to this boolean.
     * @param aEditable   When true, the panel will be non-editable.
     */
    public CommentValidationDialog(String aTitle, Mediator aMediator, boolean aBoolAccept, boolean aValidated, boolean aEditable) {
        super(((PeptizerGUI) SwingUtilities.getRoot(aMediator)), aTitle, true);

        iMediator = aMediator;
        iValidated = aValidated;
        iEditable = aEditable;
        iPeptideIdentification = iMediator.getActivePeptideIdentification();
        iBoolAccept = aBoolAccept;

        if (iBoolAccept) {
            // If accepting, get the selected column as correct peptidehit.
            iCorrectPeptidehitNumber = iMediator.getSelectedTableColumn();
        } else {
            // If rejecting, set correctNumber to -1.
            iCorrectPeptidehitNumber = -1;
        }

        this.constructScreen();

        if (aEditable == false) {
            // then disable the 'edit' components.
            btnCancel.setVisible(true);
            btnOK.setVisible(false);
            txtUserInput.setEditable(false);
            txtAutoInput.setEditable(false);
        }

        this.pack();
        this.setVisible(true);
    }

    /**
     * This method constructs all components and lays them out on the screen.
     */
    private void constructScreen() {
        // Components.
        // The textarea.
        txtInformation = new JLabel();

        // Set information text dynamic on the PeptideIdentification.
        setInformationText();

        // Create & format Auto info JTextArea
        txtAutoInput = new JTextArea(10, 60);
        txtAutoInput.setEditable(false);
        setAutoTextArea();


        // Create & format User info JTextArea
        txtUserInput = new JTextArea(10, 60);
        setUserTextArea();


        setTxtAreaColorCoding();


        // The OK button.
        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }
        });


        // The Cancel button.
        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CancelPressed();
            }
        });

        // The containers.
        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.PAGE_AXIS));

        // Information Panel.
        JPanel jpanInformation = new JPanel();
        jpanInformation.setLayout(new BoxLayout(jpanInformation, BoxLayout.LINE_AXIS));

        // Button panel.
        JPanel jpanButton = new JPanel();
        jpanButton.setLayout(new BoxLayout(jpanButton, BoxLayout.LINE_AXIS));

        // Scrollpane for textarea + panel for scrollpane.
        JScrollPane jspAutoText = new JScrollPane(txtAutoInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspAutoText.setBorder(new TitledBorder("Auto-generated comment"));

        JScrollPane jspUserText = new JScrollPane(txtUserInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspUserText.setBorder(new TitledBorder("User comment"));

        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.Y_AXIS));
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspAutoText.getHeight())));
        jpanScroll.add(jspAutoText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspAutoText.getHeight())));
        jpanScroll.add(jspUserText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspAutoText.getHeight())));

        // Start adding.
        jpanButton.add(Box.createHorizontalGlue());
        jpanButton.add(btnCancel);
        jpanButton.add(Box.createRigidArea(new Dimension(10, btnCancel.getHeight())));
        jpanButton.add(btnOK);
        jpanButton.add(Box.createRigidArea(new Dimension(10, btnCancel.getHeight())));


        jpanInformation.add(Box.createRigidArea(new Dimension(10, txtInformation.getHeight())));
        jpanInformation.add(txtInformation);
        jpanInformation.add(Box.createRigidArea(new Dimension(10, txtInformation.getHeight())));


        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanInformation);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanScroll);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanButton);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 15)));

        // Pack and go.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);

        // Set location
        int x = (new Double(Toolkit.getDefaultToolkit().getScreenSize().width * 0.2)).intValue();
        int y = (new Double(Toolkit.getDefaultToolkit().getScreenSize().height * 0.2)).intValue();
        this.setLocation(x, y);

    }

    /**
     * Compose the automatic text area component.
     */
    private void setAutoTextArea() {
        // Enable linewrapping by words.
        txtAutoInput.setLineWrap(true);
        txtAutoInput.setWrapStyleWord(true);

        // Set neutral font.
        txtAutoInput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        if (iPeptideIdentification.getValidationReport().getAutoComment() != ValidationReport.DEFAULT_COMMENT) {
            txtAutoInput.setText(iPeptideIdentification.getValidationReport().getAutoComment());
        } else {
            txtAutoInput.setText(CommentGenerator.getCommentForSelectiveAgents(iPeptideIdentification, iMediator.getSelectedTableColumn()));
        }
    }

    /**
     * Convenience method to apply color coding (green, red, black) to txtArea components (accept, reject, unvalidated)
     */
    private void setTxtAreaColorCoding() {
        if (iValidated) {
            if (iBoolAccept) {
                // Set ForegroundColor variable to the situation (accept & reject)
                // Accepting!
                txtAutoInput.setForeground(new Color(0, 200, 0));
                txtUserInput.setForeground(new Color(0, 200, 0));
            } else {
                // Rejecting!
                txtAutoInput.setForeground(new Color(175, 0, 0));
                txtUserInput.setForeground(new Color(175, 0, 0));
            }
        } else {
            // not validated!
            txtAutoInput.setForeground(Color.DARK_GRAY);
            txtUserInput.setForeground(Color.DARK_GRAY);
        }
    }


    /**
     * Compose the User text area component.
     */
    private void setUserTextArea() {
        // Enable linewrapping by words.
        txtUserInput.setLineWrap(true);
        txtUserInput.setWrapStyleWord(true);

        // Set neutral font.
        txtUserInput.setFont(new Font("Monospaced", Font.PLAIN, 12));


        if (iPeptideIdentification.getValidationReport().getAutoComment() != ValidationReport.DEFAULT_COMMENT) {
            txtUserInput.setText(iPeptideIdentification.getValidationReport().getUserComment());
        }
    }

    private void setInformationText() {
        StringBuilder sb = new StringBuilder();
        if (iBoolAccept) {
            // Accept!
            sb.append("<HTML>");

            if (iValidated) {
                sb.append("<BODY TEXT=\"#339900\">");
                sb.append("<BIG>Accept?</BIG>");
            } else {
                sb.append("<BIG>Info</BIG>");
                sb.append("<BODY TEXT=\"#000000\">");
            }

            sb.append("<DL>");
            sb.append("<DT>peptide:<DD>" + iCorrectPeptidehitNumber +
                    "<DT>sequence:<DD>" + iPeptideIdentification.getPeptideHit(iCorrectPeptidehitNumber - 1).getSequence() +
                    "<DT>spectrum:<DD>" + iPeptideIdentification.getSpectrum().getName() +
                    "<DT>peptizer name:<DD>" + iPeptideIdentification.getName() +
                    "</DL>");
        } else {
            // Reject!
            sb.append("<HTML>");

            if (iValidated) {
                sb.append("<BODY TEXT=\"#cc3300\">");
                sb.append("<BIG>Reject?</BIG>");
            } else {
                sb.append("<BODY TEXT=\"#000000\">");
                sb.append("<BIG>Info</BIG>");
            }

            sb.append("<DL>");
            sb.append("<DT>spectrum:<DD>" + iPeptideIdentification.getSpectrum().getName() +
                    "<DT>peptizer name:<DD>" + iPeptideIdentification.getName() +
                    "</DL>");

        }
        // Close tags,
        sb.append("</BODY></HTML>");

        txtInformation.setText(sb.toString());
    }

    /**
     * Cancel this Dialog in a nice way without any changes.
     */
    private void CancelPressed() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * Save the validation and comment.
     */
    private void OKPressed() {
        String lAutoComment = txtAutoInput.getText();
        String lUserComment = txtUserInput.getText();

        String linesep = System.getProperty("line.separator");
        iPeptideIdentification.getValidationReport().setAutoComment(lAutoComment.replaceAll(linesep, "*"));
        iPeptideIdentification.getValidationReport().setUserComment(lUserComment);

        iPeptideIdentification.getValidationReport().setCorrectPeptideHitNumber(iCorrectPeptidehitNumber);
        // True if we are accepting, False if we are rejecting.
        iPeptideIdentification.getValidationReport().setResult(iBoolAccept);

        // Make the mediator aware validation has changed.
        iMediator.validationPerformed();

        this.CancelPressed();
    }
}
