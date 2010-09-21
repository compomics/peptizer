package com.compomics.peptizer.util.datatools;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: Mar 22, 2010
 * Time: 12:27:24 PM
 * This factory will provide the identifications after preprocessing when necessary.
 */
public class IdentificationFactory {
	// Class specific log4j logger for IdentificationFactory instances.
	 private static Logger logger = Logger.getLogger(IdentificationFactory.class);

    // Attributes

    private static IdentificationFactory singleton = null;
    private final ArrayList<SearchEngineEnum> implementedSearchEngines = FileToolsFactory.getInstance().getImplementedSearchEngines();

    // working with ms_lims
    private Connection connection = null;
    private long projectID = -1;
    private ArrayList<Long> iIdentificationIDs = null;

    // working with various search engines
    private HashMap<SearchEngineEnum, HashSet<PeptideIdentification>> identifications = new HashMap();
    private FileToolsFactory fileToolsFactory = FileToolsFactory.getInstance();


    // Constructor

    private IdentificationFactory() {
        for (int i = 0; i < implementedSearchEngines.size(); i++) {
            identifications.put(implementedSearchEngines.get(i), new HashSet());
        }
    }

    public static IdentificationFactory getInstance() {
        if (singleton == null) {
            singleton = new IdentificationFactory();
        }
        return singleton;
    }


    // Methods

    public PeptideIdentificationIterator getIterator() {
        // For now ms_lims contains only Mascot results. Future versions might need preprocessing if not made upstream.
        if (projectID != -1) {
            return fileToolsFactory.getIterator(connection, projectID);
        } else if (iIdentificationIDs != null) {
            return fileToolsFactory.getIterator(iIdentificationIDs);
        } else if (identifications.size() > 0) {
            return new PreprocessedIdentificationIterator();
        } else {
            return null;
        }
    }

    public void load(File aFile) {
        try {
            PeptideIdentificationIterator newIdentificationsIt = fileToolsFactory.getIterator(aFile);
            PeptideIdentification newIdentification, oldIdentification;
            SearchEngineEnum currentkey;
            Iterator<SearchEngineEnum> keyIterator;
            Iterator<PeptideIdentification> oldIdentificationsIt;
            String currentSpectrum;
            boolean found;
            while (newIdentificationsIt.hasNext()) {
                newIdentification = newIdentificationsIt.next();
                currentSpectrum = newIdentification.getSpectrum().getName();
                keyIterator = identifications.keySet().iterator();
                found = false;
                while (keyIterator.hasNext() && !found) {
                    currentkey = keyIterator.next();
                    if (currentkey != newIdentification.getAdvocate().getAdvocatesList().get(0)) {
                        oldIdentificationsIt = identifications.get(currentkey).iterator();
                        while (oldIdentificationsIt.hasNext()) {
                            oldIdentification = oldIdentificationsIt.next();
                            if (oldIdentification.getSpectrum().getName().equals(currentSpectrum)) {
                                oldIdentification.fuse(newIdentification);
                                found = true;
                                break;
                            }
                        }
                    }
                }
                if (!found) {
                    identifications.get(newIdentification.getAdvocate().getAdvocatesList().get(0)).add(newIdentification);
                }
            }
        } catch (Exception e) {
                MatLogger.logExceptionalEvent("File Import Failure.\n" + e.getMessage() + "\n" + e.getStackTrace());
            identifications = new HashMap();
        }
    }

    public void load(Connection aConnection, long aProjectID) {
        this.connection = aConnection;
        this.projectID = aProjectID;
    }

    public void load(ArrayList<Long> iIdentificationIDs) {
        this.iIdentificationIDs = iIdentificationIDs;
    }


    private class PreprocessedIdentificationIterator implements PeptideIdentificationIterator {

        private int progress = 0;
        private Iterator<SearchEngineEnum> keyIterator = identifications.keySet().iterator();
        private SearchEngineEnum currentKey = keyIterator.next();
        private Iterator<PeptideIdentification> identificationIterator = identifications.get(currentKey).iterator();

        public PreprocessedIdentificationIterator() {

        }

        /**
         * Returns the next PeptideIdentification.
         *
         * @return the next PeptideIdentification. null if no more left.
         */
        public PeptideIdentification next() {
            if (this.hasNext()) {
                progress++;
                return identificationIterator.next();
            } else {
                return null;
            }
        }

        /**
         * Returns true if more PeptideIdentifications are left.
         *
         * @return true if more PeptideIdentifications are left<br></br>false otherwise.
         */
        public boolean hasNext() {
            if (identificationIterator.hasNext()) {
                return true;
            }
            while (keyIterator.hasNext()) {
                currentKey = keyIterator.next();
                if (identifications.get(currentKey).size() > 0) {
                    identificationIterator = identifications.get(currentKey).iterator();
                    return true;
                }
            }
            return false;
        }

        /**
         * Not implemented.
         */
        public void remove() {

        }

        /**
         * Returns an <b>estimate</b> of the iteration size to measure progress of the worker.
         *
         * @return estimation of the iteration size.
         */
        public int estimateSize() {
            int size = 0;
            Iterator<SearchEngineEnum> keyIt = identifications.keySet().iterator();
            while (keyIt.hasNext()) {
                size += identifications.get(keyIt.next()).size();
            }
            return size;
        }

        /**
         * Returns an <b>estimate</b> of the work left of the iteration to measure progress of the worker.
         *
         * @return
         */
        public int estimateToDo() {
            return estimateSize() - progress;
        }

        /**
         * Returns a String description of the current activity of the Iterator.
         *
         * @return String description of the Iterator.
         */
        public String getCurrentFileDescription() {
            return "Preprocessed identifications";
        }

        /**
         * Returns a String description of the general function of the Iterator.
         *
         * @return String description of the Iterator.
         */
        public String getGeneralDescription() {
            return "Preprocessed identifications iterator";
        }
    }

}
