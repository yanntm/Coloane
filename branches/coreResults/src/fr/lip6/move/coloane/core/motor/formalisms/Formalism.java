package fr.lip6.move.coloane.core.motor.formalisms;

import fr.lip6.move.coloane.core.motor.formalisms.constraints.IConstraintLink;
import fr.lip6.move.coloane.core.motor.formalisms.constraints.IConstraintNode;
import fr.lip6.move.coloane.interfaces.formalism.IArcFormalism;
import fr.lip6.move.coloane.interfaces.formalism.IFormalism;
import fr.lip6.move.coloane.interfaces.formalism.IGraphFormalism;
import fr.lip6.move.coloane.interfaces.model.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition d'un formalisme.<br>
 * L'instanciation d'un tel formalisme provoque systématiquement la création d'un objet {@link GraphFormalism}.<br>
 * Sans aucune autre précision, les éléments de formalisme créés par la suite seront associé à cet objet {@link GraphFormalism}.
 */
public class Formalism implements IFormalism {

	/** Id du formalisme. */
	private String id;

	/** Nom du formalisme. */
	private String name;

	/** Parent du formalisme (identifiant historique). */
	private String fkname;

	/** Liste des regles du formalisme concernant les liens entre objets. */
	private List<IConstraintLink> linkconstraints;

	/** Liste des regles du formalisme concernant les actions sur les noeuds. */
	private List<IConstraintNode> nodeconstraints;

	/** Le graphe défnini par le formalisme */
	private IGraphFormalism master = null;

	/** Nom du fichier de l'image avec extension ex: icon.gif */
	private String image;

	/**
	 * Création d'un formalisme
	 *
	 * @param id Id du formalisme
	 * @param name Nom du formalisme.
	 * @param fkname Le nom du formalisme à utiliser avec FrameKit
	 * @param image L'image associé à toutes les instances de ce formalisme
	 */
	Formalism(String id, String name, String fkname, String image) {
		this.id = id;
		this.name = name;
		this.fkname = fkname;
		this.image = image;

		this.linkconstraints = new ArrayList<IConstraintLink>();
		this.nodeconstraints = new ArrayList<IConstraintNode>();
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isLinkAllowed(INode source, INode target, IArcFormalism arcFormalism) {
		// Parcours de toutes les contraintes définies dans le formalisme
		for (IConstraintLink constraint : linkconstraints) {
			if (!constraint.isSatisfied(source, target, arcFormalism)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isActionAllowed(INode node) {
		// Parcours de toutes les contraintes définies dans le formalisme
		for (IConstraintNode constraint : nodeconstraints) {
			if (!constraint.isSatisfied(node)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Ajouter une contrainte de lien au formalisme
	 * @param constraint La contrainte de lien à ajouter au formalisme
	 * @see {@link IConstraintLink}
	 * @see {@link IConstraint}
	 */
	public final void addConstraintLink(IConstraintLink constraint) {
		if (constraint == null) { return; }
		this.linkconstraints.add(constraint);
	}

	/**
	 * Ajouter une contrainte de noeud au formalisme
	 * @param constraint La contrainte de noeud à ajouter au formalisme
	 * @see {@link IConstraintNode}
	 * @see {@link IConstraint}
	 */
	public final void addConstraintNode(IConstraintNode constraint) {
		if (constraint == null) { return; }
		this.nodeconstraints.add(constraint);
	}

	/** {@inheritDoc} */
	public final String getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getFKName() {
		return this.fkname;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getImageName() {
		return "/" + image; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public final IGraphFormalism getMasterGraph() {
		return this.master;
	}

	/**
	 * Indique quel est le graphe principal du formalisme (point d'entrée)
	 * @param master Le graphe principal du formalisme
	 */
	public final void setMasterGraph(IGraphFormalism master) {
		this.master = master;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return getName();
	}
}