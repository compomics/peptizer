package com.compomics.peptizer.interfaces;/**
 * Created by IntelliJ IDEA.
 * User: Kenni
 * Date: 2-jun-2006
 * Time: 10:32:08
 * To change this template use File | Settings | File Templates.
 */

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentAggregationResult;
import com.compomics.peptizer.util.fileio.MatLogger;

import java.util.Collection;
import java.util.Properties;

/**
 * This interface must be implemented by classes that want to score PeptideHits by their properties.
 */
public abstract class AgentAggregator implements Comparable {

    /**
     * The Agent's used by the AgentAggregator.
     */
    protected Collection<Agent> iAgentsCollection = null;

    /**
     * The properties of the Aggregator.
     */
    protected Properties iProperties = new Properties();

    /**
     * The name for the AgentAggregator.
     */
    private String iName = null;


    /**
     * Initialize the Agent as in setting the name, the activity status and the veto status from the agent configuration
     * file as well as specfic properties.
     *
     * @param aSpecificProperties String array with a Property identifier for each specific property of the Agent.
     */
    protected void initialize(String[] aSpecificProperties) {
        initialize();

        // Specific properties.
        for (int i = 0; i < aSpecificProperties.length; i++) {
            String lSpecificProperty = aSpecificProperties[i];
            initProperty(lSpecificProperty);
        }
    }

    /**
     * Initialize the Agent as in setting the name, the activity status and the veto status from the agent configuration
     * file as well as specfic properties.
     *
     * @param aSpecificProperty String with a single Property identifier for the Agent.
     */
    protected void initialize(String aSpecificProperty) {
        initialize();
        initProperty(aSpecificProperty);
    }

    /**
     * Initializes the general properties of an Agent without any specific properties.
     */
    protected void initialize() {
        // General properties.
        Properties prop = MatConfig.getInstance().getAggregatorProperties(this.getUniqueID());
        this.setName(prop.getProperty("name"));

    }

    /**
     * Private handling of a specific Agent property.
     *
     * @param aSpecificProperty
     */
    private void initProperty(final String aSpecificProperty) {
        try {
            this.iProperties.put(aSpecificProperty, MatConfig.getInstance().getAggregatorProperties(this.getUniqueID()).getProperty(aSpecificProperty));
        } catch (NullPointerException npe) {
            MatLogger.logExceptionalGUIMessage("Missing Parameter!!", "Parameter " + aSpecificProperty + " for Agent \"" + this.getName() + "\" is null!!\nExit..");
            System.exit(0);
        }
    }


    /**
     * Matches the PeptideIdentification against the AgentAggregator's values by a series of independent Agents.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be matched.
     * @return </dl>AgentAggregationResult if the PeptideIdentifications suits the profile values.<br> <dl>
     *         <dt>AgentAggregationResult.MATCH<dd> The PeptideIdentification is <b>confident</b> and is a <b>true
     *         match</b> against the profile. <dt>AgentAggregationResult.NON_MATCH<dd> The PeptideIdentification is
     *         <b>confident</b> and is a <b>false match</b> against the profile. <dt>AgentAggregationResult.NON_CONFIDENT
     *         <dd> The PeptideIdentification is <b>not confident</b>. <dt><dd>
     */
    public abstract AgentAggregationResult match(PeptideIdentification aPeptideIdentification);

    /**
     * Returns a String identifier for the Agent.(Package name!)
     *
     * @return String identifier for the Agent.
     */
    public String getUniqueID() {
        return this.getClass().getName();
    }

    /**
     * Returns the Collection with Agent's that is being used by the Aggregator.
     *
     * @return Collection with Agents.
     */
    public Collection<Agent> getAgentsCollection() {
        return iAgentsCollection;
    }


    /**
     * Set the Agent's to be used by the Aggregator.
     *
     * @param aAgentsCollection Collection with Agents.
     */
    public void setAgentsCollection(Collection<Agent> aAgentsCollection) {
        iAgentsCollection = aAgentsCollection;
    }

    /**
     * Return a String descritiption of the AgentAggregator.
     *
     * @return String describing the AgentAggregator.
     */
    public abstract String getDescription();

    /**
     * Return a String description with HTML formatting of the Aggregator.
     *
     * @return String with HTML describing the Aggregator - for use in GUI!
     */
    public abstract String getHTMLDescription();

    /**
     * Sets the name for the AgentAggregator.
     *
     * @param aName String name for the AgentAggregator.
     */
    public void setName(String aName) {
        iName = aName;
    }

    /**
     * Returns the name for the AgentAggregator.
     *
     * @return aName String name for the AgentAggregator.
     */
    public String getName() {
        return iName;
    }


    /**
     * Returns the Properties of the AgentAggregator.
     *
     * @return Properties instance for the AgentAggregator.
     */
    public Properties getProperties() {
        return iProperties;
    }

    public String toString() {
        return getName();
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object. <p>The implementor must
     * ensure <tt>sgn(x.compareTo(y)) == -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This implies
     * that <tt>x.compareTo(y)</tt> must throw an exception iff <tt>y.compareTo(x)</tt> throws an exception.) <p>The
     * implementor must also ensure that the relation is transitive: <tt>(x.compareTo(y)&gt;0 &amp;&amp;
     * y.compareTo(z)&gt;0)</tt> implies <tt>x.compareTo(z)&gt;0</tt>. <p>Finally, the implementor must ensure that
     * <tt>x.compareTo(y)==0</tt> implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.
     * <p>It is strongly recommended, but <i>not</i> strictly required that <tt>(x.compareTo(y)==0) ==
     * (x.equals(y))</tt>. Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended language is "Note: this class has a natural
     * ordering that is inconsistent with equals." <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical <i>signum</i> function, which is defined to
     * return one of <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i> is
     * negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this object.
     */
    public int compareTo(Object o) {
        AgentAggregator other = (AgentAggregator) o;
        return this.getName().compareTo(other.getName());
    }
}
