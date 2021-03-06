/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.core.model;

import fr.lip6.move.coloane.core.model.interfaces.ILink;
import fr.lip6.move.coloane.core.model.interfaces.ILinkableElement;
import fr.lip6.move.coloane.core.model.interfaces.ILocatedElement;
import fr.lip6.move.coloane.core.ui.rulers.EditorGuide;
import fr.lip6.move.coloane.core.ui.rulers.EditorRulerProvider;
import fr.lip6.move.coloane.interfaces.formalism.INodeFormalism;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.ILocationInfo;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.interfaces.model.INodeGraphicInfo;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Describe a model node object.
 *
 * @author Jean-Baptiste Voron
 */
public class NodeModel extends AbstractElement implements INode, ILocatedElement, ILinkableElement {
	/** Core Logger */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/** The node formalism */
	private final INodeFormalism nodeFormalism;

	/** Graphical information associated to the node */
	private final INodeGraphicInfo graphicInfos;

	/** Guides where the node is stuck to */
	private EditorGuide horizontalGuide;
	private EditorGuide verticalGuide;

	/** List of incoming and outgoing arcs */
	private List<IArc> outgoingArcs = new ArrayList<IArc>();
	private List<IArc> incomingArcs = new ArrayList<IArc>();

	/** Links (comments) associated to this node */
	private List<ILink> links = new ArrayList<ILink>();

	private boolean isInterface = false;
	private String nodeLink;

	/**
	 * Constructor
	 * @param parent The parent of this node (often the graph itself)
	 * @param nodeFormalism The formalism description of the node
	 * @param id The identifier (unique) of the node
	 * @see {@link GraphModel#getId()} to get a new unique ID
	 */
	NodeModel(IElement parent, INodeFormalism nodeFormalism, int id) {
		super(id, parent, nodeFormalism.getAttributes(), nodeFormalism.getComputedAttributes());
		LOGGER.finest("Build a node: " + nodeFormalism.getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		this.nodeFormalism = nodeFormalism;
		this.graphicInfos = new NodeGraphicInfo(this);
	}

	/**
	 * Delete all input or output arcs and sticky links from the node
	 */
	final void deleteArcsLinks() {
		LOGGER.finest("Delete arcs from node #" + getId()); //$NON-NLS-1$
		for (IArc arc : outgoingArcs) {
			((NodeModel) arc.getTarget()).removeIncomingArc(arc);
			((ArcModel) arc).delete();
		}
		for (IArc arc : incomingArcs) {
			((NodeModel) arc.getSource()).removeOutgoingArc(arc);
			((ArcModel) arc).delete();
		}
		for (ILink link : new ArrayList<ILink>(this.getLinks())) {
			link.disconnect();
		}
		outgoingArcs.clear();
		incomingArcs.clear();

		// The sticky links list should be empty (due to link.disconnect())
		if (!links.isEmpty()) {
			LOGGER.warning("The sticky link list is not clean... cleaning it now"); //$NON-NLS-1$
			links.clear();
		}
	}

	/** {@inheritDoc} */
	@Override
	public final INodeFormalism getNodeFormalism() {
		return this.nodeFormalism;
	}

	/** {@inheritDoc} */
	@Override
	public final INodeGraphicInfo getGraphicInfo() {
		return this.graphicInfos;
	}

	/** {@inheritDoc} */
	@Override
	public final ILocationInfo getLocationInfo() {
		return this.getGraphicInfo();
	}

	/**
	 * Add an outgoing arc for the considered node
	 * @param outArc The arc to add to the list
	 */
	final void addOutgoingArc(IArc outArc) {
		LOGGER.finest("Add outgoing arc #" + outArc.getId()); //$NON-NLS-1$
		outgoingArcs.add(outArc);
		firePropertyChange(INode.OUTGOING_ARCS_PROP, null, outArc);
	}

	/**
	 * Add an incoming arc for the considered node
	 * @param inArc The arc to add to the list
	 */
	final void addIncomingArc(IArc inArc) {
		LOGGER.finest("Add incoming arc #" + inArc.getId()); //$NON-NLS-1$
		incomingArcs.add(inArc);
		firePropertyChange(INode.INCOMING_ARCS_PROP, null, inArc);
	}

	/**
	 * Remove an outgoing arc from the node
	 * @param outArc The arc to remove from the list
	 */
	final void removeOutgoingArc(IArc outArc) {
		LOGGER.finest("Remove outgoing arc #" + outArc.getId()); //$NON-NLS-1$
		outgoingArcs.remove(outArc);
		firePropertyChange(INode.OUTGOING_ARCS_PROP, null, outArc);
	}

	/**
	 * Remove an incoming arc from the node
	 * @param inArc The arc to remove from the list
	 */
	final void removeIncomingArc(IArc inArc) {
		LOGGER.finest("Remove incoming arc #" + inArc.getId()); //$NON-NLS-1$
		incomingArcs.remove(inArc);
		firePropertyChange(INode.INCOMING_ARCS_PROP, null, inArc);
	}

	/** {@inheritDoc} */
	@Override
	public final List<IArc> getOutgoingArcs() {
		return Collections.unmodifiableList(outgoingArcs);
	}

	/** {@inheritDoc} */
	@Override
	public final List<IArc> getIncomingArcs() {
		return Collections.unmodifiableList(incomingArcs);
	}

	/** {@inheritDoc} */
	@Override
	public final EditorGuide getGuide(int orientation) {
		if (orientation == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			return this.horizontalGuide;
		} else {
			return this.verticalGuide;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void setGuide(EditorGuide guide) {
		if (guide.getOrientation() == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			LOGGER.fine("New horizontal guide for the node #" + this.getId()); //$NON-NLS-1$
			this.horizontalGuide = guide;
		} else {
			LOGGER.fine("New vertical guide for the node #" + this.getId()); //$NON-NLS-1$
			this.verticalGuide = guide;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void removeGuide(int orientation) {
		if (orientation == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			LOGGER.fine("No more horizontal guide for the node #" + this.getId()); //$NON-NLS-1$
			this.horizontalGuide = null;
		} else {
			LOGGER.fine("No more vertical guide for the node #" + this.getId()); //$NON-NLS-1$
			this.verticalGuide = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void updateTips() {
		firePropertyChange(INCOMING_ARCS_PROP, null, null);
	}

	/** {@inheritDoc} */
	@Override
	public final void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (IAttribute.VALUE_PROP.equals(prop)) {
			// Value changes are sent to the parent level
			firePropertyChange(prop, evt.getOldValue(), evt.getNewValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void addLink(ILink link) {
		links.add(link);
		firePropertyChange(INode.OUTGOING_ARCS_PROP, null, link);
	}

	/** {@inheritDoc} */
	@Override
	public final List<ILink> getLinks() {
		return Collections.unmodifiableList(links);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean removeLink(ILink link) {
		boolean res = links.remove(link);
		firePropertyChange(INode.OUTGOING_ARCS_PROP, null, link);
		return res;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		IAttribute attrName = getAttribute("name"); //$NON-NLS-1$
		String name = ""; //$NON-NLS-1$
		if (attrName != null) {
			name = attrName.getValue();
		}
		return "Node #" + getId() + " (" + name + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isInterface() {
		return this.isInterface;
	}

	/** {@inheritDoc} */
	@Override
	public final void setInterface(boolean state) {
		LOGGER.finest(this + " setInterface(" + state + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		boolean oldValue = isInterface;
		this.isInterface = state;
		firePropertyChange(IAttribute.VALUE_PROP, oldValue, state);
	}

	/** {@inheritDoc} */
	@Override
	public final String getNodeLink() {
		return this.nodeLink;
	}

	/** {@inheritDoc} */
	@Override
	public final void setNodeLink(String link) {
		this.nodeLink = link;
	}
}


