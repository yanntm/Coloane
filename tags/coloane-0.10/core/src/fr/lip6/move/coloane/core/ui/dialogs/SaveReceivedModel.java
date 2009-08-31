package fr.lip6.move.coloane.core.ui.dialogs;

import fr.lip6.move.coloane.core.main.Coloane;
import fr.lip6.move.coloane.core.ui.Editor;
import fr.lip6.move.coloane.core.ui.ModifyWorkspace;
import fr.lip6.move.coloane.core.ui.model.IModelImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Gere la sauvegarde d'un model recu de FrameKit.<br>
 * En general, ces modeles ne doivent pas s'appeler n'importe comment.
 * Ils doivent etre lies a leur modele de reference. Ici, leur nom est determine
 * a partir du nom du modele de base. Pour eviter les problemes, on evite les
 * doublons. Par consequent, on calcule des noms de fichiers qui n'existe pas deja.
 *
 */
public class SaveReceivedModel implements Runnable {

	private IModelImpl model;
	private IWorkbenchWindow window;

	/**
	 * Constructeur
	 * @param model Le modele a sauvegarder
	 */
	public SaveReceivedModel(IModelImpl m, IWorkbenchWindow w) {
		this.model = m;
		this.window = w;
	}

	/**
	 * Corps du thread
	 */
	public final void run() {

		// Definition des boutons de la boite de dialogue
		String[] buttons = new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL};

		// Definition de la boite de dialogue
		MessageDialog d = new MessageDialog(window.getShell(), Messages.SaveReceivedModel_0, null, Messages.SaveReceivedModel_1,	MessageDialog.QUESTION,	buttons, 0);

		// Ouverture de la boite de dialogue
		if (d.open() == IDialogConstants.OK_ID) {
			IPath path = ((Editor) window.getActivePage().getActiveEditor()).getCurrentPath();

			// Manipulation du path (pour ajout de l'extension du nouveu formalisme)
			path = path.removeFileExtension();
			path = path.addFileExtension(model.getFormalism().getExtension());

			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

			// On doit verifier que le fichier n'existe pas deja
			while (file.exists()) {

				// Le nom complet du fichier plus son extension
				String modelCompleteName = path.lastSegment();


				// Decoupage : L'extension + Le nom de fichier
				int extPos = modelCompleteName.lastIndexOf('.');
				String modelNameExtension = modelCompleteName.substring(extPos + 1);
				String modelName = modelCompleteName.substring(0, extPos);

				// On cherche si le modele possede deja un indicage
				int indPos = modelName.lastIndexOf("."); //$NON-NLS-1$
				if (indPos == -1) {
					modelName = modelName + ".2"; //$NON-NLS-1$
				} else {
					String currentIndice = modelName.substring(indPos + 1);

					// On verifie que l'extension est bien numerique
					try {
						int indice = Integer.parseInt(currentIndice);
						modelName = modelName.substring(0, indPos).concat(Integer.toString(indice + 1));
					} catch (NumberFormatException ne) {
						modelName = modelName + ".2"; //$NON-NLS-1$
					}
				}

				// Fin du nom
				modelCompleteName = modelName + "." + modelNameExtension; //$NON-NLS-1$
				path = path.removeLastSegments(1);
				path = path.append(modelCompleteName);
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}

			try {
				// Sauvegarde effective et affichage dans un nouvel onglet
				new ProgressMonitorDialog(window.getShell()).run(false,	false, new ModifyWorkspace(this.window, file, this.model.getGenericModel()));
			} catch (Exception e) {
				Coloane.showErrorMsg(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}