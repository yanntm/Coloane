package fr.lip6.move.coloane.core.ui.wizards.importmodel;

import fr.lip6.move.coloane.core.exceptions.ColoaneException;
import fr.lip6.move.coloane.core.extensions.IImportFrom;
import fr.lip6.move.coloane.core.extensions.ImportFromExtension;
import fr.lip6.move.coloane.core.main.Coloane;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Assistant d'import de fichier (externes au workspace).
 * Cet assistant est composé d'une page {@link ImportWizardPage}
 */
public class ImportWizard extends Wizard implements IImportWizard, IExecutableExtension {

	/** Identifiant de l'assistant (wizard) **/
	private String idWizard = null;

	/** La page du wizard chargee d'afficher les controles graphiques **/
	private ImportWizardPage selectFilePage;

	/** L'instance de conversion a utiliser */
	private IImportFrom instance;

	/** {@inheritDoc} */
	public final void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.ImportWizard_0);
		selectFilePage = new ImportWizardPage(workbench, selection, instance);
		setNeedsProgressMonitor(true);
	}

	/** {@inheritDoc} */
	@Override
	public final void addPages() {
		addPage(selectFilePage);
		super.addPages();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean canFinish() {
		if (this.idWizard == null) {
			selectFilePage.setErrorMessage(Messages.ImportWizard_1);
			return false;
		}
		return super.canFinish();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean performFinish() {
		try {
			selectFilePage.finish();
			return true;
		} catch (ColoaneException e) {
			Coloane.getLogger().warning("Echec de l'import : " + e.getMessage()); //$NON-NLS-1$
			Coloane.showErrorMsg(Messages.ImportWizard_3);
			return false;
		}
	}

	/**
	 * Indique l'identifiant du wizard (extension) ayant appelee cette methode.
	 * Cet identifiant est necessaire pour determiner le type de convertisseur a instancier
	 * @param id Le format a utiliser pour l'export
	 */
	protected final void setImportFormat(String id) {
		this.idWizard = id;
	}

	/** {@inheritDoc} */
	public final void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		this.idWizard = config.getAttribute("id"); //$NON-NLS-1$
		Coloane.getLogger().finer("Wizard selectionne : " + idWizard); //$NON-NLS-1$

		this.instance = (IImportFrom) ImportFromExtension.createConvertInstance(this.idWizard);

		if (this.instance == null) {
			Coloane.getLogger().warning("Erreur lors de la creation de l'instance de conversion"); //$NON-NLS-1$
		}
	}
}
