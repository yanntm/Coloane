package fr.lip6.move.coloane.core.ui.actions;

import fr.lip6.move.coloane.core.main.Coloane;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Demande de deconnexion de la session courante<br>
 * Cette action est provoquee par un clic de l'utilisateur sur :
 * <ul>
 * 	<li>Le bouton dans la barre d'outils</li>
 * 	<li>L'item dans le menu de la plateforme</li>
 * </ul>
 */
public class BreakConnectionAction implements IWorkbenchWindowActionDelegate {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public final void init(IWorkbenchWindow w) { }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public final void run(IAction action) {
		Coloane.getDefault().getMotor().breakConnection();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) { }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public final void dispose() { return; }
}
