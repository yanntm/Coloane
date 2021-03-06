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
package fr.lip6.move.coloane.core.ui.checker;

import fr.lip6.move.coloane.core.ui.commands.CheckableCmd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * This class listens to the {@link CommandStack}.
 * When a command {@link CheckableCmd} is executed, this class executes {@link CheckableCmd#checkElements()} of each command.
 * @author Florian David
 */
public class CommandStackListener implements CommandStackEventListener {

	/** {@inheritDoc} */
	@Override
	public final void stackChanged(CommandStackEvent event) {
		if ((event.getDetail() & CommandStack.POST_MASK) != 0) {
			List<CheckableCmd> checkableCommandsList = new ArrayList<CheckableCmd>();

			Command eventCommand = event.getCommand();
			// A CompoundCommand is sometimes given when an undo/redo is done.
			// We have to get all the CheckableCmd from it.
			if (eventCommand instanceof CompoundCommand) {
				CompoundCommand compoundCommand = (CompoundCommand) eventCommand;
				for (Object command : compoundCommand.getCommands()) {
					if (command instanceof CheckableCmd) {
						checkableCommandsList.add((CheckableCmd) command);
					}
				}
			} else {
				if (eventCommand instanceof CheckableCmd) {
					checkableCommandsList.add((CheckableCmd) eventCommand);
				}
			}

			// Then we call the checkElements() method for all CheckableCmd
			for (CheckableCmd command : checkableCommandsList) {
				command.checkElements();
			}
		}
	}
}
