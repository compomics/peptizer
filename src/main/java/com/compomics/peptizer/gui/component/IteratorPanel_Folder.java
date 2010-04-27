package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.interfaces.IteratorPanel;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.datatools.FileToolsFactory;
import com.compomics.peptizer.util.fileio.MatLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-jun-2007
 * Time: 22:02:23
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class IteratorPanel_Folder extends JPanel implements IteratorPanel {

    private static IteratorPanel_Folder iSingleton = null;

    private JTextField txtFolder = null;
    private JButton btnFolder = null;

    private boolean boolFolderHasCHanged = false;

    private File iFolder = null;
    private FileToolsFactory iFileToolsFactory = FileToolsFactory.getInstance();


    private IteratorPanel_Folder() {
        // Super constructor.
        super();
        // Construct JPanel
        construct();

        // Try to load parameters.
        String s = null;
        if ((s = MatConfig.getInstance().getGeneralProperty("ITERATOR_FOLDER_PATH")) != null) {
            setFolder(new File(s));
        }


    }

    /**
     * Construct this InteratorPanel_Folder instance.
     */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select a Folder with identification files.");

        // Components initiation

        // JTextField
        txtFolder = new JTextField();
        txtFolder.setFont(txtFolder.getFont().deriveFont(11.0F));
        txtFolder.setEditable(false);
        txtFolder.setText("/");

        // JButton
        btnFolder = new JButton();
        btnFolder.setText("Browse");
        btnFolder.setMnemonic(KeyEvent.VK_B);
        btnFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                folderSelection();
            }
        });

        // Put components on the panel.
        this.add(Box.createHorizontalStrut(10));
        this.add(txtFolder);
        this.add(Box.createHorizontalStrut(10));
        this.add(btnFolder);
        this.add(Box.createHorizontalGlue());

    }

    /**
     * {@inheritDoc}
     */
    public static IteratorPanel_Folder getInstance() {
        if (iSingleton == null) {
            iSingleton = new IteratorPanel_Folder();
        }
        return iSingleton;
    }

    /**
     * {@inheritDoc}
     */
    public PeptideIdentificationIterator getIterator() {
        if (iFolder != null) {
            if (boolFolderHasCHanged) {
                return iFileToolsFactory.getIterator(iFolder);
            } else {
                return null;
            }
        } else {
            MatLogger.logExceptionalEvent("A folder must be selected first! Iterator creation failed.");
            return null;
        }
    }


    private void folderSelection() {

        // Previous selected path.
        String lPath = "";

        if (iFolder != null) {
            lPath = iFolder.getPath();
        } else {
            lPath = "/";
        }

        JFileChooser jfc = new JFileChooser(lPath);
        jfc.setDialogTitle("Select identification files Folder");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = jfc.showDialog(this.getParent(), "OK");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File lFolder = jfc.getSelectedFile();
            // Check for existing file.
            if (lFolder.isDirectory()) {
                setFolder(lFolder);
            }
        }
    }

    /**
     * Set the file whereon we want to iterate.
     *
     * @param aFolder File to identification file.
     */
    public void setFolder(File aFolder) {
        iFolder = aFolder;
        boolFolderHasCHanged = true;
        try {
            txtFolder.setText(iFolder.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String toString() {
        return "identification files Folder";
    }

}
