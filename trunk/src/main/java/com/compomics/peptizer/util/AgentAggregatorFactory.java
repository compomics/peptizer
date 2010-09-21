package com.compomics.peptizer.util;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.util.fileio.MatLogger;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-aug-2007
 * Time: 14:53:27
 */

/**
 * Class description:
 * ------------------
 * This class was developed to control the AgentAggregators in peptizer.
 * Based on Aggregator.xml, a collection of AgentAggregators are constructed and can be accessed trough
 * this singleton instance.
 */
public class AgentAggregatorFactory {
	// Class specific log4j logger for AgentAggregatorFactory instances.
	 private static Logger logger = Logger.getLogger(AgentAggregatorFactory.class);

    /**
     * The Map with Aggregators.
     * Keys: Class reference.
     * Values: AgentAggregator implementations.
     */
    private HashMap iAgentAggregators = null;

    /**
     * The singleton AgentAggregatorFactory.
     */
    private static AgentAggregatorFactory iAgentAggregatorFactory = null;

    /**
     * Private constructor for Singleton pattern.
     */
    private AgentAggregatorFactory() {

        iAgentAggregators = new HashMap();
        StringBuffer sb = null;

        // 1. Create a series of reusable fields to create the AgentAggregators by the properties file.
        // 1a) Aggregator object.
        AgentAggregator lAgentAggregator = null;
        // 1b) The AgentAggregator's class reference, is the key of the properties file as well.
        String lAgentAggregatorClassReference = null;

        // 2. Get all the Agent identifiers.
        String[] lAgentAggregatorIDs = MatConfig.getInstance().getUniqueAgentAggregatorIDs();
        for (int i = 0; i < lAgentAggregatorIDs.length; i++) {

            // Mind this is pretty messy code. Just as in the AgentFactory, class references from MatConfig are retrieved.
            // If one of them is not in the classpath, the AgentAggregator will not be in the AgentAggregatorFactory.
            //
            // If one of the following exceptions is thrown, a boolean(lFailure) is set true and further on an error is printed simply
            // saying the AgentFactory failed to load the specified AgentAggregator(s).
            //
            // Nothing more is done.

            boolean lFailure = true;
            try {
                // Key is the Agent class reference and Agent's unique ID!
                lAgentAggregatorClassReference = lAgentAggregatorIDs[i];
                // Dynamically initiate the Agent.
                lAgentAggregator = (AgentAggregator) Class.forName(lAgentAggregatorClassReference).newInstance();
                // Store in the Agent container.
                iAgentAggregators.put(lAgentAggregatorClassReference, lAgentAggregator);
                // Agent added succesfully, no failure!
                lFailure = false;

            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            }
            if (lFailure) {
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("AgentAggregatorFactory failed to load the following AgentAggregators during initialization:\n");
                }
                sb.append("\n\t-" + lAgentAggregatorClassReference);
            }
        }
        if (sb != null) {
            MatLogger.logExceptionalEvent(sb.toString());
        }
    }

    /**
     * Returns the AgentAggregatorFactory instance.
     * Retrieve Aggregator by getAgentAggregator(String UniqueID) method.
     *
     * @return AgentAggregatorFactory instance.
     */
    public static AgentAggregatorFactory getInstance() {
        if (iAgentAggregatorFactory == null) {
            iAgentAggregatorFactory = new AgentAggregatorFactory();
        }
        return iAgentAggregatorFactory;
    }

    /**
     * Returns the AgentAggregator with class reference aUniqueID.
     *
     * @param aUniqueID class reference of the returning AgentAggregator.
     * @return AgentAggregator corresponding class reference aUniqueID.
     */
    public AgentAggregator getAgentAggregator(String aUniqueID) {
        if (iAgentAggregators != null) {
            return (AgentAggregator) iAgentAggregators.get(aUniqueID);
        } else {
            MatLogger.logExceptionalEvent("AgentAggregator with class reference: \"" + aUniqueID + "\" is not availlably in the AgentAggregatorFatory!!");
            return null;
        }
    }

    /**
     * Returns all availlable AgentAggregators.
     *
     * @return AgentAggregator[]
     */
    public AgentAggregator[] getAgentAggregators() {
        AgentAggregator[] result = new AgentAggregator[iAgentAggregators.size()];
        Object[] objects = iAgentAggregators.values().toArray();
        for (int i = 0; i < objects.length; i++) {
            result[i] = (AgentAggregator) objects[i];
        }
        Arrays.sort(result);
        return result;
    }

    /**
     * Resets the AgentAggregatorFactory.
     * Reloading all AgentAggregators from the MatConfig instance.
     */
    public static void reset() {
        iAgentAggregatorFactory = new AgentAggregatorFactory();
    }

}
