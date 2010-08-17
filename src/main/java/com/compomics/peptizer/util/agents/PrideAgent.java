package com.compomics.peptizer.util.agents;

import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.AgentVote;

/**
 * Created by IntelliJ IDEA.
 * User: vaudel
 * Date: 22.07.2010
 * Time: 15:31:14
 * To change this template use File | Settings | File Templates.
 */
public class PrideAgent extends Agent {

    @Override
    protected AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {
        return new AgentVote[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
