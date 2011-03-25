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
import org.apache.log4j.Logger;

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
public class TestModification extends TestCaseLM {
    // Class specific log4j logger for TestModification instances.
    private static Logger logger = Logger.getLogger(TestModification.class);
    ModificationAgent iModification;

    public TestModification() {
        super("Testscenario TestModification. ");
        iModification =
                (ModificationAgent) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.ModificationAgent");
        iModification.setProperty(ModificationAgent.MODIFICATION_NAME, "ox");
        iModification.setProperty(ModificationAgent.EXACT, "false");

    }

    public void testInspect() {
        //query160
        String datFile = getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 5;

        // 1) AcD3-VLIK<AcD3K*>EFR-COOH
        // 2) NH2-QELK<AcD3K*>EM<Mox*>R-COOH

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        AgentVote[] lResult = iModification.inspect(lPeptideIdentification);
        // First and second id, single Modification - negative test.
        Assert.assertEquals(0, lResult[0].score);
        // Third id, double Modification - positive test.
        Assert.assertEquals(1, lResult[1].score);
        Assert.assertTrue(lResult.length == 2);

        Assert.assertEquals("ox", iModification.getProperty(ModificationAgent.MODIFICATION_NAME));
        Assert.assertEquals(0, lPeptideIdentification.getAgentReport(1, iModification.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("NA", lPeptideIdentification.getAgentReport(1, iModification.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iModification.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(2, iModification.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("ox", lPeptideIdentification.getAgentReport(2, iModification.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(2, iModification.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }

    public void testInspectExactMatch() {
        //query160
        String datFile = getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 5;

        // 1) AcD3-VLIK<AcD3K*>EFR-COOH
        // 2) NH2-QELK<AcD3K*>EM<Mox*>R-COOH

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);
        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        // Set to substring matching.
        iModification.setProperty(ModificationAgent.EXACT, "false");
        iModification.setProperty(ModificationAgent.MODIFICATION_NAME, "ox");
        AgentVote[] lResult;


        // Second hit - Mox Modification  - substring match to 'ox'.
        lResult = iModification.inspect(lPeptideIdentification);
        Assert.assertEquals(1, lResult[1].score);

        // Set to exact matching.
        iModification.setProperty(ModificationAgent.EXACT, "true");

        lResult = iModification.inspect(lPeptideIdentification);
        // Second hit , Mox Modification - no exact match to 'ox'.
        Assert.assertEquals(0, lResult[1].score);

    }
}
