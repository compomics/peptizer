package com.compomics.peptizer.gui.component;

import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: May 13, 2009
 * Time: 4:38:19 PM
 * <p/>
 * This class
 */
public class PeptideIdentificationPanel {
	// Class specific log4j logger for PeptideIdentificationPanel instances.
	 private static Logger logger = Logger.getLogger(PeptideIdentificationPanel.class);
    private JLabel lblSequence;
    private JLabel lblScore;
    private JLabel lblThreshold;
    private JLabel lblConfidence;
    private JPanel content;
    private JPanel jpanInfo;
    private JPanel jpanSpectrum;
    private JPanel jpanSequence;
    private JLabel lblProtein;
    private JPanel spectrumContainer;
    private JLabel lblPrecursorMass;
    private JLabel lblCharge;
    private JLabel lblMassError;
    private PeptideIdentification iPeptideIdentification;
    private PeptizerPeptideHit iPeptideHit;

    public PeptideIdentificationPanel(PeptideIdentification aPeptideIdentification) {
        iPeptideIdentification = aPeptideIdentification;
        iPeptideHit = iPeptideIdentification.getValidatedPeptideHit();

        $$$setupUI$$$();

        // Set the spectrum filename into the border of the spectrumpanel.
        spectrumContainer.setBorder(BorderFactory.createTitledBorder(null, iPeptideIdentification.getSpectrum().getName(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(spectrumContainer.getFont().getName(), Font.ITALIC, 10)));

        createLabels();
    }

    private void createSpectrum() {
        PeptizerSpectrum lSpectrum = iPeptideIdentification.getSpectrum();
        PeptizerPeak[] lPeaks = lSpectrum.getPeakList();
        // annotationsFromLastType will contain the annotations of the last type found, ie Fuse for Mascot.
        HashMap lAnnotationMap = iPeptideHit.getAllAnnotation(iPeptideIdentification, 1);
        AnnotationType lAnnotationType = iPeptideHit.getAnnotationType().get(1);

        Vector annotationsFromLastType = (Vector) lAnnotationMap.get(lAnnotationType.getIndex() + "" + lAnnotationType.getSearchEngine().getId() + "" + 1);


        double[] lMasses = new double[lSpectrum.getPeakList().length];
        double[] lIntensities = new double[lSpectrum.getPeakList().length];
        Vector lAnnotations = new Vector();

        /*
        for (int i = 0; i < lPeaks.length; i++) {
            if (lPeaks[i].getMZ() < 1000) {
                lMasses[i] = lPeaks[i].getMZ();
                lIntensities[i] = lPeaks[i].getIntensity();
            }
        }

        for (int i = 0; i < annotationsFromLastType.size(); i++) {
            if (((PeptizerFragmentIon) annotationsFromLastType.get(i)).getMZ() < 1000) {
                lAnnotations.add(annotationsFromLastType.get(i));
            }
        }
        */

        for (int i = 0; i < lPeaks.length; i++) {
            lMasses[i] = lPeaks[i].getMZ();
            lIntensities[i] = lPeaks[i].getIntensity();
        }

        for (int i = 0; i < annotationsFromLastType.size(); i++) {
            lAnnotations.add(annotationsFromLastType.get(i));
        }

        SpectrumpanelMcp lSpectrumPanel = new SpectrumpanelMcp(lMasses, lIntensities, lSpectrum.getPrecursorMZ(), lSpectrum.getChargeString(), lSpectrum.getName());
        lSpectrumPanel.setAnnotations(lAnnotations);

        spectrumContainer = new JPanel();
        jpanSpectrum = lSpectrumPanel;

    }

    private void createLabels() {

        Double alpha = Double.parseDouble((String) MatConfig.getInstance().getGeneralProperties().get("DEFAULT_MASCOT_ALPHA"));
        double alphaPercentage = (1 - alpha) * 100;
        BigDecimal lPercentage = new BigDecimal(alphaPercentage).setScale(2, BigDecimal.ROUND_UP);

        PeptideHit lPeptideHit = (PeptideHit) iPeptideHit.getOriginalPeptideHit(SearchEngineEnum.Mascot);
        BigDecimal lThreshold = new BigDecimal(lPeptideHit.calculateIdentityThreshold(alpha)).setScale(2, BigDecimal.ROUND_UP);
        ArrayList lProteins;
        lProteins = iPeptideHit.getProteinHits();
        StringBuffer lProteinText = new StringBuffer();
        for (int i = 0; i < lProteins.size(); i++) {
            PeptizerProteinHit lProteinHit = (PeptizerProteinHit) lProteins.get(i);
            lProteinText.append(lProteinHit.getAccession());
            if (lProteins.size() > i + 1) {
                lProteinText.append(", ");
            }
        }
        double lDeltaMass = ((PeptideHit) (iPeptideHit.getOriginalPeptideHit(SearchEngineEnum.Mascot))).getDeltaMass();
        double lPrecursorMZ = iPeptideIdentification.getSpectrum().getPrecursorMZ();
        String lChargeString = iPeptideIdentification.getSpectrum().getChargeString();

        lblSequence.setText(iPeptideHit.getModifiedSequence());
        lblScore.setText(iPeptideHit.getIonsScore() + "");
        //   lblThreshold.setText(lThreshold + "");
        lblConfidence.setText(lPercentage + "%");
        lblProtein.setText(lProteinText.toString());
        lblThreshold.setText(lThreshold + "");
        lblPrecursorMass.setText(lPrecursorMZ + "");
        lblCharge.setText(lChargeString);
        lblMassError.setText(lDeltaMass + "");

    }


    private void createUIComponents() {
        createSpectrum();
        //createAnnotatedSequence();
    }

    public JComponent getContentPanel() {
        return content;
    }


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setBackground(new Color(-1));
        jpanInfo = new JPanel();
        jpanInfo.setLayout(new GridBagLayout());
        jpanInfo.setBackground(new Color(-1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 8;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(jpanInfo, gbc);
        jpanInfo.setBorder(BorderFactory.createTitledBorder(null, "Info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(jpanInfo.getFont().getName(), Font.ITALIC, 10), new Color(-16777216)));
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, 11));
        label1.setText("Modified Sequence");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label1, gbc);
        final MySpacer mySpacer1 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 10;
        jpanInfo.add(mySpacer1, gbc);
        final MySpacer mySpacer2 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanInfo.add(mySpacer2, gbc);
        final MySpacer mySpacer3 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        jpanInfo.add(mySpacer3, gbc);
        final MySpacer mySpacer4 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        jpanInfo.add(mySpacer4, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, 11));
        label2.setText("Confidence level");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label2, gbc);
        final MySpacer mySpacer5 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 4;
        gbc.gridheight = 15;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanInfo.add(mySpacer5, gbc);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, 11));
        label3.setText("Precursor m/z");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, 11));
        label4.setText("Ion score");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label4, gbc);
        lblScore = new JLabel();
        lblScore.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblScore.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblScore, gbc);
        lblConfidence = new JLabel();
        lblConfidence.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblConfidence.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblConfidence, gbc);
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), Font.BOLD, 11));
        label5.setText("Protein");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label5, gbc);
        lblProtein = new JLabel();
        lblProtein.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblProtein.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblProtein, gbc);
        lblSequence = new JLabel();
        lblSequence.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblSequence.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblSequence, gbc);
        lblPrecursorMass = new JLabel();
        lblPrecursorMass.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblPrecursorMass.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblPrecursorMass, gbc);
        final JLabel label6 = new JLabel();
        label6.setFont(new Font(label6.getFont().getName(), Font.BOLD, 11));
        label6.setText("Identity Threshold");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label6, gbc);
        lblThreshold = new JLabel();
        lblThreshold.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblThreshold.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblThreshold, gbc);
        final MySpacer mySpacer6 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        jpanInfo.add(mySpacer6, gbc);
        final JLabel label7 = new JLabel();
        label7.setFont(new Font(label7.getFont().getName(), Font.BOLD, 11));
        label7.setText("Mass error");
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label7, gbc);
        lblMassError = new JLabel();
        lblMassError.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblMassError.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblMassError, gbc);
        final JLabel label8 = new JLabel();
        label8.setFont(new Font(label8.getFont().getName(), Font.BOLD, 11));
        label8.setText("Charge");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(label8, gbc);
        lblCharge = new JLabel();
        lblCharge.setFont(UIManager.getFont("TabbedPane.smallFont"));
        lblCharge.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 10;
        jpanInfo.add(lblCharge, gbc);
        spectrumContainer.setLayout(new GridBagLayout());
        spectrumContainer.setBackground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        content.add(spectrumContainer, gbc);
        spectrumContainer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(spectrumContainer.getFont().getName(), spectrumContainer.getFont().getStyle(), spectrumContainer.getFont().getSize())));
        jpanSpectrum.setLayout(new GridBagLayout());
        jpanSpectrum.setBackground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        spectrumContainer.add(jpanSpectrum, gbc);
        jpanSpectrum.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-10066330)));
        final MySpacer mySpacer7 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanSpectrum.add(mySpacer7, gbc);
        final MySpacer mySpacer8 = new MySpacer();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanSpectrum.add(mySpacer8, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return content;
    }
}
