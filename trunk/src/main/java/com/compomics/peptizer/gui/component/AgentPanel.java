package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.dialog.AddAgentDialog;
import com.compomics.peptizer.gui.dialog.ParameterDialog;
import com.compomics.peptizer.gui.interfaces.Updateable;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.fileio.ConfigurationWriter;
import com.compomics.peptizer.util.fileio.FileManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-jun-2007
 * Time: 15:10:15
 */

/**
 * Class description: ------------------ This class was developed to present a group of Agents in an editable Table.
 */
public class AgentPanel extends JPanel implements Updateable {

    /**
     * The table to display the Agents.
     */
    private JTable iAgentTable;

    // Gui components.
    private JPanel jpanContent;
    private JPanel jpanButtons;
    private JScrollPane scroll1;
    private JButton btnAdd;
    private JButton btnLoad;
    private JButton btnSave;
    private JButton btnClear;

    private final String NAME = "Name";
    private final String ACTIVE = "Active";
    private final String VETO = "Veto";
    private final String INFORMER = "Inform";

    private final String PARAMETERS = "Parameters";
    private final String COMPATIBILITY = "Restriction";

    /**
     * The Collection of Agents to display in the Table.
     */
    private Agent[] iAgents = null;

    /**
     * The names of the columns.
     */
    private ArrayList iColumnNames = null;

    /**
     * The rootpane whereon this AgentPanel is located.
     */
    private Window iOwner = null;


    /**
     * This constructor takes an Array with Agents as a single input.
     *
     * @param aAgents Agent array to be displayed in the JPanel.
     * @param aOWner  Window whereon this AgentPanel is located.
     */
    public AgentPanel(Agent[] aAgents, Window aOWner) {
        super();
        iAgents = aAgents;
        iOwner = aOWner;
        construct();
    }

    /**
     * This constructor takes no arguments. Agents are loaded from the AgentFactory.
     *
     * @param aOwner Window whereon this AgentPanel is located.
     */
    public AgentPanel(Window aOwner) {
        super();
        loadAgentsFromAgentFactory();
        iOwner = aOwner;
        construct();
    }

    /**
     * Loads the Agent[] that fills the table with all the availlable Agents from the AgentFactory.
     */
    public void loadAgentsFromAgentFactory() {
        iAgents = AgentFactory.getInstance().getAllAgents();
    }

    /**
     * Construct the Panel.
     */
    private void construct() {

        // First create the Table to display the Agents.
        iColumnNames = new ArrayList();
        iColumnNames.add(NAME);
        iColumnNames.add(ACTIVE);
        iColumnNames.add(VETO);
        iColumnNames.add(INFORMER);
        iColumnNames.add(PARAMETERS);
        iColumnNames.add(COMPATIBILITY);


        iAgentTable = new JTableImpl();
        iAgentTable.setModel(new AgentPanelTableModel());
        iAgentTable.setRowHeight(20);
        iAgentTable.setCellSelectionEnabled(false);
        iAgentTable.setRowSelectionAllowed(true);
        iAgentTable.setColumnSelectionAllowed(false);
        // This enable's the JScrollpane again for some reason!
        iAgentTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Second create buttons to add, load and save Agent configurations.

        // Save.
        btnSave = new JButton("Save Agents");
        btnSave.setToolTipText("Save current Agent profile into an Agent configuration file.");
        btnSave.setMnemonic(KeyEvent.VK_S);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FileManager.getInstance().selectAgentConfigurationOutput(AgentPanel.this)) {
                    ConfigurationWriter.writeAgentConfiguration(FileManager.getInstance().getAgentConfigurationOutput());
                }
            }
        });

        // Add.
        btnAdd = new JButton("Add Agents");
        btnAdd.setToolTipText("Add Agents to the table from the classpath or the complete Agent configuration file.");
        btnAdd.setMnemonic(KeyEvent.VK_S);
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component c = (Component) e.getSource();
                Component frame = SwingUtilities.getRoot(c);

                JDialog dialog = new AddAgentDialog((JFrame) frame.getParent(), AgentPanel.this);
                dialog.setVisible(true);


            }
        });

        // Load.
        btnLoad = new JButton("Load Agents");
        btnLoad.setMnemonic(KeyEvent.VK_L);
        btnLoad.setToolTipText("Load an Agent profile from an Agent configuration file.");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (FileManager.getInstance().selectAgentConfigurationInput(AgentPanel.this)) {
                    MatConfig.getInstance().reloadConfigurationFile(FileManager.getInstance().getAgentConfigurationInput(), MatConfig.AGENT_CONFIG);
                    update();
                }
            }
        });
        // Clear
        btnClear = new JButton("Clear");
        btnClear.setMnemonic(KeyEvent.VK_C);
        btnClear.setToolTipText("Clear the current Agent profile.");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AgentFactory.getInstance().setAllActiveFalse();
                AgentFactory.getInstance().setAllVetoFalse();
                AgentPanel.this.repaint();
            }
        });

        jpanButtons = new JPanel();
        BoxLayout lBoxLayout = new BoxLayout(jpanButtons, BoxLayout.PAGE_AXIS);
        jpanButtons.setLayout(lBoxLayout);
        jpanButtons.add(btnLoad);
        jpanButtons.add(Box.createVerticalStrut(10));
        jpanButtons.add(btnAdd);
        jpanButtons.add(Box.createVerticalStrut(10));
        jpanButtons.add(btnSave);
        jpanButtons.add(Box.createVerticalStrut(10));
        jpanButtons.add(btnClear);
        jpanButtons.add(Box.createVerticalGlue());

        JPanel jpanButtonsBorder = new JPanel();
        BoxLayout lBoxLayout2 = new BoxLayout(jpanButtonsBorder, BoxLayout.X_AXIS);

        jpanButtonsBorder.setLayout(lBoxLayout2);
        jpanButtonsBorder.add(Box.createHorizontalStrut(5));
        jpanButtonsBorder.add(jpanButtons);
        jpanButtonsBorder.add(Box.createHorizontalStrut(5));

        scroll1 = new JScrollPane(iAgentTable);

        jpanContent = new JPanel();
        jpanContent.setLayout(new BorderLayout());
        jpanContent.add(scroll1, BorderLayout.CENTER);
        jpanContent.add(jpanButtonsBorder, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(jpanContent, BorderLayout.CENTER);
        this.validate();

        setListeners();
        doOptimalResize();
    }

    public void update() {
        loadAgentsFromAgentFactory();
        this.doOptimalResize();
        iOwner.pack();
    }

    /**
     * Resizes the components in an optimal way.
     */
    public void doOptimalResize() {

        // A. Resize the columns to their content.
        iAgentTable.setSize(jpanContent.getSize().width, iAgentTable.getSize().height);

        int lSpacer = 30;

        for (int i = 0; i < iAgentTable.getColumnModel().getColumnCount(); i++) {
            TableColumn lTableColumn = iAgentTable.getColumnModel().getColumn(i);
            int lColumnWidth = lTableColumn.getWidth();
            String lName = (String) lTableColumn.getHeaderValue();

            // 1) Name or Parameters column (find largest name and resize)
            if (lName.equals(NAME) || lName.equals(PARAMETERS)) {
                int lRowCount = iAgentTable.getRowCount();
                int lMaxLength = 0;
                int lMaxRow = -1;
                for (int j = 0; j < lRowCount; j++) {
                    int lRowLength = ((String) iAgentTable.getValueAt(j, i)).length();
                    if (lMaxLength < lRowLength) {
                        lMaxLength = lRowLength;
                        lMaxRow = j;
                    }
                }
                lColumnWidth =
                        iAgentTable.getFontMetrics(iAgentTable.getFont()).stringWidth(((String) iAgentTable.getValueAt(lMaxRow, i)));
                if (lTableColumn.getWidth() != lColumnWidth) {
                    if (lName.equals(NAME)) {
                        lTableColumn.setMinWidth(lColumnWidth + lSpacer);
                        lTableColumn.setMaxWidth(lColumnWidth + lSpacer);
                    } else if ((lName.equals(PARAMETERS))) {
                        int lTotalColumnWidth = iAgentTable.getColumnModel().getTotalColumnWidth();
                        int lTableWidth = scroll1.getSize().width;
                        lTableColumn.setMinWidth(lColumnWidth);
                    }
                }

            } else if (lName.equals(VETO)) {
                // 2) Veto column (Header size)
                lColumnWidth = iAgentTable.getFontMetrics(iAgentTable.getFont()).stringWidth((VETO));
                if (lTableColumn.getWidth() != lColumnWidth) {
                    lTableColumn.setMinWidth(lColumnWidth + lSpacer);
                    lTableColumn.setMaxWidth(lColumnWidth + lSpacer);
                }
            } else if (lName.equals(ACTIVE)) {
                // 3) Active column (Header size)
                lColumnWidth = iAgentTable.getFontMetrics(iAgentTable.getFont()).stringWidth((ACTIVE));
                if (lTableColumn.getWidth() != lColumnWidth) {
                    lTableColumn.setMinWidth(lColumnWidth + lSpacer);
                    lTableColumn.setMaxWidth(lColumnWidth + lSpacer);
                }
            } else if (lName.equals(INFORMER)) {
                // 3) Active column (Header size)
                lColumnWidth = iAgentTable.getFontMetrics(iAgentTable.getFont()).stringWidth((INFORMER));
                if (lTableColumn.getWidth() != lColumnWidth) {
                    lTableColumn.setMinWidth(lColumnWidth + lSpacer);
                    lTableColumn.setMaxWidth(lColumnWidth + lSpacer);
                }
            }
        }
        this.validate();
        this.repaint();
    }

    /**
     * Sets the listeners on the AgentPanel.
     */
    private void setListeners() {

        // A. Detect <ctrl>+<c> combinations.
        this.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if ((e.isShiftDown()) && (e.getKeyCode() == KeyEvent.VK_C)) {
                    int col = iAgentTable.getSelectedColumn();
                    int row = iAgentTable.getSelectedRow();
                    if ((col >= 0) && (row >= 0)) {
                        String value = iAgentTable.getValueAt(row, col).toString();
                        Object temp = new StringSelection(value);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) temp, (ClipboardOwner) temp);
                    }
                } else {
                    super.keyPressed(e);
                }
            }
        });

        // B. Open a parameterDialog to modify Agent settings.
        iAgentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        // Get columnname of selected column, must equal final String PARAMETERS.
                        if (iAgentTable.getColumnName(iAgentTable.getSelectedColumn()).equals(PARAMETERS)) {
                            int row = iAgentTable.getSelectedRow();
                            if (iAgents[row].getProperties().size() > 0) {
                                new ParameterDialog((JFrame) (SwingUtilities.getRoot(iAgentTable)).getParent(), (iAgents[row].getName() + " properties."), iAgents[row].getProperties());
                            }
                        }
                        AgentPanel.this.doOptimalResize();
                    }
                }
                super.mouseClicked(e);
            }
        });

    }

    /**
     * TableModel for the Table in the AgentPanel.
     */
    private class AgentPanelTableModel extends DefaultTableModel {

        /**
         * {@inheritDoc}
         */
        public int getRowCount() {
            return iAgents.length;
        }

        /**
         * {@inheritDoc}
         */
        public int getColumnCount() {
            return iColumnNames.size();
        }

        /**
         * '0' returns the name of the first column. {@inheritDoc}
         */
        public String getColumnName(int column) {
            return (String) iColumnNames.get(column);
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int column) {
            Agent lAgent = iAgents[row];
            Object o = null;
            String s = this.getColumnName(column);

            if (s.equals(NAME)) {
                o = lAgent.getName();
            } else if (s.equals(ACTIVE)) {
                o = Boolean.valueOf(lAgent.isActive());
            } else if (s.equals(VETO)) {
                o = Boolean.valueOf(lAgent.hasVeto());
            } else if (s.equals(INFORMER)) {
                o = Boolean.valueOf(lAgent.isInforming());
            } else if (s.equals(PARAMETERS)) {
                StringBuffer sb = new StringBuffer();
                Properties prop = lAgent.getProperties();
                if (prop.size() == 0) {
                    sb.append("NA");
                } else {
                    Object[] keys = prop.keySet().toArray();
                    Object[] values = prop.values().toArray();
                    sb.append("<html>");
                    for (int i = 0; i < keys.length; i++) {
                        sb.append(keys[i] + " : <b>" + values[i] + "</b>");
                        if (i + 1 < keys.length) {
                            sb.append("   ");
                        }
                    }
                    sb.append("</html>");
                }
                // Return the String processing.
                o = sb.toString();
            } else if (s.equals(COMPATIBILITY)) {
                StringBuffer sb = new StringBuffer();
                SearchEngineEnum[] compatibleEngines = lAgent.getCompatibleEngines();
                for (int i = 0; i < compatibleEngines.length; i++) {
                    sb.append(compatibleEngines[i].getName());
                    if (i == compatibleEngines.length - 1) {
                        sb.append(".");
                    } else {
                        sb.append(", ");
                    }
                }

                // Return the String processing.
                o = sb.toString();
            }

            return o;
        }


        /**
         * {@inheritDoc} This extended version updates the Agent's in the AgentFactory if the boolean values have
         * changed.
         */
        public void setValueAt(Object aValue, int row, int column) {
            String lUniqueID = iAgents[row].getUniqueID();

            Agent lAgentFromFactory = AgentFactory.getInstance().getAgent(lUniqueID);
            Agent lAgentInTable = iAgents[row];
            boolean lStatus = ((Boolean) aValue).booleanValue();

            // If a value in the "active status" column is changed, update the corresponding Agent in the factory.
            if (column == 1) {
                lAgentInTable.setActive(lStatus);
                lAgentFromFactory.setActive(lStatus);

                if (!lStatus) {
                    lAgentInTable.setVeto(false);
                    lAgentInTable.setInforming(false);

                    lAgentFromFactory.setVeto(false);
                    lAgentFromFactory.setInforming(false);
                }

                // If a value in the "veto status" column is changed, update the corresponding Agent in the factory.
            } else if (column == 2) {
                lAgentInTable.setVeto(lStatus);
                lAgentFromFactory.setVeto(lStatus);

                if (lStatus) {
                    lAgentFromFactory.setActive(true);
                    lAgentInTable.setActive(true);
                }

            } else if (column == 3) {
                lAgentInTable.setInforming(lStatus);
                lAgentFromFactory.setInforming(lStatus);

                if (lStatus) {
                    lAgentFromFactory.setActive(true);
                    lAgentInTable.setActive(true);
                }

            }
            iAgentTable.repaint();

        }

        /**
         * {@inheritDoc}
         */
        public boolean isCellEditable(int row, int column) {

            boolean lEditable;

            switch (column) {
                case 0:
                    lEditable = false;
                    break;

                case 1:
                    lEditable = true;
                    break;

                case 2:
                    lEditable = true;
                    break;

                case 3:
                    lEditable = true;
                    break;

                default:
                    lEditable = false;
                    break;
            }
            return lEditable;
        }

        /**
         * {@inheritDoc}
         */
        public Class getColumnClass(int columnIndex) {
            return getValueAt(1, columnIndex).getClass();
        }
    }

    private class JTableImpl extends JTable {

        /**
         * Constructs a default <code>JTable</code> that is initialized with a default data model, a default column
         * model, and a default selection model.
         *
         * @see #createDefaultDataModel
         * @see #createDefaultColumnModel
         * @see #createDefaultSelectionModel
         */
        public JTableImpl() {
            super();
        }


        /**
         * Prepares the renderer by querying the data model for the value and selection state of the cell at
         * <code>row</code>, <code>column</code>. Returns the component (may be a <code>Component</code> or a
         * <code>JComponent</code>) under the event location. <b>Note:</b> Throughout the table package, the internal
         * implementations always use this method to prepare renderers so that this default behavior can be safely
         * overridden by a subclass.
         *
         * @param renderer the <code>TableCellRenderer</code> to prepare
         * @param row      the row of the cell to render, where 0 is the first row
         * @param column   the column of the cell to render, where 0 is the first column
         * @return the <code>Component</code> under the event location
         */
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (c instanceof JComponent) {
                String lTooltip = "";
                switch (column) {
                    case 0:
                        lTooltip = iAgents[row].getDescription();
                        break;

                    case 1:
                        lTooltip = "Sets wether or not the Agent should be used.";
                        break;

                    case 2:
                        lTooltip = "Sets wether or not the Agent has a dominant veto on the result. ";
                        break;

                    case 3:
                        lTooltip = "Sets wether or not the Agent is only informing.";
                        break;

                    case 4:
                        lTooltip = "Sets optional parameters of the Agent. Click to change. ";
                        break;

                    default:
                        lTooltip = "Implement tooltip for column " + column + " in " + this.getClass();
                }
                ((JComponent) c).setToolTipText(lTooltip);
            }
            return c;
        }

    }
}
