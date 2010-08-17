package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.File;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-aug-2007
 * Time: 16:53:46
 */

/**
 * TestClass description: ------------------ This TestClass was developed to test the BionCoverage Agent.
 */
public class TestBCoverage extends TestCaseLM {
    BCoverage iBCoverage;

    public TestBCoverage() {
        super("Testscenario TestBCoverage. ");
        // BCoverage Agent @ 30%
        iBCoverage = (BCoverage) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.BCoverage");
        iBCoverage.setProperty(BCoverage.PERCENTAGE, "0.30");
    }

    public void testInspect() {
        //query160
        String datFile = getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 4;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);
        lPeptideIdentification.addMetaData(MetaKey.Masses_section, lMascotDatfile.getMasses());
        lPeptideIdentification.addMetaData(MetaKey.Parameter_section, lMascotDatfile.getParametersSection());


        AgentVote[] lResult = iBCoverage.inspect(lPeptideIdentification);
        // 67% bion coverage - negative agent test.
        Assert.assertEquals(0, lResult[0].score);
        // 0% bion coverage - positive agent test.
        Assert.assertEquals(1, lResult[1].score);
        // Last id, no deamidation - negative test.
        Assert.assertTrue(lResult.length == 2);

        Assert.assertEquals(0, lPeptideIdentification.getAgentReport(1, iBCoverage.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("0.67", lPeptideIdentification.getAgentReport(1, iBCoverage.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iBCoverage.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(2, iBCoverage.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("0.0", lPeptideIdentification.getAgentReport(2, iBCoverage.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(2, iBCoverage.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}