package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.AgentAggregatorFactory;
import com.compomics.peptizer.util.AgentFactory;

import java.io.*;
import java.util.Iterator;
import java.util.Set;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 4-jul-2007
 * Time: 10:43:14
 */

/**
 * Class description: ------------------ This class was developed to save the configuration. Static methods can be
 * accessed to save the different configuration objects being Agents, Table or General.
 */
public class ConfigurationWriter {

    /**
     * Empty constructor.
     */
    public ConfigurationWriter() {
    }


    /**
     * Writes the configuration of the Agents, AgentAggregators and General properties into a single file.
     *
     * @param aFile
     */
    public static void writeTaskConfiguration(File aFile) {
        BufferedWriter bw = null;
        try {
            // Create BufferedWriter from File aFile.
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile)));

            bw.write(getXMLVersionHeader());
            writeAgentConfiguration(bw);

            bw.write("--SEPARATOR--\n");

            bw.write(getXMLVersionHeader());
            writeAgentAggregatorConfiguration(bw);

            bw.write("--SEPARATOR--\n");

            bw.write(getXMLVersionHeader());
            writeGeneralConfiguration(bw);

            bw.write("--SEPARATOR--\n");
        } catch (IOException e) {
            MatLogger.logExceptionalEvent("IOEException while saving AgentConfiguration to " + aFile.getName());
            e.printStackTrace();
        }
    }

    /**
     * Writes the current state of the AgentFactory into a given file.
     *
     * @param aFile Target file to write the Agent configurations.
     */
    public static void writeAgentConfiguration(File aFile) {
        BufferedWriter bw = null;
        try {
            // Create BufferedWriter from File aFile.
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile)));
            bw.write(getXMLVersionHeader());
            writeAgentConfiguration(bw);

        } catch (IOException e) {
            MatLogger.logExceptionalEvent("IOEException while saving AgentConfiguration to " + aFile.getName());
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes the current state of the AgentAggregatorFactory into a given file.
     *
     * @param aFile Target file to write the Agent configurations.
     */
    public static void writeAgentAggregatorConfiguration(File aFile) {
        BufferedWriter bw = null;
        try {
            // Create BufferedWriter from File aFile.
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile)));
            bw.write(getXMLVersionHeader());
            writeAgentAggregatorConfiguration(bw);

        } catch (IOException e) {
            MatLogger.logExceptionalEvent("IOEException while saving AgentAggregatorConfiguration to " + aFile.getName());
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes the current state of the AgentFactory into a given buffered writer.
     *
     * @param aBufferedWriter BufferedWriter to write the Agent configurations.
     * @throws java.io.IOException by writing to the BufferedWriter
     */
    public static void writeAgentConfiguration(BufferedWriter aBufferedWriter) throws IOException {
        String lConfigurationName = "agents";
        aBufferedWriter.write(getXMLStart(lConfigurationName));
        aBufferedWriter.flush();
        Agent[] lAgents = AgentFactory.getInstance().getAllAgents();
        for (int i = 0; i < lAgents.length; i++) {
            Agent lAgent = lAgents[i];
            aBufferedWriter.write(nextAgentUnit(lAgent));
            aBufferedWriter.flush();
        }
        aBufferedWriter.write(getXMLStop(lConfigurationName));
        aBufferedWriter.newLine();
        aBufferedWriter.flush();
        MatLogger.logNormalEvent("Saved Agent configuration to '" + aBufferedWriter.toString() + "'.");
    }

    /**
     * Writes the current state of the AgentFactory into a given buffered writer.
     *
     * @param aBufferedWriter BufferedWriter to write the Agent configurations.
     * @throws java.io.IOException by writing to the BufferedWriter
     */
    public static void writeAgentAggregatorConfiguration(BufferedWriter aBufferedWriter) throws IOException {
        String lConfigurationName = "aggregators";
        aBufferedWriter.write(getXMLStart(lConfigurationName));
        aBufferedWriter.flush();
        AgentAggregator[] lAgentAggregators = AgentAggregatorFactory.getInstance().getAgentAggregators();
        for (int i = 0; i < lAgentAggregators.length; i++) {
            AgentAggregator lAgentAggregator = lAgentAggregators[i];
            aBufferedWriter.write(nextAgentAggregatorUnit(lAgentAggregator));
            aBufferedWriter.flush();
        }
        aBufferedWriter.write(getXMLStop(lConfigurationName));
        aBufferedWriter.newLine();
        aBufferedWriter.flush();
        MatLogger.logNormalEvent("Saved AgentAggregator configuration to '" + aBufferedWriter.toString() + "'.");
    }

    /**
     * Writes the current state of the General MatConfig properties into a given buffered writer.
     *
     * @param aBufferedWriter BufferedWriter to write the general configurations.
     * @throws java.io.IOException by writing to the BufferedWriter
     */
    public static void writeGeneralConfiguration(BufferedWriter aBufferedWriter) throws IOException {
        String lConfigurationName = "general";
        aBufferedWriter.write(getXMLStart(lConfigurationName));
        aBufferedWriter.flush();
        Set keys = MatConfig.getInstance().getGeneralKeySet();

        StringBuffer sb = new StringBuffer();
        Iterator iter = keys.iterator();

        // Pretty hardcoded output presentation of an Agent tag in the configuration file.

        sb.append("        <aggregator>\n" +
                "          <!-- General properties for Peptizer. -->\n");

        while (iter.hasNext()) {
            // Property Name - Key
            Object key = iter.next().toString();
            // Property Value returned by Key.
            Object value = MatConfig.getInstance().getGeneralProperty(key.toString()).toLowerCase();
            sb.append("            <property name=\"" + key.toString().toLowerCase() + "\">" + value.toString().toLowerCase() + "</property>\n");
        }
        sb.append("        </aggregator>\n");
        aBufferedWriter.write(sb.toString());

        aBufferedWriter.write(getXMLStop(lConfigurationName));
        aBufferedWriter.flush();
        aBufferedWriter.newLine();
        MatLogger.logNormalEvent("Saved General configuration to '" + aBufferedWriter.toString() + "'.");
    }


    /**
     * Returns an Xml configuration block for a given AgentAggregator
     *
     * @param aAgentAggregator AgentAggregator to extract configuration settings from.
     * @return String with configuration for a given Agent.
     */
    private static String nextAgentAggregatorUnit(AgentAggregator aAgentAggregator) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = aAgentAggregator.getProperties().keySet().iterator();

        // Pretty hardcoded output presentation of an Agent tag in the configuration file.

        sb.append("        <aggregator>\n" +
                "            <!-- " + aAgentAggregator.getDescription() + " -->\n" +
                "            <uniqueid>" + aAgentAggregator.getUniqueID() + "</uniqueid>\n" +
                "            <property name=\"name\">" + aAgentAggregator.getName() + "</property>\n");

        while (iter.hasNext()) {
            // Property Name - Key
            Object key = iter.next().toString();
            // Property Value returned by Key.
            Object value = aAgentAggregator.getProperties().get(key).toString().toLowerCase();
            sb.append("            <property name=\"" + key.toString().toLowerCase() + "\">" + value.toString().toLowerCase() + "</property>\n");
        }
        sb.append("        </aggregator>\n");

        return sb.toString();
    }


    /**
     * Returns a Xml configuration block for a given Agent.
     *
     * @param aAgent Agent to extract configuration settings from.
     * @return String with Configuration for a given Agent.
     */
    private static String nextAgentUnit(Agent aAgent) {
        StringBuffer sb = new StringBuffer();
        Iterator iter = aAgent.getProperties().keySet().iterator();

        // Pretty hardcoded output presentation of an Agent tag in the configuration file.

        sb.append("        <agent>\n" +
                "            <!-- " + aAgent.getTagFreeDescription() + " -->\n" +
                "            <uniqueid>" + aAgent.getUniqueID() + "</uniqueid>\n" +
                "            <property name=\"name\">" + aAgent.getName() + "</property>\n" +
                "            <property name=\"active\">" + aAgent.isActive() + "</property>\n" +
                "            <property name=\"veto\">" + aAgent.hasVeto() + "</property>\n");

        while (iter.hasNext()) {
            // Property Name - Key
            Object key = iter.next().toString();
            // Property Value returned by Key.
            Object value = aAgent.getProperties().get(key).toString().toLowerCase();
            sb.append("            <property name=\"" + key.toString().toLowerCase() + "\">" + value.toString().toLowerCase() + "</property>\n");
        }
        sb.append("        </agent>\n");

        return sb.toString();
    }


    /**
     * Returns the start for the xml file.
     *
     * @param aConfigurationName String on the type of configuration file.
     * @return String for starting the configuration file.
     */
    private static String getXMLStart(String aConfigurationName) {
        StringBuffer sb = new StringBuffer();
        sb.append("<!--  Peptizer " + aConfigurationName + " properties -->\n");
        sb.append("<config name=\"" + aConfigurationName + "\">\n");
        sb.append("    <" + aConfigurationName + ">\n");

        return sb.toString();
    }

    /**
     * Returns the stop for the xml file.
     *
     * @param aConfigurationName String on the type of configuration file.
     * @return String for stopping the configuration file.
     */
    private static String getXMLStop(String aConfigurationName) {
        StringBuffer sb = new StringBuffer();
        sb.append("    </" + aConfigurationName + ">\n");
        //sb.append("    <!-- End of " + aConfigurationName + "configuration file-->\n");
        sb.append("</config>");

        return sb.toString();
    }

    /**
     * Returns the
     *
     * @return
     */
    private static String getXMLVersionHeader() {
        String s = "<?xml version=\"1.0\"?>\n";
        return s;
    }
}
