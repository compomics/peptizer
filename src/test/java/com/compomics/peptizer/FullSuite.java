package com.compomics.peptizer;

import com.compomics.peptizer.util.agents.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.net.URL;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 28-aug-2007
 * Time: 14:08:45
 */

/**
 * Class description:
 * ------------------
 * This class was developed to group all tests for peptizer.
 */
public class FullSuite extends TestCase {
    public FullSuite() {
        this("Full suite of tests for peptizer project.");
    }

    public FullSuite(String aName) {
        super(aName);


    }

    public static Test suite() {
        TestSuite ts = new TestSuite();

        
        // Ok, start the tests.

        // The TestClasses wich must be run.
        ts.addTest(new TestSuite(TestProteinAccession.class));
        ts.addTest(new TestSuite(TestMultipleConfidentHits.class));
        ts.addTest(new TestSuite(TestSequenceRegexp.class));
        ts.addTest(new TestSuite(TestSubSequence.class));
        ts.addTest(new TestSuite(TestProlineAgent.class));
        ts.addTest(new TestSuite(TestModification.class));
        ts.addTest(new TestSuite(TestHomology.class));
        ts.addTest(new TestSuite(TestSuspectResidue.class));
        ts.addTest(new TestSuite(TestDeltaScore.class));
        ts.addTest(new TestSuite(TestBCoverage.class));
        ts.addTest(new TestSuite(TestBCoverage.class));
        ts.addTest(new TestSuite(TestDeamidation.class));
        ts.addTest(new TestSuite(TestDeltaMass.class));


        return ts;
    }
}