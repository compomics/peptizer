package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-aug-2007
 * Time: 10:03:36
 */

/**
 * Class description:
 * ------------------
 * This class was developed to di
 */
public class HomologyScoreTableRowImpl extends AbstractTableRow {

    /**
     * {@inheritDoc}
     */
    public HomologyScoreTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        BigDecimal lBigDecimal = new BigDecimal(aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getPeptidHit(SearchEngineEnum.Mascot).getHomologyThreshold());
        lBigDecimal = lBigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN);

        return lBigDecimal.toString();
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Generalrow - The homology threshold";
    }
}
