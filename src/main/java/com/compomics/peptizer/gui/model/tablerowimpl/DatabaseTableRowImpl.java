package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 15:45:29
 */

/**
 * Class description: ------------------ This class was developed as a TableRow implementation to populate the table.
 */
public class DatabaseTableRowImpl extends AbstractTableRow {


    /**
     * {@inheritDoc}
     */
    public DatabaseTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        return aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getDatabase(aPeptideIdentification);
    }

    /**
     * Returns a description for the TableRow. Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Databse tablerow implementation. I show the database into which this identification was made.";
    }
}