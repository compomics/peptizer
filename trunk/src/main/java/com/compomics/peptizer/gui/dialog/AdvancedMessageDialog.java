package com.compomics.peptizer.gui.dialog;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-aug-2007
 * Time: 18:00:30
 */

/**
 * Class description:
 * ------------------
 * This class was developed to offer a more complex Information dialog for help functions and instructions.
 */
public class AdvancedMessageDialog extends JDialog {
	// Class specific log4j logger for AdvancedMessageDialog instances.
	 private static Logger logger = Logger.getLogger(AdvancedMessageDialog.class);

    /**
     * The textarea that will display the help text.
     */
    private JLabel txtInformation = null;

    /**
     * This button can be pressed to exit the dialog.
     */
    private JButton btnOK = null;

    /**
     * The ImageIcon with the mat image icon?
     */
    private static ImageIcon iMat = null;

    /**
     * The helptext to display in the textarea.
     */
    private static String iMessage = null;

    /**
     * This constructor mimics the constructor on the superclass and allows
     * specification of the parent JFrame as well as the title for the dialog.
     * Note that about dialog is always modal!
     *
     * @param aParent  JFrame that is the parent of this dialog.
     * @param aTitle   String with the title for this dialog.
     * @param aMessage The Message to be displayed by the dialog.
     */
    public AdvancedMessageDialog(JFrame aParent, String aTitle, String aMessage) {
        super(aParent, aTitle, true);

        iMessage = aMessage;

        this.constructScreen();
        this.pack();

        // Set minimum size.
        this.setMinimumSize(new Dimension(250, 100));

        this.setVisible(true);
    }

    /**
     * This method constructs all components and lays them out on the screen.
     */
    private void constructScreen() {
        // Components.
        // The textarea.
        txtInformation = new JLabel();
        txtInformation.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInformation.setText(iMessage);

        // The OK button.
        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        // The containers.
        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        //jpanMain.setBorder(new CompoundBorder(new EmptyBorder(new Insets(1, 4, 1, 4)), jpanMain.getBorder()));

        // Button panel.
        JPanel jpanButton = new JPanel();
        jpanButton.setLayout(new BoxLayout(jpanButton, BoxLayout.X_AXIS));

        // Scrollpane for textarea + panel for scrollpane.
        JScrollPane jspText = new JScrollPane(txtInformation, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.X_AXIS));
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));
        jpanScroll.add(jspText);
        jpanScroll.add(Box.createRigidArea(new Dimension(20, jspText.getHeight())));

        // Start adding.
        jpanButton.add(Box.createHorizontalGlue());
        jpanButton.add(btnOK);
        jpanButton.add(Box.createRigidArea(new Dimension(15, btnOK.getHeight())));

        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanScroll);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 20)));
        jpanMain.add(jpanButton);
        jpanMain.add(Box.createRigidArea(new Dimension(txtInformation.getWidth(), 15)));

        // Pack and go.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);

        // Set location
        int x = (new Double(Toolkit.getDefaultToolkit().getScreenSize().width * 0.2)).intValue();
        int y = (new Double(Toolkit.getDefaultToolkit().getScreenSize().height * 0.2)).intValue();
        this.setLocation(x, y);


    }

    /**
     * Closes this dialog in a nice way.
     */
    private void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method loads the appropriate image for displaying it in
     * the imagelabel and icon.
     private void loadImages() {
     // Toolkit icon for the label.
     lblImageTools = new JLabel("No image found!");
     try {
     URL url = this.getClass().getClassLoader().getResource("toolbox.jpg");
     if(url != null) {
     ImageIcon icon = new ImageIcon(url);
     lblImageTools = new JLabel(icon);
     }
     } catch(Exception e) {
     e.printStackTrace();
     }

     // RUG icon for the window.
     try {
     URL url = this.getClass().getClassLoader().getResource("rugLogo.jpg");
     if(url != null) {
     iMat = new ImageIcon(url);
     lblImageRUG = new JLabel(iMat);
     }
     } catch(Exception e) {
     e.printStackTrace();
     }
     }
     */

    /**
     * This method extracts the last version from the 'about.txt' file.
     *
     * @return String with the String of the latest version, or '
    private String getLastVersion() {
    String result = null;
    int start = iMessage.lastIndexOf("- Version ") + 10;
    int end = iMessage.indexOf("\n", start);
    if(start > 0 && end > 0) {
    result = iMessage.substring(start, end).trim();
    } else {
    result = "(unknown - missing original help text)!";
    }

    return result;
    }
     */


}
