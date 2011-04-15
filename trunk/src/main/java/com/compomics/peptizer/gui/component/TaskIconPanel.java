package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.dialog.CommentValidationDialog;
import com.compomics.peptizer.util.ValidationReport;
import com.compomics.peptizer.util.fileio.ConfigurationWriter;
import com.compomics.peptizer.util.fileio.FileManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-apr-2007
 * Time: 13:14:55
 */

/**
 * Class description:
 * ------------------
 * TaskIconPanel is an Iconbar JPanel with actionlisteners coupled to the Mediator.
 */
public class TaskIconPanel extends JPanel {
    // Class specific log4j logger for TaskIconPanel instances.
    private static Logger logger = Logger.getLogger(TaskIconPanel.class);
    /**
     * The parent Mediator.
     */
    private Mediator iMediator = null;

    /**
     * The instance output file.
     */
    private File iOutput = null;

    public TaskIconPanel(Mediator aMediator) {
        // JPanel constructor.
        super();
        // Layout and Look & Feel.
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(lBoxLayout);
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        Dimension dmsButton = new Dimension(40, 30);

        // The parent Mediator.
        iMediator = aMediator;

        // A. Clear Panels button.
        URL urlClearPanels = ClassLoader.getSystemResource("image/ICON_clear.png");
        ImageIcon imgClearPanels = new ImageIcon(urlClearPanels);
        JButton btnClearPanels = new JButton(imgClearPanels);
        btnClearPanels.setToolTipText("Close all SpectrumPanels.");
        btnClearPanels.setPreferredSize(dmsButton);

        btnClearPanels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iMediator.clearView();
            }
        });

        // Agent Graph button.
        URL urlAgentGraph = ClassLoader.getSystemResource("image/ICON_accept.png");
        ImageIcon imgAgentGraph = new ImageIcon(urlAgentGraph);
        JButton btnAgentGraph = new JButton(imgAgentGraph);
        btnAgentGraph.setToolTipText("Show AgentGraph.");
        btnAgentGraph.setPreferredSize(dmsButton);

        btnAgentGraph.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iMediator.showAgentGraph();
            }
        });


        // B. Save agent configuration button.
        URL urlSave = ClassLoader.getSystemResource("image/ICON_save.png");
        ImageIcon imgSave = new ImageIcon(urlSave);
        JButton btnSave = new JButton(imgSave);
        btnSave.setToolTipText("Save current Agent settings.");
        btnSave.setPreferredSize(dmsButton);


        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FileManager.getInstance().selectAgentConfigurationOutput(iMediator)) {
                    ConfigurationWriter.writeAgentConfiguration(FileManager.getInstance().getAgentConfigurationOutput());
                }
            }
        });

        // C. Validate identification button without comments.
        URL urlAccept = ClassLoader.getSystemResource("image/ICON_accept.png");
        ImageIcon imgAccept = new ImageIcon(urlAccept);
        JButton btnAccept = new JButton(imgAccept);
        btnAccept.setToolTipText("Accept selected identification.");
        btnAccept.setPreferredSize(dmsButton);

        btnAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int lPeptideHit = iMediator.getSelectedTableColumn();
                if (lPeptideHit == 0) {
                    JOptionPane.showMessageDialog(iMediator, "Please select the correct PeptideHit!");
                } else {
                    ValidationReport lValidation = (ValidationReport) iMediator.getActivePeptideIdentification().getValidationReport();
                    lValidation.setResult(true);
                    lValidation.setCorrectPeptideHitNumber(lPeptideHit);

                    iMediator.validationPerformed();
                    iMediator.repaint();
                }
            }
        });

        // D. InValidate identification button without comments.
        URL urlReject = ClassLoader.getSystemResource("image/ICON_reject.png");
        ImageIcon imgReject = new ImageIcon(urlReject);
        JButton btnReject = new JButton(imgReject);
        btnReject.setToolTipText("Reject selected identification.");
        btnReject.setPreferredSize(dmsButton);

        btnReject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ValidationReport lValidation = iMediator.getActivePeptideIdentification().getValidationReport();
                lValidation.setResult(false);
                lValidation.setCorrectPeptideHitNumber(-1);
                iMediator.validationPerformed();
                iMediator.repaint();
            }
        });

        // E. Reset Validation.
        URL urlResetValidation = ClassLoader.getSystemResource("image/ICON_resetvalidation.png");
        ImageIcon imgResetValidation = new ImageIcon(urlResetValidation);
        JButton btnResetValidation = new JButton(imgResetValidation);

        btnResetValidation.setToolTipText("Reset validation of the identification.");
        btnResetValidation.setPreferredSize(dmsButton);

        btnResetValidation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iMediator.getActivePeptideIdentification().getValidationReport().reset();
                iMediator.validationPerformed();
                iMediator.repaint();
            }
        });

        // F. Validate identification button with comment.
        URL urlAcceptComment = ClassLoader.getSystemResource("image/ICON_accept_comment.png");
        ImageIcon imgAcceptComment = new ImageIcon(urlAcceptComment);
        JButton btnAcceptComment = new JButton(imgAcceptComment);
        btnAcceptComment.setToolTipText("Accept selected identification and comment..");
        btnAcceptComment.setPreferredSize(dmsButton);

        btnAcceptComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int lPeptideHit = iMediator.getSelectedTableColumn();
                if (lPeptideHit == 0) {
                    JOptionPane.showMessageDialog(iMediator, "Please select the correct PeptideHit!");
                } else {
                    new CommentValidationDialog("Comment dialog for accepting the spectrum.", iMediator, true, true);
                    iMediator.repaint();
                }
            }
        });

        // G. InValidate identification button with comment.
        URL urlRejectComment = ClassLoader.getSystemResource("image/ICON_reject_comment.png");
        ImageIcon imgRejectComment = new ImageIcon(urlRejectComment);
        JButton btnRejectComment = new JButton(imgRejectComment);
        btnRejectComment.setToolTipText("Reject selected identification and comment.");
        btnRejectComment.setPreferredSize(dmsButton);

        btnRejectComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CommentValidationDialog("Comment dialog for accepting the spectrum.", iMediator, false, true);
                iMediator.repaint();
            }
        });

        // H. InValidate identification button with comment.
        URL urInfoComment = ClassLoader.getSystemResource("image/ICON_info.png");
        ImageIcon imgInfoComment = new ImageIcon(urInfoComment);
        JButton btnInfoComment = new JButton(imgInfoComment);
        btnInfoComment.setToolTipText("Display current comment.");
        btnInfoComment.setPreferredSize(dmsButton);

        btnInfoComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CommentValidationDialog("Comments Information dialog for the spectrum.", iMediator, false, false, false);
                iMediator.repaint();
            }
        });


        this.add(btnClearPanels);
        this.add(Box.createVerticalStrut(3));
        this.add(btnSave);
        this.add(Box.createVerticalGlue());
        this.add(btnAccept);
        this.add(Box.createVerticalStrut(3));
        this.add(btnReject);
        this.add(Box.createVerticalStrut(10));
        this.add(btnResetValidation);
        this.add(Box.createVerticalStrut(10));
        this.add(btnAcceptComment);
        this.add(Box.createVerticalStrut(3));
        this.add(btnRejectComment);
        this.add(Box.createVerticalStrut(3));
        this.add(btnInfoComment);
        this.add(Box.createVerticalStrut(3));

        /*
                  // B. Next Button.
                  URL urlTEMP = ClassLoader.getSystemResource("TEMP_ICON.png");
                  ImageIcon imgTEMP = new ImageIcon(urlTEMP);
                  JButton btnTEMP = new JButton(imgTEMP);
                  btnTEMP.setToolTipText("TEMP_TOOLTIP.");
                  btnTEMP.setPreferredSize(dmsButton);

                  btnTEMP.addActionListener(new ActionListener() {
                      public void actionPerformed(ActionEvent e) {
                          // action @mediator!
                      }
                  });
                  this.add(btnTEMP);
                  */
    }
}
