package com.compomics.peptizer;

import com.compomics.software.CompomicsWrapper;

import java.io.File;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Mar 23, 2010 Time: 2:14:55 PM
 * <p/>
 * This class
 */
public class PeptizerStarter extends CompomicsWrapper {

    private static Logger logger = Logger.getLogger(PeptizerStarter.class);

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args
     */
    public PeptizerStarter(String[] args) {
        try {
            File jarFile = new File(PeptizerStarter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String mainClass = "com.compomics.peptizer.gui.PeptizerGUI";
            launchTool("Peptizer", jarFile, null, mainClass, args);
        } catch (URISyntaxException ex) {
            logger.error(ex);
        }

    }

    public static void main(String[] args) {
        new PeptizerStarter(args);
    }
}
