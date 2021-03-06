/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.core.ui.wizards;

import fr.lip6.move.coloane.core.main.Coloane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * Wizard dedicated to new project creation
 *
 * @author Jean-Baptiste Voron
 */
public class NewProjectWizard extends Wizard implements INewWizard {
	/** the wizard page */
	private ProjectCreationPage page;

	/** {@inheritDoc} */
	@Override
	public final void addPages() {
		addPage(page);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean performFinish() {
		// Fetch information about the new project
		IProject newProject = page.getProjectHandle();

		// Fetch the template for new projects
		IProjectDescription basicDescription = ResourcesPlugin.getWorkspace().newProjectDescription(newProject.getName());

		// Set the nature of the project. Use the coloane one
		String[] natures = basicDescription.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = "fr.lip6.move.coloane.core.modelingproject"; //$NON-NLS-1$
		basicDescription.setNatureIds(newNatures);

		IPath platformPath = Platform.getLocation();
		try {
			IPath askedPath = page.getLocationPath();
			// Take care of the asked path
			if (!platformPath.equals(askedPath)) {
				platformPath = askedPath;
				basicDescription.setLocation(askedPath);
			}
		} catch (NullPointerException e) {
			// nothing, revert to platformpath
			// avoids the potential
			//at org.eclipse.ui.dialogs.WizardNewProjectCreationPage.getLocationPath(WizardNewProjectCreationPage.java:238)
			//at fr.lip6.move.coloane.core.ui.wizards.NewProjectWizard.performFinish(NewProjectWizard.java:71)
			// due to the wizard page never actually ahaving been initialized
		}

		try {
			// Check whether the project does not already exist
			if (!newProject.exists()) {
				newProject.create(basicDescription, new NullProgressMonitor());
			}

			// Open the project if it is not
			if (!newProject.isOpen()) {
				newProject.open(new NullProgressMonitor());
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// Select and reveal the project in the workbench window
		BasicNewProjectResourceWizard.updatePerspective(null);
		BasicNewResourceWizard.selectAndReveal(newProject, Coloane.getInstance().getWorkbench().getActiveWorkbenchWindow());
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final void init(IWorkbench workbench, IStructuredSelection selection) {
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(Coloane.class, "/resources/icons/newproject_corner.png")); //$NON-NLS-1$
		setWindowTitle(Messages.NewProjectWizard_1);
		page = new ProjectCreationPage("newproject", selection); //$NON-NLS-1$
	}
}
