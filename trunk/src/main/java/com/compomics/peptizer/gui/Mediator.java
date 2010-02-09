package com.compomics.peptizer.gui;

import com.compomics.peptizer.gui.component.IconPanel;
import com.compomics.peptizer.gui.component.TabPanel;
import com.compomics.peptizer.gui.dialog.AdvancedMessageDialog;
import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.gui.view.TabbedView;
import com.compomics.peptizer.gui.view.TableView;
import com.compomics.peptizer.gui.view.TreeView;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.ValidationReport;
import com.compomics.peptizer.util.fileio.MatLogger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 13:51:38
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a Super Controller of the GUI.
 * It coordinates a custom JTree, JTable and JTabbedPane based on the resultset of the AgentAggregator.
 */
public class Mediator extends JPanel {

    /**
     * The "datasource" of the mat GUI.
     */
    private SelectedPeptideIdentifications iSelectedPeptideIdentifications;

    /**
     * The colleage Tree of this mediator.
     */
    private TreeView iTree;
    /**
     * The colleage Table of this mediator.
     */
    private TableView iTable;

    /**
     * The colleage Tabbed Pane of this mediator.
     */
    private TabbedView iTabbedPane;

    /**
     * This boolean whether there have been validations to the Mediator since the last save.
     */
    private boolean iChangedSinceLastSave = false;

    /**
     * The Titled border around the Tree.
     */
    private TitledBorder iTreeTitledBorder;


    // Instance gui components.
    private JPanel jpanMain;
    private JPanel jpanTree;
    private JPanel jpanTable;
    private JPanel jpanTabbedPane;

    private JSplitPane spltVerticalSplitter;
    private JSplitPane spltHorizontalSplitter;


    /**
     * This constructor takes a AggregaterResults object as a single parameter.
     *
     * @param aSelectedPeptideIdentifications
     *         AggregatorResult data source.
     */
    public Mediator(SelectedPeptideIdentifications aSelectedPeptideIdentifications) {
        // Construct the mediator.
        iSelectedPeptideIdentifications = aSelectedPeptideIdentifications;
        if (iSelectedPeptideIdentifications.getNumberOfSpectra() > 0) {
            this.build();
        } else {
            this.buildEmptySelection();
        }
    }

    /**
     * This build is done if the Task returned an empty selection!
     */
    private void buildEmptySelection() {

        // Components.
        JLabel txtInformation = new JLabel();
        // The textarea, fill with information on the task being the Iterator used and the Current AgentFactory setting.
        txtInformation.setFont(new Font("Monospaced", Font.PLAIN, 12));
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><DL>");
        sb.append("<DT>Iterator description:<DD>" + iSelectedPeptideIdentifications.getMeta(SelectedPeptideIdentifications.MK_ITERITOR_DESCRIPTION) + "<BR />");
        sb.append("<DT>AgentFactory active Agents:<DD>");
        ArrayList lAgents = (ArrayList) AgentFactory.getInstance().getActiveAgents();
        if (lAgents.size() == 0) {
            sb.append("No Agents active.<BR />");
        } else {
            for (int i = 0; i < lAgents.size(); i++) {
                Agent lAgent = (Agent) lAgents.get(i);
                sb.append(lAgent.getName() + " - (" + lAgent.getUniqueID() + "<BR />");
            }
        }
        sb.append("</DL></HTML>");

        String lBorderTitle = "No identifications were selected during the task.";
        txtInformation.setText(sb.toString());

        // The OK button.
        JButton btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (SwingUtilities.getRoot(Mediator.this) instanceof PeptizerGUI) {
                    ((PeptizerGUI) SwingUtilities.getRoot(Mediator.this)).removeSelectedTab();
                }
            }
        });

        // The containers.
        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));

        // Button panel.
        JPanel jpanButton = new JPanel();
        jpanButton.setLayout(new BoxLayout(jpanButton, BoxLayout.X_AXIS));

        // Scrollpane for textarea + panel for scrollpane.
        JScrollPane jspText = new JScrollPane(txtInformation, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.X_AXIS));
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));
        jpanScroll.add(jspText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));

        // Start adding.
        jpanButton.add(Box.createHorizontalGlue());
        jpanButton.add(btnOK);
        jpanButton.add(Box.createRigidArea(new Dimension(15, btnOK.getHeight())));

        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanScroll);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanButton);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 15)));

        jpanMain.setBorder(BorderFactory.createTitledBorder(lBorderTitle));

        // Pack and go.
        this.add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method builds the gui.
     */
    private void build() {

        // 0) Set layoutmanager for this Mediator instance.
        this.setLayout(new BorderLayout());

        // 1) Create colleage components
        iTree = new TreeView(this);
        iTable = new TableView(this);
        iTabbedPane = new TabbedView(this);

        // 2) Set listeners
        this.setListeners();

        // 3) Construct IconBar
        IconPanel lJpanIcons = new IconPanel(this);

        // 4) Name the PeptideIdentifcations.
        iSelectedPeptideIdentifications.setNumberedNamesToPeptideIdentifications();

        // 5) Construct the gui of the Mediator panel
        // 5 a) Tree Panel
        jpanTree = new JPanel(new BorderLayout());
        jpanTree.add(new JScrollPane(iTree));
        iTreeTitledBorder = BorderFactory.createTitledBorder(createTreeBorderText());
        jpanTree.setBorder(iTreeTitledBorder);

        // 5 b) Table Panel
        jpanTable = new JPanel(new BorderLayout());
        jpanTable.setBorder(BorderFactory.createTitledBorder("Identification details"));

        jpanTable.add(new JScrollPane(iTable));

        // 5 c) Tabs Panel
        initFirstTab();
        jpanTabbedPane = new JPanel(new BorderLayout());
        jpanTabbedPane.add(iTabbedPane, BorderLayout.CENTER);

        // 5 d) Arrange the JPanels in splitpanes.

        spltVerticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, iTabbedPane, jpanTable);
        spltVerticalSplitter.setOneTouchExpandable(true);
        spltVerticalSplitter.setDividerSize(10);

        spltHorizontalSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, jpanTree, spltVerticalSplitter);
        spltHorizontalSplitter.setOneTouchExpandable(true);

        // x) finish by adding into a main panel.
        jpanMain = new JPanel(new BorderLayout());
        jpanMain.add(spltHorizontalSplitter, BorderLayout.CENTER);
        jpanMain.add(lJpanIcons, BorderLayout.EAST);

        this.add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * Sets the listeners on the GUI.
     */
    private void setListeners() {

        // 1. Double click on a PeptideIdentification -> create new tab.
        iTree.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    treeNodeSelection();
                }
            }
        });

        // 2. Track peptidehit selection to spectrum annotation.
        iTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                columnSelected();
            }
        });

        // 3. Right click popup menu on a tab.
        iTabbedPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Right click opens popupmenu.
                if (e.getButton() == MouseEvent.BUTTON3) {
                    // Save the Point of the Tab that got clicked as final for the inner class ActionListers.
                    final MouseEvent lMouseEvent = e;
                    // Build the popupmenu.
                    JPopupMenu jpop = new JPopupMenu("Tab options");

                    JMenuItem item = null;
                    JMenu menu = null;

                    // first menu opens copy fucnctions.
                    menu = new JMenu("Copy");
                    menu.setMnemonic(KeyEvent.VK_C);

                    // PeptideSequence
                    item = new JMenuItem("Sequence");
                    item.setMnemonic(KeyEvent.VK_S);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int lPeptideHit = Mediator.this.getSelectedTableColumn();
                            String s = Mediator.this.getActivePeptideIdentification().getPeptideHit(lPeptideHit - 1).getSequence();
                            // This method writes a string to the system clipboard.
                            StringSelection ss = new StringSelection(s);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                        }
                    });
                    menu.add(item);

                    // Modified PeptideSequence
                    item = new JMenuItem("Modified sequence");
                    item.setMnemonic(KeyEvent.VK_M);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int lPeptideHit = Mediator.this.getSelectedTableColumn();
                            String s = Mediator.this.getActivePeptideIdentification().getPeptideHit(lPeptideHit - 1).getModifiedSequence();
                            // This method writes a string to the system clipboard.
                            StringSelection ss = new StringSelection(s);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                        }
                    });
                    menu.add(item);
                    // Spectrum Filename
                    item = new JMenuItem("Filename");
                    item.setMnemonic(KeyEvent.VK_F);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int lPeptideHit = Mediator.this.getSelectedTableColumn();
                            String s = Mediator.this.getActivePeptideIdentification().getSpectrum().getFilename();
                            // This method writes a string to the system clipboard.
                            StringSelection ss = new StringSelection(s);
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                        }
                    });
                    menu.add(item);

                    jpop.add(menu);
                    jpop.addSeparator();

                    // second item closes the selected tab.
                    item = new JMenuItem("Close this tab");
                    item.setMnemonic(KeyEvent.VK_T);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Mediator.this.removeTab(iTabbedPane.getSelectedIndex());
                        }
                    });
                    jpop.add(item);
                    // third item closes all tabs but except the selected tab.
                    item = new JMenuItem("Close all but this");
                    item.setMnemonic(KeyEvent.VK_B);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Mediator.this.removeAllButSelectedTab();
                        }
                    });
                    jpop.add(item);

                    // more items?
                    // Show the popupmenu.
                    jpop.show(iTabbedPane, lMouseEvent.getX(), lMouseEvent.getY());
                }
            }
        });

        // 4. Show MessageDialog with Legend to Table if Header of Column0 is clicked.
        iTable.getTableHeader().addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton() == MouseEvent.BUTTON1)) {
                    if (iTable.getSelectedColumn() == 0) {
                        // Legend message.
                        String lTitle = "Legend to Peptizer's Table.";
                        String lMessage =
                                "<HTML>" +
                                        "<DL>" +
                                        "<DT><STRONG>Strong type</STRONG>" +
                                        "<DD>If the Agent inspected positive (+1) on the property it stands for, this type is used." +
                                        "<DT><EM>Emphasize type</EM>" +
                                        "<DD>If the Agent inspected opposite (-1) on the property it stands for, this type is used." +
                                        "<DT>Normal type" +
                                        "<DD>If the Agent inspected neutral (0) on the property it stands for, this type is used." +
                                        "</DL>" +
                                        "</HTML>";
                        new AdvancedMessageDialog((JFrame) SwingUtilities.getRoot(Mediator.this), lTitle, lMessage);
                    }
                }
            }
        });
    }

    /**
     * Returns whether Validations have changed since last save.
     *
     * @return boolean changestatus
     */
    public boolean isChangedSinceLastSave() {
        return iChangedSinceLastSave;
    }

    /**
     * Set to true when the Mediator is changed since.
     *
     * @param aChangedSinceLastSave boolean changestatus
     */
    public void setChangedSinceLastSave(boolean aChangedSinceLastSave) {
        iChangedSinceLastSave = aChangedSinceLastSave;
    }

    /**
     * Handle the events after a validation has been performed.
     */
    public void validationPerformed() {
        // 1. Note the Mediator there have been changes since last save.
        setChangedSinceLastSave(true);

        // 2. Update the Tree border.
        this.setTreeBorderText(createTreeBorderText());

        //3. Message in log.
        if (this.getActivePeptideIdentification().getValidationReport().isValidated()) {
            String s = this.getActivePeptideIdentification().getValidationReport().getResult() ? "Accepted " : "Rejected ";
            s = s + this.getActivePeptideIdentification().getName();
            MatLogger.logNormalEvent(s);
        }

        // 3a. Get current selected PeptideIdentification index.
        int lSelectedTabIndex = iTabbedPane.getSelectedIndex();

        // 3b. Move to the next peptideidentification, except if the last tree node is reached!

        this.moveToNextPeptideIdentification();

        // 3c. Remove the last validated PeptideIdentification.
        this.removeTab(lSelectedTabIndex);

        // 3. Update the TreeUI
        iTree.updateUI();
    }

    /**
     * Creates a String for the Tree Border. It's based on the number of selected peptideidentifications and the overall validation status.
     *
     * @return String for TreeBorder.
     */
    private String createTreeBorderText() {
        String lTreeBorderText = "";
        int lNumberOfSpectra = iSelectedPeptideIdentifications.getNumberOfSpectra();
        int lNumberOfValidatedSpectra = iSelectedPeptideIdentifications.getNumberOfValidatedSpectra();

        if (lNumberOfSpectra != 0) {
            lTreeBorderText = lNumberOfValidatedSpectra + " of " + lNumberOfSpectra + " validated. ("
                    + (lNumberOfValidatedSpectra * 100 / lNumberOfSpectra) + "%).";
        }
        return lTreeBorderText;
    }


    /**
     * Sets the first peptideidentification as the first tab for convenience.
     */
    private void initFirstTab() {
        iTabbedPane.addTabID(iSelectedPeptideIdentifications.getPeptideIdentification(0));
    }

    /**
     * Returns the number of PeptideIdentifications in the datasource.
     *
     * @return The number of PeptideIdentifications.
     */
    public int getNumberOfPeptideIdentifications() {
        return iSelectedPeptideIdentifications.getNumberOfSpectra();
    }

    /**
     * Returns the PeptideIdentification at aIndex.
     *
     * @param aIndex int index of the PeptideIdentification - zero based.
     * @return the PeptideIdentification at aIndex.
     */
    public PeptideIdentification getPeptideIdentification(int aIndex) {
        return iSelectedPeptideIdentifications.getPeptideIdentification(aIndex);
    }

    /**
     * Returns the index of the first selected column,
     * -1 if no column is selected.
     *
     * @return the index of the first selected column.
     */
    public int getSelectedTableColumn() {
        return iTable.getSelectedColumn();
    }

    /**
     * Returns the PeptideIdentification that is currently displayed in the Table.
     *
     * @return PeptideIdentification of the Table.
     */
    public PeptideIdentification getActivePeptideIdentification() {
        return iTable.getTableID();
    }

    /**
     * Returns selectedPeptideIdentifications of the Mediator.
     *
     * @return selectedPeptideIdentifications.
     */
    public SelectedPeptideIdentifications getSelectedPeptideIdentifications() {
        return iSelectedPeptideIdentifications;
    }

    /**
     * Sync handling (Table & Tabs) of the Mediator when a treeNode is selected.
     */
    public void treeNodeSelection() {
        Object o = iTree.getLastSelectedPathComponent();
        if (o instanceof PeptideIdentification) {
            if (iTabbedPane.getTabCount() != 0) {
                iTable.setColumnSelectionInterval(1, 1);
            }
            iTabbedPane.addTabID((PeptideIdentification) o);
        }
    }

    /**
     * Sync handling (Table & Tabs) of the Mediator when a Table column is selected, update the annotations on the spectrum.
     */
    public void columnSelected() {
        // Sync the annotations on the TabPanel
        if (Mediator.this.getSelectedTableColumn() > 0) {
            iTabbedPane.updateSpectrumAnnotation();
        }
    }

    /**
     * Sync handling (Table & Tabs) of the Mediator when the Tab selection index setter is accessed.
     *
     * @param aIndex Selected index of the tab.
     */
    public void tabSelection(int aIndex) {
        if (aIndex != -1) {
            TabPanel tab = (TabPanel) iTabbedPane.getComponentAt(aIndex);
            iTable.setTableID(tab.getPeptideIdentification());
            // Sync the Table
        } else {
            clearTable();
        }
    }

    /**
     * Sync handling (Table & Tabs) whenever a tab is removed.
     */
    public void tabRemoved() {
        if (iTabbedPane.getTabCount() > 0) {
            // Set to index 0 of array.
            iTabbedPane.setSelectedIndex(iTabbedPane.getTabCount() - 1);
        }
        // Mind that the SingleSelectionModelImpl will be triggered and will head up to the tabSelection() handling above.
    }

    /**
     * Close TabPanel tab by index.
     * Taken care of at Mediator level since it has to be forwarded to the Table.
     *
     * @param aIndex of Tab to be closed.
     */
    public void removeTab(int aIndex) {
        iTabbedPane.remove(aIndex);
        tabRemoved();
    }

    /**
     * Remove all tabs.
     */
    public void removeAllTabs() {
        iTabbedPane.removeAll();
    }

    /**
     * Remove all tabs except the selected tab.
     */
    public void removeAllButSelectedTab() {
        // First get the component that was selected,
        TabPanel lTabPanel = (TabPanel) iTabbedPane.getComponent(iTabbedPane.getSelectedIndex());
        // Remove all components,
        removeAllTabs();
        // re-add the selected component.
        iTabbedPane.add(lTabPanel.getPeptideIdentification().getName(), lTabPanel);
        iTabbedPane.setSelectedIndex(0);
    }

    /**
     * Remove all the tabs.
     */
    public void clearView() {
        iTabbedPane.removeAll();
        clearTable();
    }

    public void clearTable() {
        iTable.setTableID(null);
    }

    /**
     * Set the text on the Tree Border.
     *
     * @param aString Text for titled border.
     */
    public void setTreeBorderText(String aString) {
        iTreeTitledBorder.setTitle(aString);
        jpanTree.validate();
    }

    /**
     * Set the Filter to be applied to the Tree.
     *
     * @param aFilter TreeFilter.
     */
    public void setFilter(TreeFilter aFilter) {
        iTree.setFilter(aFilter);
    }

    /**
     * Disable the filter.
     */
    public void disableFilter() {
        iTree.disableFilter();
    }

    /**
     * @Inherit
     */
    public void validate() {
        // Call super validation,
        super.validate();
        // And reset the split dividers after packing.
        if (spltHorizontalSplitter != null && spltVerticalSplitter != null) {
            spltVerticalSplitter.setDividerLocation(0.80);
            spltHorizontalSplitter.setDividerLocation(0.20);
        }
    }


    /**
     * String representation of the Mediator.
     *
     * @return String describing the Mediator.
     */
    public String toString() {
        return "Hi, i am a Mediator.\n  "
                + "I contain a selection of " + iSelectedPeptideIdentifications.getNumberOfSpectra() + " spectra with PeptideIdentifcations.\n"
                + "(Iterator description: " + iSelectedPeptideIdentifications.getMeta(SelectedPeptideIdentifications.MK_ITERITOR_DESCRIPTION);
    }

    /**
     * Returns the number of rows that are currently being displayed in the Mediator JTable.
     *
     * @return int nubmer of rows in the Table.
     */
    public int getNumberOfVisibleTableRows() {
        return iTable.getNumberOfVisibleRows();
    }

    /**
     * Returns the row at aRowIndex.
     *
     * @param aRowIndex index of the TableRow (0 returns the first row).
     * @return The AbstractTableRow at aRowIndex.
     */
    public AbstractTableRow getTableRow(int aRowIndex) {
        return iTable.getTableRow(aRowIndex);
    }

    /**
     * Move the Tree & TabPanel selection to the next.
     */
    public void moveToNextPeptideIdentification() {
        PeptideIdentification lPeptideIdentification = iTree.nextInTree();
        if (lPeptideIdentification != null) {
            iTable.setColumnSelectionInterval(1, 1);
            iTabbedPane.addTabID(lPeptideIdentification);
        }
    }

    /**
     * Move the Tree & TabPanel selection to the next.
     */
    public void moveToPreviousPeptideIdentification() {
        PeptideIdentification lPeptideIdentification = iTree.previousInTree();
        if (lPeptideIdentification != null) {
            iTable.setColumnSelectionInterval(1, 1);
            iTabbedPane.addTabID(lPeptideIdentification);
        }
    }

    /**
     * Returns a list of Agents that were used to select the PeptideIdentifcations in this Mediator.
     *
     * @return List with
     */
    public List getActiveAgents() {
        return getPeptideIdentification(0).getAgentIDList();
    }

    /**
     * Calls a Dialog with graphs for the table parameters.
     */
    public void showAgentGraph() {

    }

    /**
     * This method accepts all PeptideIdentifications in the Tree.
     */
    public void acceptAll() {
        int lNumberOfSpectra = iSelectedPeptideIdentifications.getNumberOfSpectra();
        for (int i = 0; i < lNumberOfSpectra; i++) {
            PeptideIdentification lIdentification = iSelectedPeptideIdentifications.getPeptideIdentification(i);
            ValidationReport lValidationReport = lIdentification.getValidationReport();
            lValidationReport.setResult(true);
            lValidationReport.setComment("AUTO_ACCEPT");
        }
        validationPerformed();
        repaint();
    }

    /**
     * This method rejects all PeptideIdentifcations  in the Tree.
     */
    public void rejectAll() {
        int lNumberOfSpectra = iSelectedPeptideIdentifications.getNumberOfSpectra();
        for (int i = 0; i < lNumberOfSpectra; i++) {
            PeptideIdentification lIdentification = iSelectedPeptideIdentifications.getPeptideIdentification(i);
            ValidationReport lValidationReport = lIdentification.getValidationReport();
            lValidationReport.setResult(false);
            lValidationReport.setComment("AUTO_REJECT");
        }
        validationPerformed();
        repaint();
    }
}
