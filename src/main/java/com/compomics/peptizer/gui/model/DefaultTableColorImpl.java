package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.interfaces.TableColor;
import org.apache.log4j.Logger;

import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-aug-2007
 * Time: 11:21:14
 */

/**
 * Class description:
 * ------------------
 * This class was developed as a default Color scheme for the Table coloring.
 */
public class DefaultTableColorImpl implements TableColor {
    // Class specific log4j logger for DefaultTableColorImpl instances.
    private static Logger logger = Logger.getLogger(DefaultTableColorImpl.class);

    /**
     * The light color for selected column.
     */
    private Color iSelectedLight = new Color(245, 245, 245);
    /**
     * The dark color for selected column.
     */
    private Color iSelectedDark = new Color(240, 240, 240);

    /**
     * The light color for the non-selected columns.
     */
    private Color iNonSelectedLight = new Color(235, 235, 235);
    /**
     * The dark color for the non-selected columns.
     */
    private Color iNonSelectedDark = new Color(230, 230, 230);

    /**
     * The light color for the header column.
     */
    private Color iHeaderLight = new Color(225, 225, 225);
    /**
     * the dark color for the header column.
     */
    private Color iHeaderDark = new Color(220, 220, 220);


    /**
     * Returns the light color for selected column.
     *
     * @return Color light-selected
     */
    public Color getSelectedLight() {
        return iSelectedLight;
    }

    /**
     * Returns the dark color for the selected column.
     *
     * @return Color dark-selected
     */
    public Color getSelectedDark() {
        return iSelectedDark;
    }

    /**
     * Returns the light color for non-selected column.
     *
     * @return Color light-non selected
     */
    public Color getNonSelectedLight() {
        return iNonSelectedLight;
    }

    /**
     * Returns the dark color for non-selected column.
     *
     * @return Color dark-non selected
     */
    public Color getNonSelectedDark() {
        return iNonSelectedDark;
    }

    /**
     * Returns the light color for the Header column.
     *
     * @return Color light header
     */
    public Color getHeaderLight() {
        return iHeaderLight;
    }

    /**
     * Returns the dark color for the Header column.
     *
     * @return Color dark header.
     */
    public Color getHeaderDark() {
        return iHeaderDark;
    }
}
