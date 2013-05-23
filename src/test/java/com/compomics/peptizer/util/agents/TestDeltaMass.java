package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.mascotdatfile.util.mascot.factory.MascotDatfileFactory;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.util.junit.TestCaseLM;
import junit.framework.Assert;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Vector;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-aug-2007
 * Time: 16:53:46
 */

/**
 * TestClass description: ------------------ This TestClass was developed to test the Deamidation Agent.
 */
public class TestDeltaMass extends TestCase {
    // Class specific log4j logger for TestDeltaMass instances.
    private static Logger logger = Logger.getLogger(TestDeltaMass.class);

    public TestDeltaMass() {
        super("Testscenario TestDeltaMass. ");
    }

    public void testDeltaMassPPM() {

        Agent lAgent = AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.DeltaMassPPMAgent");

        //query160
        String datFile = TestCaseLM.getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfileInf lMascotDatfile = MascotDatfileFactory.create(datFile, MascotDatfileType.INDEX);
        int lQueryNumber = 1;


        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);


        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "100");
        AgentVote[] lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(1, lResult[0].score);
        Assert.assertEquals(1, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(1, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(-136.58025914779105, lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("-136.5803", lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));

        Assert.assertEquals(322.720322017764, lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("322.7203", lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));


        // Increase the tolerance to 200 ppm.
        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "200");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(1, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);

        // Increase the tolerance to 200 ppm.
        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "500");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(0, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);


    }

    public void testDeltaMassDa() {


        Agent lAgent = AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.DeltaMassDaAgent");

        //query160
        String datFile = TestCaseLM.getFullFilePath("F015264.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 161;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "0.1");
        AgentVote[] lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(1, lResult[0].score);
        Assert.assertEquals(1, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(1, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(-0.292705, lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("-0.2927", lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, lAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));

        Assert.assertEquals(0.691304, lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("0.6913", lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA).toString());
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, lAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));


        // Increase the tolerance to 200 ppm.
        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "0.3");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(1, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);

        // Increase the tolerance to 200 ppm.
        lAgent.setProperty(DeltaMassPPMAgent.TOLERANCE, "0.8");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);
        // First and second id, single DeltaScore - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double DeltaScore - positive test.
        Assert.assertEquals(0, lResult[2].score);

        Assert.assertTrue(lResult.length == 3);
    }


    public void testDeltaMassDaC13() {


        Agent lAgent = AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.DeltaMassDaAgent");

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
        lAgent.setProperty(DeltaMassDaAgent.C13, "FALSE");

        AgentVote[] lResult = lAgent.inspectIfPossible(lPeptideIdentification);

        // Difference is to big! -1.15Da! Score should equal 1.
        Assert.assertEquals(1, lResult[0].score);

        // Second, set the EXACT to TRUe.
        // Now -0.15Da is also evaluated and the score should equal 0.
        lAgent.setProperty(DeltaMassDaAgent.C13, "TRUE");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);

        Assert.assertEquals(0, lResult[0].score);

    }

    public void testDeltaMassPpmC13() {


        Agent lAgent = AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.DeltaMassPPMAgent");

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

        lAgent.setProperty(DeltaMassDaAgent.TOLERANCE, "200");
        lAgent.setProperty(DeltaMassDaAgent.C13, "FALSE");

        AgentVote[] lResult = lAgent.inspectIfPossible(lPeptideIdentification);

        // Difference is to big! -1.15Da! Score should equal 1.
        Assert.assertEquals(1, lResult[0].score);

        // Second, set the EXACT to TRUe.
        // Now -0.15Da is also evaluated and the score should equal 0.
        lAgent.setProperty(DeltaMassDaAgent.C13, "TRUE");
        lResult = lAgent.inspectIfPossible(lPeptideIdentification);

        Assert.assertEquals(0, lResult[0].score);

    }
}
