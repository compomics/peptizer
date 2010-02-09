package com.compomics.peptizer.util.iterators;

import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import com.compomics.mascotdatfile.util.interfaces.QueryToPeptideMapInf;
import com.compomics.mascotdatfile.util.interfaces.Spectrum;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.mascotdatfile.util.mascot.factory.MascotDatfileFactory;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.MetaKey;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotPeptideHit;
import com.compomics.peptizer.util.datatools.implementations.mascot.MascotSpectrum;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-mei-2007
 * Time: 17:10:40
 */

/**
 * Class description: ------------------ This class was developed to
 */
public class DatfileIterator implements PeptideIdentificationIterator {

    /**
     * The MascotDatfile instance of the Iterator.
     */
    private MascotDatfileInf iMascotDatfile = null;

    /**
     * The MascotDatfile type of the Iterator.
     */        
    private MascotDatfileType iMascotDatfileType;

    /**
     * Integer index to track the iterator. This represents the index of the next PeptideIdentification that will be
     * returned when the next method is invoked.
     */
    private int iCountIndex = 0;

    /**
     * The File handle to the current Mascot Results file.
     */
    private File iFile = null;

    /**
     * This boolean flags whether a new MascotDatfile was set. As such, the MascotDatfile instance is only constructed
     * upon calling the hasNext() method instead of during construction of the iterator itself.
     */
    private boolean hasConstructedMascotDatfile = false;

    /**
     * This constructor takes a MascotDatfile instance as a single parameter.
     *
     * @param aFile              File targets the MascotDatfile.
     * @param aMascotDatfileType
     */
    public DatfileIterator(File aFile, final MascotDatfileType aMascotDatfileType) {
        setDatfile(aFile, aMascotDatfileType);
    }

    public DatfileIterator(final MascotDatfileInf aMascotDatfile) {
        iMascotDatfile = aMascotDatfile;
        hasConstructedMascotDatfile = true;
        iCountIndex = 0;
    }

    /**
     * Sets the MascotDatfile instance
     *
     * @param aFile              File targets the MascotDatfile.
     * @param aMascotDatfileType
     */
    public void setDatfile(File aFile, final MascotDatfileType aMascotDatfileType) {
        if (aFile != null) {
            System.out.println("NEW DATFILE \' " + aFile.getName() + "\' INITIATED AT " + new Date(System.currentTimeMillis()) + "(" + aMascotDatfileType + ").");
            iFile = aFile;
            hasConstructedMascotDatfile = false;
            iCountIndex = 0;
            iMascotDatfileType = aMascotDatfileType;
        }
    }

     /**
     * Getter for property 'mascotDatfileType'.
     *
     * @return Value for property 'mascotDatfileType'.
     */
    public MascotDatfileType getMascotDatfileType() {
        return iMascotDatfileType;
    }

    /**
     * {@inheritDoc}
     */
    public Object next() {
        if (hasNext()) {
            // Get the information of the next.
            // QueryList is a Vector, 0 returns Query 1 whereas the QueryToPeptideMap returns Query 1 for if 1 is given as a parameter.
            MascotSpectrum lSpectrum = new MascotSpectrum((Spectrum) iMascotDatfile.getQuery(iCountIndex + 1));
            //Vector lPeptideHits = iMascotDatfile.getQueryToPeptideMap().getAllPeptideHits(iCountIndex + 1);
            QueryToPeptideMapInf lQueryToPeptideMap = iMascotDatfile.getQueryToPeptideMap();
            if (lQueryToPeptideMap != null) {
                Vector mascotPeptideHits = lQueryToPeptideMap.getAllPeptideHits(iCountIndex + 1);
                int hitsNumber = 0;
                if (mascotPeptideHits != null) {
                    hitsNumber = mascotPeptideHits.size();
                }
                Vector peptizerPeptideHits = new Vector(hitsNumber);
                // translation into peptizer object
                for (int i = 0; i < hitsNumber; i++) {
                    peptizerPeptideHits.add(new MascotPeptideHit((PeptideHit) mascotPeptideHits.get(i)));
                }
                // Create a new PeptideIdentification and shift the index.
                PeptideIdentification lPeptideIdentification = new PeptideIdentification(lSpectrum, peptizerPeptideHits, SearchEngineEnum.Mascot);
                lPeptideIdentification.addMetaData(MetaKey.Masses_section, iMascotDatfile.getMasses());
                lPeptideIdentification.addMetaData(MetaKey.Parameter_section, iMascotDatfile.getParametersSection());
                //System.out.println("Q" + (iCountIndex+1));

                iCountIndex = iCountIndex + 1;
                return lPeptideIdentification;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        if (!hasConstructedMascotDatfile) {
            try {
                iMascotDatfile = MascotDatfileFactory.create(iFile.getCanonicalPath(), iMascotDatfileType);
                CurrentMascotDatfile.getInstance().setCurrentMascotDatfile(iMascotDatfile, iMascotDatfile.getFileName());

                /* 090110
                * This boolean indicates whether or not the Query filenames must be transformed.
                * When Mascot Distiller performs the searches out of the raw data (Parameter:Filename ends with '.raw')
                * we loose control on the filename. As such, ms_lims filenames can no longer be mapped onto id's in the mascot result files.
                * If set to true, this boolean will transform the distiller filename into a shorter sensible filename
                * as used in ms_lims.
                */


                hasConstructedMascotDatfile = true;
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        boolean lResult = false;
        if (iMascotDatfile != null) {
            if (iCountIndex < iMascotDatfile.getNumberOfQueries()) {
                lResult = true;
            }
        }
        return lResult;
    }

    /**
     * {@inheritDoc} The number of Queries in a Datfile in this implementation.
     */
    public int estimateSize() {
        return iMascotDatfile.getNumberOfQueries();
    }

    /**
     * {@inheritDoc} The number of Queries left in a Datfile in this implementation.
     */
    public int estimateToDo() {
        return (iMascotDatfile.getNumberOfQueries() - iCountIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
    }


    /**
     * Called by the garbage collector on an object when garbage collection determines that there are no more references
     * to the object. A subclass overrides the <code>finalize</code> method to dispose of system resources or to perform
     * other cleanup. Any exception thrown by the <code>finalize</code> method causes the finalization of this object to
     * be halted, but is otherwise ignored.
     *
     * @throws Throwable the <code>Exception</code> raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Hi I am a DatfileIterator. (" + iFile.getName() + ").";
    }

    /**
     * String representation of the File Iterator.
     *
     * @return String representation of the file iterator.
     */
    public String getCurrentFileDescription() {
        String s = "";
        if (iFile != null) {
            s = iFile.getName();
        } else {
            s = "Mascot dat File";
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public String getGeneralDescription() {
        return getCurrentFileDescription();
    }

}