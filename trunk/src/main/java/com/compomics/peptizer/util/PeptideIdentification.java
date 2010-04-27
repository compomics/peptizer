package com.compomics.peptizer.util;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.datatools.Advocate;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.io.Serializable;
import java.util.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 27-feb-2007
 * Time: 10:43:56
 */

/**
 * Class description: ------------------ This class was developed to wrap a MS/MS spectrum and its identifications.
 */
public class PeptideIdentification implements Comparable, Serializable {

    /**
     * The MS/MS Spectrum.
     */
    private PeptizerSpectrum iSpectrum = null;

    /**
     * The PeptideHits.
     */
    private Vector<PeptizerPeptideHit> iPeptideHits = null;

    /**
     * The metadata on this PeptideIdentification in a HashMap.
     */
    private HashMap iMetaData = null;

    /**
     * The AgentReports on this PeptideIdentification. The Array contains HashMaps for each PeptideHit that could have been
     * reported by the Agent. The HashMap keys are the Agent String ID's, values are AgentReport instances.
     */
    private HashMap[] iAgentReports = null;

    /**
     * The number of confident peptidehits in this spectrum.
     */
    private int iNumberOfConfidentPeptideHits = 0;

    /**
     * The ValidationReport on this PeptideIdentification.
     */
    private ValidationReport iValidationReport = null;

    /**
     * The name for this PeptideIdentification.
     */
    private String iName;

    /**
     * The alpha double used to calculate the number of ConfidentPeptideHits.
     */
    private double iAlpha = -1;

    /**
     * advocates of the peptidehits of this identification
     */
    private Advocate advocate;

    /**
     * This constructor takes a Spectrum and a single PeptideHit as parameters.
     *
     * @param aSpectrum   of the PeptideIdentification.
     * @param aPeptideHit of the PeptideIdentification.
     */
    public PeptideIdentification(PeptizerSpectrum aSpectrum, PeptizerPeptideHit aPeptideHit, SearchEngineEnum aSearchEngineEnum) {
        iSpectrum = aSpectrum;
        iPeptideHits = new Vector<PeptizerPeptideHit>();
        iPeptideHits.add(aPeptideHit);
        advocate = new Advocate(aSearchEngineEnum, 0);
    }

    /**
     * This constructor takes a Spectrum and a Vector with PeptideHits as parameters.
     *
     * @param aSpectrum    of the PeptideIdentification
     * @param aPeptideHits of the PeptideIdentification.
     */
    public PeptideIdentification(PeptizerSpectrum aSpectrum, Vector<PeptizerPeptideHit> aPeptideHits, SearchEngineEnum aSearchEngineEnum) {
        iSpectrum = aSpectrum;
        iPeptideHits = aPeptideHits;
        advocate = new Advocate(aSearchEngineEnum, 0);
    }

    /**
     * Returns the Spectrum of the PeptideIdentification
     *
     * @return Spectrum of the PeptideIdentification.
     */
    public PeptizerSpectrum getSpectrum() {
        return iSpectrum;
    }

    /**
     * Returns the PeptideHit(s) of the PeptideIdentification.
     *
     * @return Vector with the PeptideHit(s).
     */
    public Vector<PeptizerPeptideHit> getPeptideHits() {
        return iPeptideHits;
    }

    /**
     * Returns PeptideHit by INDEX. <b>0' will return the first PeptideHit. '1' the second etc..</b>'
     *
     * @param aIndex of the PeptideHit.
     * @return PeptideHit at aIndex.
     */
    public PeptizerPeptideHit getPeptideHit(int aIndex) {
        PeptizerPeptideHit p;
        p = (PeptizerPeptideHit) iPeptideHits.get(aIndex);
        return p;
    }

    /**
     * Returns the best Peptidehit.
     *
     * @return PeptideHit number one of the Spectrum. <br>Returns null if no PeptideHits were made from this spectrum.
     */
    public PeptizerPeptideHit getBestPeptideHit() {
        if (iPeptideHits != null) {
            return iPeptideHits.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns Array with (default) confident PeptideHits.
     *
     * @return PeptizerPeptideHit[] with confident PeptideHits.
     */
    public PeptizerPeptideHit[] getConfidentPeptideHits() {
        int length = getNumberOfConfidentPeptideHits();
        PeptizerPeptideHit[] lPeptideHits = new PeptizerPeptideHit[length];
        for (int i = 0; i < lPeptideHits.length; i++) {
            lPeptideHits[i] = iPeptideHits.get(i);
        }
        return lPeptideHits;
    }

    /**
     * Returns the number of Peptidehits in this Spectrum. Including the not-confident ones!! Use
     * getNumberOfConfidentPeptideHits() otherwise!
     *
     * @return int number of PeptideHits.
     */
    public int getNumberOfPeptideHits() {
        if (iPeptideHits == null) {
            return 0;
        } else {
            return iPeptideHits.size();
        }
    }

    /**
     * Returns the number of (default) confident PeptideHits.
     *
     * @return int number of confident PeptideHits.
     */
    public int getNumberOfConfidentPeptideHits() {

        // Confidence alpha from general.properties.
        double lConfigAlpha = Double.parseDouble(MatConfig.getInstance().getGeneralProperty("DEFAULT_MASCOT_ALPHA"));

        // Since this method is used a lot, do calculations when necessary. Two situations,

        // a) Alpha is still -1, this is the first time! Calculate!
        // b) lConfigAlpha is no different then iAlpha. This means the calculation has been performed previously and must not be redone.
        if (iAlpha == -1 || lConfigAlpha != iAlpha) {
            calculateNumberOfConfidentPeptideHits();
        }
        return iNumberOfConfidentPeptideHits;
    }

    /**
     * Calculate the number of confident peptidehits according to the MatConfig instance DEFAULT_MASCOT_ALPHA value. Mind this
     * method is only used when necessairy.
     */
    private void calculateNumberOfConfidentPeptideHits() {
        // If there are PeptideHits and the iNumberOfConfidentPeptideHits is equal to 0, try to calculate the number of confident hits.
        if (iPeptideHits != null) {
            if (iNumberOfConfidentPeptideHits == 0) {
                for (int i = 0; i < iPeptideHits.size(); i++) {
                    PeptizerPeptideHit lPeptideHit = iPeptideHits.elementAt(i);
                    if (lPeptideHit.scoresAboveThreshold()) {
                        iNumberOfConfidentPeptideHits++;
                    } else {
                        break;
                    }
                }
            }
        }
    }


    /**
     * Add a metadata entry.
     *
     * @param aKey   of the entry.
     * @param aValue of the entry.
     */

    public void addMetaData(Object aKey, Object aValue) {
        if (iMetaData == null) {
            iMetaData = new HashMap();
        }
        this.iMetaData.put(aKey, aValue);
    }

    /**
     * Tells if a key is in the metadata
     *
     * @param aKey for the meta data.
     * @return boolean true if yes, false if no
     */

    public boolean metaDataContainsKey(Object aKey) {
        if (this.iMetaData != null) {
            return this.iMetaData.containsKey(aKey);
        } else {
            return false;
        }
    }

    /**
     * Returns the value object that corresponds to the parameter Key.
     *
     * @param aKey for the meta data.
     * @return aValue
     */
    public Object getMetaData(Object aKey) {
        return this.iMetaData.get(aKey);
    }

    public HashMap getAllMetaData() {
        return iMetaData;
    }

    /**
     * Add an AgentReport.
     *
     * @param aPeptideHitNumber PeptideHitNumber of this PeptideIdentification. (1 for first peptidehit, 2 for the second,
     *                          etc.)
     * @param aAgentUniqueID    String identifier for the Agent that is adding the report.
     * @param aAgentReport      of the Agent.
     */
    public void addAgentReport(int aPeptideHitNumber, String aAgentUniqueID, AgentReport aAgentReport) {
        // Lazy cache the HashMap array.
        if (iAgentReports == null) {
            iAgentReports = new HashMap[this.getNumberOfConfidentPeptideHits()];
        }
        // Lazy cache the HashMaps.
        if (iAgentReports[aPeptideHitNumber - 1] == null) {
            iAgentReports[aPeptideHitNumber - 1] = new HashMap();
        }
        iAgentReports[aPeptideHitNumber - 1].put(aAgentUniqueID, aAgentReport);
    }

    /**
     * Returns an AgentReport instance that was generated by an Agent during its analysis of a PeptideIdentification.
     *
     * @param aPeptideHitNumber PeptideHitNumber of this PeptideIdentification.(1 for first peptidehit)
     * @param aAgentID          String identifier for the Agent.
     * @return AgentReport instance with the reports of the Agent. <b>returns null if not found.</b>
     */
    public AgentReport getAgentReport(int aPeptideHitNumber, String aAgentID) {
        if (iAgentReports != null) {
            if (iAgentReports[aPeptideHitNumber - 1] != null) {
                return (AgentReport) iAgentReports[aPeptideHitNumber - 1].get(aAgentID);
            }
        }
        return null;
    }

    /**
     * Returns all AgentReports of a given PeptideHit. (1 for first peptidehit)
     *
     * @param aPeptideHitNumber identification of the spectrum.
     * @return Set with all availlable AgentReports.
     */
    public List getAgentReports(int aPeptideHitNumber) {
        if (iAgentReports != null) {
            if (iAgentReports[aPeptideHitNumber - 1] != null) {
                return new ArrayList(iAgentReports[aPeptideHitNumber - 1].values());
            }
        }
        return null;
    }

    /**
     * Returns a List with AgentID Strings of the Agent that reported their inspection on this PeptideIdentifcation.
     *
     * @return List with AgentID Strings. can be null if no agents used.
     */
    public List getAgentIDList() {
        if (iAgentReports != null) {
            if (iAgentReports[0] != null) {
                return new ArrayList(iAgentReports[0].keySet());
            }
        }
        return null;
    }

    /**
     * Returns ValidationReport for this PeptideIdentification. <i>Check it's status to see whether it has been validated
     * or not.</i>
     *
     * @return ValidationReport for this PeptideIdentification.
     */
    public ValidationReport getValidationReport() {
        if (iValidationReport == null) {
            iValidationReport = new ValidationReport();
        }
        return iValidationReport;
    }

    /**
     * Returns wheteher this PeptideIdentification has been validated or not.
     *
     * @return boolean with validation status of the PeptideIdentification.
     */
    public boolean isValidated() {
        if (iValidationReport == null) {
            return false;
        } else {
            return iValidationReport.isValidated();
        }
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.<p>
     * In the foregoing description, the notation <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> according to
     * whether the value of <i>expression</i> is negative, zero or positive.
     * The implementor must ensure <tt>sgn(x.compareTo(y)) == -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.
     * (This implies that <tt>x.compareTo(y)</tt> must throw an exception iff <tt>y.compareTo(x)</tt> throws an
     * exception.)<p>
     * The implementor must also ensure that the relation is transitive: <tt>(x.compareTo(y)&gt;0 &amp;&amp;
     * y.compareTo(z)&gt;0)</tt> implies <tt>x.compareTo(z)&gt;0</tt>.<p>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt> implies that <tt>sgn(x.compareTo(z)) ==
     * sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.<p>
     * It is strongly recommended, but <i>not</i> strictly required that <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.
     * Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates this condition should
     * clearly indicate this fact.  The recommended language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
     *         specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     */
    public int compareTo(Object o) {
        if (o instanceof PeptideIdentification) {
            PeptideIdentification p = (PeptideIdentification) o;
            if (this.isValidated() == p.isValidated()) {
                // They are equal.
                return 0;
            } else if (this.isValidated()) {
                // They are not equal, and this is Validated - Object o is not validated. Return 1.
                return 1;
            } else {
                // They are not equal, and this is not Validated - Object o is validated. Return -1.
                return -1;
            }
        }
        return 0;
    }

    /**
     * Returns the search engine(s) which has/have been used
     *
     * @return Advocate iAdvocates
     */
    public Advocate getAdvocate() {
        return advocate;
    }

    /**
     * Returns a name for this PeptideIdentification.
     *
     * @return String iName
     */
    public String getName() {
        if (iName == null) {
            return iSpectrum.getName();
        } else {
            return iName;
        }
    }

    /**
     * Sets a name for this PeptideIdentifcation
     *
     * @param aName String
     */
    public void setName(String aName) {
        iName = aName;
    }

    /**
     * Returns the Validated Peptidehit that was accepted. If none were validated or the peptideidentification was
     * rejected, the first peptidehit is returned.
     *
     * @return lPeptideHit The accepted peptidehit, else the first peptidehit.
     */
    public PeptizerPeptideHit getValidatedPeptideHit() {
        int lPeptideHitNumber = getValidationReport().getCorrectPeptideHitNumber();
        if (lPeptideHitNumber < 1) {
            lPeptideHitNumber = 1;
        }
        return getPeptideHit(lPeptideHitNumber - 1);
    }

    /**
     * Will fuse two peptideIdentifications made by different search engines on the same spectrum
     */

    public void fuse(PeptideIdentification aPeptideIdentification) {
        advocate.addAdvocate(aPeptideIdentification.getAdvocate());
        boolean found;
        for (int i = 0; i < aPeptideIdentification.getPeptideHits().size(); i++) {
            found = false;
            for (int j = 0; j < iPeptideHits.size(); j++) {
                if (iPeptideHits.get(j).isSameAs(aPeptideIdentification.getPeptideHits().get(i))) {
                    iPeptideHits.get(j).fuse(aPeptideIdentification.getPeptideHits().get(i));
                    HashMap newMetaData = aPeptideIdentification.getAllMetaData();
                    if (newMetaData != null) {
                        Iterator it = newMetaData.keySet().iterator();
                        while (it.hasNext()) {
                            Object newKey = it.next();
                            addMetaData(newKey, newMetaData.get(newKey));
                        }
                    }
                    found = true;
                }
            }
            if (!found) {
                iPeptideHits.add(aPeptideIdentification.getPeptideHits().get(i));
            }
        }
    }

}