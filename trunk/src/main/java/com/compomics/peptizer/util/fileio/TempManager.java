package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jan 21, 2009
 * Time: 5:26:25 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class was created to manage the creation and handling of temporary files.
 */
public class TempManager {
	// Class specific log4j logger for TempManager instances.
	 private static Logger logger = Logger.getLogger(TempManager.class);


    /**
     * This static list manages all the temp files.
     */
    private HashMap<SelectedPeptideIdentifications, Vector<TempFile>> iFileMap = new HashMap<SelectedPeptideIdentifications, Vector<TempFile>>();

    /**
     * Private static string identifier for temporary peptizer files in the temp folder.
     */
    private static String ID = "peptizer";

    /**
     * Singleton instance.
     */
    private static TempManager iManager = null;

    /**
     *
     */
    private File iTempRoot = null;

    /**
     * Private constructor for singleton instance.
     */
    private TempManager() {
        try {
            // Look for undeleted tmp files from previous peptizer sessions.
            File lTmp = File.createTempFile("construction", null);
            lTmp.deleteOnExit();
            lTmp.delete();
            File lTmpRoot = lTmp.getParentFile();

            File[] lTmpFiles = lTmpRoot.listFiles();
            for (int i = 0; i < lTmpFiles.length; i++) {
                File lTmpFile = lTmpFiles[i];
                if (lTmpFile.isDirectory()) {
                    if (lTmpFile.getName().equals(ID)) {
                        // We are in a temp folder named by the Peptizer ID.
                        // At startup, there should be no files within.
                        File[] oldFiles = lTmpFile.listFiles();
                        if (oldFiles != null) {
                            for (int j = 0; j < oldFiles.length; j++) {
                                File lOldFile = oldFiles[j];
                                lOldFile.delete();
                            }
                        }
                        break;
                    }
                }
            }

            // ok, now lets make the peptizer tmp folder if its non existing.
            iTempRoot = new File(lTmpRoot, ID);
            if (!iTempRoot.exists()) {
                iTempRoot.mkdir();
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /**
     * Get the singleton instance.
     *
     * @return
     */
    public static TempManager getInstance() {
        if (iManager == null) {
            iManager = new TempManager();
        }
        return iManager;
    }

    /**
     * Returns the tmp file for the given type and index.
     *
     * @param aType
     * @return
     */
    public File[] getFiles(SelectedPeptideIdentifications aSelectedPeptideIdentifications, TempFileEnum aType) {
        Vector<TempFile> v = iFileMap.get(aSelectedPeptideIdentifications);
        if (v == null) {
            return null;
        } else {
            ArrayList<File> lList = new ArrayList<File>();
            for (int i = 0; i < v.size(); i++) {
                TempFile lTempFile = v.elementAt(i);
                if (lTempFile.getType() == aType) {
                    lList.add(lTempFile.getFile());
                }
            }
            File[] lResult = new File[lList.size()];
            for (int i = 0; i < lResult.length; i++) {
                lResult[i] = lList.get(i);

            }
            return lResult;
        }
    }


    public File makeNextTempFile(SelectedPeptideIdentifications aSelectedPeptideIdentifications, TempFileEnum aType) throws IOException {
        Vector v = iFileMap.get(aSelectedPeptideIdentifications);
        if (v == null) {
            v = new Vector<TempFile>();
            iFileMap.put(aSelectedPeptideIdentifications, v);
        }

        File lFile = new File(iTempRoot, aType.iName + "_" + v.size() + "_" + System.currentTimeMillis());
        lFile.deleteOnExit();

        if (lFile.createNewFile()) {
            v.add(new TempFile(aType, lFile));
        }
        return lFile;

    }


    public int getNumberOfFiles(SelectedPeptideIdentifications aSelectedPeptideIdentifications, TempFileEnum aType) {
        Vector v = iFileMap.get(aSelectedPeptideIdentifications);
        if (v == null) {
            return 0;
        } else {
            int lCounter = 0;
            for (int i = 0; i < v.size(); i++) {
                TempFile lTempFile = (TempFile) v.elementAt(i);
                if (lTempFile.getType() == aType) {
                    lCounter++;
                }
            }
            return lCounter;
        }
    }

    private class TempFile {
        TempFileEnum iType = null;
        File iFile = null;

        private TempFile(final TempFileEnum aType, final File aFile) {
            iType = aType;
            iFile = aFile;
        }

        private TempFileEnum getType() {
            return iType;
        }

        private File getFile() {
            return iFile;
        }
    }
}
