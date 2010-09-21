package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 28-aug-2007
 * Time: 14:54:25
 */

/**
 * Class description:
 * ------------------
 * This class was developed to provide static methods concerning Serialization of object of peptizer.
 */
public class PeptizerSerialization {
	// Class specific log4j logger for PeptizerSerialization instances.
	 private static Logger logger = Logger.getLogger(PeptizerSerialization.class);

    public PeptizerSerialization() {
        // empty
    }

    /**
     * Attempts to serialize the Objects of a given Vector to a ObjectOutputStream to aFile.
     *
     * @param aVector Vector with Objects to be serialized.
     * @param aFile   File target.
     * @throws java.io.IOException by an ObjectOutputStream
     */
    public static void serializeVectorToFile(Vector aVector, File aFile) throws IOException {
        if (aVector.size() > 0) {
            int lCount = 0;

            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(new FileOutputStream(aFile));
            for (int i = 0; i < aVector.size(); i++) {
                oos.writeObject(aVector.elementAt(i));
                oos.flush();
                lCount++;
            }
            oos.close();
            MatLogger.logNormalEvent("Serialized " + lCount + " PeptideIdentifications to " + aFile.getCanonicalPath() + ".");
        } else {
            MatLogger.logExceptionalEvent("Vector is empty.");
        }
    }


    /**
     * Attempts to serialize the Objects of a given Vector to a ObjectOutputStream to aFile.
     *
     * @param aArrayList ArrayList with PeptideIdentifications to be serialized.
     * @param aFile      File target.
     * @throws java.io.IOException by an ObjectOutputStream
     */
    public static void serializePeptideIdentificationsToFile(ArrayList aArrayList, File aFile) throws IOException {
        if (aArrayList.size() > 0) {
            int lCount = 0;
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(new FileOutputStream(aFile));
            for (Object o : aArrayList) {
                if (o instanceof PeptideIdentification) {
                    oos.writeObject(o);
                    oos.flush();
                    lCount++;
                } else {
                    MatLogger.logExceptionalEvent("Received instance of " + o.getClass() + " for serialization while expecting PeptideIdentification!!");
                }
            }
            oos.close();
            MatLogger.logNormalEvent("Serialized " + lCount + " PeptideIdentifications to " + aFile.getCanonicalPath() + ".");
        } else {
            MatLogger.logExceptionalEvent("ArrayList is empty.");
        }
    }

    /**
     * Attempts to read serialized Objects of a given file by an ObjectInputStream.
     *
     * @param aFile File source.
     * @return ArrayList with PeptideIdentifications from the Serialized File.
     * @throws java.io.IOException    by an ObjectInputStream.
     * @throws ClassNotFoundException by an ObjectInputStream.
     */
    public static ArrayList readSerializedPeptideIdentifications(File aFile) throws IOException, ClassNotFoundException {
        ArrayList result = null;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(aFile));
        Object o = null;
        int lCount = 0;
        // Loop through object input stream.
        // I know this is messy, though I do not see any method to check EOF on the ObjectInputStream.o
        try {
            while ((o = ois.readObject()) != null) {
                if (o instanceof PeptideIdentification) {
                    if (result == null) {
                        result = new ArrayList();
                    }

                    result.add(o);
                    lCount++;
                }
            }
        } catch (EOFException eof2) {
            // Do nothing.
        }
        MatLogger.logNormalEvent("Read " + lCount + " Serialized PeptideIdentifications from " + aFile.getCanonicalPath() + ".");
        return result;
    }

}
