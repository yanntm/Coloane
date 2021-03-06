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
package fr.lip6.move.coloane.core.extensions;

import fr.lip6.move.coloane.core.model.factory.FormalismManager;
import fr.lip6.move.coloane.interfaces.extensions.IImportFrom;
import fr.lip6.move.coloane.interfaces.formalism.IFormalism;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Describe an importe extension.<br>
 */
public final class ImportFromExtension {
	/**
	 * Extension attributes
	 */
	private static final String EXTENSION_POINT_ID = "fr.lip6.move.coloane.core.imports"; //$NON-NLS-1$

	private static final String WIZREF_EXTENSION = "wizard_id"; //$NON-NLS-1$
	private static final String CLASS_EXTENSION = "class"; //$NON-NLS-1$
	private static final String FORMALISMS_EXTENSION = "id"; //$NON-NLS-1$

	/** The logger */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/**
	 * Constructor
	 */
	private ImportFromExtension() {	}

	/**
	 * Build a import builder
	 * @param importType référence du convertiseur a instancier
	 * @return un convertiseur
	 * @throws CoreException Exception lors de la creation de une instance
	 */
	public static IImportFrom createConvertInstance(String importType) throws CoreException {
		// Fetch all available builders, and try to match with the importType
		IConfigurationElement[] contributions = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		IConfigurationElement convertContribution = null;
		for (int i = 0; i < contributions.length; i++) {
			if (contributions[i].getAttribute(WIZREF_EXTENSION).equals(importType)) {
				convertContribution = contributions[i];
				break;
			}
		}

		// Set up the builder
		IImportFrom convertInstance = null;
		if (convertContribution != null) {
			convertInstance = (IImportFrom) convertContribution.createExecutableExtension(CLASS_EXTENSION);
		}

		return convertInstance;
	}

	/**
	 * Fetch all available formalism for this format
	 * @param importType The type of import... This information is used in order to find mathing builders
	 * @return The list of supported formalism for this import wizard
	 */
	public static List<IFormalism> getFormalisms(String importType) {
		List<IFormalism> formalisms = new ArrayList<IFormalism>();
		IConfigurationElement[] contributions = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);

		// Browse all extensions, and build the list of formalisms
		for (IConfigurationElement contribution : contributions) {
			if (contribution.getAttribute(WIZREF_EXTENSION).equals(importType)) {
				for (IConfigurationElement child : contribution.getChildren()) {
					try {
						formalisms.add(FormalismManager.getInstance().getFormalismById(child.getAttribute(FORMALISMS_EXTENSION)));
					} catch (IllegalArgumentException e) {
						LOGGER.info("This import extension can import " + child.getAttribute(FORMALISMS_EXTENSION) + " but this formalism is not installed."); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}

		return formalisms;
	}
}
