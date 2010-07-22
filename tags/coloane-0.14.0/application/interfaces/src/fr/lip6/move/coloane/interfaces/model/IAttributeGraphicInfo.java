package fr.lip6.move.coloane.interfaces.model;

import org.eclipse.swt.graphics.Color;

/**
 * Définition des propriétés et comportements graphique d'un objet attribut
 * @see IAttribute
 */
public interface IAttributeGraphicInfo extends ILocationInfo {

	/**
	 * @return couleur de fond
	 */
	Color getBackground();

	/**
	 * @param background La couleur d'arrière plan
	 */
	void setBackground(Color background);

	/**
	 * @return couleur du noeud
	 */
	Color getForeground();

	/**
	 * @param foreground La couleur d'avant plan
	 */
	void setForeground(Color foreground);

}