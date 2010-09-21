package com.compomics.peptizer.util.fileio;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 5-jul-2007
 * Time: 12:28:41
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a central object to Manage Files for input and output.
 */
public class FileManager {
	// Class specific log4j logger for FileManager instances.
	 private static Logger logger = Logger.getLogger(FileManager.class);

    /**
     * Singleton instance
     */
    private static FileManager iFileManager = null;
    /**
     * Agent Configuration Input file.
     */
    private File iAgentConfigurationInput = null;

    /**
     * Agent Configuration Output file.
     */
    private File iAgentConfigurationOutput = null;

    /**
     * Task Configuration Input file.
     */
    private File iTaskInput = null;

    /**
     * Task Configuration Output file.
     */
    private File iTaskOutput = null;

    /**
     * Txt output file.
     */
    private File iTXTOutput = null;

    /**
     * Pdf output file.
     */
    private File iPDFOutput;

    /**
     * Default for Serialized output of a group of PeptideIdentifications.
     */
    private File iPeptideIdentificationsSerializedOutput = null;

    /**
     * Default for Serialized input of a group of PeptideIdentifications.
     */
    private File iPeptideIdentificationsSerializedInput = null;

    /**
     * Basic file referencing the user home directory.
     */
    private File iHome = new File(System.getProperty("user.home"));


    /**
     * This private constructor takes no arguments.
     */
    private FileManager() {
    }

    /**
     * Returns the Singleton instance of the FileManager.
     *
     * @return Filemanager
     */
    public static FileManager getInstance() {
        if (iFileManager == null) {
            iFileManager = new FileManager();
        }
        return iFileManager;
    }


    /**
     * Returns a File for Agent configuration input.
     *
     * @return File for Agent configuration input. Can be null if not set previously.
     */
    public File getAgentConfigurationInput() {
        return iAgentConfigurationInput;
    }

    /**
     * Returns a File for Agent configuration output.
     *
     * @return File for Agent configuration output. Can be null if not set previously.
     */
    public File getAgentConfigurationOutput() {
        return iAgentConfigurationOutput;
    }


    /**
     * Returns a File for Task configuration input.
     *
     * @return File
     */
    public File getTaskInput() {
        return iTaskInput;
    }

    /**
     * Returns a File for Task configuration output.
     *
     * @return File
     */
    public File getTaskOutput() {
        return iTaskOutput;
    }

    /**
     * Sets Agent configuration input File.
     *
     * @param aAgentConfigurationInput File input for Agent configuration.
     */
    public void setAgentConfigurationInput(File aAgentConfigurationInput) {
        iAgentConfigurationInput = aAgentConfigurationInput;
    }

    /**
     * Sets Agent configuration output File.
     *
     * @param aAgentConfigurationOutput File output for Agent configuration.
     */
    public void setAgentConfigurationOutput(File aAgentConfigurationOutput) {
        iAgentConfigurationOutput = aAgentConfigurationOutput;
    }


    /**
     * Returns TXTOutput File to save validation.
     *
     * @return TXTOutput File target.
     */
    public File getTXTOutput() {
        return iTXTOutput;
    }

    /**
     * Sets TXTOutput File to save validation.
     *
     * @param aTXTOutput File target.
     */
    public void setTXTOutput(File aTXTOutput) {
        iTXTOutput = aTXTOutput;
    }

    /**
     * Returns PDFOutput File to save validation.
     *
     * @return PDFOutput File target.
     */
    public File getPDFOutput() {
        return iPDFOutput;
    }

    /**
     * Sets PDFOutput File to save validation.
     *
     * @param aPDFOutput File target.
     */
    public void setPDFOutput(File aPDFOutput) {
        iPDFOutput = aPDFOutput;
    }

    /**
     * Returns Output File for PeptideIdentification Serialization.
     *
     * @return File target.
     */
    public File getPeptideIdentificationsSerializedOutput() {
        return iPeptideIdentificationsSerializedOutput;
    }

    /**
     * Set the output File for PeptideIdentification Serialization.
     *
     * @param aPeptideIdentificationsSerializedOutput
     *         File
     */
    public void setPeptideIdentificationsSerializedOutput(File aPeptideIdentificationsSerializedOutput) {
        iPeptideIdentificationsSerializedOutput = aPeptideIdentificationsSerializedOutput;
    }


    /**
     * Returns input File for PeptideIdentification Serialization.
     *
     * @return File target.
     */
    public File getPeptideIdentificationsSerializedInput() {
        return iPeptideIdentificationsSerializedInput;
    }

    /**
     * Set the input File for PeptideIdentification Serialization read out.
     *
     * @param aPeptideIdentificationsSerializedInput
     *         File
     */
    public void setPeptideIdentificationsSerializedInput(File aPeptideIdentificationsSerializedInput) {
        iPeptideIdentificationsSerializedInput = aPeptideIdentificationsSerializedInput;
    }


    /**
     * Opens a selection dialog for Agent Configuration Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the Agent Configuration Output selection was accomplished.
     */
    public boolean selectAgentConfigurationOutput(JComponent aParent) {

        boolean boolSelection = false;
        // Looping boolean.
        boolean boolContinue = true;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iAgentConfigurationOutput != null) {
            previousPath = iAgentConfigurationOutput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".xml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Agent xml configuration file";
            }
        };

        while (boolContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Select agent configuration output file");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(aParent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iAgentConfigurationOutput = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if (jfc.getFileFilter() == filter && !iAgentConfigurationOutput.getName().toLowerCase().endsWith(".xml")) {
                    iAgentConfigurationOutput = new File(iAgentConfigurationOutput.getAbsolutePath() + ".xml");
                }
                // Check for existing file.
                if (iAgentConfigurationOutput.exists()) {
                    int reply = JOptionPane.showConfirmDialog(aParent, new String[]{"File '" + iAgentConfigurationOutput.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply != JOptionPane.YES_OPTION) {
                        previousPath = iAgentConfigurationOutput.getParent();
                        continue;
                    }
                }
                boolContinue = false;
                boolSelection = true;

            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                boolContinue = false;
            }
        }
        return boolSelection;
    }


    /**
     * Opens a selection dialog for Agent Configuration Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the Agent Configuration Output selection was accomplished.
     */
    public boolean selectAgentConfigurationInput(JComponent aParent) {
        boolean boolSelection = false;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iAgentConfigurationInput != null) {
            previousPath = iAgentConfigurationInput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".xml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Agent xml configuration file";
            }
        };

        // Define FileChooser.
        JFileChooser jfc = new JFileChooser(previousPath);
        jfc.setDialogTitle("Select agent configuration input file");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(filter);

        int returnVal = jfc.showOpenDialog(aParent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // If selected file exists, assign it to iAgentConfigurationInput.
            if (jfc.getSelectedFile().exists()) {
                iAgentConfigurationInput = jfc.getSelectedFile();
                boolSelection = true;
            } else {
                // Else try to create it.
                try {
                    if (jfc.getSelectedFile().createNewFile()) {
                        iAgentConfigurationInput = jfc.getSelectedFile();
                        boolSelection = true;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
            boolSelection = false;
        }
        return boolSelection;
    }


    /**
     * Opens a selection dialog for Task Configuration Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the Task Configuration Output selection was accomplished.
     */
    public boolean selectTaskOutput(JComponent aParent) {

        boolean boolSelection = false;
        // Looping boolean.
        boolean boolContinue = true;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iTaskOutput != null) {
            previousPath = iTaskOutput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".xml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Task xml configuration file";
            }
        };

        while (boolContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Select Task configuration output file");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(aParent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iTaskOutput = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if (jfc.getFileFilter() == filter && !iTaskOutput.getName().toLowerCase().endsWith(".xml")) {
                    iTaskOutput = new File(iTaskOutput.getAbsolutePath() + ".xml");
                }
                // Check for existing file.
                if (iTaskOutput.exists()) {
                    int reply = JOptionPane.showConfirmDialog(aParent, new String[]{"File '" + iTaskOutput.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply != JOptionPane.YES_OPTION) {
                        previousPath = iTaskOutput.getParent();
                        continue;
                    }
                }
                boolContinue = false;
                boolSelection = true;

            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                boolContinue = false;
            }
        }
        return boolSelection;
    }

    /**
     * Opens a selection dialog for Task Configuration Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the Task Configuration Output selection was accomplished.
     */
    public boolean selectTaskInput(JComponent aParent) {
        boolean boolSelection = false;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iTaskInput != null) {
            previousPath = iTaskInput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".xml")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Task xml configuration file";
            }
        };

        // Define FileChooser.
        JFileChooser jfc = new JFileChooser(previousPath);
        jfc.setDialogTitle("Select Task configuration input file");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(filter);

        int returnVal = jfc.showOpenDialog(aParent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // If selected file exists, assign it to iTaskInput.
            if (jfc.getSelectedFile().exists()) {
                iTaskInput = jfc.getSelectedFile();
                boolSelection = true;
            } else {
                // Else try to create it.
                try {
                    if (jfc.getSelectedFile().createNewFile()) {
                        iTaskInput = jfc.getSelectedFile();
                        boolSelection = true;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
            boolSelection = false;
        }
        return boolSelection;
    }


    /**
     * Opens a selection dialog for validation txt Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the TXT file selection was accomplished.
     */
    public boolean selectTXTOutput(JComponent aParent) {

        boolean boolSelection = false;
        // Looping boolean.
        boolean boolContinue = true;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iTXTOutput != null) {
            previousPath = iTXTOutput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".txt")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Tab delimited output file";
            }
        };

        while (boolContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Select txt output file");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(aParent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iTXTOutput = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if (jfc.getFileFilter() == filter && !iTXTOutput.getName().toLowerCase().endsWith(".txt")) {
                    iTXTOutput = new File(iTXTOutput.getAbsolutePath() + ".txt");
                }
                // Check for existing file.
                if (iTXTOutput.exists()) {
                    int reply = JOptionPane.showConfirmDialog(aParent, new String[]{"File '" + iTXTOutput.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply != JOptionPane.YES_OPTION) {
                        previousPath = iTXTOutput.getParent();
                        continue;
                    }
                }
                boolContinue = false;
                boolSelection = true;

            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                boolContinue = false;
            }
        }
        return boolSelection;
    }

    /**
     * Opens a selection dialog for validation txt Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the TXT file selection was accomplished.
     */

    public boolean selectPDFOutput(JComponent aParent) {

        boolean boolSelection = false;
        // Looping boolean.
        boolean boolContinue = true;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iPDFOutput != null) {
            previousPath = iPDFOutput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".pdf")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "PDF output file";
            }
        };

        while (boolContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Select pdf output file");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(aParent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iPDFOutput = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if (jfc.getFileFilter() == filter && !iPDFOutput.getName().toLowerCase().endsWith(".pdf")) {
                    iPDFOutput = new File(iPDFOutput.getAbsolutePath() + ".pdf");
                }
                // Check for existing file.
                if (iPDFOutput.exists()) {
                    int reply = JOptionPane.showConfirmDialog(aParent, new String[]{"File '" + iPDFOutput.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply != JOptionPane.YES_OPTION) {
                        previousPath = iPDFOutput.getParent();
                        continue;
                    }
                }
                boolContinue = false;
                boolSelection = true;

            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                boolContinue = false;
            }
        }
        return boolSelection;
    }

    /**
     * Opens a selection dialog for Serialization of PeptideIdentifications Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the TXT file selection was accomplished.
     */
    public boolean selectPeptideIdentificationOutput(JComponent aParent) {

        boolean boolSelection = false;
        // Looping boolean.
        boolean boolContinue = true;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iPeptideIdentificationsSerializedOutput != null) {
            previousPath = iPeptideIdentificationsSerializedOutput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".ser")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Serialized Object File";
            }
        };

        while (boolContinue) {
            JFileChooser jfc = new JFileChooser(previousPath);
            jfc.setDialogTitle("Select Serialized Object File");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            jfc.setFileFilter(filter);
            int returnVal = jfc.showSaveDialog(aParent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                iPeptideIdentificationsSerializedOutput = jfc.getSelectedFile();
                // Append the file extension if it is not already there.
                if (jfc.getFileFilter() == filter && !iPeptideIdentificationsSerializedOutput.getName().toLowerCase().endsWith(".ser")) {
                    iPeptideIdentificationsSerializedOutput = new File(iPeptideIdentificationsSerializedOutput.getAbsolutePath() + ".ser");
                }
                // Check for existing file.
                if (iPeptideIdentificationsSerializedOutput.exists()) {
                    int reply = JOptionPane.showConfirmDialog(aParent, new String[]{"File '" + iPeptideIdentificationsSerializedOutput.getAbsolutePath() + "' exists.", "Do you wish to overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply != JOptionPane.YES_OPTION) {
                        previousPath = iPeptideIdentificationsSerializedOutput.getParent();
                        continue;
                    }
                }
                boolContinue = false;
                boolSelection = true;

            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                boolContinue = false;
            }
        }
        return boolSelection;
    }

    /**
     * Opens a selection dialog for Agent Configuration Output.
     *
     * @param aParent Component parent for the Selection dialog.
     * @return boolean whether or not the PeptideIdentification Serialized File selection was accomplished.
     */
    public boolean selectPeptideIdentificationSerializedInput(JComponent aParent) {
        boolean boolSelection = false;

        // Previous selected path or OS root.
        String previousPath = iHome.getPath();
        if (iPeptideIdentificationsSerializedInput != null) {
            previousPath = iPeptideIdentificationsSerializedInput.getPath();
        }

        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".ser")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "Serialized Object File";
            }
        };

        // Define FileChooser.
        JFileChooser jfc = new JFileChooser(previousPath);
        jfc.setDialogTitle("Select Serialized Object input file");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(filter);

        int returnVal = jfc.showOpenDialog(aParent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // If selected file exists, assign it to iPeptideIdentificationsSerializedInput.
            if (jfc.getSelectedFile().exists()) {
                iPeptideIdentificationsSerializedInput = jfc.getSelectedFile();
                boolSelection = true;
            } else {
                // Else try to create it.
                try {
                    if (jfc.getSelectedFile().createNewFile()) {
                        iPeptideIdentificationsSerializedInput = jfc.getSelectedFile();
                        boolSelection = true;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
            boolSelection = false;
        }
        return boolSelection;
    }
}
