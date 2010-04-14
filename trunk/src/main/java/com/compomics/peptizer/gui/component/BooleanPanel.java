package com.compomics.peptizer.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 17-aug-2007
 * Time: 16:20:45
 */

/**
 * Class description:
 * ------------------
 * This class was developed to
 */
public class BooleanPanel extends JPanel {

    JLabel[] iKeyLabels = null;
    JRadioButton[] rdbBoolean = null;

    boolean[] iBooleans;

    /**
     * This Panel displays the toString of an Array of Objects along with a Boolean[].
     * This can be used to select a series of objects.
     *
     * @param aObjects  Array with Objects. Define a nice toString!
     * @param aBooleans Boolean status corresponding the Object.
     */
    public BooleanPanel(Object[] aObjects, boolean[] aBooleans) {
        super();
        iBooleans = aBooleans;
        this.constructPanel(aObjects, aBooleans);
    }

    /**
     * Construct a JPanel to display properties.
     *
     * @param aObjects  Array with Objects. Define a nice toString!
     * @param aBooleans Boolean status corresponding the Object.
     */
    public void constructPanel(Object[] aObjects, boolean[] aBooleans) {
        this.removeAll();

        int lNumber = aObjects.length;

        iKeyLabels = new JLabel[lNumber];
        rdbBoolean = new JRadioButton[lNumber];

        int count = 0;
        for (int i = 0; i < lNumber; i++) {
            Object o = aObjects[i];
            iKeyLabels[count] = new JLabel();
            iKeyLabels[count].setText(o.toString());

            rdbBoolean[count] = new JRadioButtonImpl(count);
            rdbBoolean[count].setSelected(aBooleans[i]);

            count++;
        }

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new GridLayout(lNumber, 2, 10, 5));
        for (int i = 0; i < lNumber; i++) {
            jpanMain.add(iKeyLabels[i]);
            jpanMain.add(rdbBoolean[i]);
        }
        this.add(jpanMain);
    }

    /**
     * JRadioButton extension to facilitate the coupling with the boolean array.
     */
    private class JRadioButtonImpl extends JRadioButton {
        private int iIndex;

        /**
         * Creates an initially unselected radio button
         * with no set text.
         */
        private JRadioButtonImpl(int aIndex) {
            iIndex = aIndex;
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    iBooleans[iIndex] = JRadioButtonImpl.this.isSelected();
                }
            });
        }
    }
}