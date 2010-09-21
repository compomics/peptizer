package com.compomics.peptizer.util.fileio;

import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.component.PeptideIdentificationPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.enumerator.TempFileEnum;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 14:59:39
 */

/**
 * Class description:
 * This class was developed to save the results of manual validation into a comma
 * separated file.
 */
public class ValidationSaveToPDF extends ValidationSaver {
	// Class specific log4j logger for ValidationSaveToPDF instances.
	 private static Logger logger = Logger.getLogger(ValidationSaveToPDF.class);
    /**
     * This object holds the data that must be written into the csv file.
     * Can be a SelectedPeptideIdentifications or an ArrayList with PeptideIdentifications.
     * TODO- Make a "Saveable interface for these objects!
     * TODO- Improssible for the moment (Nov '07 since we do not want to change the version of these classes untill publication of peptizer.
     */

    /**
     * The target csv file.
     */
    private File iFile = null;

    /**
     * The List of PeptideIdentifications that needs to be selected.
     */
    private ArrayList iPeptideIdentifications = null;

    /**
     * The count Number of Accepted PeptideIdentifications.
     */
    private int iNumberAccepted;
    /**
     * The count Number of Rejected PeptideIdentifications.
     */
    private int iNumberRejected;
    /**
     * The count Number of non validated PeptideIdentifications.
     */
    private int iNumberNotValidated;

    /**
     * If they are stored, this int counts the number of confident not selected id's.
     */
    private int iNumberConfidentNotSelected = 0;

    /**
     * If they are stored, this int counts the number of non-confident id's.
     */
    private int iNumberNonConfident = 0;

    /**
     * This Boolean defines the inclusion of comments to the csv file. Default set to false.
     */
    private boolean iComments = false;

    /**
     * This Boolean defines whether to include Not Selected though confident PeptideIdentifications if availlable.
     */
    private boolean iIncludeConfidentNotSelected = false;

    /**
     * This Boolean defines whether to include non confident PeptideIdentifications if availlable.
     */
    private boolean iIncludeNonConfident = false;
    private Document iDocument;

    /**
     * The target BufferedWriter.
     */
    private PdfWriter iWriter;
    private OutputStream iFos;


    /**
     * This constructor takes a single target File as argument. A BufferedWriter is created on this file. Be sure to
     * close it afterwards.
     *
     * @param aFile that will be used for csv output.
     */
    public ValidationSaveToPDF(File aFile) {
        iFile = aFile;
    }


    /**
     * This constructor takes a single target File as argument. A BufferedWriter is created on this file. Be sure to
     * close it afterwards.
     *
     * @param aFile      that will be used for csv output.
     * @param aTableRows ArrayList with AbstractTableRows whose getData() method will be used to print the csv file.
     * @param aProgress  The Default progressbar for GUI operation.
     */
    public ValidationSaveToPDF(File aFile, ArrayList aTableRows, DefaultProgressBar aProgress) {
        this(aFile);
        iProgress = aProgress;
    }


    /**
     * Save a selection of PeptideIdentifications.
     * If the SelectedPeptideIdentifications has File Handles to NonSelected Confident and NonConfident
     * PeptideIdentifications these can be included as well by setting the relevant booleans
     * iIncludeConfidentNotSelected and iIncludeNonConfident. DEFAULT:false
     *
     * @param aSelectedPeptideIdentifications
     *         SelectedPeptideIdentifications contains a list of PeptideIdentifications.
     */
    private void save(SelectedPeptideIdentifications aSelectedPeptideIdentifications) {
        // Always save the SelectedPeptideIdentifications.
        this.save(aSelectedPeptideIdentifications.getSelectedPeptideIdentificationList());

        // Include Confident non matched peptideidentifications??
        // DEFAULT FALSE
        if (iIncludeConfidentNotSelected) {

            File[] lFiles = TempManager.getInstance().getFiles(aSelectedPeptideIdentifications, TempFileEnum.CONFIDENT_NOT_SELECTED);

            if (lFiles != null) {
                for (int i = 0; i < lFiles.length; i++) {
                    try {
                        File lFile = lFiles[i];
                        ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(lFile));
                        Object o = null;
                        // Loop through object input stream.
                        // I know this is messy, though I do not see any method to check EOF on the ObjectInputStream.o
                        while ((o = ois1.readObject()) != null) {
                            if (o instanceof PeptideIdentification) {
                                PeptideIdentification lPeptideIdentification = (PeptideIdentification) o;
                                lPeptideIdentification.getValidationReport().setComment("CONFIDENT_NOTSELECTED");
                                savePeptideIdentification(lPeptideIdentification);
                                iNumberConfidentNotSelected++;
                            }
                        }
                    } catch (EOFException eof) {
                        // The end of the file is reached, go to the next file ..
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    } catch (ClassNotFoundException e) {
                        logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }

        }
    }

    /**
     * Save a List of PeptideIdentifications.
     *
     * @param aPeptideIdentifications PeptideIdentifications in an ArrayList.
     */
    private void save(ArrayList aPeptideIdentifications) {
        // This is always used when a csv is written. Set of the HTML of the AgentTableRows.

        this.iPeptideIdentifications = aPeptideIdentifications;
        // Set counters.
        iNumberAccepted = 0;
        iNumberNotValidated = 0;
        iNumberRejected = 0;


        try {
            // 2. IDENTIFICATIONS.
            PeptideIdentification lPeptideIdentification = null;
            int lCount = 1;
            for (Object o : aPeptideIdentifications) {
                lPeptideIdentification = (PeptideIdentification) o;
                // a) save every peptide identification.
                savePeptideIdentification(lPeptideIdentification);

                // b) For report statistics.
                boolean isValidated = lPeptideIdentification.getValidationReport().isValidated();
                if (isValidated) {
                    if (lPeptideIdentification.getValidationReport().getResult()) {
                        iNumberAccepted++;
                    } else {
                        iNumberRejected++;
                    }
                } else {
                    iNumberNotValidated++;
                }
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Writes a PeptideIdentification to the active bufferedWriter and using fixed values and the Tablerows.
     *
     * @param aPeptideIdentification PeptideIdentification to save.
     * @throws java.io.IOException Exception while writing.
     */
    public void savePeptideIdentification(PeptideIdentification aPeptideIdentification) throws IOException {

        // A. Fixed print of certain values.

        if (aPeptideIdentification.getNumberOfPeptideHits() > 0) {


            JComponent lPeptideIdentificationPanel = new PeptideIdentificationPanel(aPeptideIdentification).getContentPanel();
            // step 0. Load in a jframe
            JFrame FRAME = new JFrame();
            Dimension lDimension = new Dimension(800, 600);
            lPeptideIdentificationPanel.setPreferredSize(lDimension);
            FRAME.setPreferredSize(lDimension);
            lPeptideIdentificationPanel.validate();
            FRAME.getContentPane().add(lPeptideIdentificationPanel);
            FRAME.pack();
            FRAME.setVisible(false);

            saveComponent(lPeptideIdentificationPanel);

            // step 5: we close the document
            FRAME.dispose();

        }
    }


    /**
     * Sets the boolean whether to include comments in the csv output or not.
     *
     * @param aComments boolean to include comments.
     */
    public void setComments(boolean aComments) {
        iComments = aComments;
    }

    /**
     * {@inheritDoc} Finish the ValidationToCSV by closing the stream and present a report to the user.
     */
    public void finish() {
        // 1. Close the document.
        iDocument.close();

        // 2. Close the stream.
        closeWriter();

        // 3. GUI message to user.
        MatLogger.logExceptionalGUIMessage("Save task report", "Pdf save finished!");

        // 4. Simple log to statuspanel.
        MatLogger.logNormalEvent("Saved task to " + iFile.getPath());
    }


    public void closeWriter() {
        if (iWriter != null) {
            iWriter.flush();
            iWriter.close();
        }
    }


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    public boolean isIncludeConfidentNotSelected() {
        return iIncludeConfidentNotSelected;
    }

    public void setIncludeConfidentNotSelected(boolean aIncludeConfidentNotSelected) {
        iIncludeConfidentNotSelected = aIncludeConfidentNotSelected;
    }

    public boolean isIncludeNonConfident() {
        return iIncludeNonConfident;
    }

    public void setIncludeNonConfident(boolean aIncludeNonConfident) {
        iIncludeNonConfident = aIncludeNonConfident;
    }


    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public Object construct() {
        if (iData == null) {
            MatLogger.logExceptionalEvent("Data not set!" +
                    "\nPlace an data object containing PeptideIdentifications by the setData() method before launching the worker!!");
        } else {
            // For temporary ease, just show an indeterminate progressbar.
            // Check the data origin.
            if (iData instanceof SelectedPeptideIdentifications) {
                // 1. SelectedPeptideIdentifications!
                // Call the save method for SelectedPeptideIdentifications.
                this.save((SelectedPeptideIdentifications) iData);
            } else if (iData instanceof ArrayList) {
                // 2. ArrayList!
                // Call the save method for ArrayList with PeptideIdentifications.
                this.save((ArrayList) iData);
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("\nResults written:\n\t" + (iNumberAccepted + iNumberNotValidated + iNumberRejected) + " selected by profile\n")
                .append(isIncludeConfidentNotSelected() ? "\t" + iNumberConfidentNotSelected + " not selected by profile but confident\n" : "")
                .append(isIncludeNonConfident() ? "\t" + iNumberNonConfident + " not confident" : "");
        return sb.toString();
    }

    public boolean saveComponent(Component aComponent) {


        // step 1: creation of a document-object
        try {

            int w = aComponent.getWidth();
            int h = aComponent.getHeight();
            Rectangle lRectangle = new Rectangle(w, h);
            if (iDocument == null) {
                iDocument = new Document(lRectangle, 5f, 5f, 5f, 5f);
                iFile.createNewFile();
                iFos = new FileOutputStream(iFile);
                iWriter = PdfWriter.getInstance(iDocument, iFos);
                iDocument.open();
            }
            iDocument.newPage();
            iDocument.setPageSize(lRectangle);

            // step 2: creation of the writer
            // step 4: we grab the ContentByte and do some stuff with it

            // we create a fontMapper and read all the fonts in the font directory
            DefaultFontMapper mapper = new DefaultFontMapper();
            FontFactory.registerDirectories();
            // we create a template and a Graphics2D object that corresponds with it
            PdfContentByte cb = iWriter.getDirectContent();
            PdfTemplate tp = cb.createTemplate(w, h);
            Graphics2D g2 = tp.createGraphics(w, h, mapper);

            tp.setWidth(w);
            tp.setHeight(h);

            aComponent.paint(g2);

            g2.dispose();
            cb.addTemplate(tp, 0, 0);

        }
        catch (DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return true;
    }

}
