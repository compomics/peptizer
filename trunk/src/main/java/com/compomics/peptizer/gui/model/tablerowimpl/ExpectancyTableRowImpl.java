package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 14-aug-2007
 * Time: 13:55:45
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class ExpectancyTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for ExpectancyTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(ExpectancyTableRowImpl.class);


    /**
     * {@inheritDoc}
     */
    public ExpectancyTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        ArrayList<SearchEngineEnum> advocates = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getAdvocate().getAdvocatesList();
        String result = "";
        for (int i = 0; i < advocates.size(); i++) {
            double lConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
            BigDecimal lBigDecimal = new BigDecimal(aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getPeptidHit(advocates.get(i)).getExpectancy(lConfidence));
            lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);
            result += lBigDecimal + "(" + advocates.get(i).getInitial() + ") ";
        }
        return result;
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        double lConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));
        return "Generalrow - The E-value (for default " + new Double((1.0 - lConfidence) * 100) + "% confidence).";
    }
}
