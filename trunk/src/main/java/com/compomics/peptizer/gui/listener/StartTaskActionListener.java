package com.compomics.peptizer.gui.listener;

import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.dialog.CreateTaskDialog;
import com.compomics.peptizer.gui.interfaces.ImportPanel;
import com.compomics.peptizer.gui.progressbars.DefaultProgressBar;
import com.compomics.peptizer.interfaces.AgentAggregator;
import com.compomics.peptizer.interfaces.PeptideIdentificationIterator;
import com.compomics.peptizer.util.datatools.IdentificationFactory;
import com.compomics.peptizer.util.fileio.MatLogger;
import com.compomics.peptizer.util.worker.MatWorker;
import com.compomics.peptizer.util.worker.WorkerResult;
import org.divxdede.swing.busy.FutureBusyModel;
import org.divxdede.swing.busy.JBusyComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This actionlistener is invoked when a "Start" button is clicked upon starting a new Peptizer Task.
 */
public class StartTaskActionListener implements ActionListener, Observer {
    /**
     * Refrence to the parent dialog.
     */
    private CreateTaskDialog iCreateTaskDialog;

    /**
     * JBusyComponent to
     */
    private JBusyComponent<JPanel> iBusyContent;

    public StartTaskActionListener(CreateTaskDialog aCreateTaskDialog, JBusyComponent<JPanel> aBusyContent) {
        iCreateTaskDialog = aCreateTaskDialog;
        iBusyContent = aBusyContent;
    }

    public void actionPerformed(ActionEvent e) {
        try {

            DefaultProgressBar lProgress = new DefaultProgressBar(iCreateTaskDialog.getPeptizerGUI(), "Data loading", 0, 2);
            MatLogger.logNormalEvent("New task started.");

            IdentificationFactory.getInstance().reset();
            ImportPanel importPanel = iCreateTaskDialog.getSelectedImport();
            importPanel.loadIdentifications(lProgress);
            PeptideIdentificationIterator iter = IdentificationFactory.getInstance().getIterator();

            if (iter != null) {
                AgentAggregator lAggregator = null;
                lAggregator = iCreateTaskDialog.getAgentAggregator();

                SelectedPeptideIdentifications lSelectedPeptideIdentifications = new SelectedPeptideIdentifications();

                MatWorker worker = new MatWorker(iter, lAggregator, lSelectedPeptideIdentifications, iCreateTaskDialog.getPeptizerGUI(), this);

                FutureBusyModel lFutureBusyModel = new FutureBusyModel();
                lFutureBusyModel.setCancellable(false);
                worker.setFutureBusyModel(lFutureBusyModel);

                Future lFuture = Executors.newSingleThreadExecutor().submit(worker);

                lFutureBusyModel.setFuture(lFuture);

                iBusyContent.setBusyModel(lFutureBusyModel);
            }

//            CreateTaskDialog.this.startPressed();
        } catch (OutOfMemoryError oom) {
            MatLogger.logExceptionalEvent("Out of memory error!\nPlease supply the Java Virtual Machine(JVM) with more memory.\n\nExample: Startup parameter \"-Xmx512m\" supplies the JVM with 512m memory.");
            System.err.println("Out Of Memory Exception!!!!");
        }
    }


    /**
     * This can be updated by a MatWorker when the Task is finished.
     * @param aObservable null
     * @param o Materworker.result enum item.
     */
    public void update(Observable aObservable, Object o) {
        if(o.equals(WorkerResult.SUCCES)){
            iCreateTaskDialog.dispose();
        }
    }
}
