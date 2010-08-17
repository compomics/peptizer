package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class RatioEValue12TableRowImpl extends AbstractTableRow {

    /**
     * {@inheritDoc}
     */

    public RatioEValue12TableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */

    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        String result = "";

        // To keep things clear, the getPeptideHit method returns items from a Vector.
        // Where "0" returns the first peptidehit.
        // Seems unlogical now, though it was a design decision during MascotDatfile development where
        // 0 gives you the first element in the Vector.

        int aFirstPeptideHit = aPeptideHitNumber - 1;
        int total = aPeptideIdentification.getNumberOfPeptideHits();
        if (aPeptideHitNumber < total) {
            double lConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
            ArrayList<SearchEngineEnum> advocates = aPeptideIdentification.getPeptideHit(aFirstPeptideHit).getAdvocate().getAdvocatesList();
            for (int i = 0; i < advocates.size(); i++) {
                for (int j = aPeptideHitNumber; j < aPeptideIdentification.getNumberOfPeptideHits(); j++) {
                    if (aPeptideIdentification.getPeptideHit(j).identifiedBy(advocates.get(i))) {
                        double lEValue1 = aPeptideIdentification.getPeptideHit(aFirstPeptideHit).getPeptidHit(advocates.get(i)).getExpectancy(lConfidence);
                        double lEValue2 = aPeptideIdentification.getPeptideHit(j).getPeptidHit(advocates.get(i)).getExpectancy(lConfidence);
                        double difference = Math.log10(lEValue1 / lEValue2);
                        result += new BigDecimal(difference).setScale(2, BigDecimal.ROUND_HALF_DOWN) + "(" + advocates.get(i).getInitial() + ") ";
                        break;
                    }
                }
            }
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
        String s = "Generalrow - difference of the log of the E-Values between peptidehit n and peptidehit n+1.";
        return s;
    }
}
