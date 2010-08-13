package fr.lip6.move.coloane.interfaces.model;

import fr.lip6.move.coloane.interfaces.formalism.INodeFormalism;

import java.util.List;

/**
 * Définition d'un noeud du modèle<br>
 * Cette définition comporte aussi tous les événements qui peuvent se produire sur un noeud
 * @see IElement
 * @author Jean-Baptiste Voron
 * @author Clément Demoulins
 */
public interface INode extends IElement {

	/** ID pour la propriete lors d'un changement des arcs sortants */
	String OUTCOMING_ARCS_PROP = "Node.OutputArc"; //$NON-NLS-1$

	/** ID pour la propriete lors d'un changement des arcs entants */
	String INCOMING_ARCS_PROP = "Node.InputArc"; //$NON-NLS-1$

	/** ID pour la propriete lorsqu'un changement de la valeur */
	String VALUE_PROP = "Node.ValueUpdate"; //$NON-NLS-1$

	/** ID pour la propriete lorsque le noeud est selectionne */
	String SELECT_PROP = "Node.SelectUpdate"; //$NON-NLS-1$

	/** ID pour la propriete lorsque le noeud est deselectionne */
	String UNSELECT_PROP = "Node.UnSelectUpdate"; //$NON-NLS-1$

	/** ID pour la propriete lorsque le noeud est selectionne */
	String SPECIAL_PROP = "Node.SpecialUpdate"; //$NON-NLS-1$

	/** ID pour la propriete lorsque le noeud est deselectionne */
	String UNSPECIAL_PROP = "Node.UnSpecialUpdate"; //$NON-NLS-1$

	/** ID pour la propriété lorsque la couleur d'un noeud change */
	String BACKGROUND_COLOR_PROP = "Node.Color.Background"; //$NON-NLS-1$

	/** ID pour la propriété lorsque la couleur d'un noeud change */
	String FOREGROUND_COLOR_PROP = "Node.Color.Foreground"; //$NON-NLS-1$

	/** ID pour la propriété de changement de zoom */
	String RESIZE_PROP = "Node.Zoom"; //$NON-NLS-1$

	/**
	 * @return le FormalismElement décrivant ce noeud.
	 */
	INodeFormalism getNodeFormalism();

	/**
	 * @return les informations graphiques liées a l'arc.
	 */
	INodeGraphicInfo getGraphicInfo();

	/**
	 * Permet de parcourir la liste des arcs sortants de ce noeud.
	 * @return une liste <b>non modifiable</b>
	 */
	List<IArc> getOutcomingArcs();

	/**
	 * Permet de parcourir la liste des arcs entrants de ce noeud.
	 * @return une liste <b>non modifiable</b>
	 */
	List<IArc> getIncomingArcs();

	/**
	 * Positionne tous les attributs attaches a ce noeud en fonction du deplacement du noeud lui-meme
	 * @param deltaX Deplacement horizontal
	 * @param deltaY Deplacement vertical
	 */
	void updateAttributesPosition(int deltaX, int deltaY);

	/**
	 * Lors du deplacement d'un noeud, il est necessaire de faire suivre les attributs
	 * attaches aux arcs sortant et entrants du noeud en question
	 */
	void updateArcAttributesPosition();

	/**
	 * Mettre à jours les tips attachés à cette élément
	 */
	void updateTips();
}