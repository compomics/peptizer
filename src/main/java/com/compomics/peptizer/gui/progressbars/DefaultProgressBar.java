package com.compomics.peptizer.gui.progressbars;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 8-mei-2007
 * Time: 11:21:39
 */

/**
 * Class description: ------------------ This class implements JDialog and offers a simple message and progressbar.
 */
public class DefaultProgressBar extends JDialog {
	// Class specific log4j logger for DefaultProgressBar instances.
	 private static Logger logger = Logger.getLogger(DefaultProgressBar.class);
    /**
     * The message to display on the progress bar.
     */
    private String iMessage = null;
    /**
     * The minimum for the progress bar.
     */
    private int iMinimum = 0;
    /**
     * The maximum for the progress bar.
     */
    private int iMaximum = 0;
    /**
     * The label.
     */
    private JLabel lblMessage = null;
    /**
     * The progressbar component.
     */
    private JProgressBar jpbProgress = null;

    /**
     * @param owner
     * @param title
     * @throws HeadlessException
     */
    public DefaultProgressBar(final Frame owner, final String title) throws HeadlessException {
        this(owner, title, 0, 2);
        setIndeterminate(true);
    }

    /**
     * The constructor takes an owner, a title and minimum and maximum values for the progress. The message is set to
     * "Progress..." <br /> By default, the initial value is set to the minimum specified. You can specify another starting
     * value by calling the 'setValue()' method after construction.
     *
     * @param aOwner   Frame with the owner of this JDialog.
     * @param aTitle   String with the title for the JDialog.
     * @param aMinimum int with the minimum value of the progress.
     * @param aMaximum int with the maximum value of the progress.
     */
    public DefaultProgressBar(Frame aOwner, String aTitle, int aMinimum, int aMaximum) {
        this(aOwner, aTitle, aMinimum, aMaximum, "Progress:");
    }

    /**
     * The constructor takes an owner, a title, minimum and maximum values for the progress and a message for the dialog.
     * <br /> By default, the initial value is set to the minimum specified. You can specify another starting value by
     * calling the 'setValue()' method after construction.
     *
     * @param aOwner   Frame with the owner of this JDialog.
     * @param aTitle   String with the title for the JDialog.
     * @param aMinimum int with the minimum value of the progress.
     * @param aMaximum int with the maximum value of the progress.
     * @param aMessage String with the message to be displayed on the JDialog
     */
    public DefaultProgressBar(Frame aOwner, String aTitle, int aMinimum, int aMaximum, String aMessage) {
        // Super class.
        super(aOwner, aTitle, true);

        // Initializations.
        iMinimum = aMinimum;
        iMaximum = aMaximum;
        iMessage = aMessage;
        jpbProgress = new JProgressBar(iMinimum, iMaximum);
        jpbProgress.setValue(iMinimum);
        jpbProgress.setStringPainted(false);

        // GUI.
        this.constructScreen();
        this.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width / 4), (screen.height / 4));
    }

    /**
     * This method basically tells the progressbar to move on. <br /> Note that setting a value that is greater than or
     * equal to the maximum, will cause the progress bar to be disposed of.
     *
     * @param aValue int with the value to which the progressbar should move.
     */
    public void setValue(int aValue) {
        if (aValue < iMinimum) {
            aValue = iMinimum;
        }
        jpbProgress.setValue(aValue);
        if (aValue >= iMaximum) {
            this.setVisible(false);
            this.dispose();
        }
    }

    /**
     * This method will return the current value of the progressbar.
     *
     * @return int with the current value of the progressbar.
     */
    public int getValue() {
        return jpbProgress.getValue();
    }

    public int getMaximum() {
        return iMaximum;
    }

    public int getMinimum() {
        return iMinimum;
    }


    /**
     * This method reports on the message currently showing (useful for appending something).
     *
     * @return String with the message.
     */
    public String getMessage() {
        return this.iMessage;
    }

    /**
     * This method alters the message visible on the dialog.
     *
     * @param aMessage String with the message to display.
     */
    public void setMessage(String aMessage) {
        this.iMessage = aMessage;
        this.lblMessage.setText(aMessage);
    }

    /**
     * This method sets the maximum.
     *
     * @param aMaximum int with the maximum.
     */
    public void setMaximum(int aMaximum) {
        iMaximum = aMaximum;
    }

    /**
     * Switches the progress bar between determinate and indeterminate.
     *
     * @param aIndeterminate boolean to indicate whether the progress bar should be (in-)determinate.
     */
    public void setIndeterminate(boolean aIndeterminate) {
        this.jpbProgress.setIndeterminate(aIndeterminate);
    }

    /**
     * This method create the GUI for this dialog.
     */
    private void constructScreen() {
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        lblMessage = new JLabel(iMessage);
        lblMessage.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(lblMessage);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpbProgress);
        jpanMain.add(Box.createVerticalStrut(5));

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }
}
