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
public class TestHomology extends TestCaseLM {
    Homology iHomology;

    public TestHomology() {
        super("Testscenario TestHomology. ");
        iHomology = (Homology) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.Homology");
    }

    public void testInspect() {
        //query160
        String datFile = getFullFilePath("F015264.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 403;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i+1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        AgentVote[] lResult = iHomology.inspect(lPeptideIdentification);
        // First no Homology - negative test.
        Assert.assertEquals(0, lResult[0].score);
        // Second yes Homology - positive test.
        Assert.assertEquals(1, lResult[1].score);
        Assert.assertTrue(lResult.length == 2);

        Assert.assertEquals(0, lPeptideIdentification.getAgentReport(1, iHomology.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("NA", lPeptideIdentification.getAgentReport(1, iHomology.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iHomology.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(2, iHomology.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("homology", lPeptideIdentification.getAgentReport(2, iHomology.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(2, iHomology.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}