package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-jul-2007
 * Time: 17:23:27
 */

/**
 * Class description:
 * ------------------
 * This class was developed to display the Modified sequence in the TableView.
 */
public class ModifiedSequenceTableRowImpl extends AbstractTableRow {

    /**
     * {@inheritDoc}
     */
    public ModifiedSequenceTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        String s = "";
        s = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getModifiedSequence();
        return s;
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    @Override
    public String getDescription() {
        String s = "Generalrow - The modified sequence of peptidehit n.";
        return s;
    }


}