package fr.lip6.move.coloane.ui.wizards;

import fr.lip6.move.coloane.interfaces.model.IModel;
import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.model.Model;
import fr.lip6.move.coloane.motor.formalism.FormalismManager;
import fr.lip6.move.coloane.ui.XmlEditor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

public class ModelCreationPage extends WizardNewFileCreationPage {

	private final IWorkbench workbench;
	private static int fileCount = 0;

	/**
	 * Creer une nouvelle wizard page
	 * @param workbench le workbench courant
	 * @param selection l'object selection courant
	 */
	public ModelCreationPage(IWorkbench currentWorkbench, IStructuredSelection currentSelection) {
		super("newmodel", currentSelection); //$NON-NLS-1$
		this.workbench = currentWorkbench;
		setTitle(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.1")); //$NON-NLS-1$
		setDescription(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.2")); //$NON-NLS-1$
		setPageComplete(true);
	}

	/**
	 * Cette methode ajoute les controle pour visualiser les projets ouverts
	 * pour le control standard de WizardNewFileCreationPage
	 */
	@Override
	public final void createControl(Composite parent) {
		super.createControl(parent);

		// On propose un nom par defaut
		/* TODO: Meilleure gestion des nouveaux nom de fichier... */
		setFileName(Coloane.getParam("WIZARD_FILENAME_BASE") + "_" + fileCount); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Permission de passer a la page suivante ?
	 * @return false
	 * @see NewModelWizard#canFlipToNextPage()
	 */
	@Override
	public final boolean canFlipToNextPage() {
		return false;
	}

	/**
	 * Methode invoquee lorsque le bouton finish est pressee
	 * @return true si ok
	 * @see NewModelWizard#performFinish()
	 */
	public final boolean finish() {

		FormalismManager formManager = Coloane.getDefault().getMotor().getFormalismManager();

		// On doit verifier que le chargement du formalismManager est OK
		if (formManager == null) {
			setErrorMessage(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.5")); //$NON-NLS-1$
			return false;
		}

		// Recupere le nom du formalisme deceide la page precedente
		String formalismName = ((NewModelWizard) getWizard()).getFormalismName();
		String extension = formManager.loadFormalism(formalismName).getExtension();
		setFileName(getFileName() + "." + extension); //$NON-NLS-1$

		// Tentative de creation de fichier
		// newFile != null si la creation reussie
		IFile newFile = createNewFile();
		if (newFile == null) {
			setErrorMessage(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.7")); //$NON-NLS-1$
			System.err.println(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.8")); //$NON-NLS-1$
			return false;
		}

		// Changement de l'increment pour le prochain nom temporaire
		fileCount++;

		// Ouverture du nouveau fichier
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		if (newFile != null && page != null) {
			try {
				IDE.openEditor(page, newFile, true);
			} catch (CoreException ce) {
				System.err.println(Coloane.getTranslate().getString("ui.wizards.ModelCreationPage.9")); //$NON-NLS-1$
				Coloane.showErrorMsg(ce.getMessage());
				return false;
			}
		}
		return true;
	}

	/**
	 * Methode remplissant le fichier avec un contenu par defaut
	 * @return InputStream
	 */
	@Override
	protected final InputStream getInitialContents() {

		// InputStream a retourner
		InputStream inputS = null;

		// Nouveau model
		IModel model = new Model();

		// Formalisme choisi
		String formalismName = ((NewModelWizard) getWizard()).getFormalismName();

		model.setFormalism(formalismName);

		// Traduction du modele au format xml
		String xmlString = XmlEditor.translateToXML(model);

		// Creation de l'input stream a partir d'une chaine de caractere
		inputS = new ByteArrayInputStream(xmlString.getBytes());

		return inputS;
	}

}