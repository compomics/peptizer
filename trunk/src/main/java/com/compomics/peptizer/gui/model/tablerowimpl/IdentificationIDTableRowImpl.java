package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 15:45:29
 */

/**
 * Class description: ------------------ This class was developed as a TableRow implementation to populate the table.
 */
public class IdentificationIDTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for IdentificationIDTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(IdentificationIDTableRowImpl.class);


    /**
     * {@inheritDoc}
     */
    public IdentificationIDTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        Object o = null;
        if (aPeptideIdentification.metaDataContainsKey(MetaKey.Identification_id)) {
            o = aPeptideIdentification.getMetaData(MetaKey.Identification_id);
        }
        String s = null;
        if (o == null) {
            s = "";
        } else {
            s = o.toString();
        }
        return s;
    }

    /**
     * Returns a description for the TableRow. Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Template abstract tablerow implementation.";
    }
}
