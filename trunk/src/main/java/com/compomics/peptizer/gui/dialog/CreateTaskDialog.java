package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.listener.StartTaskActionListener;
import com.compomics.peptizer.gui.component.*;
import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.fileio.ConfigurationWriter;
import com.compomics.peptizer.util.fileio.FileManager;
import org.apache.log4j.Logger;
import org.divxdede.swing.busy.JBusyComponent;

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

    private JBusyComponent<JPanel> busyPanel = null;

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


        btnCancel = new JButton("Cancel");

        btnLoadConfiguration = new JButton("Load Task");
        btnSaveConfiguration = new JButton("Save Task");


        JPanel jpanFinalButtons = new JPanel();
        jpanFinalButtons.setLayout(new BoxLayout(jpanFinalButtons, BoxLayout.LINE_AXIS));


        jpanFinalButtons.add(Box.createHorizontalStrut(20));
        jpanFinalButtons.add(btnLoadConfiguration);
        jpanFinalButtons.add(Box.createHorizontalStrut(10));
        jpanFinalButtons.add(btnSaveConfiguration);
        jpanFinalButtons.add(Box.createGlue());
        jpanFinalButtons.add(btnStart);
        jpanFinalButtons.add(Box.createHorizontalStrut(10));
        jpanFinalButtons.add(btnCancel);
        jpanFinalButtons.add(Box.createHorizontalStrut(30));

        // Vertical spacing around the buttons.
        JPanel jpanFinalButtonWrapper = new JPanel();
        jpanFinalButtonWrapper.setLayout(new BoxLayout(jpanFinalButtonWrapper, BoxLayout.PAGE_AXIS));
        jpanFinalButtonWrapper.add(Box.createVerticalStrut(15));
        jpanFinalButtonWrapper.add(jpanFinalButtons);
        jpanFinalButtonWrapper.add(Box.createVerticalStrut(15));

        busyPanel = new JBusyComponent<JPanel>(jpanFinalButtonWrapper);

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
        jpanContent.add(busyPanel);
        jpanContent.add(Box.createVerticalStrut(5));


        // Add Listeners.
        setListeners();

        this.add(jpanContent);

        this.pack();

        setSize();


    }

    private void setListeners() {
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CreateTaskDialog.this.cancelPressed();
            }
        });

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

        btnSaveConfiguration.setMnemonic(KeyEvent.VK_C);
        btnSaveConfiguration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FileManager.getInstance().selectTaskOutput(jpanSource)) {
                    ConfigurationWriter.writeTaskConfiguration(FileManager.getInstance().getTaskOutput());
                }
            }
        });


        btnStart.addActionListener(new StartTaskActionListener(this, busyPanel));
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

    /**
     * Returns the selected import panel
     *
     * @return
     */
    public ImportPanel getSelectedImport() {
        return jpanSource.getSelectedImport();
    }

    /**
     * Returns the PeptizerGUI parent frame.
     *
     * @return
     */
    public Frame getPeptizerGUI() {
        return iPeptizerGUI;
    }

    /**
     * Return the selected AgentAggregator of the Create Task Dialog.
     *
     * @return
     */
    public AgentAggregator getAgentAggregator() {
        return jpanAggregator.getAgentAggregator();
    }
}
