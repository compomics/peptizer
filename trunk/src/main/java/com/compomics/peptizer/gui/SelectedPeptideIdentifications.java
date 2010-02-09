package com.compomics.peptizer.gui;

import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:34:59
 */

/**
 * Class description:
 * ------------------
 * The SelectedPeptideIdentifications is the underlying datasource for the mat GUI.
 * This object is created during instantiation of the Mediator
 * and contains the datasource that is used by the
 * into the Tree, Table and TabbedPane structure.
 */
public class SelectedPeptideIdentifications {

    /**
     * The MetaKay for the Iterator description.
     */
    public static final Integer MK_ITERITOR_DESCRIPTION = new Integer(1);

    /**
     * The MetaKay that returns Integer reflecting the number of confident selected peptideidentifications.
     */
    public static final Integer MK_NUMBER_CONFIDENT = new Integer(2);
    /**
     * The MetaKay that returns Integer reflecting the number of non confident selected peptideidentifications.
     */
    public static final Integer MK_NUMBER_NOT_CONFIDENT = new Integer(3);
    /**
     * The MetaKey that returns Integer reflecting the number of spectra that had no peptide identifications.
     */
    public static final Integer MK_NUMBER_NO_IDENTIFICATION = new Integer(4);

    /**
     * This ArrayList is the information storage for the AgentAggregator.
     */
    private ArrayList iPeptideIdentifications = null;
    /**
     * This HashMap contains metadata on the identifications.
     * The keys are static final integers on this class.
     */
    private HashMap iIdentificationsMetaData = null;

    /**
     * This is the resulting object of an aggregator. The parameters that will be necessairy are still unknown.
     */

    public SelectedPeptideIdentifications() {
        iPeptideIdentifications = new ArrayList();
    }

    /**
     * This method returns the number of distinct spectra there are in the Datamap.
     *
     * @return int Number of spectra in the results.
     */

    public int getNumberOfSpectra() {
        return iPeptideIdentifications.size();
    }

    /**
     * Get the Spectrum instance at position aIndex.
     *
     * @param aIndex integer index of the Spectrum instance.
     * @return Spectrum instance
     */

    public PeptizerSpectrum getSpectrum(int aIndex) {

        PeptizerSpectrum s;
        s = ((PeptideIdentification) iPeptideIdentifications.get(aIndex)).getSpectrum();
        return s;

    }

    /**
     * Get the PeptideIdentification instance at position aIndex.
     *
     * @param aIndex integer index of the PeptideIdentification instance - zero based.
     * @return PeptideIdentifciation
     */

    public PeptideIdentification getPeptideIdentification(int aIndex) {

        PeptideIdentification p;
        p = (PeptideIdentification) iPeptideIdentifications.get(aIndex);
        return p;

    }

    /**
     * Interface to store PeptideIdentification results from the aggregator.
     *
     * @param aPeptideIdentification PeptideIdentification to be saved in the results.
     */

    public void addResult(PeptideIdentification aPeptideIdentification) {

        iPeptideIdentifications.add(aPeptideIdentification);

    }

    /**
     * Getter to retrieve information from the Meta data hashmap.
     *
     * @param aKey Defined as Final Static Integer MetaKeys (MK-) on SelectedPeptideIdentifications.
     * @return Object Value. <b>null if key does not exist!</b>
     */

    public Object getMeta(Object aKey) {
        if (iIdentificationsMetaData == null) {
            return null;
        } else {
            return this.iIdentificationsMetaData.get(aKey);
        }
    }

    /**
     * Add Metadata into the HashMap
     *
     * @param aKey   Defined as Final Static Integer MetaKeys (MK-) on SelectedPeptideIdentifications.
     * @param aValue Object for Key aKey
     */

    public void setMeta(Object aKey, Object aValue) {
        if (iIdentificationsMetaData == null) {
            iIdentificationsMetaData = new HashMap();
        }
        iIdentificationsMetaData.put(aKey, aValue);
    }

    /**
     * Returns a List with the selected PeptideIdentifications.
     *
     * @return ArrayList with the selected PeptideIdentifications.
     */
    public ArrayList getSelectedPeptideIdentificationList() {
        return iPeptideIdentifications;
    }

    /**
     * Returns the number of spectra that were allready validated.
     *
     * @return int number of spectra that were allready validated.
     */
    public int getNumberOfValidatedSpectra() {
        int lCount = 0;
        for (Object lPeptideIdentification : iPeptideIdentifications) {
            // Raise the count if the identification is validated.
            if (((PeptideIdentification) lPeptideIdentification).isValidated()) {
                lCount++;
            }
        }
        return lCount;
    }

    public void setNumberedNamesToPeptideIdentifications() {
        for (int i = 0; i < iPeptideIdentifications.size(); i++) {
            ((PeptideIdentification) iPeptideIdentifications.get(i)).setName("Spectrum  " + (i + 1));
        }
    }
}
