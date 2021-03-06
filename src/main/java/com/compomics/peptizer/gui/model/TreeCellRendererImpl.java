package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 28-feb-2007
 * Time: 11:00:58
 */

/**
 * Class description:
 * ------------------
 * This class was developed to have full control on the rendering of the Tree.
 */
public class TreeCellRendererImpl extends JPanel implements TreeCellRenderer {
    // Class specific log4j logger for TreeCellRendererImpl instances.
    private static Logger logger = Logger.getLogger(TreeCellRendererImpl.class);

    /**
     * TreeTextArea is the actual object that is displayed.
     */
    protected TreeTextArea text;
    public Color iRejectColor = new Color(252, 53, 0);
    public Color iAcceptColor = new Color(0, 185, 76);
    public Color iNeutralColor = new Color(30, 97, 171);

    /**
     * Empty constructor.
     * Create and add the TreeTextArea.
     */
    public TreeCellRendererImpl() {
        setLayout(new BorderLayout());

        text = new TreeTextArea();
        text.setAlignmentX(text.getAlignmentX() + 10);

        EmptyBorder lBorder = new EmptyBorder(0, 0, 0, 0);

        text.setBorder(lBorder);
        this.setBorder(lBorder);

        text.setBackground(Color.WHITE);
        this.setBackground(Color.WHITE);

        add(text, BorderLayout.CENTER);
    }

    /**
     * @{InheritDoc}
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Our tree is enabled
        setEnabled(tree.isEnabled());

        // Toggle our selected info
        setSelected(isSelected, hasFocus, row);

        // Set all to bold except the leafs.
        text.setBold(!leaf);
        text.setPreferredSize(new Dimension(tree.getWidth(), 15));

        // Set our Color
        text.setSelectedTextColor(Color.white);

        // Now let's check what sort of instance the node represents.
        Object result = value;

        // A. PeptideIdentification
        // If it's a PeptideIdentification, we want to highlight the identifications that need validation.
        // The validated one's, go to normal(black) coloring.
        if (value instanceof PeptideIdentification) {
            PeptideIdentification lPeptideIdentification = (PeptideIdentification) value;
            int aIndex = (tree.getModel().getIndexOfChild(tree.getModel().getRoot(), value) + 1);
            result = lPeptideIdentification.getName() + " (" + lPeptideIdentification.getConfidentPeptideHits().length + ")";
            if (!lPeptideIdentification.isValidated()) {
                text.setForeground(iNeutralColor);
            } else {
                // Accepted ID's are green, rejected are red.
                if (lPeptideIdentification.getValidationReport().getResult()) {
                    text.setForeground(iAcceptColor);
                } else {
                    text.setForeground(iRejectColor);
                }
            }
        }
        // B. PeptideHit
        else if (value instanceof PeptizerPeptideHit) {
            PeptizerPeptideHit lPeptideHit = (PeptizerPeptideHit) value;
            BigDecimal lScore = new BigDecimal(lPeptideHit.getIonsScore());
            lScore = lScore.setScale(1, BigDecimal.ROUND_DOWN);
            Double threshold = lPeptideHit.calculateThreshold();
            if (threshold == null) {
                threshold = 0.0;
            }
            BigDecimal lThreshold = new BigDecimal(threshold);
            lThreshold = lThreshold.setScale(1, BigDecimal.ROUND_DOWN);

            result = lPeptideHit.getModifiedSequence();
            if (lPeptideHit.getIonsScore() >= 0) {
                result = result + " | " + lScore + "";
            }
            result = result + "(" + lThreshold + ")";

            // C. TreeRoot is occupied by the Mediator itself.
        } else if (value instanceof Mediator) {
            result = "Selected spectra";
        }

        text.setText(result.toString());
        return this;
    }

    private void setSelected(boolean isSelected, boolean hasFocus, int row) {
        // If we are selected
        if (isSelected) {
            // If we have focus
            if (hasFocus) {
                super.setBackground(UIManager.getColor("Tree.textBackground"));
                setBorder(BorderFactory.createLineBorder(UIManager.getColor("Tree.selectionBorderColor")));
                text.setForeground(Color.black);
            } else {
                super.setBackground(UIManager.getColor("Tree.selectionBackground"));
                setBorder(BorderFactory.createEmptyBorder());
                text.setForeground(Color.white);
            }
        } else {
            super.setBackground(UIManager.getColor("Tree.textBackground"));

            setBorder(BorderFactory.createEmptyBorder());
            text.setForeground(Color.black);
        }
    } // End setSelected function

    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;

        super.setBackground(color);
    } // End setBackground function

    class TreeTextArea extends JTextArea {
        Dimension preferredSize;

        TreeTextArea() {
            setOpaque(true);
        } // End TreeTextArea constructor

        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }

        public void setPreferredSize(Dimension d) {
            if (d != null) {
                preferredSize = d;
            }
        }

        public Dimension getPreferredSize() {
            return preferredSize;
        }

        public void setText(String str) {
            FontMetrics fontMetrics = getFontMetrics(getFont());
            BufferedReader br = new BufferedReader(new StringReader(str));
            int height = fontMetrics.getHeight();
            setPreferredSize(new Dimension(SwingUtilities.computeStringWidth(fontMetrics, str) + 6, height));
            super.setText(str);
        } // End setText function

        void setBold(boolean bold) {
            Font font;
            if (bold)
                font = new Font(text.getFont().getFamily(), Font.BOLD, text.getFont().getSize());
            else
                font = new Font(text.getFont().getFamily(), 0, text.getFont().getSize());
            text.setFont(font);
        } // End setBold function

    } // End TreeTextArea class
} // End of CustomCellRenderer
