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

import fr.lip6.move.coloane.core.session.ISession;
import fr.lip6.move.coloane.core.session.SessionManager;
import fr.lip6.move.coloane.core.ui.checker.Checker;
import fr.lip6.move.coloane.core.ui.checker.CheckerManager;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.INode;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;

/**
 * Users who want to implement a new Command which involve modifying attributes or adding, deleting, ... elements like {@link INode}, {@link IArc} <b>must</b> extend this class.<br>
 * If not, the checker won't check these modified, added, deleted ... elements.<br>
 * Just use the {@link CheckableCmd#addCheckableElement(IElement)} method to add the elements to check.<br>
 * For attributes, add the referenced element.
 *
 * @author Florian David
 * @author Jean-Baptiste Voron
 */
public abstract class CheckableCmd extends Command {
	/** Set of {@link IElement} to check when the command executed */
	private Set<IElement> checkableElements = new HashSet<IElement>();

	/** Constructor corresponding to {@link Command#Command()}. */
	public CheckableCmd() {
		super();
	}

	/**
	 * Constructor corresponding to {@link Command#Command(String)}.
	 * @param label the CheckableCmd's label.
	 */
	public CheckableCmd(String label) {
		super(label);
	}

	/**
	 * Add an element which will be check when the command is executed.
	 * @param element the element to check.
	 */
	protected final void addCheckableElement(IElement element) {
		this.checkableElements.add(element);
	}

	/**
	 * Call checkers on all elements in the checkableElements list.
	 */
	public final void checkElements() {
		// First we get the current session, the resource associated with the session, the session checker and the session graph.
		ISession session = SessionManager.getInstance().getCurrentSession();
		IGraph graph = session.getGraph();
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(session.getSessionId()));
		if (resource==null)
			return;
		
		Checker checker = session.getChecker();

		
		// We call the checker on the graph.
		CheckerManager.getInstance().checkGraph(checker, resource, graph);
		// Then for each IElement of checkableElement list, . . .
		for (IElement element : checkableElements) {
			if (element instanceof INode) {
				// We call the CheckerManager.getInstance().checkNode(...) method if it's a node . . .
				CheckerManager.getInstance().checkNode(checker, resource, graph, (INode) element);
			} else if (element instanceof IArc) {
				// or the CheckerManager.getInstance().checkArc(...) method if it's an arc.
				CheckerManager.getInstance().checkArc(checker, resource, graph, (IArc) element);
			}
		}
	}
}
