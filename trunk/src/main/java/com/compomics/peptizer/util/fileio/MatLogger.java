package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.dialog.AdvancedMessageDialog;
import com.compomics.peptizer.gui.interfaces.StatusView;
import com.compomics.util.io.StartBrowser;
import org.apache.log4j.Logger;

import javax.swing.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 5-jul-2007
 * Time: 16:47:04
 */

/**
 * Class description:
 * ------------------
 * This class was developed to have simple logging funcionality in the System resources & the StatusView panel.
 */
public class MatLogger {
    // Class specific log4j logger for MatLogger instances.
    private static Logger logger = Logger.getLogger(MatLogger.class);

    /**
     * The boolean defining the status of the System out logging.
     */
    private static boolean boolSystemOut = false;

    /**
     * The boolean defining the status of the StatusView logging.
     */
    private static boolean boolStatusView = false;

    /**
     * The Statusview instance for logging.
     */
    private static StatusView iStatusView = null;


    /**
     * Log status to active loggers.
     *
     * @param aMessage String describing the status.
     */
    public static void logNormalEvent(String aMessage) {
        if (boolSystemOut) {
            logger.info(aMessage);
            logger.info(aMessage);
        }
        if (boolStatusView) {
            iStatusView.setStatus(aMessage);
        }
    }

    /**
     * Log Exceptional Event (ex: an error or unexpected conditions) to active loggers.
     *
     * @param aMessage String describing the error.
     */
    public static void logExceptionalEvent(String aMessage) {
        if (boolSystemOut) {
            System.err.println(aMessage);
        }
        if (boolStatusView) {
            iStatusView.setError(aMessage);
            if (iStatusView instanceof PeptizerGUI) {
                //JOptionPane.showMessageDialog((PeptizerGUI) iStatusView, aMessage, "Exceptional event!", JOptionPane.ERROR_MESSAGE);
                int lResult = javax.swing.JOptionPane.showOptionDialog(null,
                        aMessage,
                        "Peptizer: Exceptional Event!!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        UIManager.getIcon("OptionPane.errorIcon"),
                        new Object[]{"Report issue", "Exit"},
                        "Report issue");

                if (lResult == JOptionPane.OK_OPTION) {
                    String lIssuesPage = new String("http://code.google.com/p/peptizer/issues/list");
                    StartBrowser.start(lIssuesPage);
                }
            }
        }

    }

    /**
     * Log Exceptional Messages such as process reports towards the user.
     * In GUI mode, the message is printed on a JLabel, so HTML will be displayed correctly.
     * <p/>
     * <br></br><strong>The message is not logged to the StatusPanel or system.out!</strong>
     *
     * @param aMessage String describing the message.
     * @param aTitle   Title for the Dialog.
     */
    public static void logExceptionalGUIMessage(String aTitle, String aMessage) {
        if (boolStatusView) {
            if (iStatusView instanceof PeptizerGUI) {
                new AdvancedMessageDialog((PeptizerGUI) iStatusView, aTitle, aMessage);
            }
        }
    }


    /**
     * Log time to active loggers.
     */
    public static void logTime() {
        if (boolSystemOut) {
            System.err.println(System.currentTimeMillis());
        }
        if (boolStatusView) {
            iStatusView.setStatus("" + System.currentTimeMillis());
        }
    }

    /**
     * Returns the System out activity.
     *
     * @return boolean activity of System out.
     */
    public static boolean isSystemOut() {
        return boolSystemOut;
    }

    /**
     * Sets the System out activity.
     *
     * @param aBoolSystemOut activity of System out.
     */
    public static void setSystemOut(boolean aBoolSystemOut) {
        boolSystemOut = aBoolSystemOut;
    }

    /**
     * Returns the StatusView activity.
     *
     * @return boolean activity of System out.
     */
    public static boolean isStatusView() {
        return iStatusView != null && boolStatusView;
    }

    /**
     * Disables statusview.
     * Can only be turned on by passing a StatusView component.
     */
    public static void disableStatusView() {
        boolStatusView = false;
    }

    /**
     * Sets the StatusView Component and turns it active.
     *
     * @param aStatusView StatusView component.
     */
    public static void setStatusViewComponent(StatusView aStatusView) {
        iStatusView = aStatusView;
        boolStatusView = true;
    }
}
