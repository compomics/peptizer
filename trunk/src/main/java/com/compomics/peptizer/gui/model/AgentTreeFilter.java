package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 30-okt-2007
 * Time: 10:09:46
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class AgentTreeFilter implements TreeFilter {
	// Class specific log4j logger for AgentTreeFilter instances.
	 private static Logger logger = Logger.getLogger(AgentTreeFilter.class);

    /**
     * The Agents this Filter must check on.
     */
    private ArrayList iAgents;

    /**
     * Constructs a Treefilter implementation that filters PeptideIdentifications on
     * the report of a collection of Agents.
     *
     * @param aAgents ArrayList Agents to check on.
     */
    public AgentTreeFilter(ArrayList aAgents) {
        iAgents = aAgents;
    }

    /**
     * Returns a boolean whether a peptideidentification is allowed to pass the filter.
     *
     * @param aPeptideIdentification PeptideIdentification
     * @return boolean with status
     */
    public boolean pass(PeptideIdentification aPeptideIdentification) {
        boolean pass = false;
        int lNumber = aPeptideIdentification.getNumberOfConfidentPeptideHits();
        for (Iterator lIterator = iAgents.iterator(); lIterator.hasNext();) {
            Agent lAgent = (Agent) lIterator.next();
            for (int i = 0; i < lNumber; i++) {
                AgentReport lReport = aPeptideIdentification.getAgentReport((i + 1), lAgent.getUniqueID());
                if (lReport != null) {
                    Integer lAgentResult = ((AgentVote) lReport.getReport(AgentReport.RK_RESULT)).score;
                    if (lAgentResult == 1) {
                        return true;
                    }
                }
            }
        }
        return pass;
    }
}
