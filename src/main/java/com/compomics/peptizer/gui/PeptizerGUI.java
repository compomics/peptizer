package com.compomics.peptizer.gui;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.component.HyperLinkLabel;
import com.compomics.peptizer.gui.component.StatusPanel;
import com.compomics.peptizer.gui.dialog.*;
import com.compomics.peptizer.gui.interfaces.StatusView;
import com.compomics.peptizer.gui.listener.AddAgentFilterActionListener;
import com.compomics.peptizer.gui.listener.AddSequenceFilterActionListener;
import com.compomics.peptizer.gui.model.ValidationTreeFilter;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.peptizer.util.fileio.FileManager;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.fileio.PeptizerSerialization;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 5-mrt-2007
 * Time: 14:39:35
 */

/**
 * Class description: ------------------ This class was developed to for testing. 070305 - simulate a
 * SelectedPeptideIdentifications class to test the GUI.
 */
public class PeptizerGUI extends JFrame implements StatusView {
    // Class specific log4j logger for PeptizerGUI instances.
    private static Logger logger = Logger.getLogger(PeptizerGUI.class);

    private static boolean isRunningGUI = false;
    private static Component iRunningComponent = null;
    // MAT variables
    public static String PEPTIZER_VERSION;

    // GUI variables
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JMenu subMenu;
    private JMenu taskMenu;
    private JPanel jpanContent;
    private StatusPanel jpanStatus;

    // TabbedPane contains the Mediators.
    private JTabbedPane iTabPanel;

    private boolean isConnectedToMsLims;

    /**
     * Constant for the startup-title tab.
     */
    private static final String START_TAB_TITLE = "Welcome";


    /**
     * The constructor takes a single argument for the title of the frame.
     */
    public PeptizerGUI() {

        //
        // 1. General inits.
        //
        // Super constructor.
        super();
        Container cp = this.getContentPane();
        cp.setLayout(new BorderLayout(10, 10));
        PEPTIZER_VERSION = getLastVersion();
        super.setTitle("Peptizer - " + PEPTIZER_VERSION);

        // Set program icon for this JFrame
        Image img = null;
        URL url = java.net.URLClassLoader.getSystemResource("image/ICON_frame.png");
        img = Toolkit.getDefaultToolkit().getImage(url);
        setIconImage(img);

        // Log in to the MatLogger.
        MatLogger.setStatusViewComponent(this);
        MatLogger.setSystemOut(true);

        // Define look and feel.
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(final WindowEvent e) {
                super.windowClosing(e);    //To change body of overridden methods use File | Settings | File Templates.
                exit();

            }
        });

        // Set size.
        Dimension lScreenResolutionToolkit = Toolkit.getDefaultToolkit().getScreenSize();
        Double lWidth = new Double(lScreenResolutionToolkit.getWidth() * 0.9);
        Double lHeight = new Double(lScreenResolutionToolkit.getHeight());
        this.setPreferredSize(new Dimension(lWidth.intValue(), lHeight.intValue()));

        //
        // 2. Menu.
        //
        // Construct and set the menubar.
        constructMenuBar();
        this.setJMenuBar(menuBar);

        //
        // 3. JTabbedPane.
        //
        iTabPanel = new JTabbedPane();

        // This home panel will only be shown after starting Peptizer.
        JPanel jpanStart = new JPanel();
        URL urlStartImage = URLClassLoader.getSystemResource("image/IMAGE_start_panel.png");
        Image lStartImage = Toolkit.getDefaultToolkit().getImage(urlStartImage);
        JLabel lbl = new JLabel(new ImageIcon(lStartImage));
        HyperLinkLabel lblStartImage = new HyperLinkLabel("", new ImageIcon(lStartImage), "http://code.google.com/p/peptizer/");

        jpanStart.add(lblStartImage, BorderLayout.CENTER);
        iTabPanel.add(START_TAB_TITLE, jpanStart);


        jpanContent = new JPanel(new BorderLayout());
        jpanContent.add(iTabPanel, BorderLayout.CENTER);


        jpanStatus = new StatusPanel(false);
        jpanStatus.setStatus("Welcome to Peptizer.\t(http://genesis.ugent.be/peptizer)");
        jpanStatus.setError("None.");
        jpanStatus.setBorder(BorderFactory.createTitledBorder("Peptizer status"));

        jpanContent.setSize(new Dimension(this.getSize().width, (new Double(this.getSize().height * 0.8)).intValue()));

        JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, jpanContent, jpanStatus);
        split1.setOneTouchExpandable(true);

        cp.add(split1, BorderLayout.CENTER);

        this.setListeners();
        this.setVisible(true);
        this.validate();
        this.pack();

        // Tiring splitpanes, only does what i want by setting the dividerlocation after the pack call.
        split1.setDividerLocation(0.85);

        // declare an active GUI session.
        isRunningGUI = true;
        iRunningComponent = this;
    }

    /**
     * This method extracts the last version from the 'about.txt' file.
     *
     * @return String with the String of the latest version, or '
     */
    public String getLastVersion() {
        String result = null;
        // get the version number set in the pom file
        Properties properties = PropertiesManager.getInstance().getProperties(CompomicsTools.PEPTIZER, "peptizer.properties");
        result = properties.getProperty("version");

        return result;
    }

    /**
     * All code producing the menubar.
     */
    private void constructMenuBar() {
        menuBar = new JMenuBar();

        // Main menu.
        menu = new JMenu("Main");
        menu.setMnemonic(KeyEvent.VK_M);
        menu.getAccessibleContext().setAccessibleDescription(
                "The menu with general functionality.");
        // new task submenu
        subMenu = new JMenu("New task");

        // new selection task
        menuItem = new JMenuItem("Selection task");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newTask();
            }
        });
        subMenu.add(menuItem);

        // new arff task.
        menuItem = new JMenuItem("Arff task");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newArffProcess();
            }
        });

        subMenu.add(menuItem);
        menu.add(subMenu);

        // save a task.
        menuItem = new JMenuItem("Save");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveTask();
            }
        });
        menu.add(menuItem);

        // Modify general properties.
        menuItem = new JMenuItem("Properties");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new ParameterDialog(PeptizerGUI.this, "Main properties", MatConfig.getInstance().getGeneralProperties());
            }
        });
        menu.add(menuItem);

        // exit Peptizer.
        menuItem = new JMenuItem("Exit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exit();
            }
        });
        menu.add(menuItem);

        // Add menu to the menubar.
        menuBar.add(menu);

        // Menu with functionality on performed tasks.
        menu = new JMenu("Task");
        menu.setMnemonic(KeyEvent.VK_T);

        // Filter the task
        subMenu = new JMenu("Filter");
        subMenu.setMnemonic(KeyEvent.VK_F);

        // Agent filter dialog.
        menuItem = new JMenuItem("Apply AgentFilter");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new AddAgentFilterActionListener(this));
        subMenu.add(menuItem);

        // Peptide sequence filter dialog.
        menuItem = new JMenuItem("Apply Peptide Sequence Filter");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(new AddSequenceFilterActionListener(this));
        subMenu.add(menuItem);

        menuItem = new JMenuItem("Apply ValidationFilter");
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ValidationTreeFilter lFilter = new ValidationTreeFilter();
                ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).setFilter(lFilter);
            }
        });
        subMenu.add(menuItem);

        menuItem = new JMenuItem("Disable Filters");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).disableFilter();
            }
        });
        subMenu.add(menuItem);
        menu.add(subMenu);

        // Serialization of the task.

        subMenu = new JMenu("Store");
        subMenu.setMnemonic(KeyEvent.VK_S);

        // fourth item is tricky code to serialize all selected peptideidentifications to a serialized oos.
        // Target is to have nice objects for the JUnit tests.
        menuItem = new JMenuItem("All");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (FileManager.getInstance().selectPeptideIdentificationOutput(iTabPanel)) {
                    File lFile = FileManager.getInstance().getPeptideIdentificationsSerializedOutput();
                    try {
                        SelectedPeptideIdentifications lSelectedPeptideIdentifications =
                                ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).getSelectedPeptideIdentifications();
                        PeptizerSerialization.serializePeptideIdentificationsToFile(lSelectedPeptideIdentifications.getSelectedPeptideIdentificationList(), lFile);
                    } catch (IOException e1) {
                        logger.error(e1.getMessage(), e1);
                    }
                }
            }
        });
        subMenu.add(menuItem);

        menuItem = new JMenuItem("Accepted");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (FileManager.getInstance().selectPeptideIdentificationOutput(iTabPanel)) {
                    File lFile = FileManager.getInstance().getPeptideIdentificationsSerializedOutput();
                    ArrayList lArrayList = new ArrayList();

                    SelectedPeptideIdentifications lSelectedPeptideIdentifications =
                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).getSelectedPeptideIdentifications();
                    int lNumber = lSelectedPeptideIdentifications.getNumberOfSpectra();
                    for (int i = 0; i < lNumber; i++) {
                        PeptideIdentification lPeptideIdentification =
                                lSelectedPeptideIdentifications.getPeptideIdentification(i);
                        if (lPeptideIdentification.isValidated()) {
                            if (lPeptideIdentification.getValidationReport().getResult()) {
                                lArrayList.add(lPeptideIdentification);
                            }
                        }
                    }
                    try {
                        PeptizerSerialization.serializePeptideIdentificationsToFile(lArrayList, lFile);
                    } catch (IOException e1) {
                        logger.error(e1.getMessage(), e1);
                    }
                }
            }
        });
        subMenu.add(menuItem);

        menu.add(subMenu);

        // load a previous task.
        menuItem = new JMenuItem("Load");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loadSerializedPeptideIdentifications();
            }
        });
        menu.add(menuItem);


        menuBar.add(menu);

        // Help menu
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        // The help menu conatins only some info about the program.
        menu.setMnemonic(KeyEvent.VK_H);
        // The about menuitem will pop-up the about dialog.
        menuItem = new JMenuItem("About");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
        menu.add(menuItem);

        // The manual menuitem will pop-up with a URL to the Peptizer website.
        menuItem = new JMenuItem("License");
        menuItem.setMnemonic(KeyEvent.VK_L);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLicense();
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // Add menu to the menubar.
        menuBar.add(menu);
    }


    /**
     * Action when the save menu is selected.
     */
    private void saveTask() {
        if (getNumberOfTabs() > 0) {
            new SaveValidationDialog(PeptizerGUI.this);
        } else {
            JOptionPane.showMessageDialog(this, "An task needs to be run before anything can be saved.\nStart a new task through the main menu or by pressing 'Ctrl + N'.", "No task to save.", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void newArffProcess() {
        JDialog dialog = new CreateArffDialog(this);
    }

    private void newTask() {
        JDialog dialog = new CreateTaskDialog(this);
    }

    private void loadSerializedPeptideIdentifications() {
        if (FileManager.getInstance().selectPeptideIdentificationSerializedInput(iTabPanel)) {
            File lFile = FileManager.getInstance().getPeptideIdentificationsSerializedInput();
            ArrayList lArrayList = null;
            try {
                lArrayList = PeptizerSerialization.readSerializedPeptideIdentifications(lFile);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

            if (lArrayList != null) {
                SelectedPeptideIdentifications lSelectedPeptideIdentifications = new SelectedPeptideIdentifications();
                Iterator iter = lArrayList.iterator();
                for (Object o : lArrayList) {
                    lSelectedPeptideIdentifications.addResult((PeptideIdentification) o);
                }
                this.passTask(lSelectedPeptideIdentifications);
            }
        }
    }

    /**
     * Sets the listeners on the GUI.
     */
    private void setListeners() {

        // 1. Right click popup menu on a tab.
        iTabPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Right click opens popupmenu.
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // Save the Point of the Tab that got clicked as final for the inner class ActionListers.
                    final MouseEvent lMouseEvent = e;
                    // Build the popupmenu.
                    JPopupMenu jpop = new JPopupMenu("Options");

                    // first item closes the selected tab.
                    JMenuItem item = new JMenuItem("Close this tab");
                    item.setMnemonic(KeyEvent.VK_C);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            removeSelectedTab();
                        }
                    });
                    jpop.add(item);
                    // second item closes all tabs but except the selected tab.
                    item = new JMenuItem("Save");
                    item.setMnemonic(KeyEvent.VK_S);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            saveTask();
                        }
                    });
                    jpop.add(item);

                    jpop.addSeparator();

                    // second item closes all tabs but except the selected tab.
                    item = new JMenuItem("Accept all");
                    item.setMnemonic(KeyEvent.VK_S);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).acceptAll();
                        }
                    });
                    jpop.add(item);

                    // second item closes all tabs but except the selected tab.
                    item = new JMenuItem("Reject all");
                    item.setMnemonic(KeyEvent.VK_S);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).rejectAll();
                        }
                    });
                    jpop.add(item);


                    jpop.addSeparator();

                    // third item is tricky code to serialize correct validated selected peptideidentifications to a serialized oos.
                    // Target is to have nice objects for the JUnit tests.
                    item = new JMenuItem("Serialize Correct ID's");
                    item.setMnemonic(KeyEvent.VK_T);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {

                            if (FileManager.getInstance().selectPeptideIdentificationOutput(iTabPanel)) {
                                File lFile = FileManager.getInstance().getPeptideIdentificationsSerializedOutput();
                                ArrayList lArrayList = new ArrayList();

                                SelectedPeptideIdentifications lSelectedPeptideIdentifications =
                                        ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).getSelectedPeptideIdentifications();
                                int lNumber = lSelectedPeptideIdentifications.getNumberOfSpectra();
                                for (int i = 0; i < lNumber; i++) {
                                    PeptideIdentification lPeptideIdentification =
                                            lSelectedPeptideIdentifications.getPeptideIdentification(i);
                                    if (lPeptideIdentification.isValidated()) {
                                        if (lPeptideIdentification.getValidationReport().getResult()) {
                                            lArrayList.add(lPeptideIdentification);
                                        }
                                    }
                                }
                                try {
                                    PeptizerSerialization.serializePeptideIdentificationsToFile(lArrayList, lFile);
                                } catch (IOException e1) {
                                    logger.error(e1.getMessage(), e1);
                                }
                            }
                        }
                    });
                    jpop.add(item);

                    // fourth item is tricky code to serialize all selected peptideidentifications to a serialized oos.
                    // Target is to have nice objects for the JUnit tests.
                    item = new JMenuItem("Serialize all selected ID's");
                    item.setMnemonic(KeyEvent.VK_T);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {

                            if (FileManager.getInstance().selectPeptideIdentificationOutput(iTabPanel)) {
                                File lFile = FileManager.getInstance().getPeptideIdentificationsSerializedOutput();
                                try {
                                    SelectedPeptideIdentifications lSelectedPeptideIdentifications =
                                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).getSelectedPeptideIdentifications();
                                    PeptizerSerialization.serializePeptideIdentificationsToFile(lSelectedPeptideIdentifications.getSelectedPeptideIdentificationList(), lFile);
                                } catch (IOException e1) {
                                    logger.error(e1.getMessage(), e1);
                                }
                            }
                        }
                    });
                    jpop.add(item);

                    jpop.addSeparator();

                    // fifth item is allows to apply the validation filter to the current SelectedPeptideIdentifications.
                    // Target is to have nice objects for the JUnit tests.
                    item = new JMenuItem("Apply ValidationFilter");
                    item.setMnemonic(KeyEvent.VK_V);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            ValidationTreeFilter lFilter = new ValidationTreeFilter();
                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).setFilter(lFilter);
                        }
                    });
                    jpop.add(item);

                    // Sixth item allows to call an Agent filter dialog.
                    item = new JMenuItem("Apply AgentFilter");
                    item.setMnemonic(KeyEvent.VK_A);
                    item.addActionListener(new AddAgentFilterActionListener(PeptizerGUI.this));
                    jpop.add(item);

                    // Sixth item allows to call an Agent filter dialog.
                    item = new JMenuItem("Apply Peptide Sequence Filter");
                    item.setMnemonic(KeyEvent.VK_P);
                    item.addActionListener(new AddSequenceFilterActionListener(PeptizerGUI.this));
                    jpop.add(item);

                    // seventh item allows to disable the current applied filter.
                    item = new JMenuItem("Disable Filters");
                    item.setMnemonic(KeyEvent.VK_D);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            ((Mediator) PeptizerGUI.this.getTabs()[PeptizerGUI.this.getSelectedTabIndex()]).disableFilter();
                        }
                    });
                    jpop.add(item);

                    // more items?
                    // Show the popupmenu.
                    jpop.show(iTabPanel, lMouseEvent.getX(), lMouseEvent.getY());
                }
            }
        });
    }


    /**
     * Run a new MAT gui.
     *
     * @param args
     */
    public static void main(String[] args) {
        // initiate a new mat gui.
        PropertiesManager.getInstance().updateLog4jConfiguration(logger, CompomicsTools.PEPTIZER);
        logger.debug("Starting peptizer");
        logger.debug("OS : " + System.getProperties().getProperty("os.name"));

        new PeptizerGUI();

    }

    /**
     * This method is invoked when Mat is exiting (exit in menu or close button). Make sure existing Database
     * connections and preferences are saved!
     */
    private void exit() {
        // Is all fine?
        boolean exit = true;

        // Iterate over all tasks, , .
        int lNumberOfTasks = iTabPanel.getTabCount();
        for (int i = 0; i < lNumberOfTasks; i++) {
            if (iTabPanel.getComponentAt(i) instanceof Mediator) {
                Mediator lMediator = (Mediator) iTabPanel.getComponentAt(i);
                // if any of them has changed
                if (lMediator.isChangedSinceLastSave()) {
                    // ask for confirmation to exit
                    int result =
                            JOptionPane.showConfirmDialog(this, "Your validations have not been saved.\nDo you really want to exit?\n", "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    // Save confirmation and break the iteration.
                    if (result == JOptionPane.NO_OPTION) {
                        exit = false;
                        break;
                    } else if (result == JOptionPane.YES_OPTION) {
                        exit = true;
                        break;
                    }
                    // Continue iteration.
                }
            }
        }

        if (!isConnectedToMsLims) {
            if (ConnectionManager.getInstance().hasConnection()) {
                try {
                    int lResult = JOptionPane.showConfirmDialog(this, "Do you want to close your ms-lims connection?");

                    if (lResult == JOptionPane.OK_OPTION) {
                        logger.info("Closing connection to '" + ConnectionManager.getInstance().getConnection().getMetaData().getURL() + "'.");
                        ConnectionManager.getInstance().closeConnection();
                    }

                } catch (SQLException e1) {
                    logger.error(e1.getMessage(), e1);  //To change body of catch statement use File | Settings | File Templates.
                }
            }


        }

        if (exit) {
            // All is fine to exit!
            System.exit(0);
        }
    }

    /**
     * Pass a SelectedPeptideIdentifications instance as the result from a MatWorker. If invoked, create a new Mediator
     * and set it in the front.
     *
     * @param aSelectedPeptideIdentifications
     *         SelectedPeptideIdentifications result from a MatWorker.
     */
    public void passTask(SelectedPeptideIdentifications aSelectedPeptideIdentifications) {
        Mediator lMediator = new Mediator(aSelectedPeptideIdentifications);
        if (iTabPanel.getTabCount() == 1 && iTabPanel.getTitleAt(0) == START_TAB_TITLE) {
            iTabPanel.remove(0);
        }

        // Name the tabs by number, howerver display the Mediator toString in the Tooltip to supply the necessairy information.
        iTabPanel.add("Task " + (iTabPanel.getTabCount() + 1), lMediator);
        iTabPanel.setToolTipTextAt(iTabPanel.getTabCount() - 1, lMediator.toString());

        StringBuffer sb = new StringBuffer();
        int lSelectedNumber = aSelectedPeptideIdentifications.getNumberOfSpectra();
        String s = "";

        if (aSelectedPeptideIdentifications.getMeta(SelectedPeptideIdentifications.MK_NUMBER_CONFIDENT) != null) {
            double lConfidentNonSelected =
                    ((Integer) aSelectedPeptideIdentifications.getMeta(SelectedPeptideIdentifications.MK_NUMBER_CONFIDENT)).doubleValue();
            double lConfidentNumber = lConfidentNonSelected + lSelectedNumber;
            BigDecimal lSelectedPercentage =
                    new BigDecimal(100 * new Double(aSelectedPeptideIdentifications.getNumberOfSpectra()).doubleValue() / lConfidentNumber).setScale(1, BigDecimal.ROUND_HALF_UP);
            s = lConfidentNumber + " (" + lSelectedPercentage + "%)";
        }
        s = s + ".";


        sb.append(aSelectedPeptideIdentifications.getNumberOfSpectra()).append(" spectra selected out of " + s);
        MatLogger.logNormalEvent((sb.toString()));

        this.validate();
        this.pack();

        lMediator.validate();
    }

    /**
     * This method allows the caller to specify the status message that is being displayed.
     *
     * @param aStatus String with the desired status message.
     */
    public void setStatus(String aStatus) {
        this.jpanStatus.setStatus(aStatus);
    }

    /**
     * This method allows the caller to specify the error message that is being displayed.
     *
     * @param aError String with the desired error message.
     */
    public void setError(String aError) {
        this.jpanStatus.setError(aError);
    }

    /**
     * Returns the number of component tabs (Mediators) on the PeptizerGUI.
     *
     * @return the number of component tabs on PeptizerGUI
     */
    public int getNumberOfTabs() {
        return iTabPanel.getTabCount();
    }

    /**
     * Returns the index of the current selected tab on PeptizerGUI.
     *
     * @return integer index of the current selected tab.
     */
    public int getSelectedTabIndex() {
        return iTabPanel.getSelectedIndex();
    }

    /**
     * Returns the Components on PeptizerGUI.
     *
     * @return
     */
    public Component[] getTabs() {
        return iTabPanel.getComponents();
    }

    /**
     * ToString method for PeptizerGUI.
     *
     * @return String describing PeptizerGUI.
     */
    public String toString() {
        return "Hi I am PeptizerGUI and i currently contain + " + iTabPanel.getTabCount() + " tasks.";
    }

    /**
     * Close the current selected tab of PeptizerGUI.
     */
    public void removeSelectedTab() {
        boolean lRemove = false;
        int index = iTabPanel.getSelectedIndex();
        if (iTabPanel.getComponentAt(index) instanceof Mediator) {
            Mediator lMediator = (Mediator) iTabPanel.getComponentAt(index);
            if (lMediator.isChangedSinceLastSave()) {
                int result =
                        JOptionPane.showConfirmDialog(this, "The validations of Task " + (index + 1) + " have changed since last save.\nAre you sure you want to close?");
                if (result == JOptionPane.OK_OPTION) {
                    lRemove = true;
                }
            } else {
                lRemove = true;
            }
        }
        if (lRemove) {
            iTabPanel.remove(index);
            System.gc();
        }
    }

    public void setsConnectedToMsLims(final boolean aSConnectedToMsLims) {
        isConnectedToMsLims = aSConnectedToMsLims;
    }

    public boolean issConnectedToMsLims() {
        return isConnectedToMsLims;
    }

    /**
     * This static boolean informs if a Peptizer GUI has been constructed in this JVM.
     *
     * @return
     */
    public static boolean isRunningGUI() {
        return isRunningGUI;
    }

    /**
     * This static getter returns a GUI Component based on the PeptizerGUI.
     * Returns null if no PeptizerGUI has been created in this JVM.
     *
     * @return
     */
    public static Component getRunningComponent() {
        return iRunningComponent;
    }

    /**
     * This method shows the about dialog.
     */
    public void showAbout() {
        AboutDialog ad = new AboutDialog(this, "About Peptizer");
        Point p = this.getLocation();
        Point result = new Point((int) (p.x + this.getWidth() / (6)), (int) (p.y + this.getHeight() / 5));
        ad.setLocation(result);
        ad.setVisible(true);
    }

    /**
     * This method pops up a MessageDialog with licence information.
     */
    private void showLicense() {
        String s =
                "Copyright 2011 Helsens Kenny\n\n Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License.\nYou may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0\n\nUnless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\nSee the License for the specific language governing permissions and limitations under the License.";
        JOptionPane.showMessageDialog(this, s, "Peptizer license", JOptionPane.INFORMATION_MESSAGE);
    }
}
