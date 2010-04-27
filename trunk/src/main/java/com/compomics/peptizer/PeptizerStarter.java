package com.compomics.peptizer;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Mar 23, 2010 Time: 2:14:55 PM
 * <p/>
 * This class
 */
public class PeptizerStarter {

/**
 * A wrapper class used to start the jar file with parameters. The parameters are read from the JavaOptions file in the
 * Properties folder.
 */
    /**
     * Starts the launcher by calling the launch method. Use this as the main class in the jar file.
     *
     * @author Kenny Helsens
     */

    private boolean debug = false;

    public PeptizerStarter() {
        try {
            launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the jar file with parameters to the jvm.
     *
     * @throws java.lang.Exception
     */
    private void launch() throws Exception {

        // get the version number set in the pom file
        Properties properties = PropertiesManager.getInstance().getProperties(CompomicsTools.PEPTIZER, "peptizer.properties");

        String jarFileName = "peptizer-" + properties.get("version").toString() + ".jar";

        // Get the jarFile path.
        String path;
        path = this.getClass().getResource("PeptizerStarter.class").getPath();
        path = path.substring(5, path.indexOf(jarFileName));
        path = path.replace("%20", " ");

        // Get Java vm options.
        String options = properties.get("java").toString();


        String quote = "";
        if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
            quote = "\"";
        }

        String javaHome = System.getProperty("java.home") + File.separator +
                "bin" + File.separator;

        String cmdLine = javaHome + "java " + options + " -cp " + quote
                + new File(path, jarFileName).getAbsolutePath()
                + quote + " com.compomics.peptizer.gui.PeptizerGUI";

        if (debug) {
            System.out.println(cmdLine);
        }

        try {
            Process p = Runtime.getRuntime().exec(cmdLine);

            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            String temp = null;

            temp += "<ERROR>\n\n";

            if (debug) {
                System.out.println("<ERROR>");
            }

            line = br.readLine();

            boolean error = false;

            while (line != null) {

                if (debug) {
                    System.out.println(line);
                }

                temp += line + "\n";
                line = br.readLine();
                error = true;
            }

            if (debug) {
                System.out.println("</ERROR>");
            }

            temp += "\nThe command line executed:\n";
            temp += cmdLine + "\n";
            temp += "\n</ERROR>\n";
            int exitVal = p.waitFor();

            if (debug) {
                System.out.println("Process exitValue: " + exitVal);
            }

            if (error) {

                javax.swing.JOptionPane.showMessageDialog(null,
                        "Failed to start peptizer.\n\n" +
                                "Make sure that peptizer is installed in a path not containing\n" +
                                "special characters. On Linux it has to be run from a path without spaces.\n\n" +
                                "The upper memory limit used may be too high for your computer to handle.\n" +
                                "Try reducing it and see if this helps.\n\n" +
                                "For more details see:\n" +
                                path +
                                File.separator + "peptizer.log\n\n"
                                + "Or see \'Troubleshooting\' at http://peptizer.googlecode.com",
                        "peptizer - Startup Failed", JOptionPane.OK_OPTION);

                File logFile = new File(path +
                        File.separator + "peptizer.log");

                FileWriter f = new FileWriter(logFile);
                f.write(temp);
                f.close();

                System.exit(0);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main class in the jar file.
     *
     * @param args
     */
    public static void main(String[] args) {
        new PeptizerStarter();
    }
}
