package com.compomics.peptizer.util;

import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 11:31:23
 */

/**
 * Class description:
 * ------------------
 * This class was developed to save the results of an Agent in a PeptideIdentification instance.
 * The class extends a Hashmap.
 * The AgentReport has defines static final ReportKeys ("RK_*") that serve as keys in this map.
 */
public class AgentReport extends HashMap {

    // These Report Key can be used in the AgentReport

    /**
     * Report on the score weight factor, if used.
     */
    public static final String RK_WEIGHT = "Score weight";

    /**
     * Report on the result itself.
     */
    public static final String RK_RESULT = "Result notation";

    /**
     * Report on the table visualisation.
     */
    public static final String RK_TABLEDATA = "Table result notation";

    /**
     * Report on the table visualisation.
     */
    public static final String RK_ARFF = "Attribute Relation File Format notation";

    /**
     * String identifier for this Agent.
     */
    private String iAgentID;

    /**
     * The constructor takes a unique Agent identifier as a single argument.
     *
     * @param aAgentID String Identifier of this Agent.
     */
    public AgentReport(String aAgentID) {
        super(4, 1);
        iAgentID = aAgentID;
    }


    /**
     * This getter returns the String identifier of the originating Agent.
     *
     * @return String identifier of the Agent.
     */
    public String getAgentID() {
        return iAgentID;
    }

    /**
     * This method appends report information into a AgentReport object.
     *
     * @param aReportKey   Identifyies the report type. <b>Uses hard typed variables 'RK.*' on the Agent interface</b>.
     * @param aReportValue The report of the called reportKey.
     */
    public void addReport(Object aReportKey, Object aReportValue) {
        this.put(aReportKey, aReportValue);
    }

    /**
     * This method appends report information into a AgentReport object.
     *
     * @param aReportKey Identifyies the report type. <b>Uses hard typed variables 'RK.*' on the Agent interface and Agent implementations.</b>.
     * @return Object aReportValue The report of the called reportKey.
     */
    public Object getReport(Object aReportKey) {
        return this.get(aReportKey);
    }
}
