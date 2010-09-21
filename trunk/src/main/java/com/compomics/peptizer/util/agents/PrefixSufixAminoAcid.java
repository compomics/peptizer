package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.AccessionToSequenceMap;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Jul 2, 2010
 * Time: 10:00:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrefixSufixAminoAcid extends Agent{
	// Class specific log4j logger for PrefixSufixAminoAcid instances.
	 private static Logger logger = Logger.getLogger(PrefixSufixAminoAcid.class);


    /**
     * The prefix amino acids of the peptide that are allowed.
     */
    public static final String PREFIX = "prefix";


    /**
     * The suffix amino acids of the peptide that are allowed.
     */
    public static final String SUFFIX = "suffix";

    /**
     * Construct a new Agent that inspects suffix-prefix amino acids to the identified peptide.
     */
    public PrefixSufixAminoAcid() {
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA, SearchEngineEnum.XTandem};
        // Init the general Agent settings.
        initialize(new String[]{PREFIX, SUFFIX});
        compatibleSearchEngine = searchEngines;
    }

    @Override
    protected AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {
         // Localize the prefix property.
        String lPrefixPreference = ((String) this.iProperties.get(PREFIX)).toUpperCase();
        // Localize the suffixproperty.
        String lSuffixPreference = ((String) this.iProperties.get(SUFFIX)).toUpperCase();


        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            String lTableData;
            int lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            String lPeptideSequence = lPeptideHit.getSequence();
            ArrayList lProteinHits = lPeptideHit.getProteinHits();

            String lPeptideSuffixPrefix = null;
            for (int j = 0; j < lProteinHits.size(); j++) {
                PeptizerProteinHit peptizerProteinHit = (PeptizerProteinHit) lProteinHits.get(j);
                String lAccession = ((PeptizerProteinHit) lProteinHits.get(0)).getAccession();
                String lCurrentSuffixPrefix = getSuffixPrefix(lPeptideSequence, lAccession);
                if(lPeptideSuffixPrefix != null){ // multiple proteinhits!!
                    if(lPeptideSuffixPrefix.equals(lCurrentSuffixPrefix)){
                        // OK!
                    }else{
                        lPeptideSuffixPrefix = null;
                        break;
                    }
                }
                lPeptideSuffixPrefix = lCurrentSuffixPrefix;
            }


            if(lPeptideSuffixPrefix == null){
                lTableData = "NULL"; // No valid output.
                lARFFData = 0;
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
            }else{
                lTableData = lPeptideSuffixPrefix;

                if(isMatchToPreferences(lPrefixPreference, lSuffixPreference, lPeptideSuffixPrefix)){
                    lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION; // Both the prefix and suffix fulfill the preferences.
                    lARFFData = -1;
                }else{
                    lScore[i] = AgentVote.POSITIVE_FOR_SELECTION; // One of both prefix, suffix does not fulfill the preferences.
                    lARFFData = 1;
                }
            }


            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            // DUMMY implement Agent inspection!

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

    /**
     * This method 
     * @param lPrefixPreference
     * @param lSuffixPreference
     * @param lPeptideSuffixPrefix
     * @return
     */
    private boolean isMatchToPreferences(String lPrefixPreference, String lSuffixPreference, String lPeptideSuffixPrefix) {
        boolean lPrefixSuffixMatch;
        boolean lPrefixResult = false;
        boolean lSuffixResult = false;

        if(lPrefixPreference.equals("")){ // No preference for the prefix.
            lPrefixResult = true;
        }else{
            char[] lPrefixPreferences = lPrefixPreference.toCharArray();
            for (int j = 0; j < lPrefixPreferences.length; j++) {
                char c = lPrefixPreferences[j];
                if(c == lPeptideSuffixPrefix.charAt(0)){ // Try all preferences untill match. Otherwise, prefixresult remains false.
                    lPrefixResult = true;
                    break;
                }
            }
        }

        if(lSuffixPreference.equals("")){ // No preference for the suffix.
            lSuffixResult= true;
        }else{
            char[] lSuffixPreferences = lSuffixPreference.toCharArray();
            for (int j = 0; j < lSuffixPreferences.length; j++) {
                char c = lSuffixPreferences[j];
                if(c == lPeptideSuffixPrefix.charAt(1)){ // Try all preferences untill match. Otherwise, prefixresult remains false.
                    lPrefixResult = true;
                    break;
                }
            }
        }

        if(lPrefixResult == true && lSuffixResult == true){
            lPrefixSuffixMatch = true; // Both the prefix and suffix fulfill the preferences.
        }else{
            lPrefixSuffixMatch = false; // One of both prefix, suffix does not fulfill the preferences.
        }
        return lPrefixSuffixMatch;
    }

    /**
     * get the prefix and suffix amino acid to the identified peptide.
     * @param lPeptideSequence
     * @param lAccession
     * @return The prefix and the suffix amino acid of the identified peptide.
     * returns null if the peptide could not be mapped unambigously, or if the protein sequence was not found.
     */
    private String getSuffixPrefix(String lPeptideSequence, String lAccession) {
        String lProteinSequence = AccessionToSequenceMap.getInstance().getProteinSequence(lAccession);
        if (lProteinSequence != null) {
            String lResult = null;
            int lIndex = -1;
            while((lIndex = lProteinSequence.indexOf(lPeptideSequence, (lIndex + 1))) != -1){
                char lPrefix;
                char lSuffix;
                if(lIndex == 0){
                    lPrefix = 'X';  // N-terminal peptide.
                }else{
                    lPrefix = lProteinSequence.charAt(lIndex-1);
                }

                if(lIndex + lPeptideSequence.length() == lProteinSequence.length()){
                    lSuffix = 'X';  // C-terminal peptide.
                }else{
                    lSuffix = lProteinSequence.charAt(lIndex + lPeptideSequence.length());
                }

                if(lResult != null){
                    // Peptide is matched multiple times in the protein sequence!!
                    // Verify that the prefix and suffix are equal, otherwise return null.
                    if(lPrefix != lResult.charAt(0) || lSuffix != lResult.charAt(1)){
                        return null;
                    }
                }else{
                    lResult = "" + lPrefix + lSuffix;
                }
            };
            return lResult;
        }else{
            return null;
        }
    }

    @Override
    public String getDescription() {
        return "Inspects the prefix and the suffix amino acid around the identified peptide.";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
