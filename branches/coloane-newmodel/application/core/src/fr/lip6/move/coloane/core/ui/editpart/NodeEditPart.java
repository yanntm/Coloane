package fr.lip6.move.coloane.core.ui.editpart;

import fr.lip6.move.coloane.core.main.Coloane;
import fr.lip6.move.coloane.core.model.AbstractPropertyChange;
import fr.lip6.move.coloane.core.ui.commands.ArcCompleteCmd;
import fr.lip6.move.coloane.core.ui.commands.ArcCreateCmd;
import fr.lip6.move.coloane.core.ui.commands.ArcReconnectCmd;
import fr.lip6.move.coloane.core.ui.commands.NodeDeleteCmd;
import fr.lip6.move.coloane.core.ui.figures.INodeFigure;
import fr.lip6.move.coloane.core.ui.figures.nodes.Circle;
import fr.lip6.move.coloane.interfaces.formalism.IArcFormalism;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.INode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.graphics.Color;

/**
 * EditPart pour les noeuds
 */
public class NodeEditPart extends AbstractGraphicalEditPart implements ISelectionEditPartListener, PropertyChangeListener, org.eclipse.gef.NodeEditPart {
	/**
	 * Logger 'fr.lip6.move.coloane.core'.
	 */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	private ConnectionAnchor connectionAnchor;

	/**
	 * Creation de la figure associee (VUE)
	 * @return IFigure
	 */
	@Override
	protected final IFigure createFigure() {
		INode node = (INode) getModel();
//		return node.getNodeFormalism().getGraphicalDescription().getAssociatedFigure();
		return new Circle(node.getGraphicInfo());
	}


	/**
	 * Mise a jour de la vue a partir des informations du modele<br>
	 * La mise a jour utilise des methodes de parcours du modele et de moficiation de la vue
	 */
	@Override
	protected final void refreshVisuals() {
		INode nodeModel = (INode) getModel();

		Rectangle bounds = new Rectangle(nodeModel.getGraphicInfo().getLocation(), nodeModel.getGraphicInfo().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);

		// Il faut avertir FrameKit
		Coloane.notifyModelChange(nodeModel);
	}

	/**
	 * Traitements a effectuer lors de la reception d'un evenement sur l'EditPart
	 * @param property L'evenement qui a ete levee
	 */
	public final void propertyChange(PropertyChangeEvent property) {
		String prop = property.getPropertyName();

		// Propriete de connexion
		if (INode.INCOMING_ARCS_PROP.equals(prop)) {
			LOGGER.finest("Mise à jour des arcs entrants."); //$NON-NLS-1$
			refreshTargetConnections();
		} else if (INode.OUTCOMING_ARCS_PROP.equals(prop)) {
			LOGGER.finest("Mise à jour des arcs sortants."); //$NON-NLS-1$
			refreshSourceConnections();

		// Propriété de changement de couleur
		} else if (INode.FOREGROUND_COLOR_PROP.equalsIgnoreCase(prop)) {
			((INodeFigure) getFigure()).setForegroundColor((Color) property.getNewValue());
		} else if (INode.BACKGROUND_COLOR_PROP.equalsIgnoreCase(prop)) {
			((INodeFigure) getFigure()).setBackgroundColor((Color) property.getNewValue());

		// Propriété de changement de taille
		} else if (INode.RESIZE_PROP.equalsIgnoreCase(prop)) {
			INodeFigure nodeFigure = (INodeFigure) getFigure();
			Rectangle oldRect = nodeFigure.getClientArea();
			nodeFigure.setSize((Dimension) property.getNewValue());
			((GraphEditPart) getParent()).getFigure().repaint(oldRect);

		// Propriété de changement d'un attribut
		} else if (IElement.ATTRIBUTE_CHANGE.equals(prop)) {
			getParent().refresh();
		}

		refreshVisuals();
	}

	/**
	 * Regles de gestion de l'objet
	 */
	@Override
	protected final void createEditPolicies() {
		/* Ensemble de regles concernant la selection/deselection de l'objet */
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionEditPolicy() {

			// Comportement lors de la deselection de l'objet
			@Override
			protected void hideSelection() {
				if (getSelected() == SELECTED_NONE) {
					INodeFigure nodeFigure = (INodeFigure) getFigure();
					nodeFigure.setUnselect();
				}
			}

			// Comportement lors de la selection de l'objet
			@Override
			protected void showSelection() {
				if (getSelected() == SELECTED || getSelected() == SELECTED_PRIMARY) {
					INodeFigure nodeFigure = (INodeFigure) getFigure();
					nodeFigure.setSelect();
				}
			}

			// Comportement lorsque l'objet est selectionne
			@Override
			protected void setSelectedState(int state) {
				super.setSelectedState(state);
			}
		});

		/* Ensemble des regles concernant le role profond de l'element du modele */
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {

			// On autorise la suppression de l'element
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				IGraph parent = (IGraph) getHost().getParent().getModel();
				INode child = (INode) getHost().getModel();
				Command cmd =  new NodeDeleteCmd(parent, child);
				return cmd;
			}

		});

		/* Ensembles de regles gouvernant la creation et le maintient des connexions inter-noeuds */
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {

			/**
			 * Premiere etape de la creation d'un lien.<br>
			 * Lorsque l'utilisateur clique sur le noeud de depart, la commande CREATE est appelee.
			 * @see getConnectionCompleteCommand
			 */
			@Override
			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				INode source = (INode) getHost().getModel();

				// Demande la creation d'un arc (1ere etape)
				ArcCreateCmd cmd = new ArcCreateCmd(source, (IArcFormalism) request.getNewObjectType());
				request.setStartCommand(cmd);
				return cmd;
			}

			/**
			 * Deuxieme etape de la creation d'un lien.<br>
			 * Lorsque l'utilisateur clique sur le noeud d'arrivee, la commande COMPLETE est appelee.
			 */
			@Override
			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {

				// !! Recupere le noeud source depuis la premiere phase !!
				ArcCreateCmd createCmd = (ArcCreateCmd) request.getStartCommand();

				// Autorise la connexion d'un arc
				ArcCompleteCmd cmd = new ArcCompleteCmd(createCmd.getSource(), (INode) getHost().getModel(), createCmd.getArcFormalism());
				return cmd;
			}

			@Override
			protected Command getReconnectSourceCommand(ReconnectRequest request) {
				IArc arc = (IArc) request.getConnectionEditPart().getModel();
				INode newSource = (INode) getHost().getModel();
				ArcReconnectCmd cmd = new ArcReconnectCmd(arc);
				cmd.setNewSource(newSource);

				return cmd;
			}

			@Override
			protected Command getReconnectTargetCommand(ReconnectRequest request) {
				IArc arc = (IArc) request.getConnectionEditPart().getModel();
				INode newTarget = (INode) getHost().getModel();
				ArcReconnectCmd cmd = new ArcReconnectCmd(arc);
				cmd.setNewTarget(newTarget);

				return cmd;
			}
		});
	}



	/**
	 * Creation des ancres pour attacher les connexions
	 * @return ConnectionAnchor
	 */
	protected final ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = ((INodeFigure) getFigure()).getConnectionAnchor();
		}
		return connectionAnchor;
	}

	/**
	 * Retourne la liste des arcs sortant du noeud considere
	 * @return List of IArcImpl
	 */
	@Override
	protected final List<IArc> getModelSourceConnections() {
		return ((INode) getModel()).getOutcomingArcs();
	}

	/**
	 * Retourne la liste des arcs entrants du noeud considere
	 * @return List of IArcImpl
	 */
	@Override
	protected final List<IArc> getModelTargetConnections() {
		return ((INode) getModel()).getIncomingArcs();
	}

	public final ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public final ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public final ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public final ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public final void activate() {
		if (!isActive()) {
			super.activate();
			((AbstractPropertyChange) getModel()).addPropertyChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public final void deactivate() {
		if (isActive()) {
			super.deactivate();
			((AbstractPropertyChange) getModel()).removePropertyChangeListener(this);
		}
	}

	public final void childAdded(EditPart child, int index) { }

	public final void partActivated(EditPart editpart) { }

	public final void partDeactivated(EditPart editpart) { }

	public final void removingChild(EditPart child, int index) { }

	public final void selectedStateChanged(EditPart editpart) {
		switch(editpart.getSelected()) {
		case EditPart.SELECTED:
		case EditPart.SELECTED_PRIMARY:
			break;
		case EditPart.SELECTED_NONE:
			break;
		case ISelectionEditPartListener.HIGHLIGHT:
			break;
		case ISelectionEditPartListener.HIGHLIGHT_NONE:
			break;
		case ISelectionEditPartListener.SPECIAL:
			break;
		case ISelectionEditPartListener.SPECIAL_NONE:
			break;
		default:
			break;
		}
	}
}
