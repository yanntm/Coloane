package fr.lip6.move.coloane.interfaces.model.command;

import fr.lip6.move.coloane.interfaces.exceptions.ModelException;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IGraph;

/**
 * Commande de création d'un attribut
 *
 * @author Jean-Baptiste Voron
 */
public class CreateAttributeCommand implements ICommand {
	/** Le nom de l'attribut à créer */
	private String name;
	/** Le noeud de référence de l'attribut qui doit être créé */
	private int referenceId;
	/** La valeur de l'attribut à créer */
	private String value;

	/**
	 * Constructeur
	 * @param name Le nom de l'attribut à créer
	 * @param referenceId Le noeud de référence de l'attribut qui doit être créé
	 * @param value La valeur de l'attribut à créer
	 */
	public CreateAttributeCommand(String name, int referenceId, String value) {
		this.name = name;
		this.referenceId = referenceId;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void execute(IGraph graph) throws ModelException {
		if (referenceId == 1) {
			if (graph.getAttribute(name) != null) {
				graph.getAttribute(name).setValue(value);
			} else {
				// Attribut du graphe inexistant pour le formalisme
				throw new ModelException("The attribute " + name + " cannot be found for the graph element");
			}
		} else if (graph.getObject(referenceId) != null) {
			IAttribute attribute = graph.getObject(referenceId).getAttribute(name);
			if (attribute != null) {
				attribute.setValue(value);
			} else {
				// Attribut introuvable
				throw new ModelException("The attribute " + name + " cannot be found for the element" + referenceId);
			}
		} else {
			// L'element n'existe meme pas
			throw new ModelException("The element " + referenceId + " connot be retrived from the graph");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void redo(IGraph graph) { }

	/**
	 * {@inheritDoc}
	 */
	public void undo(IGraph graph) { }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "Creation attribut " + name + " (objet: " + referenceId + ") : value=" + value;
	}
}
