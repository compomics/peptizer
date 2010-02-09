package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.interfaces.IteratorPanel;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.datatools.FileToolsFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-jun-2007
 * Time: 21:09:29
 */

/** Class description: ------------------ This class was developed for the CreateAssessment Dialog. */
public class IteratorPanel_File extends JPanel implements IteratorPanel {

    private static IteratorPanel_File iSingleton = null;
    private PeptideIdentificationIterator iter = null;

    private JTextField txtFile = null;
    private JButton btnFile = null;
    private static boolean boolFileHasChanged = false;

    private File iFile = null;
    private FileToolsFactory iFileToolsFactory = FileToolsFactory.getInstance();


    private IteratorPanel_File() {
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

    /** Construct this InteratorPanel_File instance. */
    private void construct() {
        // Layout
        BoxLayout lBoxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        this.setLayout(lBoxLayout);
        this.setToolTipText("Select an identification file.");

        // Components initiation

        // JTextField
        txtFile = new JTextField();
        txtFile.setFont(txtFile.getFont().deriveFont(11.0F));
        txtFile.setEditable(false);
        txtFile.setText("/");

        // JButton
        btnFile = new JButton();
        btnFile.setText("Browse");
        btnFile.setMnemonic(KeyEvent.VK_B);
        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileSelection();
            }
        });

        // Put components on the panel.
        this.add(Box.createHorizontalStrut(10));
        this.add(txtFile);
        this.add(Box.createHorizontalStrut(10));
        this.add(btnFile);
        this.add(Box.createHorizontalGlue());

    }

    /** {@inheritDoc} */
    public static IteratorPanel_File getInstance() {
        if (iSingleton == null) {
            iSingleton = new IteratorPanel_File();
        }
        boolFileHasChanged = true;
        return iSingleton;
    }

    /** {@inheritDoc} */
    public PeptideIdentificationIterator getIterator() {
        if (iFile != null) {
            if (iFile.exists()) {
                if (boolFileHasChanged) {
                    boolFileHasChanged = false;
                    iter = iFileToolsFactory.getIterator(iFile);
                }
                return iter;
            } else {
                JOptionPane.showMessageDialog(this.getParent(), iFile.getName() + " does not exist!", "Iterator creation failed.", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            JOptionPane.showMessageDialog(this.getParent(), "A file must be selected first!", "Iterator creation failed.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    private void fileSelection() {

        // Previous selected path.
        String lPath = "";

        if (iFile != null) {
            lPath = iFile.getPath();
        } else {
            lPath = "/";
        }
        // The file filter to use.
        FileFilter filter = new FileFilter() {
            public boolean accept(File myFile) {
                return iFileToolsFactory.canYouRead(myFile) ;
            }

            public String getDescription() {
                List<String> formats = iFileToolsFactory.getFormats();
                String toWrite = "supported formats : ";
                for (int i=0 ; i < formats.size()-1 ; i++) {
                    toWrite += formats.get(i) + ", ";
                }
                toWrite += formats.get(formats.size()-1) + ".";
                return toWrite;
            }
        };
        JFileChooser jfc = new JFileChooser(lPath);
        jfc.setDialogTitle("Select identification file");
        jfc.setDialogType(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(filter);
        int returnVal = jfc.showOpenDialog(this.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File lFile = jfc.getSelectedFile();
            // Check for existing file.
            if (lFile.exists() && lFile.isFile()) {
                setFile(lFile);
            }
        }
    }


    /**
     * Set the file whereon we want to iterate.
     *
     * @param aFile File to identification file.
     */
    public void setFile(File aFile) {
        iFile = aFile;
        boolFileHasChanged = true;
        try {
            txtFile.setText(iFile.getCanonicalPath());
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
        return "Identification File";
    }


}
