package com.compomics.peptizer;

import junit.framework.TestSuite;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Feb 9, 2010
 * Time: 9:05:07 AM
 * <p/>
 * This class
 */
public class InitializeJUnit {
	// Class specific log4j logger for InitializeJUnit instances.
	 private static Logger logger = Logger.getLogger(InitializeJUnit.class);
    // This static boolean keeps track of the initialization status.
    private static boolean isInitialized = false;

    // Empty constructor.
    public InitializeJUnit() {
    }

    /**
     * Initialization of the test environment.
     */
    public static void initialize(){
        if(!isInitialized()){
        //  initiate the test-settings.
            String lFileName = "conf/agent_test_configuration.xml";


            String result = null;
            TestSuite ts = new TestSuite();
            ClassLoader cl = ts.getClass().getClassLoader();
            URL url = cl.getResource(lFileName);
            if (url == null) {
                System.err.println("\"" + lFileName + "\" was not found in the classpath!!\nThis file is needed to setup the agents properly for before running the tests!");
                System.exit(0);
            } else {
                result = url.getFile();
                // Correction for Windows platforms.
                if (File.separatorChar != '/') {
                    // Windows platform. Delete the leading '/'
                    result = result.substring(1);
                    result = result.replace("%20", " ");
                }
            }
            File lAgentTestConfiguration = new File(result);
            MatConfig.getInstance().reloadConfigurationFile(lAgentTestConfiguration, MatConfig.AGENT_CONFIG);

            setInitialized(true);
        }
    }

    private static boolean isInitialized() {
        return isInitialized;
    }

    private static void setInitialized(final boolean aInitialized) {
        isInitialized = aInitialized;
    }
}
