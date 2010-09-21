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
public class TestProteinAccession extends TestCaseLM {
	// Class specific log4j logger for TestProteinAccession instances.
	 private static Logger logger = Logger.getLogger(TestProteinAccession.class);
    ProteinAccession iProteinAccession;

    public TestProteinAccession() {
        super("Testscenario TestProteinAccession. ");
        // ProteinAccession Agent inspects must inspect for double ProteinAccession.
        iProteinAccession =
                (ProteinAccession) AgentFactory.getInstance().getAgent("com.compomics.peptizer.util.agents.ProteinAccession");
    }

    public void testInspect() {
        //q531_p1=0,1430.807388,-0.219940,5,VMEKPSPLLVGR,12,20000000000000,36.44,0002001000000000000,0,0;"Q13283":0:2:13:1,"Q9UN86":0:2:13:2

        String datFile = getFullFilePath("F015264_small.dat");
        if (File.separatorChar == '\\') {
            datFile = datFile.replace("%20", " ");
        }
        MascotDatfile lMascotDatfile = new MascotDatfile(datFile);
        int lQueryNumber = 7;

        Query lQuery = (Query) lMascotDatfile.getQuery(lQueryNumber);

        Vector lPeptideHits = lMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(lQueryNumber);
        Vector lPeptizerPeptideHits = new Vector(lPeptideHits.size());
        for (int i = 0; i < lPeptideHits.size(); i++) {
            lPeptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) lPeptideHits.get(i), i + 1));
        }
        MascotSpectrum mascotSpectrum = new MascotSpectrum(lMascotDatfile.getQuery(lQueryNumber));
        PeptideIdentification lPeptideIdentification = new PeptideIdentification(mascotSpectrum, lPeptizerPeptideHits, SearchEngineEnum.Mascot);

        iProteinAccession.setProperty(ProteinAccession.ACCESSION, "Q13283" + ProteinAccession.DELIM + "Q9UN86");
        AgentVote[] lResult = iProteinAccession.inspect(lPeptideIdentification);
        // First and second id, single ProteinAccession - negative test.
        Assert.assertEquals(1, lResult[0].score);

        iProteinAccession.setProperty(ProteinAccession.ACCESSION, "P10000");
        lResult = iProteinAccession.inspect(lPeptideIdentification);
        Assert.assertEquals(0, lResult[0].score);


        iProteinAccession.setProperty(ProteinAccession.ACCESSION, "Q13283");
        lResult = iProteinAccession.inspect(lPeptideIdentification);
        Assert.assertEquals(1, lResult[0].score);

        iProteinAccession.setProperty(ProteinAccession.ACCESSION, "Q9UN86");
        lResult = iProteinAccession.inspect(lPeptideIdentification);
        Assert.assertEquals(1, lResult[0].score);

        Assert.assertTrue(lResult.length == 1);

        Assert.assertEquals(1, lPeptideIdentification.getAgentReport(1, iProteinAccession.getUniqueID()).getReport(AgentReport.RK_ARFF));
        Assert.assertEquals("Q9UN86", lPeptideIdentification.getAgentReport(1, iProteinAccession.getUniqueID()).getReport(AgentReport.RK_TABLEDATA));
        Assert.assertEquals(AgentVote.POSITIVE_FOR_SELECTION, lPeptideIdentification.getAgentReport(1, iProteinAccession.getUniqueID()).getReport(AgentReport.RK_RESULT));
    }
}
