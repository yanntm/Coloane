package fr.lip6.move.coloane.ui.model;

import java.util.List;

/**
 * Interface definissant un element, tronc commun de tous les elements affiches sur l'editeur
 *
 */

public interface IElement {

	/** 
	 * Recupere la liste des attributs de l'objet
	 * @return La liste des element contenu dans l'objet 
	 */	
	public List<IElement> getAttributes();
	
	/**
	 * Renvoie le modele augmente
	 */
	public IModelImpl getModelAdapter();
	
}