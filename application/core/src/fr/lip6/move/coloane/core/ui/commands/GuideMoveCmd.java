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

import fr.lip6.move.coloane.core.model.interfaces.ILocatedElement;
import fr.lip6.move.coloane.core.ui.rulers.EditorGuide;
import fr.lip6.move.coloane.core.ui.rulers.EditorRulerProvider;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Move a guide with all attached nodes
 */
public class GuideMoveCmd extends Command {

	/** The guide that is moved */
	private EditorGuide guide;

	/** Guide move according its axis */
	private int delta;

	/** Objects that are sticked to the guide have to be moved too */
	private CompoundCommand moveElementsCommand;

	/**
	 * Constructor
	 * @param guide The guide that is moved
	 * @param delta The move
	 */
	public GuideMoveCmd(EditorGuide guide, int delta) {
		super(Messages.MoveGuideCommand_0);
		this.guide = guide;
		this.delta = delta;

		// Must move all elements... Create commands for all attached nodes
		moveElementsCommand = new CompoundCommand();
		for (ILocatedElement attachedElement : guide.getAttachedElements()) {
			Point location = attachedElement.getLocationInfo().getLocation().getCopy();
			if (guide.getOrientation() == EditorRulerProvider.VERTICAL_ORIENTATION) {
				// Move along the horizontal axis
				location.x += delta;
			} else {
				// Move along the vertical axis
				location.y += delta;
			}
			// Add the element move command to the compound command
			moveElementsCommand.add(new LocatedElementSetConstraintCmd(attachedElement, location));
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void execute() {
		guide.setPosition(guide.getPosition() + delta);
		moveElementsCommand.execute();
	}

	/** {@inheritDoc} */
	@Override
	public final void undo() {
		guide.setPosition(guide.getPosition() - delta);
		moveElementsCommand.undo();
	}
}
