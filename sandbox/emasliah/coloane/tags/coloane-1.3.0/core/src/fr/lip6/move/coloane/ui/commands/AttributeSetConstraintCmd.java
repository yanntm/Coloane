package fr.lip6.move.coloane.ui.commands;

import fr.lip6.move.coloane.ui.model.IAttributeImpl;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class AttributeSetConstraintCmd extends Command {

	/** Enregistre la nouvelle taille et le nouvel endroit */
	private final Rectangle newBounds;

	/** Enregistre l'ancienne taille et le nouvel endroit */
	private Point oldBounds;

	/** Noeud � manipuler */
	private final IAttributeImpl attribute;

	/**
	 * Constructeur
	 * @param node noeud
	 * @param newBounds Nouvelles limites
	 */
	public AttributeSetConstraintCmd(IAttributeImpl a, Rectangle newB) {
		if (a == null || newB == null) {
			throw new IllegalArgumentException();
		}
		this.attribute = a;
		this.newBounds = newB.getCopy();
	}

	/**
	 * On peut toujours deplacer un noeud.
	 * Le redimensionnement est bloque automatiquement par les EditPolicy
	 * @return booleen
	 */
	public final boolean canExecute() {
		return true;
	}

	/**
	 * Executer
	 */
	public final void execute() {
		oldBounds = attribute.getGraphicInfo().getLocation();
		this.redo();
	}

	/**
	 * Refaire
	 */
	public final void redo() {
		attribute.getGraphicInfo().setLocation(newBounds.getLocation().x, newBounds.getLocation().y);
	}

	/**
	 * Annuler
	 */
	public final void undo() {
		attribute.getGraphicInfo().setLocation(oldBounds.x, oldBounds.y);
	}

}