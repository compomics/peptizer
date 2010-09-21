package com.compomics.peptizer.gui.component;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Properties;
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
public class PropertyPanel extends JPanel {
	// Class specific log4j logger for PropertyPanel instances.
	 private static Logger logger = Logger.getLogger(PropertyPanel.class);
    Properties iProperties = null;

    JLabel[] iKeyLabels = null;
    JLabel[] iValueLabels = null;

    public PropertyPanel(Properties aProperties) {
        super();
        this.constructPanel(aProperties);
    }

    /**
     * Construct a JPanel to display properties.
     *
     * @param aProperties Properties to be displayed.
     */
    public void constructPanel(Properties aProperties) {
        this.removeAll();

        iProperties = aProperties;
        int lNumber = iProperties.size();

        iKeyLabels = new JLabel[lNumber];
        iValueLabels = new JLabel[lNumber];

        Enumeration e = iProperties.keys();
        int count = 0;
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            iKeyLabels[count] = new JLabel();
            iKeyLabels[count].setText(o.toString());

            iValueLabels[count] = new JLabel();
            iValueLabels[count].setText(iProperties.get(o).toString());

            count++;
        }

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new GridLayout(lNumber, 2, 10, 5));
        for (int i = 0; i < lNumber; i++) {
            jpanMain.add(iKeyLabels[i]);
            jpanMain.add(iValueLabels[i]);
        }
        this.add(jpanMain);
    }
}
