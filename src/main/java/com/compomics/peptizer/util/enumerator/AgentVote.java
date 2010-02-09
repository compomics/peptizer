package com.compomics.peptizer.util.enumerator;

/**
 * Created by IntelliJ IDEA.
 * User: Kenny
 * Date: 28-mei-2008
 * Time: 14:45:51
 * To change this template use File | Settings | File Templates.
 */

/**
 * Enum class for the Agent votes. An AgentVote is returned upon inspection of a PeptideIdentification by an Agent.
 * There are three types of votes. <ol> <li>The first type of vote positively wants to select the peptideidentification
 * for a particular property. This may be a property related to false positives, otherwise this can also be a property
 * you require to see as for example a phosphorylation site.</li> <li>The second type of vote is neutral towards the
 * selection of the peptideidentification.</li> <li>The third type of vote negatively influences the selection of the
 * peptideidentification for a particular property. This may be a property you expect a peptide to have and no further
 * inspection is req</li> </ol>
 */
public enum AgentVote {

    POSITIVE_FOR_SELECTION(1), NEGATIVE_FOR_SELECTION(-1), NEUTRAL_FOR_SELECTION(0);

    public int score;

    AgentVote(int votingScore) {
        score = votingScore;
    }

    public String toString() {
        return "" + score;
    }
}
