package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.tablerowimpl.AgentTableRowImpl;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;

import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 16-apr-2007
 * Time: 15:36:43
 */

/**
 * Class description:
 * ------------------
 * This class was developed to manage a fixed number of AbstractTableRows.
 */
public class TableRowManager {
	// Class specific log4j logger for TableRowManager instances.
	 private static Logger logger = Logger.getLogger(TableRowManager.class);
    /**
     * The General abstract TableRow implementations.
     */
    private ArrayList iGeneralTableRows;
    /**
     * The Agent abstract TableRow implementations.
     */
    private ArrayList iAgentTableRows;

    /**
     * The boolean keeps track if the Number of Visible got changed.
     */
    private boolean iNumberOfVisibleRowsChanged = false;

    /**
     * The number of visible rows.
     */
    private int iNumberOfVisibleRows = -1;

    /**
     * Cache for the visible TableRows.
     */
    private ArrayList iVisibleTableRows = new ArrayList();

    /**
     * This constructor takes a TableView parent as a single parameter.
     *
     * @param aUniqueAgentIDs List with Unqiue Agent ids's
     */
    public TableRowManager(final List aUniqueAgentIDs) {
        //
        init(aUniqueAgentIDs);
    }


    /**
     * Initiate the
     *
     * @param aUniqueAgentIDs
     */
    private void init(final List aUniqueAgentIDs) {
        createAbstractRows(aUniqueAgentIDs);
        createVisibleTableRows();
    }

    /**
     * Creates the abstract rows that can populate the TableView.
     * The objects are dynamically loaded from the static MatConfig properties.
     *
     * @param aUniqueAgentIDs
     */
    private void createAbstractRows(final List aUniqueAgentIDs) {
        // Instance fields.
        iGeneralTableRows = new ArrayList();
        iAgentTableRows = new ArrayList();

        // Local fields.
        Properties prop = null;
        String lClassName = null;
        StringTokenizer st = null;
        AbstractTableRow lTableRow = null;
        Iterator iter = null;

        // 1. Build general rows
        String[] lTableRowIDs = MatConfig.getInstance().getTableRowIDs();
        for (int i = 0; i < lTableRowIDs.length; i++) {
            boolean failed = true;
            // Key is the TableRow class reference and TableRow's unique ID!
            lClassName = lTableRowIDs[i];
            // Dynamically initiate the TableRow.
            try {
                lTableRow = ((AbstractTableRow) (Class.forName(lClassName)).newInstance());
                // Store in the TableRow container.
                iGeneralTableRows.add(lTableRow);
                // If the precodure gets here, the tablerow was loaded correctly.
                failed = false;
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            }
            if (failed) {
                MatLogger.logExceptionalEvent("Failed to load TableRow " + lClassName + " from the classpath.");
            }
        }

        // 2. Build agent rows

        if (aUniqueAgentIDs != null) {
            iter = aUniqueAgentIDs.iterator();

            while (iter.hasNext()) {
                // Key is the Agent class reference and Agent's unique ID!
                String lUniqueAgentID = (String) iter.next();
                // All Agent's are identified by their unique package name.
                // Let's use this name as well to keep track of the connection between the AgentTableRow and the Agent itself.
                lTableRow = new AgentTableRowImpl(lUniqueAgentID);
                if (AgentFactory.getInstance().getAgent(lUniqueAgentID) != null) {
                    lTableRow.setActive(AgentFactory.getInstance().getAgent(lUniqueAgentID).isActive());
                    lTableRow.setName(AgentFactory.getInstance().getAgent(lUniqueAgentID).getName());
                } else {
                    lTableRow.setActive(true);
                    lTableRow.setName(lUniqueAgentID.substring(lUniqueAgentID.lastIndexOf('.') + 1));
                }
                iAgentTableRows.add(lTableRow);
            }
        }
    }

    /**
     * Returns the number of visible rows. (cached)
     *
     * @return the number of visible rows.
     */
    public int getNumberOfVisibleRows() {
        if ((iNumberOfVisibleRows == -1) || (iNumberOfVisibleRowsChanged == true)) {
            iNumberOfVisibleRows = countVisibleRows();
            iNumberOfVisibleRowsChanged = false;
        }
        return iNumberOfVisibleRows;
    }

    /**
     * Returns the number of of visible rows.
     *
     * @return the number of visible rows.
     */
    private int countVisibleRows() {
        int lRowCount = 0;
        // 1. Count the number of Visible Agent rows.
        ListIterator iter = iAgentTableRows.listIterator();
        while (iter.hasNext()) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iter.next();
            if (lAbstractTableRow.isActive()) {
                lRowCount++;
            }
        }
        // 2. Count the number of Visible General rows.
        iter = iGeneralTableRows.listIterator();
        while (iter.hasNext()) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iter.next();
            if (lAbstractTableRow.isActive()) {
                lRowCount++;
            }
        }
        return lRowCount;
    }

    /**
     * Returns the corresponding tablerow.
     *
     * @param aRowIndex Integer with the row index.
     * @return the corresponding tablerow.
     */
    public AbstractTableRow getTableRow(int aRowIndex) {
        if ((iVisibleTableRows.size() != getNumberOfVisibleRows()) || (iNumberOfVisibleRowsChanged == true)) {
            createVisibleTableRows();
        }
        return (AbstractTableRow) iVisibleTableRows.get(aRowIndex);
    }

    /**
     * Creates (reset!) the visible TableRows.
     */
    private void createVisibleTableRows() {
        if (iVisibleTableRows.size() != 0) {
            iVisibleTableRows.clear();
        }

        ListIterator iter = null;
        iter = iAgentTableRows.listIterator();
        while (iter.hasNext()) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iter.next();
            if (lAbstractTableRow.isActive()) {
                iVisibleTableRows.add(lAbstractTableRow);
            }
        }

        iter = iGeneralTableRows.listIterator();
        while (iter.hasNext()) {
            AbstractTableRow lAbstractTableRow = (AbstractTableRow) iter.next();
            if (lAbstractTableRow.isActive()) {
                iVisibleTableRows.add(lAbstractTableRow);
            }
        }
        iNumberOfVisibleRowsChanged = false;
    }
}
