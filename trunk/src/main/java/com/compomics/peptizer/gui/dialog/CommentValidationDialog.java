package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.util.CommentGenerator;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import javax.swing.*;
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
    private JTextArea txaInput = null;

    /**
     * This button can be pressed to confirm the input and exit the dialog.
     */
    private JButton btnOK = null;

    /**
     * This button can be pressed to discard the input and exit the dialog.
     */
    private JButton btnOKWithoutComment = null;

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
    private boolean iBoolAccept;

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
    public CommentValidationDialog(String aTitle, Mediator aMediator, boolean aBoolAccept) {
        super(((PeptizerGUI) SwingUtilities.getRoot(aMediator)), aTitle, true);

        iMediator = aMediator;
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

        // Create & format JTextArea
        txaInput = new JTextArea(5, 80);
        setTextArea();

        // The OK button.
        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }
        });

        // The OKWithoutComment button.
        btnOKWithoutComment = new JButton("OK (No Comment)");
        btnOKWithoutComment.setMnemonic(KeyEvent.VK_K);
        btnOKWithoutComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKWithoutCommentPressed();
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
        JScrollPane jspText = new JScrollPane(txaInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.X_AXIS));
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));
        jpanScroll.add(jspText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));

        // Start adding.
        jpanButton.add(Box.createHorizontalGlue());
        jpanButton.add(btnCancel);
        jpanButton.add(Box.createRigidArea(new Dimension(10, btnOKWithoutComment.getHeight())));
        jpanButton.add(btnOKWithoutComment);
        jpanButton.add(Box.createRigidArea(new Dimension(10, btnOKWithoutComment.getHeight())));
        jpanButton.add(btnOK);
        jpanButton.add(Box.createRigidArea(new Dimension(15, btnOKWithoutComment.getHeight())));


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

    private void setTextArea() {
        // Enable linewrapping by words.
        txaInput.setLineWrap(true);
        txaInput.setWrapStyleWord(true);

        // Set neutral font.
        txaInput.setFont(new Font("Monospaced", Font.PLAIN, 12));

        if (iBoolAccept) {
            // Set ForegroundColor variable to the situation (accept & reject)
            // Accepting!
            txaInput.setForeground(new Color(0, 200, 0));
        } else {
            // Rejecting!
            txaInput.setForeground(new Color(175, 0, 0));
        }

        if (iPeptideIdentification.getValidationReport().getComment() != null) {
            txaInput.setText(iPeptideIdentification.getValidationReport().getComment());
        } else {
            txaInput.setText(CommentGenerator.getCommentForSelectiveAgents(iPeptideIdentification, iMediator.getSelectedTableColumn()));
        }
    }

    private void setInformationText() {
        StringBuilder sb = new StringBuilder();
        if (iBoolAccept) {
            // Accept!
            sb.append("<HTML><BODY TEXT=\"#339900\">");
            sb.append("<BIG>Accept?</BIG>" +
                    "<DL>");
            sb.append("<DT>peptide:<DD>" + iCorrectPeptidehitNumber +
                    "<DT>sequence:<DD>" + iPeptideIdentification.getPeptideHit(iCorrectPeptidehitNumber - 1).getSequence() +
                    "<DT>spectrum:<DD>" + iPeptideIdentification.getSpectrum().getName() +
                    "<DT>peptizer name:<DD>" + iPeptideIdentification.getName() +
                    "</DL>");
        } else {
            // Reject!
            sb.append("<HTML><BODY TEXT=\"#cc3300\">");

            sb.append("<BIG>Reject?</BIG>" +
                    "<DL>");
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
        String s = txaInput.getText();
        if (!s.trim().equals("")) {
            iPeptideIdentification.getValidationReport().setComment(s.replaceAll("\\n", "*"));
        } else {
            iPeptideIdentification.getValidationReport().setComment("NA");
        }
        iPeptideIdentification.getValidationReport().setCorrectPeptideHitNumber(iCorrectPeptidehitNumber);
        // True if we are accepting, False if we are rejecting.
        iPeptideIdentification.getValidationReport().setResult(iBoolAccept);

        // Make the mediator aware validation has changed.
        iMediator.validationPerformed();

        this.CancelPressed();
    }


    /**
     * Save the validation and discard the current comment (if any, it is reset to "NA")
     */
    private void OKWithoutCommentPressed() {
        txaInput.setText("");
        OKPressed();
    }
}
