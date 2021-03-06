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
package fr.lip6.move.coloane.core.ui.commands;

import fr.lip6.move.coloane.core.model.interfaces.ILinkableElement;

import org.eclipse.gef.commands.Command;

/**
 * Begin the creation of a link (virtual link between an object and a note)
 *
 * @author Jean-Baptiste Voron
 * @author Clément Démoulins
 */
public class LinkCreateCmd extends Command {
	/** The object which is linked to a note */
	private ILinkableElement source;

	/**
	 * Constructor
	 * @param source Source link
	 */
	public LinkCreateCmd(ILinkableElement source) {
		this.source = source;
	}

	/**
	 * @return The source link
	 */
	public final ILinkableElement getSource() {
		return source;
	}
}
