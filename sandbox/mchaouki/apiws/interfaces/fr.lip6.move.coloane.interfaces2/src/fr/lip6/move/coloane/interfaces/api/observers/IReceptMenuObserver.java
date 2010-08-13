package fr.lip6.move.coloane.interfaces.api.observers;

import fr.lip6.move.coloane.interfaces.api.evenements.IReceptMenu;

public interface IReceptMenuObserver {
	
	/**
	 * Met a jour l'observateur d'evenement : reception de menus
	 * @param e l'objet qui represent les menus ou les modifications a appliquer sur les menus
	 */
	public void update(IReceptMenu e);
}