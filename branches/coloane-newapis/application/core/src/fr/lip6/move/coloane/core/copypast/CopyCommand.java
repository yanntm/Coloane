package fr.lip6.move.coloane.core.copypast;

import fr.lip6.move.coloane.core.copypast.container.GraphContainer;
import fr.lip6.move.coloane.core.ui.ColoaneEditor;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.INode;

import java.util.logging.Logger;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

public class CopyCommand extends Command {
	private Logger log = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	private GraphContainer container;

	public CopyCommand(ColoaneEditor editor) {
		container = new GraphContainer(editor.getGraph().getFormalism());
	}

	/**
	 * Ajout d'un noeud à copier
	 * @param node
	 */
	public final void addNode(INode node) {
		container.addNode(node);
	}

	/**
	 * Ajout d'un arc à copier
	 * @param arc
	 */
	public final void addArc(IArc arc) {
		container.addArc(arc);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean canExecute() {
		return !container.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean canUndo() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public final void execute() {
		if (!canExecute()) {
			return;
		}
		log.fine("Copie de la sélection"); //$NON-NLS-1$
		Clipboard.getDefault().setContents(container);
	}

}
