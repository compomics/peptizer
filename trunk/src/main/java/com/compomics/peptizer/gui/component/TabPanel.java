package com.compomics.peptizer.gui.component;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.view.TabbedView;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.AnnotationType;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeak;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerSpectrum;
import com.compomics.util.gui.spectrum.SpectrumPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 4-apr-2007
 * Time: 17:24:31
 */

/**
 * Class description: ------------------ TabPanel is a Panel that is added on a Pane in TabbedView.
 */
public class TabPanel extends JPanel {
    /**
     * The index of the selectod radiobutton. Static variable so changes in one TabPanel are valid to all TabPanels.
     */
    private static int bgSelectedIndex = -1;
    private int searchEngineSelectedIndex = -1;

    /**
     * The final indexes for annotation type.
     */
    private ArrayList<AnnotationType> iAnnotationType = null;
    /**
     * The associated peptide identification.
     */
    private PeptideIdentification iPeptideIdentification = null;
    /**
     * The annotation type radiobuttons.
     */
    private ArrayList<JRadioButton> rbtAnnotation = new ArrayList<JRadioButton>();
    /**
     * The spectrum of the identification.
     */
    private SpectrumPanel jpanSpectrum = null;
    JPanel jpanBottom;
    /**
     * The inner spectrum of the sequence annotation.
     */
    private PeptizerSequenceFragmentationPanel jpanFragmentsInner = null;

    /**
     * The outer pannel of the sequence annotation.
     */
    private JPanel jpanFragments = null;

    /**
     * Boolean whether the SequenceFragmentationPanel must display the flat sequence (set to false) or the modified
     * sequence (set to true).
     */
    private boolean boolModifedSequenceFragmentation = true;

    /**
     * The parent TabbedView controller.
     */
    private TabbedView iTabbedView = null;
    /**
     * The annotations for the spectrum.
     */
    private HashMap iAnnotations = null;

    /**
     * This Constructor takes the associated peptide identification and the parent TabbedView controller.
     *
     * @param aPeptideIdentification PeptideIdentification assoctiated with the TabPanel.
     * @param aTabbedView            TabbedView parent controller.
     */
    public TabPanel(PeptideIdentification aPeptideIdentification, TabbedView aTabbedView) {
        // JPanel Constructor.
        super();
        // Create the panel by private method.
        createPanel(aPeptideIdentification, aTabbedView);
    }

    /**
     * Private method creates the Panel.
     *
     * @param aPeptideIdentification PeptideIdentification assoctiated with the TabPanel.
     * @param aTabbedView            TabbedView parent controller.
     */
    private void createPanel(PeptideIdentification aPeptideIdentification, TabbedView aTabbedView) {
        // Set instance variables.
        iPeptideIdentification = aPeptideIdentification;
        iTabbedView = aTabbedView;

        // Create the spectrum.
        jpanSpectrum = getSpectrumPanel();

        // Build the annotations and get the annotation type.
        iAnnotationType = aPeptideIdentification.getPeptideHit(0).getAnnotationType();
        iAnnotations = getAnnotations();

        // Build the SequenceFragmentationPanel.
        boolModifedSequenceFragmentation =
                Boolean.parseBoolean(MatConfig.getInstance().getGeneralProperty("MODIFEDSEQUENCE_FRAGMENTATION_PANEL"));
        String lSequence = getSequenceForFragmentationPanel(0);
        jpanFragmentsInner =
                new PeptizerSequenceFragmentationPanel(lSequence, (Vector) iAnnotations.get(iAnnotationType.get(0).getIndex() + "" + iAnnotationType.get(0).getSearchEngine().getId() + "" + 1), boolModifedSequenceFragmentation);
        jpanFragmentsInner.setBackground(Color.white);

        /**
         * The "x" close button. Add to TopPanel.
         */
        JButton btnClose = new TabCloseButton();
        btnClose.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        JPanel jpanClose = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        jpanClose.add(btnClose);
        jpanClose.setBackground(Color.white);

        // Combine the close 'X' button and the sequence fragmentation in a Panel.
        jpanFragments = new JPanel(new BorderLayout());
        jpanFragments.add(jpanClose, BorderLayout.EAST);
        jpanFragments.add(jpanFragmentsInner, BorderLayout.CENTER);

        // If necessary build the radiobuttongroup to switch the annotations on the spectrumpanel.
        jpanBottom = new JPanel();
        updateAnnotationTypeButtons(0);

        // Update the annotations on the spectrum.
        updateAnnotations();

        /**
         * Add a componentListener that get's invoked when the component has been made visible.
         */
        this.addComponentListener(new ComponentAdapter() {
            /**
             * .
             */
            public void componentShown(ComponentEvent e) {
                selectCorrectRadioButton(bgSelectedIndex, searchEngineSelectedIndex);
            }
        });

        jpanBottom.setLayout(new BoxLayout(jpanBottom, BoxLayout.LINE_AXIS));
        jpanBottom.add(Box.createHorizontalGlue());
        for (int i = 0; i < rbtAnnotation.size(); i++) {
            jpanBottom.add(rbtAnnotation.get(i));
            jpanBottom.add(Box.createRigidArea(new Dimension(15 - 5 * i, rbtAnnotation.get(i).getSize().height)));
        }
        jpanBottom.setBackground(Color.white);

        /**
         * The SequenceFragmentationPanel.
         */

        // Remove JPanel default Border.
        jpanSpectrum.setBorder(BorderFactory.createEmptyBorder());

        // Add TopPanel and SpectrumPanel into a Main JPanel.
        jpanSpectrum.setLayout(new BoxLayout(jpanSpectrum, BoxLayout.Y_AXIS));

        jpanSpectrum.add(jpanBottom);

        jpanSpectrum.setBackground(Color.white);

        JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, jpanFragments, jpanSpectrum);
        split1.setOneTouchExpandable(true);

        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.add(split1, BorderLayout.CENTER);

    }

    /**
     * Update the annotations on the spectrumpannel.
     */
    public void updateAnnotations() {

        int lSelectedTableColumn = iTabbedView.getSelectedTableColumn();
        String lSequence;
        if (lSelectedTableColumn > 0) {
            // Get the correct sequence
            lSequence = getSequenceForFragmentationPanel(lSelectedTableColumn - 1);
            jpanFragmentsInner.setSequence(lSequence, boolModifedSequenceFragmentation);
            for (int i = 0; i < iAnnotationType.size(); i++) {
                if (bgSelectedIndex == iAnnotationType.get(i).getIndex() && searchEngineSelectedIndex == iAnnotationType.get(i).getSearchEngine().getId()) {
                    jpanSpectrum.setAnnotations((Vector) iAnnotations.get(iAnnotationType.get(i).getIndex() + "" + iAnnotationType.get(i).getSearchEngine().getId() + "" + lSelectedTableColumn));
                    jpanFragmentsInner.setFragmentions((Vector) iAnnotations.get(iAnnotationType.get(i).getIndex() + "" + iAnnotationType.get(i).getSearchEngine().getId() + "" + lSelectedTableColumn));
                }
                jpanSpectrum.validate();
                jpanSpectrum.repaint();
                jpanFragmentsInner.repaint();
            }

        } else if (lSelectedTableColumn <= 0) {
            lSequence = getSequenceForFragmentationPanel(0);
            jpanFragmentsInner.setSequence(lSequence, boolModifedSequenceFragmentation);
            Vector annotations = new Vector();
            for (int i = 0; i < iAnnotationType.size(); i++) {
                if (bgSelectedIndex == iAnnotationType.get(i).getIndex() && iAnnotationType.get(i).getSearchEngine().getId() == iAnnotationType.get(i).getSearchEngine().getId()) {
                    Vector tempVector = (Vector) iAnnotations.get(iAnnotationType.get(i).getIndex() + "" + iAnnotationType.get(i).getSearchEngine().getId() + "" + 1);
                    if (tempVector != null) {
                        for (int j = 0; j < tempVector.size(); j++) {
                            annotations.add(tempVector.get(j));
                        }
                    }
                }
            }
            jpanSpectrum.setAnnotations(annotations);
            jpanFragmentsInner.setFragmentions(annotations);
            jpanSpectrum.validate();
            jpanSpectrum.repaint();
            jpanFragmentsInner.repaint();
        }
    }

    public void updateAnnotationTypeButtons(int peptideNumber) {
        boolean found = false;
        for (int i = 0; i < iPeptideIdentification.getPeptideHit(peptideNumber).getAdvocate().getAdvocates().size(); i++) {
            if (iPeptideIdentification.getPeptideHit(peptideNumber).getAdvocate().getAdvocates().get(i).getId() == searchEngineSelectedIndex) {
                found = true;
                break;
            }
        }
        if (!found) {
            searchEngineSelectedIndex = iPeptideIdentification.getPeptideHit(peptideNumber).getAdvocate().getAdvocates().get(0).getId();
            bgSelectedIndex = -1;
        }
        for (int i = 0; i < rbtAnnotation.size(); i++) {
            jpanBottom.remove(rbtAnnotation.get(i));
        }
        iAnnotationType = iPeptideIdentification.getPeptideHit(peptideNumber).getAnnotationType();
        rbtAnnotation = new ArrayList<JRadioButton>();
        for (int i = 0; i < iAnnotationType.size(); i++) {
            JRadioButton tempButton = new JRadioButton(iAnnotationType.get(i).getName());
            tempButton.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (((JRadioButton) e.getSource()).isSelected()) {
                        for (int j = 0; j < rbtAnnotation.size(); j++) {
                            if (TabPanel.this.rbtAnnotation.get(j).isSelected()) {
                                if (TabPanel.this.rbtAnnotation.get(j) == e.getSource()) {
                                    bgSelectedIndex = TabPanel.this.iAnnotationType.get(j).getIndex();
                                    searchEngineSelectedIndex = iAnnotationType.get(j).getSearchEngine().getId();
                                    TabPanel.this.updateAnnotations();
                                } else {
                                    TabPanel.this.rbtAnnotation.get(j).setSelected(false);
                                }
                            }
                        }
                    }
                }
            });
            tempButton.setBackground(Color.white);
            tempButton.setMnemonic(KeyEvent.VK_A);
            rbtAnnotation.add(tempButton);
        }

        int selected;
        // Load the variable from the property file if it has not been set before.
        if (bgSelectedIndex >= 0) {
            selected = bgSelectedIndex;
        } else {
            if (searchEngineSelectedIndex == 0) {
                selected = Integer.parseInt(MatConfig.getInstance().getGeneralProperty("RDB_ANNOTATION"));
            } else {
                selected = 0;
            }
        }

        for (int i = 0; i < rbtAnnotation.size(); i++) {
            jpanBottom.add(rbtAnnotation.get(i));
        }
        // Select the correct radiobutton.
        selectCorrectRadioButton(selected, searchEngineSelectedIndex);

    }

    /**
     * Returns the sequence that must be displayed in the FragmentationPanel. This can be the flat sequence or the
     * modified sequence.
     *
     * @param aPeptideHitNumber the corresponding PeptidehitNumber.
     * @return String with the sequence for the FragmentationPanel.
     */
    private String getSequenceForFragmentationPanel(int aPeptideHitNumber) {
        if (boolModifedSequenceFragmentation) {
            return iPeptideIdentification.getPeptideHit(aPeptideHitNumber).getModifiedSequence();
        } else {
            return iPeptideIdentification.getPeptideHit(aPeptideHitNumber).getSequence();
        }
    }

    /**
     * Returns the spectrum component.
     *
     * @return SpectrumPanel of this TabPanel's identification.
     */
    private SpectrumPanel getSpectrumPanel() {
        // Get spectrum and set local variables.
        PeptizerSpectrum lSpectrum = iPeptideIdentification.getSpectrum();
        PeptizerPeak[] lPeaks = lSpectrum.getPeakList();
        String lSpectrumname = lSpectrum.getName();
        String lCharge = "not defined";
        double lPrecursor = 0.0;

        /*
        if (iPeptideIdentication.getSpectrum() instanceof Query) {
            Query lQuery = (Query) iPeptideIdentication.getSpectrum();
            lCharge = lQuery.getChargeString();
            lPrecursor = lQuery.getPrecursorMZ();
        }
        */

        double[] lMZ = new double[lPeaks.length];
        double[] lIntensity = new double[lPeaks.length];

        for (int i = 0; i < lPeaks.length; i++) {
            PeptizerPeak lPeak = lPeaks[i];
            lMZ[i] = lPeak.getMZ();
            lIntensity[i] = lPeak.getIntensity();
        }
        // Return a new SpectrumPanel constructed with the local variables.
        return new SpectrumPanel(lMZ, lIntensity, lPrecursor, lCharge, lSpectrumname);
    }

    /**
     * Returns the annotations that can be layered upon the spectrum. The annotations are calculated and stored as
     * Vector's in HashMap values.
     *
     * @return HashMap with the annotations of the identification.
     */
    private HashMap getAnnotations() {
        HashMap lAnnotationsMap = new HashMap();
        PeptizerPeptideHit lPeptideHit = null;

        // Do this for every identification above threshold.
        for (int i = 0; i < iPeptideIdentification.getNumberOfConfidentPeptideHits(); i++) {
            lPeptideHit = this.iPeptideIdentification.getPeptideHit(i);
            lAnnotationsMap.putAll(lPeptideHit.getAllAnnotation(iPeptideIdentification, i));
        }
        // Returns the HashMap with annotation.
        return lAnnotationsMap;
    }

    /**
     * Returns the identification of the TabPanel.
     *
     * @return PeptideIdentification of the TabPanel.
     */
    public PeptideIdentification getPeptideIdentification() {
        return this.iPeptideIdentification;
    }

    /**
     * Private Class TabCloseButton was adapted from a Sun tutorial. Add ActionListeners to this button to make it
     * functional.
     */
    private class TabCloseButton extends JButton implements ActionListener {
        public TabCloseButton() {
            int size = 22;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close this tab.");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }


        /**
         * Close the current selected TabPanel when this button's action is evoked.
         *
         * @param e ActionEvent when the button is pressed.
         */
        public void actionPerformed(ActionEvent e) {
            iTabbedView.closeTab(iTabbedView.getSelectedIndex());
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.ORANGE);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    /**
     * Static Mouselistener for the mouse hover effect.
     */
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    /**
     * This method reads the specified selected index, compares it to the constants defined on this class, and selects
     * the corresponding radio button.
     *
     * @param aSelection int with the radio button that should be selected.
     */
    private void selectCorrectRadioButton(int aSelection, int searchEngine) {
        for (int i = 0; i < iAnnotationType.size(); i++) {
            if (aSelection == iAnnotationType.get(i).getIndex() && searchEngine == iAnnotationType.get(i).getSearchEngine().getId() && !rbtAnnotation.get(i).isSelected()) {
                rbtAnnotation.get(i).setSelected(true);
            }
        }
    }
}
