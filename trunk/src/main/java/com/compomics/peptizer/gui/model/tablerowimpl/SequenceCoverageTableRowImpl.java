package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;

import java.math.BigDecimal;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 20-sep-2007
 * Time: 14:00:20
 */

/**
 * Class description: ------------------ This class was developed as a TableRow implementation to populate the table
 * with sequence coverage information.
 */
public class SequenceCoverageTableRowImpl extends AbstractTableRow {


    /**
     * {@inheritDoc}
     */
    public SequenceCoverageTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(Boolean.valueOf(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public String getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        StringBuffer sb = new StringBuffer();
        PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1);
        int[] lCoverage = lPeptideHit.getSequenceCoverage(aPeptideIdentification);
        //
        BigDecimal BionPercentage =
                new BigDecimal(((lCoverage[0] + 0.0) / (lPeptideHit.getSequence().length() - 1))).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal YionPercentage =
                new BigDecimal(((lCoverage[1] + 0.0) / (lPeptideHit.getSequence().length() - 1))).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal ionPercentage =
                new BigDecimal(((lCoverage[2] + 0.0) / (lPeptideHit.getSequence().length()))).setScale(2, BigDecimal.ROUND_HALF_UP);


        sb.append("b:").append(BionPercentage).append(" - y:").append(YionPercentage).append(" - all:").append(ionPercentage);
        return sb.toString();
    }

    /**
     * Returns a description for the TableRow. Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Sequence Coverage tablerow implementation. I show b, y and general ion coverage of Fused Matched Ions.";
    }
}
