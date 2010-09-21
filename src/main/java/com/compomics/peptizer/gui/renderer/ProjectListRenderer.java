package com.compomics.peptizer.gui.renderer;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Jan 20, 2009
 * Time: 3:21:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProjectListRenderer implements ListCellRenderer {
	// Class specific log4j logger for ProjectListRenderer instances.
	 private static Logger logger = Logger.getLogger(ProjectListRenderer.class);
    private static JLabel lbl = new JLabel();

    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        String title = value.toString();
        if (title.length() > 30) {
            title = title.substring(0, 27) + "...";
        }
        lbl.setText(title);
        return lbl;
    }
}
