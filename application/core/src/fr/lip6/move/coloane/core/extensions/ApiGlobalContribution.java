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

import fr.lip6.move.coloane.core.session.ISessionManager;
import fr.lip6.move.coloane.core.session.SessionManager;
import fr.lip6.move.coloane.core.ui.actions.LocalAction;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * Retrieve contribution items of all globals apis.
 */
public class ApiGlobalContribution extends CompoundContributionItem {
	/** The logger */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/** {@inheritDoc} */
	@Override
	protected final IContributionItem[] getContributionItems() {
		ISessionManager sessionManager = SessionManager.getInstance();
		LOGGER.fine("Browsing available Global APIs");  //$NON-NLS-1$

		List<ApiDescription> availableApis = sessionManager.getAvailableGlobalApis();
		IContributionItem[] toReturn = new IContributionItem[availableApis.size()];
		for (int i = 0; i < availableApis.size(); i++) {
			toReturn[i] = availableApis.get(i).getRootMenu();
		}

		if (toReturn.length == 0) {
			LOGGER.finer("No Global API detected");  //$NON-NLS-1$
			toReturn = new IContributionItem[1];
			IAction localAction = new LocalAction("Waiting for Global Services Platforms...", "", null, null);  //$NON-NLS-1$//$NON-NLS-2$
			localAction.setEnabled(false);
			toReturn[0] = new ActionContributionItem(localAction);
		}

		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDynamic() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isDirty() {
		return true;
	}
}
