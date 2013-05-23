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
 * TestClass description: ------------------ This TestClass was developed to test the BionCoverage Agent.
 */
public class TestYCoverage extends TestCase {
	// Class specific log4j logger for TestYCoverage instances.
	 private static Logger logger = Logger.getLogger(TestYCoverage.class);
    YCoverage iYCoverage;

    public TestYCoverage() {
        super("Testscenario TestYCoverage. ");
        // YCoverage Agent @ 30%
        iYCoverage = (YCoverage) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.YCoverage");
        iYCoverage.setProperty(YCoverage.PERCENTAGE, "0.30");
    }

    public void testInspect() {
        //query160
        String datFile = TestCaseLM.getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 8;

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


        AgentVote[] lResult = iYCoverage.inspect(lPeptideIdentification);
        // 45% yion coverage - negative agent test.
        Assert.assertEquals(0, lResult[0].score);
        Assert.assertEquals(0, lResult[1].score);
        // 25% yion coverage - positive agent test.
        Assert.assertEquals(1, lResult[2].score);
        Assert.assertTrue(lResult.length == 3);

        Assert.assertEquals(0, lPeptideIdentification.getAgentReport(1, iYCoverage.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("0.45", lPeptideIdentification.getAgentReport(1, iYCoverage.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.NEUTRAL_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iYCoverage.getUniqueID()).getReport(AgentReport.RK_RESULT));
        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(3, iYCoverage.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("0.25", lPeptideIdentification.getAgentReport(3, iYCoverage.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(3, iYCoverage.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}
