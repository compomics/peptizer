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
public class TestProlineAgent extends TestCaseLM {
    ProlineAgent iProlineAgent;

    public TestProlineAgent() {
        super("Testscenario TestProlineAgent. ");
        iProlineAgent =
                (ProlineAgent) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.ProlineAgent");
        iProlineAgent.setProperty(ProlineAgent.INTENSITY, "0.40");
    }

    public void testInspect() {
        //query160
        // AcD3-PGGLLLGDVA--P-N<Dam>FEANTTVGR-COOH
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
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i+1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);
        lPeptideIdentification.addMetaData(MetaKey.Masses_section, lMascotDatfile.getMasses());
        lPeptideIdentification.addMetaData(MetaKey.Parameter_section, lMascotDatfile.getParametersSection());

        AgentVote[] lResult = iProlineAgent.inspect(lPeptideIdentification);
        // First and second id, single ProlineAgent - negative test.
        Assert.assertEquals(-1, lResult[0].score);
        Assert.assertEquals(-1, lResult[1].score);
        // Third id, double ProlineAgent - positive test.
        Assert.assertEquals(1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(-1, lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("y11", lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEGATIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("No Proline!", lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));

        // Lower the intensity threshold.
        iProlineAgent.setProperty(ProlineAgent.INTENSITY, "0.2");
        lResult = iProlineAgent.inspect(lPeptideIdentification);
        // First and second id, single ProlineAgent - negative test.
        Assert.assertEquals(-1, lResult[0].score);
        Assert.assertEquals(-1, lResult[1].score);
        // Third id, double ProlineAgent - positive test.
        Assert.assertEquals(-1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(-1, lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("b10 - y11", lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEGATIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(-1, lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("b10", lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEGATIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, iProlineAgent.getUniqueID()).getReport(AgentReport.RK_RESULT));

    }
}