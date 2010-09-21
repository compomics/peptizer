package com.compomics.peptizer.util;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-apr-2007
 * Time: 17:00:33
 */

/**
 * Class description:
 * ------------------
 * This Factory controls all the possible Agents.
 * During instantiation of this Factory a properties file with all possible Agents is read.
 * These Agents are dynamically loaded by class reference.
 * Then they are stored in a private HashMap with unique class references as keys.
 */
public class AgentFactory {
	// Class specific log4j logger for AgentFactory instances.
	 private static Logger logger = Logger.getLogger(AgentFactory.class);
    /**
     * The Agent container. The Agent's class reference serves as a Key to access the Agent itself.
     */
    private HashMap iAgents = null;

    /**
     * Single Agentfactory of singleton pattern.
     */
    private static AgentFactory iAgentFactory = null;

    /**
     * Private constructor for Singleton pattern.
     */
    private AgentFactory() {
        iAgents = new HashMap();
        StringBuffer sb = null;

        // 1. Create a series of reusable fields to create the Agents by the properties file.
        // 1a) Agent object.
        Agent lAgent = null;
        // 1b) The Agent's class reference, is the key of the properties file as well.
        String lAgentClassReference = null;

        // 2. Get all the Agent identifiers.
        String[] lAgentIDs = MatConfig.getInstance().getUniqueAgentIDs();
        for (int i = 0; i < lAgentIDs.length; i++) {

            // Mind this is pretty messy code. If a class reference from the AgentConfiguration is not in the classpath,
            // the Agent will not be in the AgentFactory.
            // When one of the exceptions is thrown, the lFailure boolean is true and a error is printed simply
            // saying the AgentFactory failed to load the specified Agent. Nothing more is done.

            boolean lFailure = true;
            try {
                // Key is the Agent class reference and Agent's unique ID!
                lAgentClassReference = lAgentIDs[i];
                // Dynamically initiate the Agent.
                lAgent = ((Agent) (Class.forName(lAgentClassReference)).newInstance());
                // Store in the Agent container.
                iAgents.put(lAgentClassReference, lAgent);
                // Agent added succesfully, no failure!
                lFailure = false;

            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            }
            if (lFailure) {
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("AgentFactory failed to load the following Agents during initialization:\n");
                }
                sb.append("\n\t-" + lAgentClassReference);
            }
        }
        if (sb != null) {
            MatLogger.logExceptionalEvent(sb.toString());
        }
    }

    /**
     * Returns an Agent by it's unique Class reference String.
     *
     * @param aClassReference String is the unique class reference String of the Agent.
     * @return Agent instance of the class reference. null if not here.
     */
    public Agent getAgent(String aClassReference) {
        return (Agent) iAgents.get(aClassReference);
    }

    /**
     * Returns the singleton instance of the AgentFactory.
     *
     * @return instance of AgentFactory.
     */
    public static AgentFactory getInstance() {
        if (iAgentFactory == null) {
            iAgentFactory = new AgentFactory();
        }
        return iAgentFactory;
    }

    /**
     * Returns Agents that are active.
     *
     * @return active Agents.
     */
    public List getActiveAgents() {
        List result = new ArrayList();
        Iterator iter = iAgents.values().iterator();
        while (iter.hasNext()) {
            Agent lAgent = (Agent) iter.next();
            if (lAgent.isActive()) {
                result.add(lAgent);
            }
        }
        return result;
    }

    /**
     * Returns all availlable Agents.
     *
     * @return all Agents in an array.
     */
    public Agent[] getAllAgents() {
        Agent[] result = new Agent[iAgents.size()];
        Object[] objects = iAgents.values().toArray();
        for (int i = 0; i < objects.length; i++) {
            result[i] = (Agent) objects[i];
        }
        Arrays.sort(result);
        return result;
    }

    /**
     * Resets the AgentFactory.
     * Reloading all Agents from the MatConfig instance.
     */
    public static void reset() {
        iAgentFactory = new AgentFactory();
    }

    /**
     * Sets all Agents activity to False.
     */
    public void setAllActiveFalse() {
        Iterator iter = iAgents.values().iterator();
        while (iter.hasNext()) {
            Agent lAgent = (Agent) iter.next();
            lAgent.setActive(false);
        }
    }

    /**
     * Sets all Agents veto rights to False.
     */
    public void setAllVetoFalse() {
        Iterator iter = iAgents.values().iterator();
        while (iter.hasNext()) {
            Agent lAgent = (Agent) iter.next();
            lAgent.setVeto(false);
        }
    }

    /**
     * Sets all Agents observing booleans False.
     */
    public void setAllInforming() {
        Iterator iter = iAgents.values().iterator();
        while (iter.hasNext()) {
            Agent lAgent = (Agent) iter.next();
            lAgent.setInforming(false);
        }
    }


    public HashMap getUnusedAgents() {
        HashMap result = new HashMap();

        // Fetch all the agents in the agent_complete.xml file through the MatConfig instance.
        HashMap lAllAgents = null;

        try {
            InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("conf/agent_complete.xml"));
            MatConfig config = MatConfig.getInstance();
            XmlPullParser xpp = config.getPullparserfactory().newPullParser();
            xpp.setInput(reader);

            int eventType = xpp.getEventType();
            boolean validated = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        eventType = xpp.next();
                        break;

                    case XmlPullParser.START_TAG:
                        String start = xpp.getName();
                        if (start != null) {
                            if (start.equals("config")) {
                                validated = true;
                                eventType = xpp.next();
                            } else if (start.equals("agents")) {
                                lAllAgents = config.processAgents(xpp);
                            }
                        } else {
                            xpp.next();
                        }
                        break;

                    default:
                        eventType = xpp.next();
                        break;
                }
            }


        } catch (XmlPullParserException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

        // Get all the agents currently active in the AgentFactory.
        Object[] lActiveAgentArray = AgentFactory.getInstance().getActiveAgents().toArray();
        HashSet lActiveAgentSet = new HashSet();

        for (int i = 0; i < lActiveAgentArray.length; i++) {
            Agent lActiveAgent = (Agent) lActiveAgentArray[i];
            lActiveAgentSet.add(lActiveAgent.getUniqueID());
        }

        Iterator iter = lAllAgents.keySet().iterator();
        while (iter.hasNext()) {
            String lAgentID = (String) iter.next();
            // Only retain those from agent_complete.xml that are not in the table yet.
            if (!lActiveAgentSet.contains(lAgentID)) {
                result.put(lAgentID, lAllAgents.get(lAgentID));
            }
        }

        return result;
    }
}
