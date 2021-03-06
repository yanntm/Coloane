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

import fr.lip6.move.coloane.interfaces.model.IArc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.gef.commands.Command;

/**
 * Command that remove all bendpoints from the considered arc
 */
public class InflexDeleteAllCmd extends Command {

	/** Arc on which the action is done */
	private final IArc arc;

	/** The list of bendpoints attached to this arc (required in case of UNDO) */
	private final List<AbsoluteBendpoint> previousList = new ArrayList<AbsoluteBendpoint>();

	/**
	 * Constructor
	 * @param arc Arc on wich the action is done
	 */
	public InflexDeleteAllCmd(IArc arc) {
		super(Messages.ArcChangeCurve_0);
		this.arc = (IArc) arc;
	}

	/** {@inheritDoc} */
	@Override
	public final void execute() {
		this.previousList.clear();
		this.previousList.addAll(arc.getInflexPoints());
		this.redo(); // Execute
	}

	/** {@inheritDoc} */
	@Override
	public final void redo() {
		arc.removeAllInflexPoints();
	}

	/** {@inheritDoc} */
	@Override
	public final void undo() {
		for (AbsoluteBendpoint p : this.previousList) {
			arc.addInflexPoint(p);
		}
	}
}
