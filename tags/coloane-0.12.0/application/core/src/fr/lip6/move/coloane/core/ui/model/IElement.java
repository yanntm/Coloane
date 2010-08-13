package fr.lip6.move.coloane.core.ui.model;

import fr.lip6.move.coloane.core.motor.formalism.ElementFormalism;
import fr.lip6.move.coloane.core.motor.formalism.Formalism;

import java.util.List;

/**
 * Interface definissant un element, tronc commun de tous les elements affiches sur l'editeur
 */
public interface IElement {

	/**
	 * Recupere la liste des attributs de l'objet
	 * @return La liste des element contenu dans l'objet
	 */
	List<IAttributeImpl> getAttributes();

	/**
	 * Retourne la valeur d'un attribut particulier ou la chaine vide si l'attribut n'existe pas
	 * @param attributeName L'attribut donton souhaite connaitre la valeur
	 * @return La valeur de l'attribut ou la chaine vide si l'attribut n'existe pas ou est vide
	 */
	String getAttributeValue(String attributeName);

	/**
	 * Associe le modele a l'arc generique
	 * @param modelAdapter
	 */
	void setModelAdapter(IModelImpl modelAdapter);

	/**
	 * Renvoie le modele augmente
	 * @return Le modele augmente
	 */
	IModelImpl getModelAdapter();

	/**
	 * Retourne l'identifiant de l'element
	 * @return l'identifiant
	 */
	int getId();

	/**
	 * Retourne le formalisme associe a l'arc augmente
	 * @return Formalism
	 */
	Formalism getFormalism();

	/**
	 * Retourne l'element de base de l'arc
	 * @return L'element de base de l'arc
	 */
	ElementFormalism getElementBase();

	/**
	 * Demande la mise en valeur des attributs attaches a l'objet
	 * @param light Epaisseur de la mise en valeur (survol = light, selection = heavy)
	 * @param state Selection / Deselection
	 */
	void setAttributesSelected(boolean light, boolean state);


	/**
	 * Permet de mettre en valeur l'arc
	 * @param state : L'etat de selection
	 */
	void setSelect(boolean state);

	/**
	 * Permet de mettre en valeur l'arc
	 * @param state : L'etat de selection
	 */
	void setSpecial(boolean state);
}