package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.junit.TestCaseLM;
import junit.framework.Assert;

import java.io.File;
import java.util.Vector;
import junit.framework.TestCase;

/**
 * This class is a
 */
public class TestC13Agent extends TestCase {

    public TestC13Agent() {
        super("Testscenario Test. ");
    }


    public void testDeltaMassDaC13() {


        Agent lAgent = AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.C13Agent");

        //Query300 is corrupted!!
        // normal mass error = -0.15, changed this to -1.15Da to test the EXACT Agent.
        String datFile = TestCaseLM.getFullFilePath("F015264.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 300;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        lAgent.setProperty(DeltaMassDaAgent.TOLERANCE, "0.5");

        AgentVote[] lResult = lAgent.inspectIfPossible(lPeptideIdentification);

        // Difference is to big! -1.15Da! Score should equal 1.
        Assert.assertEquals(1, lResult[0].score);

    }
}
