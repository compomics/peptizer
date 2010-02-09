package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;

import java.math.BigDecimal;
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
	public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
		double lConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA"));

		BigDecimal lBigDecimal = new BigDecimal(aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getExpectancy(lConfidence));
		lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);

		return lBigDecimal.toString();
	}

	/**
	 * Returns a description for the TableRow.
	 * Use for tooltips and configuration.
	 *
	 * @return String description of the TableRow.
	 */
	public String getDescription() {
		double lConfidence = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_ALPHA"));
		return "Generalrow - The E-value (for default " + new Double((1.0 - lConfidence) * 100) + "% confidence).";
	}
}
