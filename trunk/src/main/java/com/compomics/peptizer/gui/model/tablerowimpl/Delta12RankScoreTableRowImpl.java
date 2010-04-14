package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;

import java.math.BigDecimal;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 16:45:06
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table.
 */
public class Delta12RankScoreTableRowImpl extends AbstractTableRow {

    /**
     * {@inheritDoc}
     */

    public Delta12RankScoreTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */

    public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        String result;
        if (aPeptideHitNumber + 1 < aPeptideIdentification.getNumberOfPeptideHits()) {

            // To keep things clear, the getPeptideHit method returns items from a Vector.
            // Where "0" returns the first peptidehit.
            // Seems unlogical now, though it was a design decision during MascotDatfile development where
            // 0 gives you the first element in the Vector.

            int aFirstPeptideHit = aPeptideHitNumber - 1;
            int aSecondPeptideHit = aPeptideHitNumber;

            double lScore1 = aPeptideIdentification.getPeptideHit(aFirstPeptideHit).getIonsScore();
            double lScore2 = aPeptideIdentification.getPeptideHit(aSecondPeptideHit).getIonsScore();
            double delta = lScore1 - lScore2;
            if (lScore1 >= 0 && lScore2 >= 0)
                result = "" + new BigDecimal(delta).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            else result = "NA";

        } else {
            result = "NA";
        }
        return result;
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    @Override
    public String getDescription() {
        String s = "Generalrow - The difference in identityscore between peptidehit n and peptidehit n+1.";
        return s;
    }
}
