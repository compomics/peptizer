package com.compomics.peptizer.gui.view;

import com.compomics.peptizer.gui.Mediator;
import com.compomics.peptizer.gui.component.TabPanel;
import com.compomics.peptizer.gui.model.SingleSelectionModelImpl;
import com.compomics.peptizer.util.PeptideIdentification;

import javax.swing.*;
import java.awt.*;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 26-feb-2007
 * Time: 14:06:46
 */

/**
 * Class description:
 * ------------------
 * This class is the Tab Pane View-Controller.
 * It extends a JTabbedPane which is a Swing JComponent.
 * New TabPanel components are created from PeptideIdentification objects to populate the the TabbedView.
 */
public class TabbedView extends JTabbedPane {

	/**
	 * The parent super-controller.
	 */
	private Mediator iMediator;

	/**
	 * The custom SingleSelectionmodel.
	 */
	private SingleSelectionModel iSingleSelectionModel;

	/**
	 * This constructor takes a parent Mediator as a single parameter.
	 *
	 * @param aMediator Mediator parent super-controller of the gui.
	 */
	public TabbedView(Mediator aMediator) {
		// Call JTabbedPane empty constructor.
		super();
		this.iMediator = aMediator;
		iSingleSelectionModel = new SingleSelectionModelImpl(this);
		this.setModel(iSingleSelectionModel);
	}

	/**
	 * Adds a Tab to the Tabbedview
	 *
	 * @param aPeptideIdentification PeptideIdentification to be added in a new Tab.
	 */
	public void addTabID(PeptideIdentification aPeptideIdentification) {
		String lName = aPeptideIdentification.getName();
		boolean isPresent = false;
		int i = 0;
		for (; i < this.getComponentCount(); i++) {
			if (((TabPanel) this.getComponentAt(i)).getPeptideIdentification().getName().equals(lName)) {
				isPresent = true;
				break;
			}
		}
		if (!isPresent) {
			JPanel jpan1 = new JPanel(false);
			jpan1.setLayout(new GridLayout(1, 1));
			jpan1 = new TabPanel(aPeptideIdentification, this);
			this.addTab(lName, jpan1);
			this.setSelectedIndex(this.getComponentCount() - 1);
		} else {
			this.setSelectedIndex(i);
		}
	}

	/**
	 * Returns the index of the first selected column,
	 * -1 if no column is selected.
	 *
	 * @return the index of the first selected column
	 */
	public int getSelectedTableColumn() {
		return iMediator.getSelectedTableColumn();
	}

	/**
	 * Updates the annotations of the currently selected spectrum and peptidehit.
	 */
	public void updateSpectrumAnnotation() {
		((TabPanel) this.getComponentAt(this.getSelectedIndex())).updateAnnotations();
	}

	/**
	 * Close TabPanel tab by index.
	 * Taken care of at Mediator level since it has to be forwarded to the Table.
	 *
	 * @param aIndex of Tab to be closed.
	 */
	public void closeTab(int aIndex) {
		iMediator.removeTab(aIndex);
	}

	/**
	 * Act whenever selecion index of the Tabs has changed.
	 *
	 * @param aIndex int index of new selected tab.
	 */
	public void selectionChanged(int aIndex) {
		iMediator.tabSelection(aIndex);
	}
}




