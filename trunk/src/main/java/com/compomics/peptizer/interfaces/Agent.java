package com.compomics.peptizer.interfaces;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.fileio.MatLogger;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Kenni
 * Date: 2-jun-2006
 * Time: 10:55:50
 */

/**
 * This interface must be implemted by all the Agents, capable of analysis of a specific property of a Peptide
 * identification.
 */
public abstract class Agent implements Comparable {

    /**
     * Fixed reply
     */

    /**
     * Activity status of the Agent.
     */
    private boolean iActive = false;

    /**
     * This boolean can have veto rights to enclose a PeptideHit in the aggregator.
     */
    private boolean iVeto = false;

    /**
     * This boolean indicates the informing status. A spectator Agent inspects, hence its inspection is not taken
     * into account upon Aggregation.
     */
    protected boolean iInforming = false;

    /**
     * Nice String identifier for this Agent, (gui name!)
     */
    private String iName = null;

    /**
     * This Agentreport is created by every Agent during the inspection.
     */
    protected AgentReport iReport = null;

    /**
     * This Map contains dynamic properties of an Agent.
     */
    protected Properties iProperties = new Properties();

    /**
     * List of the compatible search engines
     */
    protected SearchEngineEnum[] compatibleSearchEngine;

    /**
     * This method calls the inspect method if the peptide identification is from a compatible search engine identification file.
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return the result of the inspection.
     */
    public AgentVote[] inspectIfPossible(PeptideIdentification aPeptideIdentification) {
        try {
            for (SearchEngineEnum searchEngine : compatibleSearchEngine) {
                if (aPeptideIdentification.getAdvocate().getAdvocatesList().contains(searchEngine)) {
                    return inspect(aPeptideIdentification);
                }
            }
            return inspect(aPeptideIdentification);
        } catch (Exception e) {
            iReport = new AgentReport(getUniqueID());
            AgentVote[] lAgentVotes = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];
            for (int i = 0; i < lAgentVotes.length; i++) {
                lAgentVotes[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                iReport.addReport(AgentReport.RK_RESULT, lAgentVotes[i]);
                iReport.addReport(AgentReport.RK_TABLEDATA, "-");
                iReport.addReport(AgentReport.RK_ARFF, "no data");
                aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
            }
            return lAgentVotes;
        }
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for. <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return int[] results of the Agent upon inspection on the given PeptideIdentification. Where the array of size n
     *         reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0] gives the inspection
     *         result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li> <li>[n] gives the
     *         inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands for: <ul> <li>+1
     *         if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the PeptideIdentification
     *         is a neutral suspect to the Agent's property.</li> <li>-1 if the PeptideIdentification is opposite to the
     *         Agent's property.</li> </ul><br />
     */
    protected abstract AgentVote[] inspect(PeptideIdentification aPeptideIdentification);

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
        Properties prop = MatConfig.getInstance().getAgentProperties(this.getUniqueID());
        this.setName(prop.getProperty("name"));
        this.setActive(Boolean.valueOf(prop.getProperty("active")));
        this.setVeto(Boolean.valueOf(prop.getProperty("veto")));

    }

    /**
     * Private handling of a specific Agent property.
     *
     * @param aSpecificProperty
     */
    private void initProperty(final String aSpecificProperty) {
        try {
            this.iProperties.put(aSpecificProperty, MatConfig.getInstance().getAgentProperties(this.getUniqueID()).getProperty(aSpecificProperty));
        } catch (NullPointerException npe) {
            this.iProperties.put(aSpecificProperty, "NA");
            this.setActive(false);
            this.setVeto(false);
            MatLogger.logExceptionalGUIMessage("Missing Parameter!!", "Parameter " + aSpecificProperty + " for Agent \"" + this.getName() + "\" is null!!\nExit..");
        }
    }


    /**
     * Returns a String identifier for the Agent.(Package name!)
     *
     * @return String identifier for the Agent.
     */
    public String getUniqueID() {
        return this.getClass().getName();
    }

    /**
     * Returns a nice String identifier for the Agent for use in gui environment.
     *
     * @return String identifier for the Agent (nice).
     */
    public String getName() {
        return iName;
    }

    /**
     * Returns the activity of the Agent.
     *
     * @return boolean with the activity of the Agent.
     */
    public boolean isActive() {
        return iActive;
    }

    /**
     * Returns the veto rights of the Agent. If the Agent the veto set true, then the peptidehit must be aggregated when
     * inspect score results +1.
     *
     * @return boolean representing the veto rights from Agent.
     */
    public boolean hasVeto() {
        return iVeto;
    }

    /**
     * Sets a nice String identifier for the Agent for use in gui environment.
     *
     * @param aName nice String identifier for the Agent.
     */
    protected void setName(String aName) {
        iName = aName;
    }

    /**
     * Sets the activity of the Agent.
     *
     * @param aActive boolean activity of the Agent.
     */
    public void setActive(boolean aActive) {
        iActive = aActive;
    }

    /**
     * Sets the veto status of the Agent.
     *
     * @param aVeto boolean veto status of the Agent.
     */
    public void setVeto(boolean aVeto) {
        iVeto = aVeto;
    }

    /**
     * Gets the informing status of the Agent.
     * An informing Agent vote is not used upon aggregation. Hence, the inspection is shown upon validation.
     *
     * @return boolean iInforming
     */
    public boolean isInforming() {
        return iInforming;
    }

    /**
     * Sets the informing status of the Agent.
     * An informing Agent vote is not used upon aggregation. Hence, the inspection is shown upon validation.
     *
     * @param aInforming
     */
    public void setInforming(final boolean aInforming) {
        iInforming = aInforming;
    }

    /**
     * Returns a Properties instance with Parameter Keys that can be set.
     *
     * @return Properties with dynamic Parameter keys.
     */
    public Properties getProperties() {
        return iProperties;
    }


    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public abstract String getDescription();

    /**
     * Returns a description for the Agent wherein HTML tags have been removed.
     *
     * @return String description of the Agent without HTML tags.
     */
    public String getTagFreeDescription() {
        String s = getDescription();
        int lOpenTagIndex = -1;
        int lCloseTagIndex = -1;
        while ((lOpenTagIndex = s.indexOf('<')) != -1) {
            lCloseTagIndex = s.indexOf('>', lOpenTagIndex);
            s = s.replaceAll(s.substring(lOpenTagIndex, lCloseTagIndex + 1), "");
        }
        return s;
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
     * (x.equals(y))</tt>.  Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates
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
        Agent other = (Agent) o;
        return this.getName().compareTo(other.getName());
    }

    /**
     * Change an existing general property.
     *
     * @param aKey   String key of property.
     * @param aValue String value of property.
     */
    public void setProperty(String aKey, String aValue) {
        if (iProperties.get(aKey) != null) {
            // Only modify existing properties!
            iProperties.put(aKey, aValue);
        }
    }

    /**
     * Change an existing general property.
     *
     * @param aKey String key of property. Can be null if aKey does not exist!
     */
    public Object getProperty(String aKey) {
        return iProperties.get(aKey);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object. The name of the Agent.
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Returns a boolean representing wether the agent can process the data from this search engine.
     *
     * @return a boolean
     */
    public boolean isCompatible(SearchEngineEnum aSearchEngineEnum) {
        for (int i = 0; i < compatibleSearchEngine.length; i++) {
            if (aSearchEngineEnum == compatibleSearchEngine[i]) return true;
        }
        return false;
    }

    /**
     * Returns an array of compatible searchEngines
     *
     * @return a boolean
     */
    public SearchEngineEnum[] getCompatibleEngines() {
        return compatibleSearchEngine;
    }
}
