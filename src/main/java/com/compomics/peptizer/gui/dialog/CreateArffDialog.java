package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.component.AgentAggregatorPanel;
import com.compomics.peptizer.gui.component.AgentPanel;
import com.compomics.peptizer.gui.component.ConfidencePanel;
import com.compomics.peptizer.gui.component.DataSourcePanel;
import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.datatools.IdentificationFactory;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.worker.ArffWorker;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-jun-2007
 * Time: 11:26:14
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class CreateArffDialog extends JDialog {
	// Class specific log4j logger for CreateArffDialog instances.
	 private static Logger logger = Logger.getLogger(CreateArffDialog.class);

    /**
     * The main frame.
     */
    PeptizerGUI iPeptizerGUI = null;

    // GUI components.
    private DataSourcePanel jpanSource;
    private AgentPanel jpanProfile;
    private JPanel jpanButtons;
    private JPanel jpanContent;
    private JPanel jpanRadioButtons;
    private AgentAggregatorPanel jpanAggregator;
    private JButton btnProcess;
    private JButton btnCancel;
    private JRadioButton rbtMatch;
    private JRadioButton rbtDetail;


    /**
     * Create a new Arff process. Through this dialog a DataSource and a Profile can be selected. If applied a Arff
     * worker is started and all Identifications are iterated and results are outputted into an Attribute Relation File
     * Format file (weka).
     *
     * @param aPeptizerGUI JFrame parent for the Dialog.
     */
    public CreateArffDialog(PeptizerGUI aPeptizerGUI) {
        super(aPeptizerGUI);
        iPeptizerGUI = aPeptizerGUI;
        construct();
        this.setTitle("New Attribute-Relation File Format (.arff) Task");
        this.setVisible(true);
    }

    /**
     * Construct the dialog.
     */
    private void construct() {

        // 0. Dialog settings
        this.setLocation(iPeptizerGUI.getLocation().x + 100, iPeptizerGUI.getLocation().y + 100);

        // 1. The source.

        jpanSource = new DataSourcePanel();

        // 2. The profile.

        jpanProfile = new AgentPanel(AgentFactory.getInstance().getAllAgents(), this.getOwner());
        jpanProfile.setBorder(BorderFactory.createTitledBorder("2. Agent Summary Table"));
        jpanProfile.setToolTipText("Customise the Agents and create a profile.");

        // 3. Aggregator Panel.
        jpanAggregator = new AgentAggregatorPanel();
        jpanAggregator.setBorder(BorderFactory.createTitledBorder("3. AgentAggregator Selection Table"));

        // 4. Confidence Panel.
        JPanel jpanConfidence = new ConfidencePanel();
        jpanConfidence.setBorder(BorderFactory.createTitledBorder(""));

        // 5. The output type.
        // Build the radiobuttongroup to switch the information in the ARFF file.
        // The match button lists the decision of the Agent, namely 0 or 1.
        // The detail button lists the details on the inspection.
        // An Agent inspecting on PeptideLenght smaller then 8 given a peptide 'KENNY' will return '1' on the match and '5' on the detail function.

        ButtonGroup bg1 = new ButtonGroup();

        rbtDetail = new JRadioButton("Detailed result");
        rbtDetail.setMnemonic(KeyEvent.VK_D);
        rbtDetail.setToolTipText("<html>Output <b>detailed information</b> in the ARFF file.</br> For example an Agent inspecting on PeptideLenght smaller then 8 given a peptide 'KENNY' write <b>'5'</b> on this detail function.");
        bg1.add(rbtDetail);

        rbtMatch = new JRadioButton("Match result");
        rbtMatch.setMnemonic(KeyEvent.VK_M);
        rbtMatch.setToolTipText("Output <b>match information</b> in the ARFF file.</br> For example an Agent inspecting on PeptideLenght smaller then 8 given a peptide 'KENNY' will write <b>'1'</b> on this match function.");
        bg1.add(rbtMatch);
        rbtDetail.setSelected(true);

        jpanRadioButtons = new JPanel();
        BoxLayout lBoxLayout = new BoxLayout(jpanRadioButtons, BoxLayout.LINE_AXIS);
        jpanRadioButtons.setLayout(lBoxLayout);

        jpanRadioButtons.add(Box.createHorizontalGlue());
        jpanRadioButtons.add(rbtDetail);
        jpanRadioButtons.add(Box.createHorizontalStrut(10));
        jpanRadioButtons.add(rbtMatch);
        jpanRadioButtons.add(Box.createHorizontalStrut(10));

        // 6. Go!
        btnProcess = new JButton("Start Task");
        btnProcess.setMnemonic(KeyEvent.VK_S);
        btnProcess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateArffDialog.this.startPressed();
            }
        });
        btnCancel = new JButton("Cancel");
        {
            btnCancel.setMnemonic(KeyEvent.VK_C);
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CreateArffDialog.this.cancelPressed();
                }
            });
        }

        jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.LINE_AXIS));
        jpanButtons.add(Box.createGlue());
        jpanButtons.add(btnProcess);
        jpanButtons.add(Box.createHorizontalStrut(10));
        jpanButtons.add(btnCancel);

        jpanContent = new JPanel();
        jpanContent.setLayout(new BoxLayout(jpanContent, BoxLayout.PAGE_AXIS));

        jpanContent.add(jpanSource);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanProfile);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanAggregator);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanConfidence);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanRadioButtons);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanButtons);

        this.add(jpanContent);
        this.pack();

        setSize();

    }

    private void setSize() {
        jpanSource.setMaximumSize(new Dimension(3000, jpanSource.getSize().height));
        int lNewWidth = iPeptizerGUI.getSize().width - (new Double(iPeptizerGUI.getSize().width * 0.20)).intValue();
        Dimension lScreenResolutionToolkit = Toolkit.getDefaultToolkit().getScreenSize();
        int lNewHeight = new Double(lScreenResolutionToolkit.getHeight() * 0.63).intValue();
        setSize(new Dimension(lNewWidth, lNewHeight));
    }


    /**
     * Cancel the Task creation.
     */
    private void cancelPressed() {
        this.dispose();
    }

    /**
     * Start the task.
     */
    private void startPressed() {
        MatLogger.logNormalEvent("New Attribute Relation File Format output started.");
        PeptideIdentificationIterator iter = null;
        AgentAggregator lAggregator = null;
        File lTargetFile = null;

        boolean status = true;

        // 1. Initiate a PeptideIdentificationIterator, read the source.

        DefaultProgressBar lProgress = new DefaultProgressBar(iPeptizerGUI, "Loading identifications", 0, 100);
        ImportPanel importPanel = jpanSource.getSelectedImport();
        importPanel.loadIdentifications(lProgress);
        if ((iter = IdentificationFactory.getInstance().getIterator()) == null) {
            // Iterator is null;
            status = false;
        }

        // 2. Create the BufferedWriter, first launch a fileselectiondialog!

        if (status) {
            // Looping boolean.
            boolean lbContinue = true;
            // Previous selected path.
            String previousPath = "/";
            // The file filter to use.
            FileFilter filter = new FileFilter() {
                public boolean accept(File f) {
                    boolean result = false;
                    if (f.isDirectory() || f.getName().endsWith(".arff")) {
                        result = true;
                    }
                    return result;
                }

                public String getDescription() {
                    return "Attribute Relation File Format (ARFF Weka)";
                }
            };

            while (lbContinue) {
                JFileChooser jfc = new JFileChooser(previousPath);
                jfc.setDialogTitle("Process into Attribute Relation File");
                jfc.setDialogType(JFileChooser.SAVE_DIALOG);
                jfc.setFileFilter(filter);
                int returnVal = jfc.showSaveDialog(this.getParent());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    lTargetFile = jfc.getSelectedFile();
                    // Append the file extension if it is not already there.
                    if (jfc.getFileFilter() == filter && !lTargetFile.getName().toLowerCase().endsWith(".arff")) {
                        lTargetFile = new File(lTargetFile.getAbsolutePath() + ".arff");
                    }
                    // Check for existing file.
                    if (lTargetFile.exists()) {
                        int reply =
                                JOptionPane.showConfirmDialog(this.getParent(), new String[]{"File '" + lTargetFile.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (reply != JOptionPane.YES_OPTION) {
                            previousPath = lTargetFile.getParent();
                            continue;
                        }
                    }

                    // Create the BufferedWriter on the selected file!
                    lbContinue = false;

                } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                    lbContinue = false;
                    status = false;
                    JOptionPane.showMessageDialog(this, "Could not set an output file!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        boolean lDetailOutputType = false;
        if (rbtDetail.isSelected()) {
            lDetailOutputType = true;
        }

        if (status) {
            lAggregator = jpanAggregator.getAgentAggregator();
            lProgress = new DefaultProgressBar(iPeptizerGUI, "Task progress", 0, 2);

            ArffWorker worker = new ArffWorker(iter, lAggregator, lTargetFile, lProgress, lDetailOutputType);
            worker.start();
            lProgress.setVisible(true);

            this.dispose();
        }
    }
}
