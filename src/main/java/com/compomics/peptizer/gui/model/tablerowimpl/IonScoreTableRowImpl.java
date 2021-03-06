package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 16:41:51
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a TableRow implementation to populate the table with the ionscore
 */
public class IonScoreTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for IonScoreTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(IonScoreTableRowImpl.class);

    /**
     * {@inheritDoc}
     */
    public IonScoreTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        if (aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getIonsScore() >= 0)
            return "" + aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1).getPeptidHit(SearchEngineEnum.Mascot).getIonsScore();
        else return "";
    }

    /**
     * Returns a description for the TableRow.
     * Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    @Override
    public String getDescription() {
        String s = "Generalrow - The ionscore of peptidehit n.";
        return s;
    }
}
