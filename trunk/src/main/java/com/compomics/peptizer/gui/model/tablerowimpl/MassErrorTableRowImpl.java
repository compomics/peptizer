package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;

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
public class MassErrorTableRowImpl extends AbstractTableRow {


    /**
     * {@inheritDoc}
     */
    public MassErrorTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        return Double.toString(aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getDeltaMass()) + "Da";
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am the Mass Error tablerow implementation. I show the difference between the experimental mass and the theoretical precursor mass (Da).";
    }
}