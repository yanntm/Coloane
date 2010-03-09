package fr.lip6.move.coloane.core.ui.editpart;

import fr.lip6.move.coloane.core.model.AbstractPropertyChange;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.ILocationInfo;
import fr.lip6.move.coloane.interfaces.model.INode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;

/**
 * This EditPart is in charge of managing attributes.<br>
 * All attributes are attached to the Graph EditPart because they have to be displayed in the graph drawing space.
 * 
 * @author Jean-Baptiste Voron
 */
public class AttributeEditPart extends AbstractGraphicalEditPart implements ISelectionEditPartListener, PropertyChangeListener {
	// Default spacing between attributes when displayed
	private static final int GAP = 20;
	private static final int MINGAP = 20;

	private boolean select = false;
	private boolean elementSelect = false;
	private boolean highlight = false;

	private Font font;

	/**
	 * Listening state modification from the attribute's parent. 
	 */
	private EditPartListener editPartListener = new EditPartListener.Stub() {
		// When the state of current attribute's parent has changed...
		@Override
		public void selectedStateChanged(EditPart part) {
			switch(part.getSelected()) {
			case EditPart.SELECTED:
			case EditPart.SELECTED_PRIMARY:
				elementSelect = true;
				break;
			case EditPart.SELECTED_NONE:
				elementSelect = false;
				break;
			case ISelectionEditPartListener.HIGHLIGHT:
				highlight = true;
				break;
			case ISelectionEditPartListener.HIGHLIGHT_NONE:
				highlight = false;
				break;
			default:
				break;
			}
			// Need to refresh visuals (perhaps some graphical attributes has been changed) !
			refreshVisuals();
		}
	};

	/**
	 * Creates the associated figure</br>
	 * For the attributes, the figure is, in fact, a <b>label</b>.
	 * @return IFigure
	 */
	@Override
	protected final IFigure createFigure() {
		Label figure = new Label();
		figure.setOpaque(true); // Opacity (!)

		// Fetch the associated attribute model object
		IAttribute attribute = (IAttribute) getModel();

		// If the attribute value matches its default value, the attribute should not be displayed (hidden)
		// But, the formalism can specify exceptions ! So, the defaultValueDrawable status has to be checked too.
		if (attribute.getAttributeFormalism().getDefaultValue().equals(attribute.getValue())) {
			if (!attribute.getAttributeFormalism().isDefaultValueDrawable()) {
				figure.setVisible(false);
				attribute.getGraphicInfo().setLocation(new Point(0,0));
				figure.setLocation(new Point(0, 0));
				return figure;
			}
		}

		// Compute the location of the label (try to avoid overlaps)
		Point attributeLocation = computeLocation();

		// Store graphical location and set the figure position into the editor
		attribute.getGraphicInfo().setLocation(attributeLocation);
		figure.setLocation(attributeLocation);
		return figure;
	}

	/**
	 * Compute the location of an attribute considering its parent type (arc, node or graph...).<br>
	 * If the attribute has already some location information, they have to be used instead !
	 * @return the better location (try to avoid overlaps between attributes)
	 */
	private Point computeLocation() {
		// Fetch the attribute model object
		IAttribute attribut = (IAttribute) getModel();
		Point attributePosition;

		// If the attribute parent is a node, the attribute if moved
		if (attribut.getReference() instanceof INode) {

			// If the attribute has no location information yet
			if ((attribut.getGraphicInfo().getLocation().x == 0) && (attribut.getGraphicInfo().getLocation().y == 0)) {
				Point refLocation = ((INode) attribut.getReference()).getGraphicInfo().getLocation();
				// Compute a new location given the default GAP according to the node position  
				attributePosition = new Point(refLocation.x + GAP, refLocation.y - GAP);

			// If the attribute has some location information
			} else {
				// Just put the attribute where it must be
				attributePosition = new Point(attribut.getGraphicInfo().getLocation().x, attribut.getGraphicInfo().getLocation().y);
			}

		// It the attribute parent is an arc
		} else if (attribut.getReference() instanceof IArc) {
			// If the attribute has no location information yet
			if ((attribut.getGraphicInfo().getLocation().x == 0) && (attribut.getGraphicInfo().getLocation().y == 0)) {
				attributePosition = ((IArc) attribut.getReference()).getGraphicInfo().findMiddlePoint();

			//If the attribute has already some location information 
			} else {
				attributePosition = new Point(attribut.getGraphicInfo().getLocation().x, attribut.getGraphicInfo().getLocation().y);
			}

		// If the attribute parent is the graph itself
		} else if (attribut.getReference() instanceof IGraph) {
			attributePosition = new Point(attribut.getGraphicInfo().getLocation().x, attribut.getGraphicInfo().getLocation().y);

		// In any other cases, the attribute position is set to (0,0)
		// It means that the attribute is attached to something strange... (TODO: Is the situation really possible ?)
		} else {
			attributePosition = new Point(0, 0);
		}

		// After this basic computation step, we want to check that the attribute is not overlapping another attribute.
		
		// Fetch the parent EditPart
		//GraphEditPart graphEditPart = (GraphEditPart) getParent();

		// We check that the attribute origin (x,y) is not overlapping with another object.
		// The label is not filled yet, thus we are checking that the point (x+5,y+5) is not overlapping another object too.
		//Point attributePositionZone = new Point(attributePosition.x + MINGAP, attributePosition.y + MINGAP);

		// While there is an object under the attribute, the attributed is moved (x+5,y+5)
		//while ((graphEditPart.getFigure().findFigureAt(attributePosition) != null) || (graphEditPart.getFigure().findFigureAt(attributePositionZone) != null)) {
		//	attributePosition.y = attributePosition.y + MINGAP;
		//	attributePositionZone.y = attributePositionZone.y + MINGAP;
		//}
		return attributePosition;
	}

	/**
	 * Refresh view side according model information.
	 */
	@Override
	protected final void refreshVisuals() {
		IAttribute attribut = (IAttribute) getModel();
		Label attributeFigure = (Label) getFigure();

		// Update graphical representation
		getFigure().setForegroundColor(attribut.getGraphicInfo().getForeground());
		getFigure().setBackgroundColor(attribut.getGraphicInfo().getBackground());
		
		// Select state (attribute only or parent ?)
		// TODO: Should be set in a property page
		if (this.select || this.elementSelect) {
			getFigure().setForegroundColor(ColorConstants.blue);
		}
		// Highlight state (mouseover event)
		// TODO: Should be set in a property page
		if (this.highlight) {
			getFigure().setBackgroundColor(ColorConstants.lightGray);
		}

		// Font update
		if (this.font == null || this.font.isDisposed()) {
			this.font = JFaceResources.getDefaultFont();
			if (attribut.getAttributeFormalism().isBold()) {
				this.font = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
			}
			if (attribut.getAttributeFormalism().isItalic()) {
				this.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
			}
			attributeFigure.setFont(this.font);
		}

		// The label is filled with the attribute value !
		attributeFigure.setText(attribut.getValue());

		// Graphical space (i.e. bounds) for the attribute is set here.  
		Rectangle bounds = new Rectangle(attribut.getGraphicInfo().getLocation(), new Dimension(attributeFigure.getTextBounds().width, attributeFigure.getTextBounds().height));
		if (getParent() != null) {
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
		}
	}

	/**
	 * Some policies (behaviors) applying to the object
	 */
	@Override
	protected final void createEditPolicies() {
		// Allow the edition of the attribute value directly on the editor
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new AttributeDirectEditPolicy());

		// All rules about select state
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionEditPolicy() {
			@Override
			protected void hideSelection() {
				select = false;
				refreshVisuals();
			}

			@Override
			protected void showSelection() {
				select = true;
				refreshVisuals();
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public final void activate() {
		if (!isActive()) {
			super.activate();
			((AbstractPropertyChange) getModel()).addPropertyChangeListener(this);
			if (getParent() instanceof GraphEditPart) {
				GraphEditPart graphEditPart = (GraphEditPart) getParent();
				EditPart parent = graphEditPart.getParentAttributeEditPart(this);
				if (parent != null) {
					addEditPartListener(((ISelectionEditPartListener) parent).getSelectionEditPartListener());
					parent.addEditPartListener(editPartListener);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void deactivate() {
		if (isActive()) {
			super.deactivate();
			((AbstractPropertyChange) getModel()).removePropertyChangeListener(this);
			if (getParent() instanceof GraphEditPart) {
				GraphEditPart graphEditPart = (GraphEditPart) getParent();
				EditPart parent = graphEditPart.getParentAttributeEditPart(this);
				if (parent != null) {
					setSelected(EditPart.SELECTED_NONE);
					removeEditPartListener(((ISelectionEditPartListener) parent).getSelectionEditPartListener());
					parent.removeEditPartListener(editPartListener);
				}
			}
		}
	}

	/**
	 * Draw a rectangle useful to edit the attribute value
	 * @see #createEditPolicies()
	 */
	private void performDirectEdit() {
		Label label = (Label) getFigure();
		IAttribute model = (IAttribute) getModel();
		new AttributeEditManager(this, model, new AttributeCellEditorLocator(label)).show();
	}

	/** {@inheritDoc} */
	@Override
	public final void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEdit();
		}
	}

	/** {@inheritDoc} */
	public final void propertyChange(PropertyChangeEvent event) {
		String prop = event.getPropertyName();

		// When the value of the attribute is modified somewhere
		if (IAttribute.VALUE_PROP.equals(prop)) {
			IAttribute attribute = (IAttribute) getModel();
			String newValue = (String) event.getNewValue();
			
			// If the attribute new value matches its default value, the attribute should not be displayed (hidden)
			// But, the formalism can specify exceptions ! So, the defaultValueDrawable status has to be checked too.
			if (attribute.getAttributeFormalism().getDefaultValue().equals(newValue)) {
				if (!attribute.getAttributeFormalism().isDefaultValueDrawable()) {
					figure.setVisible(false);
					attribute.getGraphicInfo().setLocation(new Point(0, 0));
					refreshVisuals();
					return;
				}
			}
			
			attribute.getGraphicInfo().setLocation(computeLocation());
			getFigure().setVisible(true);
			refreshVisuals();

			// When the attribute is moved
		} else if (ILocationInfo.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		}
	}

	/** {@inheritDoc} */
	public final EditPartListener getSelectionEditPartListener() {
		return editPartListener;
	}
}
