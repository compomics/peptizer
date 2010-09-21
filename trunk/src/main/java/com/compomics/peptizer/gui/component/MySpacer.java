package com.compomics.peptizer.gui.component;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: May 14, 2009
 * Time: 3:24:30 PM
 * <p/>
 * This class
 */
public class MySpacer extends JPanel {
	// Class specific log4j logger for MySpacer instances.
	 private static Logger logger = Logger.getLogger(MySpacer.class);
    private static Color iColor = Color.WHITE;

    public MySpacer(final LayoutManager layout, final boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        this.setBackground(iColor);
    }

    public MySpacer(final LayoutManager layout) {
        super(layout);
        this.setBackground(iColor);
    }

    public MySpacer(final boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        this.setBackground(iColor);
    }

    public MySpacer() {
        super();
        this.setBackground(iColor);
    }
}
