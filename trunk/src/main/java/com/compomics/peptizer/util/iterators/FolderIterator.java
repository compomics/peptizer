package com.compomics.peptizer.util.iterators;

import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.datatools.FileToolsFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 08.04.2009
 * Time: 15:04:59
 * To change this template use File | Settings | File Templates.
 */
public class FolderIterator implements PeptideIdentificationIterator {

    /** The File tools factory which will manage the different search engine result files */
    private FileToolsFactory iFileToolsFactory = FileToolsFactory.getInstance();

    /** The folder containing the identification files. */
    private File[] iFiles = null;

    /** The iterator that is currently loaded. */
    private PeptideIdentificationIterator iCurrentIterator = null;

    /** The index of the files. */
    private int iFileCountIndex = 0;

    /** The folder that contains the files. */
    private File iFolder = null;


    /** Empty constructor. The folder needs to be set! */
    public FolderIterator() {
    }

    public FolderIterator(File aFolder) {
        setFolder(aFolder);
    }
    /**
     * Sets the folder of the FolderIterator.
     *
     * @param aFolder            File that contains the datfiles.
     *
     * @return boolean Status whether the setting of the folder went correct. If all went fine, true is returned. </br>If
     *         something went wrong, false is returned.
     */
    public boolean setFolder(File aFolder) {
        iFolder = aFolder;

        boolean lStatus = true;

        // First check if folder exists .
        if (iFolder.exists()) {
            indexFiles(aFolder);
            // OK, folder exists.
            // Check for a null return for listing files in the given folder.
            if (iFiles != null) {
                // Check if there are actually identification files in the given folder.
                if (iFiles.length > 0) {
                    // Ok, we are here. Proceed!
                    moveToNextFile();
                } else {
                    // No identification files were indexed! Log Exceptional event.
                    try {
                        lStatus = false;
                        MatLogger.logExceptionalEvent("No identification file was found in given folder " + aFolder.getCanonicalPath() + ".");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                // Null returned for listing Files in given folder. Log Exceptional event.
                try {
                    lStatus = false;
                    MatLogger.logExceptionalEvent("Given folder " + aFolder.getCanonicalPath() + " returned 'null' when listing files!!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Folder does not exist! Log exceptional event.
            try {
                lStatus = false;
                MatLogger.logExceptionalEvent("Given folder " + aFolder.getCanonicalPath() + " does not exist!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lStatus;
    }

    /**
     * Returns the folder of the Iterator
     *
     * @return Folder that contains the datfiles.
     */
    public File getFolder() {
        return iFolder;
    }

    /**
     * Index the files in the folder.
     *
     * @param aFolder File with readable files in the folder.
     *
     * @return File[] with the files in the folder.
     */
    private void indexFiles(File aFolder) {
        iFiles = aFolder.listFiles(new FilenameFilter() {
            public boolean accept(final File aFile, final String s) {
                return FileToolsFactory.getInstance().canYouRead(aFile);
            }
        });
    }

    /** {@inheritDoc} */
    public PeptideIdentification next() {
        if (iCurrentIterator.hasNext()) {
            return iCurrentIterator.next();
        } else if (this.hasMoreFiles()) {
            moveToNextFile();
            return this.next();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        // If CurrentIterator has more, continue. If not check if there are more files.
        // If neither is true, false is returned.
        return iCurrentIterator.hasNext() || this.hasMoreFiles();

    }

    /** {@inheritDoc} The number of files in the folder in this implementation. */
    public int estimateSize() {
        if (iFiles != null) {
            return iFiles.length;
        } else {
            return 0;
        }
    }

    /** {@inheritDoc} The number of files left in the folder in this implementation. */
    public int estimateToDo() {
        if (iFiles != null) {
            if (iFileCountIndex != iFiles.length) {
                return (iFiles.length - iFileCountIndex + 1);
            } else if (iCurrentIterator.estimateToDo() != 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Moves to the next file of the folder.
     *
     * @return boolean true if succesfull, false if failure.
     */
    private boolean moveToNextFile() {
        boolean result = false;
        if (iFileCountIndex < iFiles.length) {
            // Set to the next file.
            iCurrentIterator = iFileToolsFactory.getIterator(iFiles[iFileCountIndex]);
            iFileCountIndex = iFileCountIndex + 1;
            if (iCurrentIterator != null) {
            result = true;
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Returns if there are more files.
     *
     * @return true if there are more files, false otherwise.
     */
    private boolean hasMoreFiles() {
        return iFileCountIndex < iFiles.length;
    }


    /**
     * Returns the filecountindex
     *
     * @return int count of the done files in the folder.
     */
    public int getFileCountIndex() {
        return iFileCountIndex;
    }


    /** {@inheritDoc} */
    public void remove() {
    }


    /** {@inheritDoc} */
    public String toString() {
        return "Hi I am a FolderIterator. (" + iFolder + ")";
    }

    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String getCurrentFileDescription() {
        String s = "";
        if (iFolder != null) {
            s = iFiles[iFileCountIndex - 1].getName();
        } else {
            s = "Folder";
        }
        return s;
    }

    /** {@inheritDoc} */
    public String getGeneralDescription() {
        String s = "";
        if (iFolder != null) {
            s = iFiles.length + " identification files in '" + iFolder.getAbsolutePath() + "'";
        } else {
            s = "Folder";
        }
        return s;
    }

}
