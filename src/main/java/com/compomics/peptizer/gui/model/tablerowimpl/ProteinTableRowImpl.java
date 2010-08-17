package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;

import java.util.ArrayList;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-sep-2007
 * Time: 14:14:49
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table.
 */
public class ProteinTableRowImpl extends AbstractTableRow {


    /**
     * {@inheritDoc}
     */
    public ProteinTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        ArrayList lProteins = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getProteinHits();
        StringBuffer sb = new StringBuffer();
        int lIteration;
        if (lProteins.size() <= 3) {
            lIteration = lProteins.size();
        } else {
            lIteration = 3;
        }

        for (int i = 0; i < lIteration; i++) {
            sb.append(((PeptizerProteinHit) lProteins.get(i)).getAccession());
            if ((i + 1) < lIteration) {
                sb.append(" / ");
            }
            if (lProteins.size() > lIteration && (i + 1) == lIteration) {
                sb.append("(").append(lProteins.size()).append(")");
            }
        }
        return sb.toString();
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Protein tablerow implementation.";
    }

}
