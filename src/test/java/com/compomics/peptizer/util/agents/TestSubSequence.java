package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.util.Vector;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-aug-2007
 * Time: 16:53:46
 */

/**
 * TestClass description: ------------------ This TestClass was developed to test the Deamidation Agent.
 */
public class TestSubSequence extends TestCaseLM {
    SubSequence iSubSequence;

    public TestSubSequence() {
        super("Testscenario TestSubSequence. ");
        iSubSequence = (SubSequence) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.SubSequence");
        iSubSequence.setProperty(SubSequence.SUBSEQUENCE, "LLGDVAPNFEANT");
    }

    public void testInspect() {
        //query162
        String datFile = getFullFilePath("F015264.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 162;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i)));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        AgentVote[] lResult = iSubSequence.inspect(lPeptideIdentification);
        // First and second id, single SubSequence - negative test.
        Assert.assertEquals(1, lResult[0].score);
        Assert.assertEquals(1, lResult[1].score);
        Assert.assertEquals(1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(1, iSubSequence.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("PGGL-LLGDVAPNFEANT-TVGR", lPeptideIdentification.getAgentReport(1, iSubSequence.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iSubSequence.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}