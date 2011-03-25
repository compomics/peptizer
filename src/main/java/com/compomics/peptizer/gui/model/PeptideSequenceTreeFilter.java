package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;

/**
 * This class is a Filter for the IdentificationTree in Peptizer,
 * and by the peptide sequence.
 */
public class PeptideSequenceTreeFilter implements TreeFilter {

    /**
     * The active sequence of the TreeFilter.
     */
    private String iSequence = "";


    /**
     * Returns a boolean whether a peptideidentification may pass a filter.
     *
     * @param aPeptideIdentification PeptideIdentification
     * @return boolean with status
     */
    public boolean pass(PeptideIdentification aPeptideIdentification) {
        boolean lResult = false;

        PeptizerPeptideHit[] lConfidentPeptideHits = aPeptideIdentification.getConfidentPeptideHits();

        // Iterate over all confidence peptidehits.
        for (PeptizerPeptideHit lPeptideHit : lConfidentPeptideHits) {
            if (lPeptideHit.getSequence().contains(iSequence)) {
                lResult = true;
                break;
            }
        }
        return lResult;
    }

    /**
     * Returns the peptide sequence active by this filter.
     *
     * @return
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * Set the peptide sequence active by this filter.
     *
     * @param aSequence
     */
    public void setSequence(String aSequence) {
        iSequence = aSequence.trim();
    }


}
