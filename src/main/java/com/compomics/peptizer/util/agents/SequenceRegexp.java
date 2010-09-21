package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 19-sep-2007
 * Time: 11:32:06
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class SequenceRegexp extends Agent {
	// Class specific log4j logger for SequenceRegexp instances.
	 private static Logger logger = Logger.getLogger(SequenceRegexp.class);

    public static final String SEQUENCEREGEXP = "regular expression";

    private static Pattern pattern = null;
    private static String regexp = "";

    /**
     * This empty private constructor can only be accessed from a static method getInstance.
     */
    public SequenceRegexp() {

        // Init the general Agent settings.
        initialize(SEQUENCEREGEXP);
        SearchEngineEnum[] searchEngines = {};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * {@inheritDoc} This Agent inspects the Sequence property of a PeptideIdentification. If iSequence occurs in the
     * first rank PeptideHit. If the sequence is matched, inspection will return 1. If the sequence is not matched,
     * inspection will return 0.
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        String lRegExpSequence = (String) this.iProperties.get(SEQUENCEREGEXP);

        if (!regexp.equals(lRegExpSequence)) {
            createPattern(lRegExpSequence.toUpperCase());
        }

        // The resulting Inspection score.
        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];


        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            String lTableData = "";
            int lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);

            // 2. Check if sequence matches.
            // Create a matcher
            Matcher matcher = pattern.matcher(lPeptideHit.getSequence());
            // Try to find a match and construct results.
            if (matcher.find()) {
                lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                lTableData = lPeptideHit.getSequence().substring(matcher.start(), matcher.end());
                lARFFData = 1;
            } else {
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = 0;
            }

            // Agent Result.
            // TableRow information.
            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lARFFData);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;
    }

    private void createPattern(String aRegexp) {
        regexp = aRegexp;
        pattern = Pattern.compile(regexp);
    }

    /**
     * Sets the partial sequence that the PeptideHit must contain.
     *
     * @param aSequence String partial sequence.
     */
    public void setSequence(String aSequence) {
        aSequence = aSequence.toUpperCase();
        this.iProperties.put(SEQUENCEREGEXP, aSequence);
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        String s =
                "<html>Inspects whether the peptide sequence matches a given regular expression(" + regexp + "). <b>Votes 'Positive_for_selection' if the regular expression is matched.(" + this.iProperties.get(SEQUENCEREGEXP) + ")</b>. Votes 'Neutral_for_selection' if not.</html>";
        return s;
    }
}
