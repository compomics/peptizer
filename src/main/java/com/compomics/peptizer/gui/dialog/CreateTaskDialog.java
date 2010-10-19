package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.component.*;
import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.datatools.IdentificationFactory;
import com.compomics.peptizer.util.fileio.ConfigurationWriter;
import com.compomics.peptizer.util.fileio.FileManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.worker.MatWorker;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-jun-2007
 * Time: 13:52:30
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class CreateTaskDialog extends JDialog {
	// Class specific log4j logger for CreateTaskDialog instances.
	 private static Logger logger = Logger.getLogger(CreateTaskDialog.class);

    /**
     * The main frame.
     */
    PeptizerGUI iPeptizerGUI = null;

    // GUI components.
    private DataSourcePanel jpanSource;
    private AgentPanel jpanAgentProfile;

    private ConfidencePanel jpanConfidence;
    private AgentAggregatorPanel jpanAggregator;

    private JButton btnStart;
    private JButton btnCancel;
    private JButton btnLoadConfiguration;
    private JButton btnSaveConfiguration;

    /**
     * Create a new assessment. Through this dialog a DataSource and a Profile can be selected. If applied a MatWorker
     * is started and Mediator is created and returned to the main PeptizerGUI.
     *
     * @param aPeptizerGUI PeptizerGUI JFrame that displays the results of the assessment.
     */
    public CreateTaskDialog(PeptizerGUI aPeptizerGUI) {
        super(aPeptizerGUI);
        iPeptizerGUI = aPeptizerGUI;
        construct();
        this.setTitle("New Selection Task");
        this.setVisible(true);
    }

    /**
     * Construct the dialog.
     */
    private void construct() {

        // 0. Dialog settings
        this.setLocation(iPeptizerGUI.getLocation().x + 50, iPeptizerGUI.getLocation().y + 50);

        // 1. The source.

        jpanSource = new DataSourcePanel();

        // 2. The profile.

        jpanAgentProfile = new AgentPanel(AgentFactory.getInstance().getAllAgents(), this.getOwner());
        jpanAgentProfile.setBorder(BorderFactory.createTitledBorder("2. Agent Summary Table"));
        jpanAgentProfile.setToolTipText("Customise the Agents and create a profile.");

        // 3. Aggregator Panel.
        jpanAggregator = new AgentAggregatorPanel();
        jpanAggregator.setBorder(BorderFactory.createTitledBorder("3. AgentAggregator Selection Table"));

        // 4. Confidence Panel.
        jpanConfidence = new ConfidencePanel();
        jpanConfidence.setBorder(BorderFactory.createTitledBorder(""));

        // 5. Go!
        btnStart = new JButton("Start Task");
        // Use Alt_Enter as a Start Shortcut.
        btnStart.setMnemonic(KeyEvent.VK_ENTER);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    CreateTaskDialog.this.startPressed();
                } catch (OutOfMemoryError oom) {
                    MatLogger.logExceptionalEvent("Out of memory error!\nPlease supply the Java Virtual Machine(JVM) with more memory.\n\nExample: Startup parameter \"-Xmx512m\" supplies the JVM with 512m memory.");
                    System.err.println("Out Of Memory Exception!!!!");
                }
            }
        });
        btnCancel = new JButton("Cancel");
        {
            btnCancel.setMnemonic(KeyEvent.VK_C);
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CreateTaskDialog.this.cancelPressed();
                }
            });
        }

        btnLoadConfiguration = new JButton("Load Task");
        {
            btnLoadConfiguration.setMnemonic(KeyEvent.VK_C);
            btnLoadConfiguration.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (FileManager.getInstance().selectTaskInput(jpanSource)) {
                        MatConfig.getInstance().reloadAllConfiguration(FileManager.getInstance().getTaskInput());
                        jpanAgentProfile.loadAgentsFromAgentFactory();
                        jpanAgentProfile.doOptimalResize();
                        jpanAggregator.resetAgentAggregatorPanel();
                        jpanConfidence.updateTextField();
                        CreateTaskDialog.this.getOwner().pack();
                        CreateTaskDialog.this.repaint();
                    }
                }
            });
        }

        btnSaveConfiguration = new JButton("Save Task");
        {
            btnSaveConfiguration.setMnemonic(KeyEvent.VK_C);
            btnSaveConfiguration.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (FileManager.getInstance().selectTaskOutput(jpanSource)) {
                        ConfigurationWriter.writeTaskConfiguration(FileManager.getInstance().getTaskOutput());
                    }
                }
            });
        }


        JPanel jpanFinalButtons = new JPanel();
        jpanFinalButtons.setLayout(new BoxLayout(jpanFinalButtons, BoxLayout.LINE_AXIS));

        jpanFinalButtons.add(Box.createHorizontalStrut(5));
        jpanFinalButtons.add(btnLoadConfiguration);
        jpanFinalButtons.add(Box.createHorizontalStrut(10));
        jpanFinalButtons.add(btnSaveConfiguration);
        jpanFinalButtons.add(Box.createGlue());
        jpanFinalButtons.add(btnStart);
        jpanFinalButtons.add(Box.createHorizontalStrut(10));
        jpanFinalButtons.add(btnCancel);
        jpanFinalButtons.add(Box.createHorizontalStrut(5));

        JPanel jpanContent = new JPanel();
        jpanContent.setLayout(new BoxLayout(jpanContent, BoxLayout.PAGE_AXIS));

        jpanContent.add(jpanSource);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanAgentProfile);
        jpanContent.add(Box.createVerticalGlue());
        jpanContent.add(jpanAggregator);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanConfidence);
        jpanContent.add(Box.createVerticalStrut(10));
        jpanContent.add(jpanFinalButtons);
        jpanContent.add(Box.createVerticalStrut(5));

        this.add(jpanContent);
        this.pack();

        setSize();
    }

    private void setSize() {

        int lNewWidth = new Double((iPeptizerGUI.getSize().width) * 0.64).intValue();
        Dimension lScreenResolutionToolkit = Toolkit.getDefaultToolkit().getScreenSize();
        int lNewHeight = new Double(lScreenResolutionToolkit.getHeight() * 0.48).intValue();

        this.setPreferredSize(new Dimension(lNewWidth, lNewHeight));
        jpanSource.setPreferredSize(new Dimension(lNewWidth - 50, jpanSource.getSize().height + 50));
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

        DefaultProgressBar lProgress = new DefaultProgressBar(iPeptizerGUI, "Data loading", 0, 2);
        MatLogger.logNormalEvent("New task started.");

        IdentificationFactory.getInstance().reset();
        ImportPanel importPanel = jpanSource.getSelectedImport();
        importPanel.loadIdentifications(lProgress);
        PeptideIdentificationIterator iter = IdentificationFactory.getInstance().getIterator();

        if (iter != null) {
            AgentAggregator lAggregator = null;
            lAggregator = jpanAggregator.getAgentAggregator();

            SelectedPeptideIdentifications lSelectedPeptideIdentifications = new SelectedPeptideIdentifications();

            lProgress = new DefaultProgressBar(iPeptizerGUI, "Task progress", 0, 2);

            MatWorker worker = new MatWorker(iter, lAggregator, lSelectedPeptideIdentifications, lProgress);
            worker.start();
            lProgress.setVisible(true);

            // The worker now does his thingy

            if (lProgress.isEnabled()) {
                String root = iter.toString() + " results.";
                lSelectedPeptideIdentifications.setMeta(SelectedPeptideIdentifications.MK_ITERITOR_DESCRIPTION, iter.getGeneralDescription());

                MatLogger.logNormalEvent("Task on " + iter.getGeneralDescription() + " completed!");
                iPeptizerGUI.passTask(lSelectedPeptideIdentifications);
            }
            this.dispose();
        }

    }

    public void setMs_lims_project_selected(long aProjectID) {
        ImportPanel anImport = ImportPanel_Ms_Lims_Project.getInstance();
        ((ImportPanel_Ms_Lims_Project) anImport).setProjectID(aProjectID);
        jpanSource.setSelectedIterator(anImport);
    }


    public void setMs_lims_project_selected() {
        ImportPanel anImport = ImportPanel_Ms_Lims_Project.getInstance();
        jpanSource.setSelectedIterator(anImport);
    }


    /**
     * Set the combobox selection to ms_lims enter the list with identification ids.
     *
     * @param aIdentificationId ArrayList with Long identificationId's.
     */
    public void setMs_lims_identification_id_selected(ArrayList<Long> aIdentificationId) {
        ImportPanel anImport = ImportPanel_Ms_Lims_IdentificationIDList.getInstance();
        ((ImportPanel_Ms_Lims_IdentificationIDList) anImport).setIdentificationIDs(aIdentificationId);
        jpanSource.setSelectedIterator(anImport);
    }

    /**
     * Set the combobox selection to ms_lims enter the list with identification ids.
     */
    public void setMs_lims_identification_id_selected() {
        ImportPanel anImport = ImportPanel_Ms_Lims_IdentificationIDList.getInstance();
        jpanSource.setSelectedIterator(anImport);
    }
}
