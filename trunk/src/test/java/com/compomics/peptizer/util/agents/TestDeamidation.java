package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.peptizer.InitializeJUnit;
import com.compomics.peptizer.util.AgentFactory;
import com.compomics.peptizer.util.AgentReport;
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
 * TestClass description: ------------------ This TestClass was developed to test the Deamidation Agent.
 */
public class TestDeamidation extends TestCaseLM {
    Deamidation iDeamidation;

    public TestDeamidation() {
        super("Testscenario TestDeamidation. ");
        InitializeJUnit.initialize();
    }

    public void testInspect() {

        // Deamidation Agent inspects must inspect for double deamidation.
        iDeamidation = (Deamidation) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.Deamidation");
        iDeamidation.setProperty(Deamidation.COUNT, "2");

        //query160
        String datFile = getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 2;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        AgentVote[] lResult = iDeamidation.inspect(lPeptideIdentification);
        // First and second id, single deamidation - negative test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // Third id, double deamidation - positive test.
        Assert.assertEquals(1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(1, iDeamidation.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("1 dam", lPeptideIdentification.getAgentReport(1, iDeamidation.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iDeamidation.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(2, lPeptideIdentification.getAgentReport(3, iDeamidation.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("2 dam's", lPeptideIdentification.getAgentReport(3, iDeamidation.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, iDeamidation.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}