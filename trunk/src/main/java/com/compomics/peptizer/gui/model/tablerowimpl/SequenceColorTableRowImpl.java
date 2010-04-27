package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;

import javax.swing.*;
import java.util.Properties;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-sep-2007
 * Time: 16:12:53
 */

/**
 * Class description: ------------------ This class was developed as a TableRow implementation to populate the table.
 */
public class SequenceColorTableRowImpl extends AbstractTableRow {


    /**
     * {@inheritDoc}
     */
    public SequenceColorTableRowImpl() {
        super();
        Properties prop = MatConfig.getInstance().getTableRowProperties(this.getUniqueTableRowID());
        super.setName(prop.getProperty("name"));
        super.setActive(new Boolean(prop.getProperty("active")));
    }

    /**
     * {@inheritDoc}
     */
    public Object getData(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        JLabel label = null;
        try {
            // Get the peptidehit.
            PeptizerPeptideHit ph = aPeptideIdentification.getPeptideHit(aPeptideHitNumber - 1);
            // Get the colored sequence
            label = ph.getColoredModifiedSequence(aPeptideIdentification);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;

    }


    /**
     * Returns a description for the TableRow. Use for tooltips and configuration.
     *
     * @return String description of the TableRow.
     */
    public String getDescription() {
        return "Hi, i am a Sequence Color  tablerow implementation. I display the peptide sequence colored by found fragmentation ions.";
    }

    /**
     * Private implementation to display a PeptideSequence for the toString() instead of an object reference. (CSV output
     * uses toString!)
     */
    private class JLabelImpl extends JLabel {

        String iName = "";

        /**
         * Creates a <code>JLabel</code> instance with the specified text. The label is aligned against the leading edge of
         * its display area, and centered vertically.
         *
         * @param text The text to be displayed by the label.
         */
        public JLabelImpl(String text, String aName) {
            super(text);
            iName = aName;
        }


        /**
         * Returns a string representation of this component and its values.
         *
         * @return a string representation of this component
         * @since JDK1.0
         */
        @Override
        public String toString() {
            return iName;
        }
    }
}
