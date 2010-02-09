package com.compomics.peptizer.util.datatools;

import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotParsingType;
import com.compomics.peptizer.util.datatools.interfaces.ParsingType;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.iterators.*;
import com.compomics.util.io.FilenameExtensionFilter;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 08.04.2009
 * Time: 16:19:40
 * To change this template use File | Settings | File Templates.
 */
public class FileToolsFactory {
    private static FileToolsFactory iSingleton = null;
    private List<ParsingType> iParsingType;
    private File[] otherFiles;

    public static FileToolsFactory getInstance() {
        if (iSingleton == null) {
            iSingleton = new FileToolsFactory();
        }
        return iSingleton;
    }

    private FileToolsFactory() {
        iParsingType = new ArrayList<ParsingType>();
        otherFiles = new File[0];
    }

    // -- Working with files --

    public File[] getIdentificationFiles(File aFolder) {
        // case Mascot :
        File[] mascotResult = null;
        mascotResult = aFolder.listFiles(new FilenameExtensionFilter(".dat"));
        // case OMSSA :
        File[] omssaResult = null;
        omssaResult = aFolder.listFiles(new FilenameExtensionFilter(".omx"));
        // case XTandem :
        File[] xTandemResult = null;
        xTandemResult = aFolder.listFiles(new FilenameExtensionFilter(".xml"));

        // concatenate the two arrays, any more elegant method welcome !
        File[] lResult = concat(mascotResult, omssaResult, xTandemResult);
        // get the other files
        if (getOtherFiles(aFolder) != null) {
            this.otherFiles = concat(this.otherFiles, getOtherFiles(aFolder), xTandemResult);
        }
        if (getParsingType(aFolder) != null) {
            this.iParsingType.addAll(getParsingType(aFolder));
        }
        return lResult;
    }

    public File[] getOtherFiles(File aFile) {
        File aFolder;
        if (!aFile.isDirectory()) {
            aFolder = new File(aFile.getPath());
        } else {
            aFolder = aFile;
        }
        // case Mascot :    no other file for Mascot
        File[] mascotResult = null;
        // case OMSSA :     we have to look for mods.xml and usermods.xml if aFile is an omssa file or a directory containing an omssa file
        File[] omssaResult = null;
        boolean requested = false;
        if (aFile.isDirectory()) {
            File[] datFiles = aFile.listFiles(new FilenameExtensionFilter(".omx"));
            if (datFiles.length > 0) requested = true;
        }
        if (aFile.getName().endsWith(".omx")) requested = true;
        if (requested) {
            File modsFile = null;
            File usermodsFile = null;
            File[] modsResult = aFolder.listFiles(new FilenameExtensionFilter(".xml"));
            if (modsResult != null) {
                for (int i = 0; i < modsResult.length; i++) {
                    if (modsResult[i].getName().compareToIgnoreCase("mods.xml") == 0) {
                        modsFile = modsResult[i];
                    }
                    if (modsResult[i].getName().compareToIgnoreCase("usermods.xml") == 0) {
                        usermodsFile = modsResult[i];
                    }
                }
            }
            File[] files = {modsFile, usermodsFile};
            omssaResult = files;
        }

        // case Xtandem :    no other file for XTandem
        File[] xTandemResult = null;

        // concatenate the three arrays, any more elegant method welcome !
        File[] lResult = concat(mascotResult, omssaResult, xTandemResult);
        return lResult;
    }

    public List<ParsingType> getParsingType(File aFile) {
        List<ParsingType> result = new ArrayList<ParsingType>();
        // case Mascot :   If the parsing type has not been specified yet, we have to look for the datfile parsing type if aFile is a datFile or a directory containing a datfile.
        boolean alreadySet = false;
        if (iParsingType != null) {
            for (int i = 0; i < iParsingType.size(); i++) {
                if (iParsingType.get(i).getSearchEngineEnum() == SearchEngineEnum.Mascot) {
                    alreadySet = true;
                    break;
                }
            }
        }
        if (!alreadySet) {
            boolean requested = false;
            if (aFile.isDirectory()) {
                File[] datFiles = aFile.listFiles(new FilenameExtensionFilter(".dat"));
                if (datFiles.length > 0) requested = true;
            }
            if (aFile.getName().endsWith(".dat")) requested = true;
            if (requested) {
                String memory = "Memory : Optimal for files up to tens of megabytes.";
                String index = "Index : optimal for files over a hundred of megabytes in size.";
                Object[] possibilities = {memory, index};
                String s = (String) JOptionPane.showInputDialog(
                        null,
                        "Mascot identification files were found, which parsing type would you like to use ?",
                        "Customized Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        possibilities,
                        "Memory");
                if (s.compareTo(memory) == 0) result.add(new MascotParsingType(MascotDatfileType.MEMORY));
                if (s.compareTo(index) == 0) result.add(new MascotParsingType(MascotDatfileType.INDEX));
            }
        }
        // case OMSSA :   no parsing type - for now...
        return result;
    }

    public PeptideIdentificationIterator getIterator(File idFile) {
        if (otherFiles == null) {
            otherFiles = getOtherFiles(idFile);
        } else if (otherFiles.length == 0) {
            otherFiles = getOtherFiles(idFile);
        }
        if (iParsingType.size() == 0) {
            iParsingType = getParsingType(idFile);
        }
        if (idFile.isDirectory()) {
            return new FolderIterator(idFile);
        }
        // case Mascot :
        if (idFile.getName().endsWith(".dat")) {
            // We need the parsing type
            MascotParsingType mascotParsingType = null;
            for (int i = 0; i < iParsingType.size(); i++) {
                if (iParsingType.get(i).getSearchEngineEnum() == SearchEngineEnum.Mascot) {
                    mascotParsingType = (MascotParsingType) iParsingType.get(i);
                    break;
                }
            }
            return new DatfileIterator(idFile, mascotParsingType.getParsingType());
        }
        // case OMSSA :
        if (idFile.getName().endsWith(".omx")) {
            // We need the modification files
            File modsFile = null;
            File usermodsFile = null;
            for (int i = 0; i < otherFiles.length; i++) {
                if (otherFiles[i] != null) {
                    if (otherFiles[i].getName().compareToIgnoreCase("mods.xml") == 0) {
                        modsFile = otherFiles[i];
                    }
                    if (otherFiles[i].getName().compareToIgnoreCase("usermods.xml") == 0) {
                        usermodsFile = otherFiles[i];
                    }
                }
            }
            return new OmxfileIterator(idFile, modsFile, usermodsFile);
        }
        // case XTandem :
        if (idFile.getName().endsWith(".xml")) {
            try {
                return new XTandemIterator(idFile);
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    // -- Working with MS LIMS --

    public PeptideIdentificationIterator getIterator(Connection aConnection, long aProjectID) {
        if (iParsingType == null) {
            // Mascot type pannel
        }
        Ms_Lims_ProjectIterator iter = new Ms_Lims_ProjectIterator(aConnection, aProjectID);
        MascotParsingType mascotParsingType = null;
        for (int i = 0; i < iParsingType.size(); i++) {
            if (iParsingType.get(i).getSearchEngineEnum() == SearchEngineEnum.Mascot) {
                mascotParsingType = (MascotParsingType) iParsingType.get(i);
                break;
            }
        }
        iter.setMascotDatfileType(mascotParsingType.getParsingType());
        return new Ms_Lims_ProjectIterator(aConnection, aProjectID);
    }


    // -- General functions --

    public boolean canYouRead(File aFile) {
        // case Mascot :
        if (aFile.getName().endsWith(".dat")) return true;
        // case OMSSA :
        if (aFile.getName().endsWith(".omx")) return true;
        // case XTandem :
        if (aFile.getName().endsWith(".xml")) return true;
        return aFile.isDirectory();
    }

    public List<String> getFormats() {
        ArrayList<String> formats = new ArrayList<String>();
        // case Mascot :
        formats.add(".dat");
        // case OMSSA :
        formats.add(".omx");
        // case XTandem :
        formats.add(".xml");
        return formats;
    }

    public File[] concat(File[] files1, File[] files2, File[] files3) {
        if (files1 == null && files3 == null) {
            return files2;
        } else if (files2 == null && files3 == null) {
            return files1;
        } else if (files1 == null && files2 == null) {
            return files3;
        } else {
            File[] lResult = new File[files1.length + files2.length];
            for (int i = 0; i < files1.length; i++) {
                lResult[i] = files1[i];
            }
            for (int i = 0; i < files2.length; i++) {
                lResult[i + files1.length] = files2[i];
            }
            for (int i = 0; i < files3.length; i++) {
                lResult[i + files1.length + files2.length] = files3[i];
            }
            return lResult;
        }
    }
}

