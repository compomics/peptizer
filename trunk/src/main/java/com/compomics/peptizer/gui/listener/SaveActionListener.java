package com.compomics.peptizer.gui.listener;

import com.compomics.peptizer.gui.SelectedPeptideIdentifications;
import com.compomics.peptizer.gui.dialog.SaveValidationDialog;
import com.compomics.peptizer.interfaces.ValidationSaver;
import com.compomics.peptizer.util.fileio.MatLogger;
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
 * This class is a
 */
public class SaveActionListener implements ActionListener, Observer {
    /**
     * Refrence to the parent dialog.
     */
    private SaveValidationDialog iSaveTaskDialog;

    /**
     * JBusyComponent to
     */
    private JBusyComponent<JPanel> iBusyComponent;

    public SaveActionListener(SaveValidationDialog aASaveTaskDialog, JBusyComponent<JPanel> aBusyComponent) {
        iSaveTaskDialog = aASaveTaskDialog;
        iBusyComponent = aBusyComponent;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            // This model will keep track of the save task.
            FutureBusyModel lFutureBusyModel = new FutureBusyModel();

            ValidationSaver lSaver = iSaveTaskDialog.getValidationSaver();
            // long long line, first gets the ValidationSaver from by gui input, then get the SelectedPeptideIdentifications from the gui and combine them.

            SelectedPeptideIdentifications lSelectedPeptideIdentifications = iSaveTaskDialog.getSelectedPeptideIdentifications();
            lSaver.setData(lSelectedPeptideIdentifications);
            lSaver.setObserver(this);

            // Submit the task!
            Future lFuture = Executors.newSingleThreadExecutor().submit(lSaver);

            // Fix the future to the model, and apply the model to the busy component.
            lFutureBusyModel.setFuture(lFuture);
            lFutureBusyModel.setCancellable(false);
            iBusyComponent.setBusyModel(lFutureBusyModel);


            // Ok, the action is done. Let the game continue!

        } catch (OutOfMemoryError oom) {
            MatLogger.logExceptionalEvent("Out of memory error!\nPlease supply the Java Virtual Machine(JVM) with more memory.\n\nExample: Startup parameter \"-Xmx512m\" supplies the JVM with 512m memory.");
            System.err.println("Out Of Memory Exception!!!!");
        }
    }

    /**
     * This can be updated by a MatWorker when the Task is finished.
     *
     * @param aObservable null
     * @param o           Materworker.result enum item.
     */
    public void update(Observable aObservable, Object o) {
        if (o.equals(WorkerResult.SUCCES)) {
            // Reset last save.
            iSaveTaskDialog.setChangedSinceLastSave(false);
            // Dispose the dialog.
            iSaveTaskDialog.getValidationSaver().finish();
            // Finally, clean up the saver.
            iSaveTaskDialog.dispose();
        }
    }
}
