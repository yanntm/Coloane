package fr.lip6.move.coloane.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.ui.Editor;
import fr.lip6.move.coloane.ui.MainPerspectiveFactory;
import fr.lip6.move.coloane.ui.menus.MenuManipulation;
import fr.lip6.move.coloane.ui.panels.HistoryView;

public class DisconnectModelAction implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		/*
		 * If we don't call this method here, the view is not
		 * initialized and HistoryView.instance
		 * is null (and it is bad).
		 */
		try {
			window.getActivePage().showView(MainPerspectiveFactory.HISTORY_VIEW);
		} catch (PartInitException e) {
			MessageDialog.openError(window.getShell(),
					Coloane.traduction.getString("ui.actions.DisconnectModelAction.0"), //$NON-NLS-1$
					Coloane.traduction.getString("ui.actions.DisconnectModelAction.1")); //$NON-NLS-1$
		}
		
		System.out.println(Coloane.traduction.getString("ui.actions.DisconnectModelAction.2")); //$NON-NLS-1$
		HistoryView.instance.addLine(Coloane.traduction.getString("ui.actions.DisconnectModelAction.3")); //$NON-NLS-1$

		if(window.getActivePage().getActiveEditor() == null) {
			HistoryView.instance.addLine(Coloane.traduction.getString("ui.actions.DisconnectModelAction.4")); //$NON-NLS-1$
		} else {
			Editor editor = (Editor) window.getActivePage().getActiveEditor();

			try {
				if (editor.getModel() != null) {
					// Le modele existe... On peut essayer de le connecter
					HistoryView.instance.addText(Coloane.traduction.getString("ui.actions.DisconnectModelAction.5")); //$NON-NLS-1$


					if (Coloane.getDefault().getMotor().closeSession()) {
						// TODO : Griser les menues adequats
						HistoryView.instance.addLine(Coloane.traduction.getString("ui.actions.DisconnectModelAction.6")); //$NON-NLS-1$
						MenuManipulation.setEnabled("Platform", "Connect model", true); //$NON-NLS-1$ //$NON-NLS-2$
						MenuManipulation.setEnabled("Platform", "Disconnect model", false); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						// TODO : GRiser les menus adequats
						HistoryView.instance.addLine(Coloane.traduction.getString("ui.actions.DisconnectModelAction.11")); //$NON-NLS-1$
					}

				} else {
					HistoryView.instance.addText(Coloane.traduction.getString("ui.actions.DisconnectModelAction.12")); //$NON-NLS-1$
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

}
