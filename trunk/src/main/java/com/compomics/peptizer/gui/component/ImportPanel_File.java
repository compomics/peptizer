package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.dialog.FileListDialog;
import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.util.datatools.FileToolsFactory;
import com.compomics.peptizer.util.datatools.IdentificationFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-jun-2007
 * Time: 21:09:29
 */

/**
 * Class description: ------------------ This class was developed for the CreateAssessment Dialog.
 */
public class ImportPanel_File extends JPanel implements ImportPanel {

    private static ImportPanel_File iSingleton = null;

    private JTextField txtFile = null;
    private JButton btnClear = null;
    private JButton btnEdit = null;
    private JButton btnFile = null;

    private ArrayList<File> iFile = new ArrayList();
    private FileToolsFactory iFileToolsFactory = FileToolsFactory.getInstance();


    private ImportPanel_File() {
        // Super constructor.
        super();
        // Construct JPanel.
        construct();

        // Try to load parameters.
        String s = null;
        if ((s = MatConfig.getInstance().getGeneralProperty("ITERATOR_FILE_PATH")) != null) {
            setFile(new File(s));
        }
    }

    /**
     * Construct this InteratorPanel_File instance.
     */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select identification file(s).");

        // Components initiation

        // JTextField
        txtFile = new JTextField();
        txtFile.setFont(txtFile.getFont().deriveFont(11.0F));
        txtFile.setEditable(false);
        txtFile.setText("Please add identification File(s)");

        // JButtons
        btnFile = new JButton();
        btnFile.setText("Add");
        btnFile.setMnemonic(KeyEvent.VK_B);
        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileSelection();
            }
        });

        btnClear = new JButton();
        btnClear.setText("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearSelection();
            }
        });

        btnEdit = new JButton();
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelection();
            }
        });

        // Put components on the panel.
        this.add(Box.createHorizontalStrut(10));
        this.add(txtFile);
        this.add(Box.createHorizontalStrut(10));
        this.add(btnFile);
        this.add(Box.createHorizontalStrut(10));
        this.add(btnEdit);
        this.add(Box.createHorizontalStrut(10));
        this.add(btnClear);
        this.add(Box.createHorizontalGlue());

    }

    /**
     * {@inheritDoc}
     */
    public static ImportPanel_File getInstance() {
        if (iSingleton == null) {
            iSingleton = new ImportPanel_File();
        }
        return iSingleton;
    }

    /**
     * {@inheritDoc}
     */
    public void loadIdentifications(DefaultProgressBar progressBar) {
        progressBar.setMaximum(iFile.size());

        if (iFile.size() > 0) {
            for (int i = 0; i < iFile.size(); i++) {
                progressBar.setMessage("loading " + iFile.get(i).getName());
                progressBar.setValue(i);

                if (iFile.get(i).exists()) {
                    IdentificationFactory.getInstance().load(iFile.get(i));
                } else {
                    JOptionPane.showMessageDialog(this.getParent(), iFile.get(i).getName() + " does not exist!", "Data import failed.", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this.getParent(), "Select at least a file please.", "Data import failed.", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void fileSelection() {

        // Previous selected path.
        String lPath = "";

        if (iFile.size() > 0) {
            lPath = iFile.get(iFile.size() - 1).getPath();
        } else {
            lPath = "/";
        }
        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File myFile) {
                return iFileToolsFactory.canYouRead(myFile);
            }

            public String getDescription() {
                List<String> formats = iFileToolsFactory.getFormats();
                String toWrite = "supported formats : ";
                for (int i = 0; i < formats.size() - 1; i++) {
                    toWrite += formats.get(i) + ", ";
                }
                toWrite += formats.get(formats.size() - 1) + ".";
                return toWrite;
            }
        };
        JFileChooser jfc = new JFileChooser(lPath);
        jfc.setDialogTitle("Select identification file(s)");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileFilter(filter);
        int returnVal = jfc.showOpenDialog(this.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] lFiles = jfc.getSelectedFiles();
            // Check for existing file.
            for (int i = 0; i < lFiles.length; i++) {
                if (lFiles[i].exists() && lFiles[i].isFile()) {
                    setFile(lFiles[i]);
                } else if (lFiles[i].exists() && lFiles[i].isDirectory()) {
                    File[] containedFiles = lFiles[i].listFiles();
                    for (int j = 0; j < containedFiles.length; j++) {
                        if (iFileToolsFactory.canYouRead(containedFiles[j])) {
                            setFile(containedFiles[j]);
                        }
                    }
                }
            }
        }
    }

    private void editSelection() {
        FileListDialog fileListDialog = new FileListDialog(iFile);
    }

    public void setFiles(ArrayList<File> files) {
        iFile = files;
        if (iFile.size() > 0) {
            txtFile.setText(iFile.size() + " files selected");
        } else {
            txtFile.setText("Please add identification File(s)");
        }
    }

    private void clearSelection() {
        iFile = new ArrayList();
        txtFile.setText("Please add identification File(s)");
    }

    /**
     * Set the file whereon we want to iterate.
     *
     * @param aFile File to identification file.
     */
    public void setFile(File aFile) {
        iFile.add(aFile);
        txtFile.setText(iFile.size() + " files selected.");
    }

    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String toString() {
        return "Identification File(s)";
    }


}
