package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 15:45:29
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table.
 */
public class NtermModificationLabelTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for NtermModificationLabelTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(NtermModificationLabelTableRowImpl.class);

    public static String ACE = "Ace";
    public static String Cterm = "But";

    /**
     * {@inheritDoc}
     */
    public NtermModificationLabelTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        // Transform into 0-index!
        aPeptideHitNumber = aPeptideHitNumber - 1;
        String lResult = "NA";
        String lModifiedSequence = aPeptideIdentification.getPeptideHit(aPeptideHitNumber).getModifiedSequence().toUpperCase();

        if (lModifiedSequence.startsWith("AC")) {
            if (lModifiedSequence.startsWith("ACE")) {
                lResult = "ACE";
            } else {
                int index = lModifiedSequence.indexOf("-");
                lResult = lModifiedSequence.substring(0, index);
            }

        } else if (lModifiedSequence.startsWith("BUT")) {
            lResult = "BUT";
        } else if (lModifiedSequence.startsWith("PRO")) {
            lResult = "PRO";
        } else if (lModifiedSequence.startsWith("NH2")) {
            if (lModifiedSequence.indexOf("PYR") > -1) {
                lResult = "PYR";
            } else {
                lResult = "NH2";
            }
        }

        return lResult;

    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Template abstract tablerow implementation.";
    }
}
