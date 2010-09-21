package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 10:27:06
 */

/**
 * Class description:
 * ------------------
 * This abstract class is developed to be implemented by objects that need to populate the table.
 * The TableModel will use the getData() method on the implementing object to extract specific information from
 * a PeptideIdentification to populate the table.
 */
public abstract class AbstractTableRow {
	// Class specific log4j logger for AbstractTableRow instances.
	 private static Logger logger = Logger.getLogger(AbstractTableRow.class);

    /**
     * The name of the TableRow.
     */
    private String iName = "";

    /**
     * The activity of this TableRow.
     */
    protected boolean iActive = true;

    /**
     * Boolean whether the getData() method should return String in html formatting or not.
     */
    protected static boolean iHTML = true;

    /**
     * Returns the active status.
     *
     * @return boolean of the TableRow activity.
     */
    public boolean isActive() {
        return iActive;
    }

    /**
     * Sets the active status.
     *
     * @param aActive boolean of the TableRow activity.
     */
    public void setActive(boolean aActive) {
        iActive = aActive;
    }

    /**
     * Empty Constructor.
     */
    public AbstractTableRow() {
    }

    /**
     * Returns the data on the PeptideIdentifcation.
     *
     * @param aPeptideIdentification PeptideIdentification on the table.
     * @param aPeptideHitNumber      Returns PeptideHit by NAME. * <b>1' will return the first PeptideHit. '2' the seconde etc..</b>'
     * @return data on the identification.
     */
    public Object getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        try {
            return getDataImpl(aPeptideIdentification, aPeptideHitNumber);
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * Returns the data on the PeptideIdentifcation.
     *
     * @param aPeptideIdentification PeptideIdentification on the table.
     * @param aPeptideHitNumber      Returns PeptideHit by NAME. * <b>1' will return the first PeptideHit. '2' the seconde etc..</b>'
     * @return data on the identification.
     */
    public abstract Object getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber);


    /**
     * Returns the fixed String ID.
     *
     * @return Fixed identifier of the TableRow.
     */
    public String getUniqueTableRowID() {
        return this.getClass().getName();
    }

    /**
     * Sets the name of the TableRow.
     */
    public void setName(String aName) {
        iName = aName;
    }

    /**
     * Returns the name of the Tablerow.
     *
     * @return the name of the Tablerow.
     */
    public String getName() {
        return iName;
    }

    /**
     * Sets whether the TableRows must return data in html formatting or not.
     *
     * @param aHTML boolean
     */
    public static void setHTML(boolean aHTML) {
        iHTML = aHTML;
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public abstract String getDescription();


}
