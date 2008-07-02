package fr.lip6.move.coloane.core.motor.formalisms.elements;

import java.util.ArrayList;
import java.util.List;

import fr.lip6.move.coloane.core.motor.formalisms.interfaces.IElementFormalism;
import fr.lip6.move.coloane.core.motor.formalisms.interfaces.IGraphicalDescription;

/**
 * Cette classe représente un élément du base du formalisme.<br>
 * Un element de base est le composant d'un formalisme.<br>
 * Un element de base contient toutes les informations decrivant ce composant de formalisme :
 * <ul>
 * 	<li>Nom</li>
 * 	<li>Liste des attributs attachés à cet élément de formalisme</li>
 * </ul>
 */

public class ElementFormalism implements IElementFormalism {
	/** Nom associe a l'element de base. */
	private String name;

	/** Tableau des differents attributs de l'element de base. */
	private List<AttributeFormalism> attributes = new ArrayList<AttributeFormalism>(0);
	
	/** Description graphique de l'élément de formalisme */
	private IGraphicalDescription graphicalDescription;

	/**
	 * Constructeur
	 * @param name Nom de l'élément de base.
	 */
	public ElementFormalism(String name) {
		this.name = name;
	}

	/**
	 * Ajoute un attribut à l'element de base
	 * @param attribute {@link AttributeFormalism} à ajouter à la liste des attributs
	 */
	public final void addAttribute(AttributeFormalism attribute) { 
		this.attributes.add(attribute); 
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.motor.formalisms.elements.IElementFormalism#getName()
	 */
	public final String getName() {	return name; }

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.motor.formalisms.elements.IElementFormalism#getAttributes()
	 */
	public final List<AttributeFormalism> getAttributes() { return this.attributes; }

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.motor.formalisms.elements.IElementFormalism#getGraphicalDescription()
	 */
	public IGraphicalDescription getGraphicalDescription() {
		return graphicalDescription;
	}

	/**
	 * Ajoute la description graphique à la définition de l'élément de formalisme
	 * @param graphicalDescription La description graphique
	 */
	public void setGraphicalDescription(IGraphicalDescription graphicalDescription) {
		this.graphicalDescription = graphicalDescription;
	}
}
