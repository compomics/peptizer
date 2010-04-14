package com.compomics.peptizer.gui.dialog;

import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.component.HyperLinkLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: Lennart Martens Date: 27-mei-2008 Time: 14:13:58 This template was reused from the
 * DBToolkit project (http://genesis.ugent.be/dbtoolkit/)
 */
public class AboutDialog extends JDialog {

    /**
     * The textarea that will display the help text.
     */
    private JTextArea txtHelp = null;

    /**
     * This button can be pressed to exit the dialog.
     */
    private JButton btnOK = null;

    /**
     * The labels with information on top.
     */
    private JLabel[] lblLabels = null;

    /**
     * The label with the toolkit image icon.
     */
    private static JLabel lblImageTools = null;

    /**
     * The label with the ugent image icon.
     */
    private static JLabel lblImageUGENT = null;

    /**
     * The ImageIcon with the ugent image icon.
     */
    private static ImageIcon iUGENT = null;

    /**
     * The label with the IWT image icon.
     */
    private static JLabel lblImageIWT = null;

    /**
     * The ImageIcon with the iwt logo.
     */
    private static ImageIcon iIWT = null;

    /**
     * The helptext to display in the textarea.
     */
    private static String iHelpText = null;

    /**
     * The name for the textfile.
     */
    private static final String TEXTFILE = "about.txt";


    /**
     * This constructor mimics the constructor on the superclass and allows specification of the parent JFrame as well
     * as the title for the dialog. Note that about dialog is always modal!
     *
     * @param aParent JFrame that is the parent of this dialog.
     * @param aTitle  String with the title for this dialog.
     */
    public AboutDialog(JFrame aParent, String aTitle) {
        super(aParent, aTitle, true);

        // See if we should load the display text.
        if (iHelpText == null) {
            this.loadHelpText();
        }

        // See if we should load the imagelabel and icon.
        if ((lblImageTools == null) || (iUGENT == null) || (iIWT == null)) {
            this.loadImages();
        }

        this.constructScreen();
    }

    /**
     * This method constructs all components and lays them out on the screen.
     */
    private void constructScreen() {
        // Components.
        // The textarea.
        txtHelp = new JTextArea(25, 125);
        txtHelp.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHelp.setText(iHelpText);
        txtHelp.setCaretPosition(0);
        txtHelp.setLineWrap(true);
        txtHelp.setEditable(false);

        // The OK button.
        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        // The labels.
        lblLabels = new JLabel[15];

        lblLabels[0] =
                new JLabel("<html><p style=\"font-size:large;\">Peptizer version " + PeptizerGUI.PEPTIZER_VERSION + "</p></html>");
        lblLabels[1] = new JLabel(" ");
        lblLabels[2] = new JLabel("Kenny Helsens (kenny.helsens@UGent.be)");
        lblLabels[3] = new HyperLinkLabel("          Ghent University (www.UGent.be)", null, "http://www.ugent.be/");
        lblLabels[4] = new HyperLinkLabel("          IWT (www.iwt.be)", null, "http://www.iwt.be/");
        lblLabels[5] = new JLabel(" ");
        lblLabels[6] = new JLabel(" ");
        lblLabels[7] =
                new HyperLinkLabel("This application is freeware and open source (Apache2 license)", null, "http://www.apache.org/licenses/LICENSE-2.0");
        lblLabels[8] = new JLabel(" ");
        lblLabels[9] =
                new HyperLinkLabel("Click here to proceed to the project web page.", null, "http://genesis.ugent.be/peptizer/");
        lblLabels[10] = new JLabel(" ");
        lblLabels[11] =
                new HyperLinkLabel("Click here to download a manual for Peptizer.", null, "http://genesis.ugent.be/peptizer/peptizer/peptizer_manual.pdf");
        lblLabels[12] = new JLabel(" ");
        lblLabels[13] =
                new JLabel("A Google sites for Peptizer was created to facilitate interaction on custom Agents and custom AgentAggregators.");
        lblLabels[14] =
                new HyperLinkLabel("Click here to proceed to the Google sites for Peptizer", null, "http://sites.google.com/site/peptizer");

        // The containers.
        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));

        // Button panel.
        JPanel jpanButton = new JPanel();
        jpanButton.setLayout(new BoxLayout(jpanButton, BoxLayout.X_AXIS));

        // Label panel.
        JPanel jpanLabels = new JPanel();
        jpanLabels.setLayout(new BoxLayout(jpanLabels, BoxLayout.X_AXIS));

        // Textlabels panel.
        JPanel jpanTextLabels = new JPanel();
        jpanTextLabels.setLayout(new BoxLayout(jpanTextLabels, BoxLayout.Y_AXIS));

        // Scrollpane for textarea + panel for scrollpane.
        JScrollPane jspText =
                new JScrollPane(txtHelp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.X_AXIS));
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));
        jpanScroll.add(jspText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));

        // Start adding.
        jpanButton.add(Box.createHorizontalGlue());
        jpanButton.add(btnOK);
        jpanButton.add(Box.createRigidArea(new Dimension(15, btnOK.getHeight())));

        jpanMain.add(Box.createRigidArea(new Dimension(txtHelp.getWidth(), 15)));
        for (int i = 0; i < lblLabels.length; i++) {
            JLabel lLabel = lblLabels[i];
            System.out.println(i);
            lLabel.setForeground(Color.black);
            jpanTextLabels.add(lLabel);
            jpanTextLabels.add(Box.createRigidArea(new Dimension(txtHelp.getWidth(), 5)));
        }
        jpanLabels.add(Box.createRigidArea(new Dimension(20, jpanTextLabels.getHeight())));
        jpanLabels.add(lblImageTools);
        jpanLabels.add(Box.createRigidArea(new Dimension(20, jpanTextLabels.getHeight())));
        jpanLabels.add(jpanTextLabels);
        jpanLabels.add(Box.createRigidArea(new Dimension(20, jpanTextLabels.getHeight())));
        jpanLabels.add(lblImageUGENT);
        jpanLabels.add(Box.createRigidArea(new Dimension(20, jpanTextLabels.getHeight())));
        jpanLabels.add(lblImageIWT);
        jpanLabels.add(Box.createRigidArea(new Dimension(20, jpanTextLabels.getHeight())));
        jpanLabels.add(Box.createHorizontalGlue());

        jpanMain.add(jpanLabels);
        jpanMain.add(Box.createRigidArea(new Dimension(txtHelp.getWidth(), 20)));
        jpanMain.add(jpanScroll);
        jpanMain.add(Box.createRigidArea(new Dimension(txtHelp.getWidth(), 20)));
        jpanMain.add(jpanButton);
        jpanMain.add(Box.createRigidArea(new Dimension(txtHelp.getWidth(), 15)));

        // Pack and go.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
        this.pack();
    }

    /**
     * Closes this dialog in a nice way.
     */
    private void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method will attempt to load the helptext from the classpath.
     */
    private void loadHelpText() {
        try {
            // First of all, try it via the classloader for this file.
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(TEXTFILE);
            if (is == null) {
                // Apparently not found, try again with the System (bootstrap) classloader.
                is = ClassLoader.getSystemResourceAsStream(TEXTFILE);
                if (is == null) {
                    iHelpText = "No help file (" + TEXTFILE + ") could be found in the classpath!";
                }
            }

            // See if we have an input stream.
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                iHelpText = sb.toString();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * This method loads the appropriate image for displaying it in the imagelabel and icon.
     */
    private void loadImages() {
        // Toolkit icon for the label.
        lblImageTools = new JLabel("No image found!");
        try {
            URL url = this.getClass().getClassLoader().getResource("image/peptizer.jpg");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                lblImageTools = new JLabel(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // IWT icon for the label.
        lblImageIWT = new JLabel("No image found!");
        try {
            URL url = this.getClass().getClassLoader().getResource("image/iwt.jpg");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                lblImageIWT = new JLabel(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // UGENT icon for the window.
        try {
            URL url = this.getClass().getClassLoader().getResource("image/ugentLogo.jpg");
            if (url != null) {
                iUGENT = new ImageIcon(url);
                lblImageUGENT = new JLabel(iUGENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
