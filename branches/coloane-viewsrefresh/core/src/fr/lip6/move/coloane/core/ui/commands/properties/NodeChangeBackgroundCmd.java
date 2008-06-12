package fr.lip6.move.coloane.core.ui.commands.properties;

import fr.lip6.move.coloane.core.ui.model.INodeImpl;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Color;

/**
 * Commande pour la couleur de l'arrière plan d'un noeud
 */
public class NodeChangeBackgroundCmd extends Command {
	private Color oldColor;
	private Color newColor;
	private INodeImpl node;

	/**
	 * @param node Noeud à modifier
	 * @param color Nouvelle couleur
	 */
	public NodeChangeBackgroundCmd(INodeImpl node, Color color) {
		this.node = node;
		this.newColor = color;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public final void execute() {
		oldColor = node.getGraphicInfo().getBackground();
		redo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public final void redo() {
		try {
		node.getGraphicInfo().setBackground(newColor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public final void undo() {
		try {
		node.getGraphicInfo().setBackground(oldColor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
