package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.gui.dialog.AdvancedMessageDialog;
import com.compomics.peptizer.gui.dialog.ParameterDialog;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.AgentAggregatorFactory;
import com.compomics.peptizer.util.AgentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-aug-2007
 * Time: 15:23:33
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class AgentAggregatorPanel extends JPanel {

    // Instance data
    private AgentAggregator[] iAgentAggregators;

    // GUI componenents.
    private JComboBox cmbAgentAggregators = null;
    private JButton btnInfo = null;
    private JTable iAgentAggregatorTable = null;
    private JPanel jpanTable = null;
    private JPanel jpanComboInfo = null;
    private JPanel jpanLeft = null;
    private JScrollPane scrollTable = null;


    private final String FIRST_COLUMNNAME = "Name";
    private final String SECOND_COLUMNNAME = "Value";

    /**
     * Creates a new <code>JPanel</code> displaying the availlable Aggregators of Peptizer.
     */
    public AgentAggregatorPanel() {
        // Call super constructor of JPanel.
        super();
        // Fetch all AgentAggregators ID's from AgentAggregatorFactory.
        iAgentAggregators = AgentAggregatorFactory.getInstance().getAgentAggregators();

        constructPanel();

        setListeners();
    }

    /**
     * Load the list with AgentAggregator's from the Factory.
     */
    public void resetAgentAggregatorPanel() {
        iAgentAggregators = AgentAggregatorFactory.getInstance().getAgentAggregators();
        this.removeAll();
        constructPanel();
    }

    /**
     * Construct the GUI components on this Panel.
     */
    private void constructPanel() {

        // The combobox will contain all the AgentAggregators.
        cmbAgentAggregators = new JComboBox(iAgentAggregators);
        cmbAgentAggregators.setMaximumSize(new Dimension(1000, cmbAgentAggregators.getPreferredSize().height));

        // The button will popup a help dialog on the current AgentAggregator.
        btnInfo = new JButton("Info");
        btnInfo.setMnemonic(KeyEvent.VK_H);
        btnInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdvancedMessageDialog((JFrame) (SwingUtilities.getRoot(cmbAgentAggregators)).getParent(), iAgentAggregators[cmbAgentAggregators.getSelectedIndex()].getName() + " description.", iAgentAggregators[cmbAgentAggregators.getSelectedIndex()].getHTMLDescription());
            }
        });

        // The properties table is a private implemantation. It makes use of the instance JComboBox.
        createTable();

        jpanComboInfo = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 0));
        jpanComboInfo.add(cmbAgentAggregators);
        jpanComboInfo.add(btnInfo);

        jpanLeft = new JPanel();
        jpanLeft.setLayout(new BoxLayout(jpanLeft, BoxLayout.PAGE_AXIS));
        jpanLeft.add(jpanComboInfo);
        jpanLeft.add(Box.createVerticalGlue());

        scrollTable = new JScrollPane(iAgentAggregatorTable);
        scrollTable.getViewport().setPreferredSize(new Dimension((new Double(scrollTable.getViewport().getPreferredSize().width)).intValue(),
                new Double(iAgentAggregators[cmbAgentAggregators.getSelectedIndex()].getProperties().size() * 50).intValue()));

        jpanTable = new JPanel();
        jpanTable.setBorder(BorderFactory.createTitledBorder("Properties Table"));
        jpanTable.setLayout(new BorderLayout());
        jpanTable.add(scrollTable, BorderLayout.CENTER);
        jpanTable.add(Box.createRigidArea(new Dimension(150, 1)), BorderLayout.EAST);

        this.setLayout(new BorderLayout(10, 0));
        this.add(jpanComboInfo, BorderLayout.WEST);
        this.add(jpanTable, BorderLayout.CENTER);

        this.validate();

    }

    private void createTable() {
        iAgentAggregatorTable = new JTableImpl();
        iAgentAggregatorTable.setRowHeight(20);
        iAgentAggregatorTable.setCellSelectionEnabled(false);
        iAgentAggregatorTable.setRowSelectionAllowed(true);
        iAgentAggregatorTable.setColumnSelectionAllowed(false);
        iAgentAggregatorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    /**
     * Returns the selected AgentAggregator in the Combobox.
     *
     * @return AgentAggregator that is selected in the Combobox.
     */
    public AgentAggregator getAgentAggregator() {
        // First, get the selected aggregator from the combobox for the task.
        AgentAggregator lAgentAggregator = iAgentAggregators[cmbAgentAggregators.getSelectedIndex()];
        // Second, set the latest version of the AgentFactory to the AgentAggregator.
        lAgentAggregator.setAgentsCollection(AgentFactory.getInstance().getActiveAgents());
        return lAgentAggregator;
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
                    int col = iAgentAggregatorTable.getSelectedColumn();
                    int row = iAgentAggregatorTable.getSelectedRow();
                    if ((col >= 0) && (row >= 0)) {
                        String value = iAgentAggregatorTable.getValueAt(row, col).toString();
                        Object temp = new StringSelection(value);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) temp, (ClipboardOwner) temp);
                    }
                } else {
                    super.keyPressed(e);
                }
            }
        });

        // B. Open a parameterDialog to modify Agent settings.
        iAgentAggregatorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        // Get columnname of selected column, must equal final String PARAMETERS.
                        if (iAgentAggregatorTable.getColumnName(iAgentAggregatorTable.getSelectedColumn()).equals(SECOND_COLUMNNAME)) {
                            int row = iAgentAggregatorTable.getSelectedRow();
                            if (iAgentAggregators[row].getProperties().size() > 0) {
                                new ParameterDialog((JFrame) (SwingUtilities.getRoot(iAgentAggregatorTable)).getParent(), (iAgentAggregators[row].getName() + " properties."), iAgentAggregators[row].getProperties());
                            }
                        }
                        AgentAggregatorPanel.this.validate();
                        AgentAggregatorPanel.this.updateUI();
                        AgentAggregatorPanel.this.repaint();
                    }
                }
                super.mouseClicked(e);
            }
        });

        // C. Repaint after the JCombobox selection was modified.

        cmbAgentAggregators.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createTable();
                iAgentAggregatorTable.validate();
                iAgentAggregatorTable.repaint();
                AgentAggregatorPanel.this.validate();
                AgentAggregatorPanel.this.updateUI();
                AgentAggregatorPanel.this.repaint();
            }
        });

    }


    /**
     * TableModel for the Table in the AgentPanel.
     */
    private class AgentAggregatorTableModel extends DefaultTableModel {


        /**
         * {@inheritDoc}
         */
        public int getRowCount() {
            return iAgentAggregators[cmbAgentAggregators.getSelectedIndex()].getProperties().size();
        }

        /**
         * {@inheritDoc}
         */
        public int getColumnCount() {
            return 2;
        }

        /**
         * '0' returns the name of the first column. {@inheritDoc}
         */
        public String getColumnName(int column) {
            if (column == 0) {
                return FIRST_COLUMNNAME;
            } else if (column == 1) {
                return SECOND_COLUMNNAME;
            } else {
                return "undefined column name!";
            }
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int column) {
            Properties lProperties = iAgentAggregators[cmbAgentAggregators.getSelectedIndex()].getProperties();

            Object o = null;
            String s = this.getColumnName(column);

            if (s.equals(FIRST_COLUMNNAME)) {
                o = lProperties.keySet().toArray()[row];
            } else if (s.equals(SECOND_COLUMNNAME)) {
                Object key = lProperties.keySet().toArray()[row];
                o = lProperties.get(key);
            }
            return o.toString();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
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
            setModel(new AgentAggregatorTableModel());
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
                        lTooltip = "The name of the AgentAggregator Property. Click the info button for more info.";
                        break;
                    case 1:
                        lTooltip = "Click to change the value of the AgentAggregator Property.";
                        break;
                    default:
                        lTooltip = "Implement tooltip for column " + column + " in " + this.getClass();
                        break;
                }
                ((JComponent) c).setToolTipText(lTooltip);
            }
            return c;
        }

    }

}
