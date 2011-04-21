package com.compomics.peptizer.gui.model;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.interfaces.TreeFilter;
import com.compomics.peptizer.gui.view.TreeView;
import com.compomics.peptizer.util.PeptideIdentification;
import com.compomics.peptizer.util.datatools.interfaces.PeptizerPeptideHit;
import org.apache.log4j.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:16:30
 */

/**
 * Class description:
 * ------------------
 * This class was developed to customize the TreeModel of the TreeView.
 */
public class TreeModelImpl implements TreeModel {
    // Class specific log4j logger for TreeModelImpl instances.
    private static Logger logger = Logger.getLogger(TreeModelImpl.class);

    /**
     * The optional filter for the tree.
     */
    private TreeFilter iFilter = null;

    /**
     * The parent TreeView.
     */
    private TreeView iTreeView = null;

    /**
     * This constructor takes a parent TreeView as a single argument.
     *
     * @param aTreeView TreeView parent.
     */
    public TreeModelImpl(TreeView aTreeView) {
        iTreeView = aTreeView;
    }

    /**
     * {@inheritDoc}
     */
    public Object getRoot() {
        return iTreeView.getRoot();
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount(Object parent) {
        int result = 0;
        // A. Node is root, return number of child PeptideIdentifications.
        if (parent instanceof Mediator) {
            if (iFilter == null) {
                result = iTreeView.getNumberOfPeptideIdentifications();
            } else {
                int realChildren = iTreeView.getNumberOfPeptideIdentifications();
                for (int i = 0; i < realChildren; i++) {
                    if (iFilter.pass(iTreeView.getPeptideIdentification(i))) {
                        result++;
                    }
                }
            }
        }
        // B. Node is PeptideIdentifications, return number of child PeptideHits.
        else if (parent instanceof PeptideIdentification) {
            result = ((PeptideIdentification) parent).getNumberOfConfidentPeptideHits();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeaf(Object node) {
        boolean result = false;
        // PeptideHits are the leaves.
        if (node instanceof PeptizerPeptideHit) {
            result = true;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <br> <i>Not implemented.</i>
     */
    public void addTreeModelListener(TreeModelListener l) {
        // not implemented.
    }

    /**
     * {@inheritDoc}
     * <br> <i>Not implemented.</i>
     */
    public void removeTreeModelListener(TreeModelListener l) {
        // not implemented.
    }

    /**
     * {@inheritDoc}
     */
    public Object getChild(Object parent, int index) {
        Object result = null;
        if (parent instanceof Mediator) {
            if (iFilter == null) {
                result = iTreeView.getPeptideIdentification(index);
            } else {
                int realChildren = iTreeView.getNumberOfPeptideIdentifications();
                int lRealIndex = -1;
                int lVisibleIndex = -1;
                for (int i = 0; i < realChildren; i++) {
                    if (iFilter.pass(iTreeView.getPeptideIdentification(i))) {
                        lVisibleIndex++;
                    }
                    lRealIndex++;
                    if (lVisibleIndex == index) {
                        result = iTreeView.getPeptideIdentification(lRealIndex);
                        break;
                    }
                }
            }
        } else if (parent instanceof PeptideIdentification) {
            result = ((PeptideIdentification) parent).getPeptideHit(index);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public int getIndexOfChild(Object parent, Object child) {
        int result = 0;
        int numberofchildren = 0;
        numberofchildren = this.getChildCount(parent);
        for (int i = 0; i < numberofchildren; i++) {
            Object o = getChild(parent, i);
            if (o.equals(child)) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <br> <i>Not implemented.</i>
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // not implemented.
    }


    public void setFilter(TreeFilter aFilter) {
        iFilter = aFilter;
    }

    /**
     * Disable the filter.
     */
    public void disableFilter() {
        iFilter = null;
    }

    /**
     * Returns true if the Tree is filtered.
     *
     * @return
     */
    public boolean isFiltered() {
        return (iFilter != null);
    }
}
