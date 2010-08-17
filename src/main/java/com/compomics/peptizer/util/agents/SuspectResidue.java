package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-okt-2007
 * Time: 14:49:39
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class SuspectResidue extends Agent {

    /**
     * This Parameter must define AminoAcid residue(s if separated by DELIM) that point to a missed cleavage if they
     * occur inside the peptide sequence.
     */
    public static final String SUSPECT = "sites";


    /**
     * This character separates distinct protein accessions.
     */
    private static final String DELIM = ";";


    public SuspectResidue() {
        // Init the general Agent settings.
        initialize(SUSPECT);
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for. <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification. Where the array of
     *         size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0] gives the
     *         inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li> <li>[n]
     *         gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands for:
     *         <ul> <li>+1 if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the
     *         PeptideIdentification is a neutral suspect to the Agent's property.</li> <li>-1 if the
     *         PeptideIdentification is opposite to the Agent's property.</li> </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // Localize the Dummy property.
        ArrayList lArrayList = new ArrayList();
        StringTokenizer st = new StringTokenizer((String) this.iProperties.get(SUSPECT), DELIM);
        while (st.hasMoreElements()) {
            lArrayList.add(((String) st.nextElement()).toUpperCase());
        }

        Object[] lSites = lArrayList.toArray();

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            String lSequence = lPeptideHit.getSequence().substring(0, lPeptideHit.getSequence().length() - 1);
            boolean boolSuspectResidue = false;
            String lSuspectResidue = "";
            for (Object lSite : lSites) {
                if (lSequence.indexOf(lSite.toString()) != -1) {
                    lSuspectResidue += lSite;
                    boolSuspectResidue = true;
                }
            }
            if (boolSuspectResidue) {
                lTableData = lSuspectResidue;
                lARFFData = "1";
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else {
                lTableData = "NA";
                lARFFData = "0";
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

            // DUMMY implement Agent inspection!

            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, new Integer(lARFFData));

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;

    }

    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for missed cleavages in a peptide. <b>Votes 'Positive_for_selection' if the sequence contains an amino acid that must have been cleaved.  Multiple amino acids can be entered when delimited by '" + DELIM + "' (current:" + this.iProperties.get(SUSPECT) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }
}
