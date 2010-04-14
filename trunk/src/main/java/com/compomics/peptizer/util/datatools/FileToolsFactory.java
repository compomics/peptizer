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

    public static FileToolsFactory getInstance() {
        if (iSingleton == null) {
            iSingleton = new FileToolsFactory();
        }
        return iSingleton;
    }

    private FileToolsFactory() {
        iParsingType = new ArrayList<ParsingType>();
    }

    // -- Working with files --

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
                if (s != null) {
                    if (s.compareTo(memory) == 0) result.add(new MascotParsingType(MascotDatfileType.MEMORY));
                    if (s.compareTo(index) == 0) result.add(new MascotParsingType(MascotDatfileType.INDEX));
                } else {
                    result.addAll(getParsingType(aFile));
                }
            }
        }
        // case OMSSA :   no parsing type - for now...
        return result;
    }

    public PeptideIdentificationIterator getIterator(File idFile) {
        if (iParsingType.size() == 0) {
            iParsingType = getParsingType(idFile);
        }
        // case Mascot:
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
        // case OMSSA:
        if (idFile.getName().endsWith(".omx")) {
            return new OmxfileIterator(idFile);
        }
        // case XTandem:
        if (idFile.getName().endsWith(".xml") && idFile.getName().compareToIgnoreCase("mods.xml") != 0 && idFile.getName().compareToIgnoreCase("usermods.xml") != 0) {
            try {
                return new XTandemIterator(idFile);
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
        // case folder:
        if (idFile.isDirectory()) {
            File[] containedFiles = idFile.listFiles();
            for (int j = 0; j < containedFiles.length; j++) {
                if (canYouRead(containedFiles[j])) {
                    IdentificationFactory.getInstance().load(containedFiles[j]);
                }
            }
            return IdentificationFactory.getInstance().getIterator();
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

    public PeptideIdentificationIterator getIterator(ArrayList<Long> iIdentificationIDs) {
        return new Ms_Lims_IdentificationIDIterator(iIdentificationIDs);
    }


    // -- General functions --

    public ArrayList<SearchEngineEnum> getImplementedSearchEngines() {
        ArrayList<SearchEngineEnum> result = new ArrayList();
        result.add(SearchEngineEnum.Mascot);
        result.add(SearchEngineEnum.OMSSA);
        result.add(SearchEngineEnum.XTandem);
        return result;
    }

    public boolean canYouRead(File aFile) {
        // case Mascot :
        if (aFile.getName().endsWith(".dat")) return true;
        // case OMSSA :
        if (aFile.getName().endsWith(".omx")) return true;
        // case XTandem :
        if (aFile.getName().endsWith(".xml")) return true;
        return aFile.isDirectory();
    }

    public String getFileDescription(File aFile) {
        if (aFile.getName().endsWith(".dat")) return "Mascot Identification File";
        // case OMSSA :
        if (aFile.getName().endsWith(".omx")) return "OMSSA Identification File";
        if (aFile.getName().compareTo("mods.xml") == 0 || aFile.getName().compareTo("usermods.xml") == 0)
            return "OMSSA parameter file";
        // case XTandem :
        if (aFile.getName().endsWith(".xml") && aFile.getName().compareTo("mods.xml") != 0 && aFile.getName().compareTo("usermods.xml") != 0)
            return "X!Tandem Identification File";
        if (aFile.isDirectory()) return "directory";
        return "File not recognized";
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

}

