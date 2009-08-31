package fr.lip6.move.coloane.core.model.interfaces;

/**
 * Interface pour un lien.
 */
public interface ILink {

	/**
	 * Reconnection du lien
	 * @param newSource nouvelle note source
	 * @param newTarget nouvelle cible
	 */
	void reconnect(IStickyNote newSource, ILinkableElement newTarget);

	/**
	 * @return source du lien
	 */
	IStickyNote getSource();

	/**
	 * @return cible du lien
	 */
	ILinkableElement getTarget();
}