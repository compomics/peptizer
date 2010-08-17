package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 1-okt-2007
 * Time: 17:30:41
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class ProteinAccession extends Agent {

    /**
     * This String serves a static final String for protein accessions to be matched.
     */
    public static final String ACCESSION = "accession";

    /**
     * This character separates distinct protein accessions.
     */
    public static final String DELIM = ";";

    public ProteinAccession() {
        // Init the general Agent settings.
        initialize(ACCESSION);
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

        boolean isAccessionMatch = false;

        // Localize the boundary properties.
        String lTargetAccessions = (String) (this.iProperties.get(ACCESSION));
        ArrayList lTarget = new ArrayList();
        StringTokenizer st = new StringTokenizer(lTargetAccessions, DELIM);
        while (st.hasMoreElements()) {
            lTarget.add(((String) st.nextElement()).toUpperCase());
        }

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());
            String lTableData;
            String lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            lTableData = "";
            lARFFData = "";

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            ArrayList lProteinHits = lPeptideHit.getProteinHits();
            // Outer loop, iterate over proteinhits.
            for (Iterator lIterator = lProteinHits.iterator(); lIterator.hasNext();) {
                PeptizerProteinHit lProteinHit = (PeptizerProteinHit) lIterator.next();
                String lPeptideHitAccession = lProteinHit.getAccession().toUpperCase();
                // Inner loop, itarate over distinct target accessions.
                for (Iterator lIterator1 = lTarget.iterator(); lIterator1.hasNext();) {
                    String lTargetAccession = ((String) lIterator1.next());
                    if (lPeptideHitAccession.indexOf(lTargetAccession) != -1) {
                        isAccessionMatch = true;
                        lTableData = lTargetAccession;
                    }
                }

            }

            if (isAccessionMatch) {
                // Peptide StartPosition in ProteinHit is less then lower limit, score -1.
                lARFFData = "1";
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
            } else {
                // Peptide StartPosition in ProteinHit is more then high limit, score 1.
                lTableData = "/";
                lARFFData = "0";
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }

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
        return "<html>Inspects for the protein origin of the peptidehit. <b>Votes 'Positive_for_selection' if the peptide origins from the protein with accession '" + this.iProperties.get(ACCESSION) + "'</b>. Votes 'Neutral_for_selection' if else. Multiple accessions can be entered when delimited by '" + DELIM + "'.</html>";
    }
}