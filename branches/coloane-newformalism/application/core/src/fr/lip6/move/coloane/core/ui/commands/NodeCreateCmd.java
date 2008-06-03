package fr.lip6.move.coloane.core.ui.commands;

import fr.lip6.move.coloane.core.exceptions.BuildException;
import fr.lip6.move.coloane.core.main.Coloane;
import fr.lip6.move.coloane.core.ui.model.IModelImpl;
import fr.lip6.move.coloane.core.ui.model.INodeImpl;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * Commande pour ajouter un nouveau noeud
 */
public class NodeCreateCmd extends Command {

	/** Nouveau noeud a creer */
	private INodeImpl newNode;

	/** Model */
	private final IModelImpl model;

	/** Limite */
	private Rectangle bounds;

	/**
	 * Creer une commande qui ajoutera le noeud au mod�le
	 *
	 * @param node Le nouveau noeud � ajouter
	 * @param m Le mod�le qui contiendra le noeud
	 * @param bound Les limites du noeud; (la taille peut �tre (-1, -1))
	 */
	public NodeCreateCmd(INodeImpl node, IModelImpl m, Rectangle b) {
		this.newNode = node;
		this.model = m;
		this.newNode.setModelAdapter(model);
		this.bounds = b;
	}

	/**
	 * Savoir si on peux executer la commande ?
	 * --> Toujours OK
	 * @return true
	 */
	@Override
	public final boolean canExecute() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public final void execute() {
		this.newNode.getGraphicInfo().setLocation(bounds.getLocation().x, bounds.getLocation().y);
		this.redo();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public final void redo() {
		try {
			model.addNode(newNode);
		} catch (BuildException e) {
			Coloane.getLogger().warning("Impossible de creer un noeud : " + e.getMessage()); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public final void undo() {
		try {
			model.removeNode(newNode);
		} catch (BuildException e) {
			Coloane.getLogger().warning("Impossible d'annuler la creation du noeud : " + e.getMessage()); //$NON-NLS-1$
		}
	}
}
