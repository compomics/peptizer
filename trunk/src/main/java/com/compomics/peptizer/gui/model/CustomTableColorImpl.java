package com.compomics.peptizer.gui.model;

import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 13-aug-2007
 * Time: 13:23:27
 */

/**
 * Class description:
 * ------------------
 * This class was developed to allow a custom color scheme for the Table.
 */
public class CustomTableColorImpl {
    /**
     * The light color for selected column.
     */
    private Color iSelectedLight = new Color(235, 235, 235);

    /**
     * The dark color for selected column.
     */
    private Color iSelectedDark = new Color(180, 180, 255);

    /**
     * The light color for the non-selected columns.
     */
    private Color iNonSelectedLight = new Color(255, 255, 255);

    /**
     * The dark color for the non-selected columns.
     */
    private Color iNonSelectedDark = new Color(205, 205, 235);

    /**
     * The light color for the header column.
     */
    private Color iHeaderLight = new Color(250, 250, 250);

    /**
     * the dark color for the header column.
     */
    private Color iHeaderDark = new Color(205, 205, 235);


    /**
     * This constructor takes 6 Colors as arguments for the color scheme of the Table.
     *
     * @param aSelectedLight    The light color for selected column.
     * @param aSelectedDark     The dark color for selected column.
     * @param aNonSelectedLight The light color for the non-selected columns.
     * @param aNonSelectedDark  The dark color for the non-selected columns.
     * @param aHeaderLight      The light color for the header column.
     * @param aHeaderDark       The dark color for the header column.
     */
    public CustomTableColorImpl(Color aSelectedLight, Color aSelectedDark, Color aNonSelectedLight, Color aNonSelectedDark, Color aHeaderLight, Color aHeaderDark) {
        iSelectedLight = aSelectedLight;
        iSelectedDark = aSelectedDark;
        iNonSelectedLight = aNonSelectedLight;
        iNonSelectedDark = aNonSelectedDark;
        iHeaderLight = aHeaderLight;
        iHeaderDark = aHeaderDark;
    }

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
