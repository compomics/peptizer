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
public class TestDeltaScore extends TestCaseLM {
    DeltaScore iDeltaScore;

    public TestDeltaScore() {
        super("Testscenario TestDeltaScore. ");
        iDeltaScore = (DeltaScore) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.DeltaScore");
        iDeltaScore.setProperty(DeltaScore.DELTA, "10");
    }

    public void testInspect() {
        //query160
        String datFile = getFullFilePath("F015264.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 161;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i)));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        AgentVote[] lResult = iDeltaScore.inspect(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(47.208684873691055, lPeptideIdentification.getAgentReport(1, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("47.21", lPeptideIdentification.getAgentReport(1, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(8.768684873691058, lPeptideIdentification.getAgentReport(3, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("8.77", lPeptideIdentification.getAgentReport(3, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, iDeltaScore.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}