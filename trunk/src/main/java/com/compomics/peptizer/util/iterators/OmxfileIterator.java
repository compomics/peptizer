package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.io.FilenameExtensionFilter;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSSearchSettings;
import de.proteinms.omxparser.util.MSSpectrum;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 17.03.2009
 * Time: 14:07:03
 * To change this template use File | Settings | File Templates.
 */
public class OmxfileIterator implements PeptideIdentificationIterator {
	// Class specific log4j logger for OmxfileIterator instances.
	 private static Logger logger = Logger.getLogger(OmxfileIterator.class);

    /**
     * The Omssa file instance of the Iterator.
     */
    private OmssaOmxFile iOmssaOmxFile = null;

    /**
     * Integer index to track the iterator. This represents the index of the next PeptideIdentification that will be
     * returned when the next method is invoked.
     */
    private int iCountIndex = 0;

    /**
     * The File handle to the current Omssa Results file.
     */
    private File omxFile = null;

    /**
     * The File handle to the current Omssa mods.xml file.
     */
    private File modsFile = null;

    /**
     * The File handle to the current Omssa usermods.xml file.
     */
    private File usermodsFile = null;

    /**
     * This boolean flags whether a new Omxfile was set. As such, the OmssaOmxfile instance is only constructed
     * upon calling the hasNext() method instead of during construction of the iterator itself.
     */
    private boolean hasConstructedOmssaOmxFile = false;

    /**
     * This constructor takes an Omssa file instance and a Omssa mods.xml file as parameters.
     *
     * @param idFile File targets the Ommsa file.
     */
    public OmxfileIterator(File idFile) {

        File modsFile = null;
        File userModsFile = null;
        File currentFolder = new File(idFile.getParent());
        File[] modsResult = currentFolder.listFiles(new FilenameExtensionFilter(".xml"));
        if (modsResult != null) {
            for (int i = 0; i < modsResult.length; i++) {
                if (modsResult[i].getName().compareToIgnoreCase("mods.xml") == 0) {
                    modsFile = modsResult[i];
                }
                if (modsResult[i].getName().compareToIgnoreCase("usermods.xml") == 0) {
                    userModsFile = modsResult[i];
                }
            }
        }
        if (modsFile != null && userModsFile != null) {
            setOmxfile(idFile, modsFile, userModsFile);
        } else if (modsFile == null && userModsFile != null) {
            setOmxfile(idFile, null, userModsFile);
        } else if (modsFile != null && userModsFile == null) {
            setOmxfile(idFile, modsFile, null);
        } else {
            setOmxfile(idFile, null, null);
        }
    }

    public OmxfileIterator(final OmssaOmxFile anOmssaOmxFile) {
        iOmssaOmxFile = anOmssaOmxFile;
        hasConstructedOmssaOmxFile = true;
        iCountIndex = 0;
    }

    /**
     * Sets the OmssaOmxFile instance
     *
     * @param aFile       File targets the Ommsa file.
     * @param anotherFile File targets the Omssa mods.xml file.
     * @param alastFile   File targets the Omssa usermods.xml file.
     */
    public void setOmxfile(File aFile, File anotherFile, File alastFile) {
        if (aFile != null) {
            String toPrint = "NEW OMXFILE \' " + aFile.getName() + "\' INITIATED AT " + new Date(System.currentTimeMillis());
            if (anotherFile != null) {
                toPrint = toPrint + " (" + anotherFile.getName();
            }
            if (alastFile != null) {
                toPrint = toPrint + ", " + alastFile.getName();
            }
            toPrint = toPrint + ").";
            logger.info(toPrint);
            omxFile = aFile;
            modsFile = anotherFile;
            usermodsFile = alastFile;
            hasConstructedOmssaOmxFile = false;
            iCountIndex = 0;
        }
    }

    /**
     * returns the search settings used for the identification of the peptideHit
     *
     * @return the settings used
     */
    public MSSearchSettings getSettings() {
        return iOmssaOmxFile.getParserResult().MSSearch_request.MSRequest.get(0).MSRequest_settings.MSSearchSettings;
    }

    /**
     * returns the m/z scale
     *
     * @return msResponseScale
     */
    public int getMSResponseScale() {
        return iOmssaOmxFile.getParserResult().MSSearch_response.MSResponse.get(0).MSResponse_scale;
    }


    /**
     * {@inheritDoc}
     */
    public PeptideIdentification next() {
        if (hasNext()) {
            // Get the information of the next.
            HashMap<MSSpectrum, MSHitSet> aSpectrumToHitSetMap = iOmssaOmxFile.getSpectrumToHitSetMap();
            Iterator<MSSpectrum> iterator = aSpectrumToHitSetMap.keySet().iterator();
            OmssaSpectrum aSpectrum = new OmssaSpectrum(getMSResponseScale());
            while (iterator.hasNext()) {
                MSSpectrum tempSpectrum = iterator.next();
                if (tempSpectrum.MSSpectrum_number == iCountIndex) {
                    aSpectrum.setMSSpectrum(tempSpectrum);
                    break;
                }
            }

            if (aSpectrum != null) {
                // translation into peptizer object
                MSHitSet sHitSet = aSpectrumToHitSetMap.get(aSpectrum.getOriginalSpectrum());
                int hitsNumber = 0;
                if (sHitSet != null) {
                    hitsNumber = sHitSet.MSHitSet_hits.MSHits.size();
                }
                Vector peptizerPeptideHits = new Vector();
                for (int i = 0; i < hitsNumber; i++) {
                    peptizerPeptideHits.add(new OmssaPeptideHit(sHitSet.MSHitSet_hits.MSHits.get(i), iOmssaOmxFile.getModifications(), getSettings(), getMSResponseScale(), i + 1));
                }
                PeptideIdentification lPeptideIdentification = new PeptideIdentification(aSpectrum, peptizerPeptideHits, SearchEngineEnum.OMSSA);
                iCountIndex++;
                return lPeptideIdentification;
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        if (!hasConstructedOmssaOmxFile) {
            if (modsFile != null && usermodsFile != null) {
                OmssaOmxFile anOmssaOmxFile = new OmssaOmxFile(omxFile.getPath(), modsFile.getPath(), usermodsFile.getPath());
                iOmssaOmxFile = anOmssaOmxFile;
            } else if (modsFile == null && usermodsFile != null) {
                OmssaOmxFile anOmssaOmxFile = new OmssaOmxFile(omxFile.getPath(), null, usermodsFile.getPath());
                iOmssaOmxFile = anOmssaOmxFile;
            } else if (modsFile != null && usermodsFile == null) {
                OmssaOmxFile anOmssaOmxFile = new OmssaOmxFile(omxFile.getPath(), modsFile.getPath(), null);
                iOmssaOmxFile = anOmssaOmxFile;
            } else {
                OmssaOmxFile anOmssaOmxFile = new OmssaOmxFile(omxFile.getPath(), null, null);
                iOmssaOmxFile = anOmssaOmxFile;
            }
            hasConstructedOmssaOmxFile = true;
        }

        boolean lResult = false;
        if (iOmssaOmxFile != null) {
            HashMap<MSSpectrum, MSHitSet> aSpectrumToHitSetMap = iOmssaOmxFile.getSpectrumToHitSetMap();
            if (iCountIndex < aSpectrumToHitSetMap.size()) {
                lResult = true;
            }
        }
        return lResult;
    }

    /**
     * {@inheritDoc} The number of Queries in an Omssa file in this implementation.
     */
    public int estimateSize() {
        HashMap<MSSpectrum, MSHitSet> aSpectrumToHitSetMap = iOmssaOmxFile.getSpectrumToHitSetMap();
        return aSpectrumToHitSetMap.size();
    }

    /**
     * {@inheritDoc} The number of Queries left in an Omssa file in this implementation.
     */
    public int estimateToDo() {
        HashMap<MSSpectrum, MSHitSet> aSpectrumToHitSetMap = iOmssaOmxFile.getSpectrumToHitSetMap();
        return (aSpectrumToHitSetMap.size() - iCountIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
    }


    /**
     * Called by the garbage collector on an object when garbage collection determines that there are no more references
     * to the object. A subclass overrides the <code>finalize</code> method to dispose of system resources or to perform
     * other cleanup. Any exception thrown by the <code>finalize</code> method causes the finalization of this object to
     * be halted, but is otherwise ignored.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Hi I am an OmssafileIterator. (" + omxFile.getName() + ").";
    }

    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String getCurrentFileDescription() {
        String s = "";
        if (omxFile != null) {
            s = omxFile.getName();
        } else {
            s = "Omssa omx File";
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public String getGeneralDescription() {
        return getCurrentFileDescription();
    }

}


