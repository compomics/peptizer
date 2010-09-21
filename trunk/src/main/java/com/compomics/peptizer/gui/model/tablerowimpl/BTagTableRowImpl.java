package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import org.apache.log4j.Logger;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 5-dec-2007
 * Time: 13:38:46
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class BTagTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for BTagTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(BTagTableRowImpl.class);

    /**
     * {@inheritDoc}
     */
    public BTagTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public Object getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        String result = null;
        try {
            // Get the peptidehit.
            PeptizerPeptideHit ph = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1);
            // Get the BTag
            result = Integer.toString(ph.getBTag(aPeptideIdentification));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;

    }


    /**
     * Returns a description for the TableRow. Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a B Tag tablerow implementation. I display the length of the longest sequence tag covered by b-ions.";
    }
}
