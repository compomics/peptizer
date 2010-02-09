package com.compomics.peptizer.util.enumerator;

/**
 * This enum is the output of an AgentAggregator.
 */
public enum AgentAggregationResult {

    /**
     * This state indicates that the AgentAggregator concludes that a PeptideIdentification <b>matches</b> the profile
     * defined by the Agents.
     */
    MATCH(),

    /**
     * This state indicates that the AgentAggregator concludes that a PeptideIdentification <b>does not match</b> the
     * profile defined by the Agents.
     */
    NON_MATCH(),

    /**
     * This state indicates that the AgentAggregator did not inspect the PeptideIdentification properly as it was not
     * confident.
     */
    NON_CONFIDENT(),

    /**
     * This state indicates that the AgentAggregator did not find any PeptideHits for the spectrum.
     */
    NO_IDENTIFICATION();

}
