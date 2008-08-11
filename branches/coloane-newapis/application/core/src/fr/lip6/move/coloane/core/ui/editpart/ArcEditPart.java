package fr.lip6.move.coloane.core.ui.editpart;

import fr.lip6.move.coloane.core.model.AbstractPropertyChange;
import fr.lip6.move.coloane.core.model.CoreTipModel;
import fr.lip6.move.coloane.core.model.interfaces.ISpecialState;
import fr.lip6.move.coloane.core.motor.session.SessionManager;
import fr.lip6.move.coloane.core.ui.commands.ArcDeleteCmd;
import fr.lip6.move.coloane.core.ui.commands.InflexCreateCmd;
import fr.lip6.move.coloane.core.ui.commands.InflexDeleteCmd;
import fr.lip6.move.coloane.core.ui.commands.InflexMoveCmd;
import fr.lip6.move.coloane.core.ui.figures.IArcFigure;
import fr.lip6.move.coloane.core.ui.figures.arcs.SimpleArc;
import fr.lip6.move.coloane.core.ui.prefs.ColorsPrefs;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.interfaces.objects.result.ITip;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.AnchorListener;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.GroupRequest;

/**
 * EditPart pour les arcs (CONTROLEUR)
 */
public class ArcEditPart extends AbstractConnectionEditPart implements ISelectionEditPartListener, PropertyChangeListener, NodeEditPart {
	/**
	 * Logger 'fr.lip6.move.coloane.core'.
	 */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	private static final ConnectionRouter CONNECTION_ROUTER = new BendpointConnectionRouter();

	private boolean select = false;
	private boolean special = false;
	private boolean attributSelect = false;

	/**
	 * Permet d'écouter les changements de sélections des attributs
	 */
	private EditPartListener editPartListener = new EditPartListener.Stub() {
		/** {@inheritDoc} */
		@Override
		public void selectedStateChanged(EditPart editpart) {
			switch(editpart.getSelected()) {
			case EditPart.SELECTED:
			case EditPart.SELECTED_PRIMARY:
				attributSelect = true;
				break;
			case EditPart.SELECTED_NONE:
				attributSelect = false;
				break;
			default:
				break;
			}
			refreshVisuals();
		}
	};

	private ConnectionAnchor connectionAnchor;

	/**
	 * Dessin de l'arc
	 * @return IFigure
	 */
	@Override
	protected final IFigure createFigure() {
		IArc arc = (IArc) getModel();
		IArcFigure arcFigure = (IArcFigure) arc.getArcFormalism().getGraphicalDescription().getAssociatedFigure();
		if (arcFigure == null) {
			LOGGER.warning("Aucune figure trouvé, utilisation de la figure par défaut"); //$NON-NLS-1$
			arcFigure = new SimpleArc();
		}
		arcFigure.setForegroundColor(arc.getGraphicInfo().getColor());
		arcFigure.setConnectionRouter(CONNECTION_ROUTER);
		return arcFigure;
	}

	/**
	 * Met a jour la vue en fonction de la lecture du modele<br>
	 * Cette methode utilise les accesseurs de la vue pour la modifier
	 */
	@Override
	protected final void refreshVisuals() {
		// Mise à jour de la figure (couleurs et taille)
		getFigure().setForegroundColor(((IArc) getModel()).getGraphicInfo().getColor());
		((IArcFigure) getFigure()).setLineWidth(1);
		if (special) {
			getFigure().setForegroundColor(ColorConstants.red);
			((IArcFigure) getFigure()).setLineWidth(2);
		}
		if (attributSelect) {
			getFigure().setForegroundColor(ColorsPrefs.setColor("COLORARC_HIGHLIGHT")); //$NON-NLS-1$
			((IArcFigure) getFigure()).setLineWidth(2);
		}
		if (select) {
			getFigure().setForegroundColor(ColorsPrefs.setColor("COLORARC")); //$NON-NLS-1$
			((IArcFigure) getFigure()).setLineWidth(2);
		}

		IArc arcModel = (IArc) getModel();

		Connection connection = (Connection) getFigure();
		connection.getConnectionRouter();

		List<AbsoluteBendpoint> modelConstraint = arcModel.getInflexPoints();
		getConnectionFigure().setRoutingConstraint(modelConstraint);
	}


	/** {@inheritDoc} */
	@Override
	protected final void createEditPolicies() {
		// Selection handle edit policy.
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());

		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new BendpointEditPolicy() {
			@Override
			protected Command getCreateBendpointCommand(BendpointRequest request) {
				LOGGER.finest("Creation du point d'inflexion : " + request.getIndex()); //$NON-NLS-1$
				Point p = request.getLocation();
				getConnection().translateToRelative(p);
				InflexCreateCmd com = new InflexCreateCmd((IArc) getModel(), request.getLocation(), request.getIndex());
				return com;
			}

			@Override
			protected Command getDeleteBendpointCommand(BendpointRequest request) {
				LOGGER.finest("Suppression du point d'inflexion : " + request.getIndex()); //$NON-NLS-1$
				Point p = request.getLocation();
				getConnection().translateToRelative(p);
				InflexDeleteCmd com = new InflexDeleteCmd((IArc) getModel(), request.getLocation(), request.getIndex());
				return com;
			}
			@Override
			protected Command getMoveBendpointCommand(BendpointRequest request) {
				Point p = request.getLocation();
				LOGGER.finest("Mouvement de point d'inflexion (workspace) : " + p.x + "," + p.y); //$NON-NLS-1$ //$NON-NLS-2$
				getConnection().translateToRelative(p);
				LOGGER.finest("Mouvement de point d'inflexion (univers) : " + p.x + "," + p.y); //$NON-NLS-1$ //$NON-NLS-2$
				InflexMoveCmd com = new InflexMoveCmd((IArc) getModel(), request.getLocation(), request.getIndex());
				return com;
			}
		});

		/* Ensemble de regles concernant la selection/deselection de l'objet */
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionEditPolicy() {
			// Comportement lors de la deselection de l'objet
			@Override
			protected void hideSelection() {
				select = false;
				refreshVisuals();
			}

			// Comportement lors de la selection de l'objet
			@Override
			protected void showSelection() {
				select = true;
				refreshVisuals();
			}
		});

		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());

		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			@Override
			protected Command getDeleteCommand(GroupRequest request) {
				return new ArcDeleteCmd((IArc) getModel());
			}
		});
	}

	/**
	 * Traitements a effectuer lors de la reception d'un evenement sur l'EditPart
	 * @param property L'evenement qui a ete levee
	 */
	public final void propertyChange(PropertyChangeEvent property) {
		LOGGER.finest("propertyChange(" + property.getPropertyName() + ")");  //$NON-NLS-1$//$NON-NLS-2$
		String prop = property.getPropertyName();

		// Propriété de demande de création/suppression d'un AttributEditPart
		if (IElement.ATTRIBUTE_CHANGE.equals(prop)) {
			getSource().getParent().refresh();

		// demande de refresh sur le GraphEditPart
		} else if (ISpecialState.SPECIAL_STATE_CHANGE.equals(prop)) {
			special = (Boolean) property.getNewValue();
		} else if (INode.INCOMING_ARCS_PROP.equals(prop)) {
			refreshTargetConnections();
		}

		refreshVisuals();
	}


	/**
	 * Installation des ecouteurs de l'objet
	 */
	@Override
	public final void activate() {
		if (!isActive()) {
			super.activate();
			((AbstractPropertyChange) getModel()).addPropertyChangeListener(this);
		}
	}

	/**
	 * Mise en veille des ecouteurs de l'objet
	 */
	@Override
	public final void deactivate() {
		if (isActive()) {
			super.deactivate();
			((AbstractPropertyChange) getModel()).removePropertyChangeListener(this);
		}
	}

	/** {@inheritDoc} */
	public final EditPartListener getSelectionEditPartListener() {
		return editPartListener;
	}

	private ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ConnectionAnchor() {
				public void addAnchorListener(AnchorListener listener) { }
				public Point getLocation(Point reference) {
					return getReferencePoint();
				}
				public IFigure getOwner() {
					return getFigure();
				}
				public Point getReferencePoint() {
					return ((IArc) getModel()).getGraphicInfo().findMiddlePoint();
				}
				public void removeAnchorListener(AnchorListener listener) { }
			};
		}
		return connectionAnchor;
	}

	/** {@inheritDoc} */
	public final ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/** {@inheritDoc} */
	public final ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	/** {@inheritDoc} */
	public final ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/** {@inheritDoc} */
	public final ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	/**
	 * Retourne la liste des arcs entrants du noeud considere
	 * @return List of IArcImpl
	 */
	@Override
	protected final List<Object> getModelTargetConnections() {
		List<Object> targets = new ArrayList<Object>();
		ITip tip = SessionManager.getInstance().getCurrentSession().getTip(((IArc) getModel()).getId());
		if (tip != null && tip instanceof CoreTipModel) {
			targets.add(((CoreTipModel) tip).getArcModel());
		}
		return targets;
	}
}
