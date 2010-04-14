/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 11-apr-2007
 * Time: 16:53:44
 */
package com.compomics.peptizer;

import com.compomics.peptizer.util.AgentAggregatorFactory;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Class description:
 * ------------------
 * This class was developed as a singleton instance to manage configuration settings of mat.
 */
public class MatConfig {

    // Singleton preferences instance
    private static MatConfig ourInstance;

    /**
     * The XML Pull Parser Factory.
     */
    public static XmlPullParserFactory pullparserfactory = null;

    /**
     * The agent properties.
     */
    private static HashMap agent = null;

    /**
     * The table properties.
     */
    private static HashMap table = null;

    /**
     * The aggregator properties.
     */
    private static HashMap aggregator = null;

    /**
     * The gui properties.
     */
    private static Properties general = null;

    /**
     * Instance variable of the Agent configuration file.
     */
    private static String iAgentConfigurationFile = "conf/agent.xml";
    /**
     * Instance variable of the Agent configuration file.
     */
    private static String iTableConfigurationFile = "conf/table.xml";
    /**
     * Instance variable of the Aggregator configuration file.
     */
    private static String iAggregatorConfigurationFile = "conf/aggregator.xml";
    /**
     * Instance variable of the Agent configuration file.
     */
    private static String iGeneralConfigurationFile = "conf/general.xml";

    /**
     * Static identifier for an agent configuration file.
     */
    public static final int AGENT_CONFIG = 1;

    /**
     * Static identifier for an agent aggregator configuration file.
     */
    public static final int AGENTAGGREGATOR_CONFIG = 2;

    /**
     * Static identifier for a general configuration file.
     */
    public static final int GENERAL_CONFIG = 3;

    /**
     * Static identifier for a table configuration file.
     */
    public static final int TABLE_CONFIG = 4;

    // Static initializer for the XML parser factory.
    static {
        try {
            pullparserfactory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            pullparserfactory.setNamespaceAware(true);
        } catch (XmlPullParserException xppe) {
            xppe.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Returns a singleton instance of MatConfig.
     *
     * @return Singleton mat instance.
     */
    public static MatConfig getInstance() {
        if (ourInstance == null) {
            ourInstance = new MatConfig();
        }
        return ourInstance;
    }

    /**
     * Empty constructor.
     */
    private MatConfig() {

        // Keep track which file is busy, for error reporting.
        String lCurrent = "";
        try {
            // 1. agent configuration.
            lCurrent = iAgentConfigurationFile;
            InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(iAgentConfigurationFile));
            XmlPullParser xpp = pullparserfactory.newPullParser();
            xpp.setInput(reader);
            parseConfiguration(xpp);
            reader.close();

            // 2. table configuration.
            lCurrent = iTableConfigurationFile;
            reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(iTableConfigurationFile));
            xpp = pullparserfactory.newPullParser();
            xpp.setInput(reader);
            parseConfiguration(xpp);
            reader.close();

            // 3. general configuration.
            lCurrent = iGeneralConfigurationFile;
            reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(iGeneralConfigurationFile));
            xpp = pullparserfactory.newPullParser();
            xpp.setInput(reader);
            parseConfiguration(xpp);
            reader.close();

            // 4. aggregator configuration.
            lCurrent = iAggregatorConfigurationFile;
            reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(iAggregatorConfigurationFile));
            xpp = pullparserfactory.newPullParser();
            xpp.setInput(reader);
            parseConfiguration(xpp);
            reader.close();


        } catch (XmlPullParserException e) {
            MatLogger.logExceptionalEvent("XML pull parser encountered an error while parsing configuration file ( " + lCurrent + ").");
            e.printStackTrace();
        } catch (IOException e) {
            MatLogger.logExceptionalEvent("IOE exception while reading configuration file ( " + lCurrent + ").");
            e.printStackTrace();
        } catch (NullPointerException e) {
            MatLogger.logExceptionalEvent("NullPointer Exception while initiating configuration file (" + lCurrent + "). Please make sure this configuration file (" + lCurrent + ") is availlable in the classpath.\n");
            e.printStackTrace();
        }
    }

    /**
     * This method parses XML configuration files.
     *
     * @param aXpp XmlPullParser reading an xml file.
     * @throws IOException            Input Output error.
     * @throws XmlPullParserException Xml parser error.
     */
    private void parseConfiguration(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        int eventType = aXpp.getEventType();
        boolean validated = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    eventType = aXpp.next();
                    break;
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("config")) {
                        validated = true;
                        eventType = aXpp.next();
                    } else if (start.equals("agents")) {
                        agent = processAgents(aXpp);
                        eventType = aXpp.getEventType();
                    } else if (start.equals("table")) {
                        table = processTable(aXpp);
                        eventType = aXpp.getEventType();
                    } else if (start.equals("general")) {
                        processGeneral(aXpp);
                        eventType = aXpp.getEventType();
                    } else if (start.equals("aggregators")) {
                        aggregator = processAggregators(aXpp);
                        eventType = aXpp.getEventType();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    String name = aXpp.getName();
                    if (name.equals("config")) {
                        // End of configuration unit,
                        eventType = aXpp.next();
                    }
                    break;


                case XmlPullParser.TEXT:
                    eventType = aXpp.next();
                    break;

                case XmlPullParser.COMMENT:
                    eventType = aXpp.next();
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
        if (!validated) {
            throw new IOException("No root tag '<config>' found in the XML configuration document!");
        }
    }

    /**
     * This method parses the Aggregator XML file.
     *
     * @param aXpp XmlPullParser reading an xml file.
     * @throws IOException            when the reading failed.
     * @throws XmlPullParserException when the XML parsing failed.
     */
    private HashMap processAggregators(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        int eventType = aXpp.next();
        // Initialize new Agent HashMap, meaning that Agent settings can be reloaded (saved profiles) from within the application.
        HashMap lResult = new HashMap();
        boolean lContinue = true;
        while (lContinue) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("aggregator")) {
                        processUnit(aXpp, lResult);
                        eventType = aXpp.next();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (aXpp.getName().equals("aggregators")) {
                        eventType = aXpp.next();
                        lContinue = false;
                    }
                    break;

                case XmlPullParser.COMMENT:
                    // do nothing,
                    eventType = aXpp.next();
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
        return lResult;
    }

    /**
     * This method parses the Agent XML file.
     *
     * @param aXpp XmlPullParser reading an xml file.
     * @throws IOException            when the reading failed.
     * @throws XmlPullParserException when the XML parsing failed.
     */
    public HashMap processAgents(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        HashMap lResult = new HashMap();
        int eventType = aXpp.next();
        // Initialize new Agent HashMap, meaning that Agent settings can be reloaded (saved profiles) from within the application.
        boolean lContinue = true;
        while (lContinue) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("agent")) {
                        processUnit(aXpp, lResult);
                        eventType = aXpp.next();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (aXpp.getName().equals("agents")) {
                        eventType = aXpp.next();
                        lContinue = false;
                    }
                    break;

                case XmlPullParser.COMMENT:
                    // do nothing,
                    eventType = aXpp.next();
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
        return lResult;
    }


    /**
     * This method parses the table XML file.
     *
     * @param aXpp XmlPullParser reading an xml file.
     * @throws IOException            when the reading failed.
     * @throws XmlPullParserException when the XML parsing failed.
     */
    public HashMap processTable(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        int eventType = aXpp.next();

        // Initialize new Table HashMap, meaning that Table settings can be reloaded (saved profiles) from within the application.
        HashMap lResult = new HashMap();

        boolean lContinue = true;
        while (lContinue) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("tablerow")) {
                        processUnit(aXpp, lResult);
                        eventType = aXpp.next();
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if (aXpp.getName().equals("table")) {
                        lContinue = false;
                        eventType = aXpp.next();
                    }
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
        return lResult;
    }

    /**
     * This method parses the general XML file.
     *
     * @param aXpp XmlPullParser reading an xml file.
     * @throws IOException            when the reading failed.
     * @throws XmlPullParserException when the XML parsing failed.
     */
    private void processGeneral(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        int eventType = aXpp.next();

        // Initialize new general Properties, meaning that General settings can be reloaded (saved profiles) from within the application.
        general = new Properties();

        boolean lContinue = true;
        while (lContinue) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("property")) {
                        String lName = "";
                        String lValue = "";
                        // First get the attribute name of the property.
                        if (aXpp.getAttributeCount() > 0) {
                            lName = aXpp.getAttributeValue(0);
                        }
                        // Proceed to the TEXT of the property.
                        eventType = aXpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            lValue = aXpp.getText().trim();
                        }
                        general.put(lName, lValue);
                    }
                    eventType = aXpp.next();
                    break;

                case XmlPullParser.END_TAG:
                    if (aXpp.getName().equals("general")) {
                        lContinue = false;
                    }
                    eventType = aXpp.next();
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
    }

    private void processUnit(XmlPullParser aXpp, HashMap aUnitHashMap) throws IOException, XmlPullParserException {
        int eventType = aXpp.next();
        boolean lContinue = true;
        Properties lProperties = new Properties();
        String ID = "";
        while (lContinue) {
            switch (eventType) {
                case XmlPullParser.COMMENT:
                    break;

                case XmlPullParser.START_TAG:
                    String lName = aXpp.getName();
                    // Do nothing
                    if (lName.equals("uniqueid")) {
                        // Next request is identifier TEXT.
                        eventType = aXpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            ID = aXpp.getText().trim();
                        }
                    } else if (lName.equals("property")) {
                        String lKey = "";
                        String lValue = "";
                        // First get the attribute name of the property.
                        if (aXpp.getAttributeCount() > 0) {
                            lKey = aXpp.getAttributeValue(0);
                        }
                        eventType = aXpp.next();
                        if (eventType == XmlPullParser.TEXT) {
                            lValue = aXpp.getText().trim();
                        }
                        lProperties.put(lKey, lValue);
                    }

                    eventType = aXpp.next();
                    break;

                case XmlPullParser.END_TAG:
                    lName = aXpp.getName();
                    if (lName.equals("agent") | lName.equals("tablerow") | lName.equals("aggregator")) {
                        aUnitHashMap.put(ID, lProperties);
                        lContinue = false;
                    }
                    eventType = aXpp.next();
                    break;

                default:
                    eventType = aXpp.next();
                    break;
            }
        }
    }

    /**
     * Returns properties on the specified Agent.
     *
     * @param aUniqueAgentID Agent String identifier.
     * @return Properties on the specified Agent.
     */
    public Properties getAgentProperties(String aUniqueAgentID) {
        return (Properties) agent.get(aUniqueAgentID);
    }

    /**
     * Returns a String[] with all the unique Agent id's specified in the configuration.
     *
     * @return Array of unique Agent identifiers.
     */
    public String[] getUniqueAgentIDs() {
        String[] lResults = new String[agent.keySet().size()];
        for (int i = 0; i < lResults.length; i++) {
            String s = (String) agent.keySet().toArray()[i];
            lResults[i] = s;
        }
        return lResults;
    }

    /**
     * Returns a String[] with all the unique AgentAggregator id's specified in the configuration.
     *
     * @return Array of unique AgentAggregator identifiers.
     */
    public String[] getUniqueAgentAggregatorIDs() {
        String[] lResults = new String[aggregator.keySet().size()];
        for (int i = 0; i < lResults.length; i++) {
            String s = (String) aggregator.keySet().toArray()[i];
            lResults[i] = s;
        }
        return lResults;
    }

    /**
     * Returns a the general property of the Key.
     *
     * @param aKey description of property.
     * @return String property of the key.
     */
    public String getGeneralProperty(String aKey) {
        return general.getProperty(aKey);
    }

    /**
     * Returns a Set with keys for the general properties.
     *
     * @return Set with general keys.
     */
    public Set getGeneralKeySet() {
        return general.keySet();
    }

    /**
     * Returns a Properties instance with general properties.
     *
     * @return Properties instance with general properties.
     */
    public Properties getGeneralProperties() {
        return general;
    }


    /**
     * Change an existing general property.
     *
     * @param aKey   String key of property.
     * @param aValue String value of property.
     */
    public void changeGeneralProperty(String aKey, String aValue) {
        if (general.get(aKey) != null) {
            // Only append existing properties!
            general.put(aKey, aValue);
        }
    }

    /**
     * Returns a String[] with all the unique TableRow id's specified in the configuration file.
     *
     * @return Array of unique TableRow identifiers.
     */
    public String[] getTableRowIDs() {
        Iterator iter = table.keySet().iterator();
        String[] lResult = new String[table.size()];

        int lCount = 0;
        while (iter.hasNext()) {
            String s = (String) iter.next();
            lResult[lCount] = s;
            lCount++;
        }
        return lResult;
    }

    /**
     * Returns properties on the specified TableRow.
     *
     * @param aUniqueTableRowID Tablerow String identifier.
     * @return Properties on the specified TableRow.
     */
    public Properties getTableRowProperties(String aUniqueTableRowID) {
        return (Properties) table.get(aUniqueTableRowID);
    }


    /**
     * Returns properties on the specified AgentAggregator.
     *
     * @param aUniqueAggregatorID String ID for the AgentAggregator.
     * @return Properties on the specified AgentAggregator.
     */
    public Properties getAggregatorProperties(String aUniqueAggregatorID) {
        return (Properties) aggregator.get(aUniqueAggregatorID);
    }

    public XmlPullParserFactory getPullparserfactory() {
        return pullparserfactory;
    }

    /**
     * (Re)loads the configuration from a given file.
     * Current configuration will be reset.
     *
     * @param aFile File with Agent configuration.
     * @param aType final static identifier for the file type. Declared on Matconfig as AGENT_CONFIG, AGENTAGGREGATOR_CONFIG and GENERAL_CONFIG.
     */
    public void reloadConfigurationFile(File aFile, int aType) {
        // 1. agent configuration.
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(aFile));
            XmlPullParser xpp = pullparserfactory.newPullParser();
            xpp.setInput(reader);
            parseConfiguration(xpp);
            reader.close();
            MatLogger.logNormalEvent("Agent configuration reloaded from " + aFile.getName() + ".");

            if (aType == AGENT_CONFIG) {
                // Reset factory.
                AgentFactory.reset();
            }
            if (aType == AGENTAGGREGATOR_CONFIG) {
                // Reset factory.
                AgentAggregatorFactory.reset();
            }
        } catch (XmlPullParserException e) {
            MatLogger.logExceptionalEvent("Agent configuration xml parsing from " + aFile.getName() + " failed.");
            e.printStackTrace();
        } catch (IOException e) {
            MatLogger.logExceptionalEvent("Agent configuration loading from " + aFile.getName() + " failed due to IOEexception.");
            e.printStackTrace();
        }
    }

    /**
     * @param aFile
     */
    public void reloadAllConfiguration(File aFile) {
        // 1. agent configuration.
        try {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aFile)));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.equals("--SEPARATOR--")) {
                    BufferedReader br2 = new BufferedReader(new StringReader(sb.toString()));
                    XmlPullParser xpp = pullparserfactory.newPullParser();
                    xpp.setInput(br2);
                    parseConfiguration(xpp);
                    br2.close();
                    sb = new StringBuffer();
                } else {
                    sb.append(line);
                }
            }
            MatLogger.logNormalEvent("General, Agent and AgentAggregator configuration reloaded from " + aFile.getName() + ".");
            // Reset factories.
            AgentFactory.reset();
            AgentAggregatorFactory.reset();
            System.out.println("lala");

        } catch (XmlPullParserException e) {
            MatLogger.logExceptionalEvent("Agent, General and AgentAggregator configuration xml parsing from " + aFile.getName() + " failed.");
            e.printStackTrace();
        } catch (IOException e) {
            MatLogger.logExceptionalEvent("Agent, General and AgentAggregator configuration loading from " + aFile.getName() + " failed due to IOEexception.");
            e.printStackTrace();
        }
    }

    /**
     * Add an Agent unit to the MatConfig instance.
     *
     * @param aAgentID    The class reference for dynamic loading of the Agent object.
     * @param aProperties
     */
    public void addAgent(String aAgentID, Properties aProperties) {
        agent.put(aAgentID, aProperties);
    }
}