package com.compomics.peptizer.gui.interfaces;

import com.compomics.peptizer.interfaces.ValidationSaver;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 12-jul-2007
 * Time: 15:08:56
 */

/**
 * Interface description:
 * ------------------
 * This Interface was developed to be implemented by JPanels that can construct a ValidationSaver instance by gui input.
 */
public interface SaveValidationPanel {
    /**
     * Returns an instance to save selected identifications and validation.
     *
     * @return ValidationSaver to save validation of selected identifications.
     */
    public ValidationSaver getValidationSaver();
}
