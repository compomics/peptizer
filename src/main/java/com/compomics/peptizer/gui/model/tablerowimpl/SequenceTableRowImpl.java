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
 * Time: 16:34:54
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table.
 */
public class SequenceTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for SequenceTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(SequenceTableRowImpl.class);

    /**
     * {@inheritDoc}
     */
    public SequenceTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */

    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        return aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getSequence();
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    @Override
    public String getDescription() {
        String s = "Generalrow - The sequence of peptidehit n.";
        return s;
    }

}
