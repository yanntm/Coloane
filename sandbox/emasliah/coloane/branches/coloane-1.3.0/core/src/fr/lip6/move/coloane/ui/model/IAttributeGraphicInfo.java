package fr.lip6.move.coloane.ui.model;

import org.eclipse.draw2d.geometry.Point;

public interface IAttributeGraphicInfo {

	/**
	 * Types d'affichage
	 */

	/* Niveau 1 */
	int L1 = 1;
	int SIZE_L1 = 11;

	/* Niveau 2 */
	int L2 = 2;
	int SIZE_L2 = 10;

	/* Affichage normal */
	int NOR = 3;
	int SIZE_NOR = 9;

	/* Par defaut */
	int SIZE_DEF = 9;

	/* Police */
	String FONT = "arial";



	/**
	 * Retourne l'emplacement actuel de l'attribut
	 * @return Point
	 */
	Point getLocation();

	/**
	 * Change l'emplacement de l'attribut
	 * @param x Les abcisses
	 * @param y Les ordonees
	 */
	void setLocation(int x, int y);
}