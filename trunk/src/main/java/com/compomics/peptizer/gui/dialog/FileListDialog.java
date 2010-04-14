package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.component.ImportPanel_File;
import com.compomics.peptizer.util.datatools.FileToolsFactory;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: Mar 23, 2010
 * Time: 9:48:24 AM
 * This dialog will allow the user to edit the filelist to proceed.
 */
public class FileListDialog extends JDialog {

    // Attributes

    private ArrayList<File> files;
    private ArrayList<Boolean> selection;
    private FileToolsFactory iFileToolsFactory = FileToolsFactory.getInstance();
    // panel components
    private JScrollPane scrollPane;
    private JTable fileTable = new JTable();
    private JButton ok;
    private JButton clear;
    private JButton remove;
    private JButton add;


    // Constructor

    public FileListDialog(ArrayList<File> files) {
        this.files = files;
        this.selection = new ArrayList();
        for (int i = 0; i < files.size(); i++) {
            selection.add(false);
        }
        construct();
    }


    // Methods

    public void construct() {

        fileTable.setModel(new FileTableModel());
        fileTable.setCellSelectionEnabled(false);
        fileTable.setAutoCreateRowSorter(true);
        fileTable.setEditingColumn(0);
        for (int i = 0; i < fileTable.getColumnCount(); i++) {
            TableColumn column = fileTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(10);
            }
            if (i == 2) {
                column.setPreferredWidth(50);
            }
        }
        scrollPane = new JScrollPane(fileTable);

        clear = new JButton();
        clear.setText("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Clear the input files list?", "Warning", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    files.clear();
                    selection.clear();
                    fileTable.repaint();
                }
            }
        });

        remove = new JButton();
        remove.setText("Remove File(s)");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < files.size(); i++) {
                    if (selection.get(i)) {
                        files.remove(i);
                        selection.remove(i);
                    }
                }
                fileTable.repaint();
            }
        });

        add = new JButton();
        add.setText("Add File(s)");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileSelection();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(add);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(clear);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(remove);

        ok = new JButton();
        ok.setText("OK");
        ok.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                ImportPanel_File.getInstance().setFiles(files);
                dispose();
            }
        });

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.setBorder(BorderFactory.createTitledBorder("File(s) Selected"));
        content.add(scrollPane);
        content.add(Box.createVerticalStrut(5));
        content.add(buttonPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(ok);

        this.add(content);
        this.pack();
        this.setSize(600, Math.min(800, this.getHeight()));
        this.setVisible(true);
    }

    private void fileSelection() {

        // Previous selected path.
        String lPath = "";

        if (files.size() > 0) {
            lPath = files.get(files.size() - 1).getPath();
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
        int returnVal = jfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] lFiles = jfc.getSelectedFiles();
            // Check for existing file.
            for (int i = 0; i < lFiles.length; i++) {
                if (lFiles[i].exists() && lFiles[i].isFile()) {
                    files.add(lFiles[i]);
                    selection.add(false);
                } else if (lFiles[i].exists() && lFiles[i].isDirectory()) {
                    File[] containedFiles = lFiles[i].listFiles();
                    for (int j = 0; j < containedFiles.length; j++) {
                        if (iFileToolsFactory.canYouRead(containedFiles[j])) {
                            files.add(containedFiles[j]);
                            selection.add(false);
                        }
                    }
                }
            }
            fileTable.setModel(new FileTableModel());
        }
    }

    // Private class for the table

    private class FileTableModel extends DefaultTableModel {

        public int getRowCount() {
            return files.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public String getColumnName(int column) {
            if (column == 0) {
                return "";
            } else if (column == 1) {
                return "File";
            } else if (column == 2) {
                return "File type";
            } else {
                return "";
            }
        }

        public Object getValueAt(int row, int column) {
            if (column == 0) {
                return selection.get(row);
            } else if (column == 1) {
                try {
                    return files.get(row).getCanonicalPath();
                } catch (Exception e) {
                    return files.get(row).getName();
                }
            } else if (column == 2) {
                return iFileToolsFactory.getFileDescription(files.get(row));
            } else {
                return "";
            }
        }

        public void setValueAt(Object aValue, int row, int column) {
            if (column == 0) {
                selection.set(row, !selection.get(row));
            }
            fileTable.repaint();
        }

        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }
    }

}
