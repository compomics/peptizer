package com.compomics.peptizer.gui.view;

import com.compomics.peptizer.MatConfig;
import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.gui.model.TreeCellRendererImpl;
import com.compomics.peptizer.gui.model.TreeModelImpl;
import com.compomics.peptizer.util.PeptideIdentification;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.TreeModel;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:06:27
 */

/**
 * Class description:
 * ------------------
 * This class is the Tree View-Controller.
 * It extends a JTree which is a Swing JComponent.
 * A group of PeptideIdentification instances from the aggregator is displayed.
 */
public class TreeView extends JTree {
	// Class specific log4j logger for TreeView instances.
	 private static Logger logger = Logger.getLogger(TreeView.class);

    /**
     * The parent super-controller.
     */
    private Mediator iMediator;

    /**
     * The custom TreeModel.
     */
    private TreeModelImpl iTreeModel;

    public TreeView(Mediator aMediator) {
        super();
        iMediator = aMediator;
        iTreeModel = new TreeModelImpl(this);
        setModel(iTreeModel);

        // Custom cell renderer.
        this.setCellRenderer(new TreeCellRendererImpl());
        // Tree height read from a parameter.
        this.setRowHeight(new Integer(MatConfig.getInstance().getGeneralProperty("TREE_HEIGHT")).intValue());
    }

    /**
     * Returns the root of the Tree.
     *
     * @return The root of the Tree.
     */
    public Object getRoot() {
        return iMediator;
    }

    /**
     * Returns the number of PeptideIdentifications in the Tree.
     *
     * @return The number of elements composing the Tree.
     */
    public int getNumberOfPeptideIdentifications() {
        return iMediator.getNumberOfPeptideIdentifications();
    }

    /**
     * Returns the PeptideIdentification at index aIndex.
     *
     * @param aIndex index of the PeptideIdentification.
     * @return PeptideIdentification at index aIndex.
     */
    public PeptideIdentification getPeptideIdentification(int aIndex) {
        return iMediator.getPeptideIdentification(aIndex);
    }

    /**
     * Returns the Treemodel for the TreeView.
     *
     * @return Treemodel object.
     */
    public TreeModel getTreeModel() {
        return iTreeModel;
    }


    /**
     * Set the Filter to be applied to the Tree.
     *
     * @param aFilter TreeFilter.
     */
    public void setFilter(TreeFilter aFilter) {
        iTreeModel.setFilter(aFilter);
        this.updateUI();
    }

    /**
     * Disable the filter.
     */
    public void disableFilter() {
        iTreeModel.disableFilter();
        this.updateUI();
    }

    /**
     * Move the Tree selection to the next.
     *
     * @return Returns the next peptideidentification in the tree.
     */
    public PeptideIdentification nextInTree() {
        PeptideIdentification lPeptideIdentification = null;
        int index = this.getModel().getIndexOfChild(this.getRoot(), iMediator.getActivePeptideIdentification());
        index = index + 1;
        if ((index) < this.getNumberOfPeptideIdentifications()) {
            lPeptideIdentification = (PeptideIdentification) this.getModel().getChild(this.getRoot(), index);
        }

        return lPeptideIdentification;
    }


    /**
     * Move the Tree selection to the previous.
     *
     * @return Returns the previous peptideidentification in the tree.
     */
    public PeptideIdentification previousInTree() {
        PeptideIdentification lPeptideIdentification = null;
        int index = this.getModel().getIndexOfChild(this.getRoot(), iMediator.getActivePeptideIdentification());
        if (index != 0 && index < this.getNumberOfPeptideIdentifications()) {
            lPeptideIdentification = (PeptideIdentification) this.getModel().getChild(this.getRoot(), index - 1);
        }

        return lPeptideIdentification;
    }
}
