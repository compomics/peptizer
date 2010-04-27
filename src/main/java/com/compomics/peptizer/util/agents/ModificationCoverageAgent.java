package com.compomics.peptizer.util.agents;

import com.compomics.mascotdatfile.util.interfaces.Modification;
import com.compomics.mascotdatfile.util.mascot.Masses;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.PeptideHitAnnotation;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.peptizer.interfaces.Agent;
import com.compomics.peptizer.util.AgentReport;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.Ion;
import com.compomics.peptizer.util.datatools.implementations.omssa.OmssaPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.AgentVote;
import com.compomics.peptizer.util.enumerator.IonTypeEnum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.omxparser.util.MSHits;

import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 19-sep-2007
 * Time: 11:05:30
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class ModificationCoverageAgent extends Agent {

    /**
     * The name of the modification that has to be traced.
     */
    public static final String MODIFICATION_NAME = "modification";
    public static final String MODIFIED_RESIDUE = "residue";


    public ModificationCoverageAgent() {
        // Init the general Agent settings.
        initialize(new String[]{MODIFICATION_NAME, MODIFIED_RESIDUE});
        SearchEngineEnum[] searchEngines = {SearchEngineEnum.Mascot, SearchEngineEnum.OMSSA};
        compatibleSearchEngine = searchEngines;
    }

    /**
     * This method returns an array of  weighted integers for the PeptideIdentification property the agent has to
     * inspect for. <br></br><b>Implementations must as well initiate and append AgentReport iReport</b>
     *
     * @param aPeptideIdentification PeptideIdentification that has to be inspected.
     * @return AgentVote[] results of the Agent upon inspection on the given PeptideIdentification. Where the array of
     *         size n reflects n confident PeptideHits in a given PeptideIdentification: <ul> <li>[0] gives the
     *         inspection result on PeptideHit 1</li> <li>[1] gives the inspection result on PeptideHit 2</li> <li>[n]
     *         gives the inspection result on PeptideHit n+1</li> </ul> Where the inspection result value stands for:
     *         <ul> <li>+1 if the PeptideIdentification is suspect to the Agent's property.</li> <li>0 if the
     *         PeptideIdentification is a neutral suspect to the Agent's property.</li> <li>-1 if the
     *         PeptideIdentification is opposite to the Agent's property.</li> </ul><br />
     */
    public AgentVote[] inspect(PeptideIdentification aPeptideIdentification) {

        // Localize the Dummy property.
        String lModificationName = ((String) this.iProperties.get(MODIFICATION_NAME)).toLowerCase();
        String lModifiedResidue = ((String) this.iProperties.get(MODIFIED_RESIDUE)).toLowerCase();

        AgentVote[] lScore = new AgentVote[aPeptideIdentification.getNumberOfConfidentPeptideHits()];

        for (int i = 0; i < lScore.length; i++) {

            // Make Agent Report!
            iReport = new AgentReport(getUniqueID());

            String lTableData;
            int lARFFData;

            // 1. Get the nth confident PeptideHit.
            PeptizerPeptideHit lPeptideHit = aPeptideIdentification.getPeptideHit(i);
            int lModificationLocation = isModified(lPeptideHit, lModificationName, lModifiedResidue);
            int lLength = lPeptideHit.getSequence().length();


            // Find whether the N-term or C-term was covered.
            int ntermCount = 0;
            int ctermCount = 0;
            StringBuffer sb = new StringBuffer();

            if (lModificationLocation != -1) {
                Vector<Ion> lMatchedIons = new Vector();
                boolean identifiedByMascot = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
                boolean identifiedByOMSSA = aPeptideIdentification.getPeptideHit(i).getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);
                if (identifiedByMascot) {
                    lMatchedIons = findNeighbourIons(aPeptideIdentification.getSpectrum(), (PeptideHit) lPeptideHit.getOriginalPeptideHit(SearchEngineEnum.Mascot), lModificationLocation, (Masses) aPeptideIdentification.getMetaData(MetaKey.Masses_section), (Parameters) aPeptideIdentification.getMetaData(MetaKey.Parameter_section));
                } else if (identifiedByOMSSA) {
                    lMatchedIons = findNeighbourIons((MSHits) lPeptideHit.getOriginalPeptideHit(SearchEngineEnum.OMSSA), lModificationLocation);
                }
                for (int j = 0; j < lMatchedIons.size(); j++) {
                    Ion lIon = lMatchedIons.elementAt(j);
                    if (lIon.getType() == IonTypeEnum.b) {
                        if (lIon.getNumber() == lModificationLocation - 1) {
                            // Nterm covered!
                            ntermCount++;
                        } else if (lIon.getNumber() == lModificationLocation) {
                            // Cterm covered!
                            ctermCount++;
                        } else {
                            assert false;
                        }
                    } else if (lIon.getType() == IonTypeEnum.y) {
                        if (lIon.getNumber() == lLength - (lModificationLocation - 1)) {
                            // Nterm covered!
                            ntermCount++;
                        } else if (lIon.getNumber() == lLength - lModificationLocation) {
                            // Cterm covered!
                            ctermCount++;
                        } else {
                            assert false;
                        }
                    }
                }
            }


            if (lModificationLocation > 0) {
                if (ctermCount == 0 || ntermCount == 0) {
                    if (ctermCount == 0 && ntermCount == 0) {
                        // Neither b or y ions have been found around the modification!
                        // 
                        lScore[i] = AgentVote.POSITIVE_FOR_SELECTION;
                        lTableData = "None";
                        lARFFData = 1;
                    } else if (lModificationLocation > 1) {
                        // Internal mod.
                        lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                        lTableData = "Semi|" + sb.toString();
                        lARFFData = 0;
                    } else {
                        // Nterm nitro!
                        lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION;
                        lTableData = "Full-term|" + sb.toString();
                        lARFFData = -1;
                    }
                } else {
                    lScore[i] = AgentVote.NEGATIVE_FOR_SELECTION;
                    lTableData = "Full|" + sb.toString();
                    lARFFData = -1;
                }
            } else {
                // Modification was not found
                lScore[i] = AgentVote.NEUTRAL_FOR_SELECTION;
                lTableData = "NA";
                lARFFData = 0;
            }

            // The resulting Inspection score.
            // If shorter then the given length, set to 1.

            // DUMMY implement Agent inspection!

            // Build the report!
            // Agent Result.
            iReport.addReport(AgentReport.RK_RESULT, lScore[i]);

            // TableRow information.
            iReport.addReport(AgentReport.RK_TABLEDATA, lTableData);

            // Attribute Relation File Format
            iReport.addReport(AgentReport.RK_ARFF, lARFFData);

            aPeptideIdentification.addAgentReport(i + 1, getUniqueID(), iReport);
        }
        return lScore;

    }

    /**
     * Returns a Vector with matched neighbour fragmentions. Function dedicated to Mascot result.
     *
     * @param aSpectrum
     * @param aPeptideHit
     * @param aModificationLocation
     * @param aMasses
     * @param aParameters
     * @return
     */
    private Vector<Ion> findNeighbourIons(final PeptizerSpectrum aSpectrum, final PeptideHit aPeptideHit, int aModificationLocation, final Masses aMasses, final Parameters aParameters) {
        Vector<Ion> lResult = new Vector<Ion>();
        PeptideHitAnnotation lPeptideHitAnnotation = aPeptideHit.getPeptideHitAnnotation(aMasses, aParameters, aSpectrum.getPrecursorMZ(), aSpectrum.getChargeString());

        // Get the flanking fragmentions relative to the given location.
        Vector<Ion> lNeighbourIons = new Vector<Ion>();
        FragmentIonImpl tempFragmentIonImpl = null;    // temporary fragment ion used for the conversion into a peptizer ion

        // The modificationlocation variable is 1-based.
        // Hence, the arrays wherein this var is used is 0-based. So here we adapt the modification location var.
        aModificationLocation = aModificationLocation - 1;

        FragmentIonImpl[] ions;
        // Add the flanking b and b++ ions.
        ions = lPeptideHitAnnotation.getBions();
        if (aModificationLocation > 0) {
            // Fence post! If modification at nterm, only b1 is of any value since 'b0' is the precursor..
            tempFragmentIonImpl = ions[aModificationLocation - 1];
            lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.b, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        }
        tempFragmentIonImpl = ions[aModificationLocation];
        lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.b, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));

        ions = lPeptideHitAnnotation.getBDoubleions();
        if (aModificationLocation > 0) {
            tempFragmentIonImpl = ions[aModificationLocation - 1];
            lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.b, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        }
        tempFragmentIonImpl = ions[aModificationLocation];
        lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.b, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));

        // Add the flanking y and y++ ions.
        int lLengh = aPeptideHit.getSequence().length() - 1;

        ions = lPeptideHitAnnotation.getYions();
        tempFragmentIonImpl = ions[lLengh - (aModificationLocation + 1)];
        lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.y, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        if (aModificationLocation > 0) {
            tempFragmentIonImpl = ions[lLengh - aModificationLocation];
            lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.y, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        }

        ions = lPeptideHitAnnotation.getYDoubleions();
        tempFragmentIonImpl = ions[lLengh - (aModificationLocation + 1)];
        lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.y, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        if (aModificationLocation > 0) {
            tempFragmentIonImpl = ions[lLengh - aModificationLocation];
            lNeighbourIons.add(new Ion(tempFragmentIonImpl.getMZ(), tempFragmentIonImpl.getIntensity(), IonTypeEnum.y, tempFragmentIonImpl.getNumber(), SearchEngineEnum.Mascot));
        }

        // Ok, lets now let each of these ions examine whether they occur in the spectrum.
        double lErrorMargin = Double.parseDouble(aParameters.getITOL());
        for (int i = 0; i < lNeighbourIons.size(); i++) {
            Ion lIon = lNeighbourIons.elementAt(i);
            if (lIon.isMatch(aSpectrum.getPeakList(), lErrorMargin)) {
                // Add these in the matching ions.
                lResult.add(lIon);
            }
        }
        return lResult;
    }

    /**
     * Returns a Vector with matched neighbour fragmentions. Function dedicated to Omssa result.
     *
     * @param anOPh                 the Omssa peptide hit
     * @param aModificationLocation
     * @return
     */
    private Vector<Ion> findNeighbourIons(final MSHits anOPh, int aModificationLocation) {
        Vector<Ion> lResult = new Vector<Ion>();

        // The modificationlocation variable is 1-based.
        // Hence, the arrays wherein this var is used is 0-based. So here we adapt the modification location var.
        aModificationLocation = aModificationLocation - 1;

        // Inspect ions found by Omssa
        for (int i = 0; i < anOPh.MSHits_mzhits.MSMZHit.size(); i++) {
            IonTypeEnum ionType = IonTypeEnum.other;
            int lengh = anOPh.MSHits_pepstring.length() - 1;
            switch (anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_ion.MSIonType) {
                case 1: {
                    ionType = IonTypeEnum.b;
                    if (aModificationLocation > 0) {
                        if (anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number == aModificationLocation - 1) {
                            lResult.add(new Ion(anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_mz, ionType, anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number, SearchEngineEnum.OMSSA));
                        } else if (anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number == aModificationLocation) {
                            lResult.add(new Ion(anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_mz, ionType, anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number, SearchEngineEnum.OMSSA));
                        }
                    }
                }
                case 4: {
                    ionType = IonTypeEnum.y;
                    if (aModificationLocation > 0) {
                        if (anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number == lengh - aModificationLocation) {
                            lResult.add(new Ion(anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_mz, ionType, anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number, SearchEngineEnum.OMSSA));
                        } else if (anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number == lengh - (aModificationLocation + 1)) {
                            lResult.add(new Ion(anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_mz, ionType, anOPh.MSHits_mzhits.MSMZHit.get(i).MSMZHit_number, SearchEngineEnum.OMSSA));
                        }
                    }
                }
            }
        }
        return lResult;
    }

    /**
     * Returns a description for the Agent. Use in tooltips and configuration settings. Fill in an agent description.
     * Report on purpose and a minor on actual implementation.
     *
     * @return String description of the Agent.
     */
    public String getDescription() {
        return "<html>Inspects for a Modification property of the peptide. <b>Votes 'Positive_for_selection' if the PeptideHit is modified ( " + this.iProperties.get(MODIFICATION_NAME) + ")</b>. Votes 'Neutral_for_selection' if else.</html>";
    }

    /**
     * This Method Checks the modification status of a PeptideHit, the purpose
     *
     * @param aPh               - PeptideHit upon inspection.
     * @param aModificationName String for
     * @return boolean - true if peptidehit contains the Modification as described by aModificationName.
     */
    private int isModified(PeptizerPeptideHit aPh, String aModificationName, String aModifiedResidue) {
        boolean lModified = false;
        boolean identifiedByMascot = aPh.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.Mascot);
        boolean identifiedByOMSSA = aPh.getAdvocate().getAdvocatesList().contains(SearchEngineEnum.OMSSA);
        if (identifiedByMascot) {
            PeptideHit aMPh = (PeptideHit) aPh.getOriginalPeptideHit(SearchEngineEnum.Mascot);
            int lLocation = 0;
            while (!lModified && lLocation < (aMPh.getSequence().length() + 2)) {
                Modification lMod = aMPh.getModifications()[lLocation];
// If lMod exists, check if it is an acetylation.
                if (lMod != null) {
                    if (lMod.getLocation().toUpperCase().equals(aModifiedResidue.toUpperCase())) {
                        if (lMod.getShortType().toLowerCase().indexOf(aModificationName) >= 0) {
                            return lLocation;
                        }
                    }
                }
                lLocation++;
            }
            // no match!
            return -1;
        } else if (identifiedByOMSSA) {
            OmssaPeptideHit anOPH = (OmssaPeptideHit) aPh;
// Get the id of the modification
            int id = -1;
            Vector<String> modResidues = new Vector();
            for (int i = 0; i < anOPH.modifs.size(); i++) {
                if (anOPH.modifs.get(i).getModName().compareTo(aModificationName) == 0) {
                    id = anOPH.modifs.get(i).getModType().intValue();
                    modResidues = anOPH.modifs.get(i).getModResidues();
                    break;
                }
            }

            // inspect fixed modifications
            String[] decomposedSequence = anOPH.decomposeSequence(anOPH.getSequence());
            for (int j = 0; j < decomposedSequence.length - 1; j++) {
                for (int k = 0; k < modResidues.size(); k++) {
                    // if we have the concerned residue return true
                    if (decomposedSequence[j].compareTo(modResidues.get(k)) == 0) {
                        return j;
                    }
                }
            }

            // inspect variable modifications
            MSHits aPH = (MSHits) anOPH.getOriginalPeptideHit(SearchEngineEnum.OMSSA);
            for (int i = 0; i < aPH.MSHits_mods.MSModHit.size(); i++) {
                // if we have the concerned modification return true
                if (aPH.MSHits_mods.MSModHit.get(i).MSModHit_modtype.MSMod == id) {
                    return aPH.MSHits_mods.MSModHit.get(i).MSModHit_site;
                }
            }
        }
        // no match!
        return -1;
    }
}