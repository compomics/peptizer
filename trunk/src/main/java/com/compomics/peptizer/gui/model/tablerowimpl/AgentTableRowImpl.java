package com.compomics.peptizer.gui.model.tablerowimpl;

import com.compomics.peptizer.gui.model.AbstractTableRow;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 9-mrt-2007
 * Time: 11:00:30
 */

/**
 * Class description:
 * ------------------
 * AgentTableRowImpl can populate a table with Agent information.
 */
public class AgentTableRowImpl extends AbstractTableRow {
	// Class specific log4j logger for AgentTableRowImpl instances.
	 private static Logger logger = Logger.getLogger(AgentTableRowImpl.class);

    /**
     * The String identifier for the Agent of this TableRow.
     */
    private String iUniqueAgentID = null;


    /**
     * This constructor takes a the fixed Agent identifier as a single argument.
     *
     * @param aAgentID String identifier for the Agent.
     */
    public AgentTableRowImpl(String aAgentID) {
        super();
        iUniqueAgentID = aAgentID;
    }

    /**
     * {@inheritDoc}
     */
    public String getDataImpl(PeptideIdentification aPeptideIdentification, int aPeptideHitNumber) {
        StringBuffer sb = new StringBuffer();
        AgentReport lAgentReport = aPeptideIdentification.getAgentReport(aPeptideHitNumber, iUniqueAgentID);
        if (lAgentReport != null) {
            // Table information.
            Object lReport = lAgentReport.getReport(AgentReport.RK_TABLEDATA);

            // If Abstra
            if (iHTML) {
                sb.append("<html>");
                int lAgentScore;
                Object o = lAgentReport.getReport(AgentReport.RK_RESULT);
                // As of version 1.2 of Peptizer, the Agents reply with AgentVote objects instead of integers.
                // This if clause is included for backward compatibility.

                if (o instanceof AgentVote) {
                    lAgentScore = ((AgentVote) o).score;
                } else {
                    lAgentScore = ((Integer) o).intValue();
                }

                if (lAgentScore > 0) {
                    // If positive, set the result in bold.
                    sb.append("<b>").append(lReport).append("</b>");
                } else if (lAgentScore == 0) {
                    // If neutral , hold the result normal.
                    sb.append(lReport);
                } else if (lAgentScore < 0) {
                    // If true, set the result in Italic.
                    sb.append("<i>").append(lReport).append("</i>");
                }
                sb.append("</html>");
            } else {
                sb.append(lReport);
            }
        } else {
            sb.append("/");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getUniqueTableRowID() {
        return AgentFactory.getInstance().getAgent(iUniqueAgentID).getName();
    }


    /**
     * Returns the unique Agent ID of this AgentTableRow.
     *
     * @return String unique Agent ID. (package & class name)
     */
    public String getUniqueAgentID() {
        return iUniqueAgentID;
    }

    /**
     * Returns a description for the Agent.
     * Use in tooltips and configuration settings.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        Agent lAgent = AgentFactory.getInstance().getAgent(iUniqueAgentID);
        if (lAgent != null) {
            return lAgent.getDescription();
        } else {
            return "Agent not in Factory! No description.";
        }

    }
}
