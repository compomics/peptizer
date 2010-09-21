package com.compomics.peptizer.util.datatools;

import org.apache.log4j.Logger;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.uuw.services.remoting.EntryRetrievalService;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Jul 2, 2010
 * Time: 10:06:24 AM
 * This singleton instance serves to store all protein sequences and accession that are used while running Peptizer.
 * Note that only UNIPROT accessions are allowed.
 */
public class AccessionToSequenceMap extends HashMap {
	// Class specific log4j logger for AccessionToSequenceMap instances.
	 private static Logger logger = Logger.getLogger(AccessionToSequenceMap.class);


    // Singleton instance.
    private static AccessionToSequenceMap instance = null;
    private EntryRetrievalService iEntryRetrievalService;

    /**
     * Empty constructor.
     */
    private AccessionToSequenceMap() {
        super();
        initRetrievalService();
    }

    /**
     * Get the singleton Instance that enholds protein accessions as keys and protein sequences as values.
     *
     * @return
     */
    public static AccessionToSequenceMap getInstance() {
        if (instance == null) {
            instance = new AccessionToSequenceMap();
        }
        return instance;
    }

    /**
     * Initialize the Uniprot retrieval service instance variable.
     */
    private void initRetrievalService() {
        //Create entry retrival service
        iEntryRetrievalService = UniProtJAPI.factory.getEntryRetrievalService();
    }

    /**
     * Get a UniProtEntry for the given accession. Can be null!!
     *
     * @param lAccession
     * @return
     */
    private UniProtEntry getEntryFromRetrievalService(String lAccession) {
        UniProtEntry entry = (UniProtEntry) iEntryRetrievalService.getUniProtEntry(lAccession);
        return entry;
    }

    /**
     * Returns the protein sequence for the given accession. NULL if none found.
     *
     * @param aAccession
     * @return
     */
    public String getProteinSequence(String aAccession) {
        String lResult = null;
        if (this.get(aAccession) == null) {
            UniProtEntry lEntry = getEntryFromRetrievalService(aAccession);
            if (lEntry == null) {
                logger.info("No Entry found for accession '" + aAccession + "'.");
            } else {
                String lSequence = lEntry.getSequence().getValue();
                this.put(aAccession, lSequence);
            }
        }
        lResult = (String) this.get(aAccession);
        return lResult;
    }
}
