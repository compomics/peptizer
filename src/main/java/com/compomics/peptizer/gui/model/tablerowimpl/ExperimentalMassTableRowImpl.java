package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-sep-2007
 * Time: 14:01:00
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table.
 */
public class ExperimentalMassTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for ExperimentalMassTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(ExperimentalMassTableRowImpl.class);


    /**
     * {@inheritDoc}
     */
    public ExperimentalMassTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        return Double.toString(aPeptideIdentification.getSpectrum().getPrecursorMZ());
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am the Experimental Mass tablerow implementation. I show the precursor mass of the fragmentation spectrum (Da).";
    }
}
