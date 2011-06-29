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
package fr.lip6.move.coloane.interfaces.model;

import fr.lip6.move.coloane.interfaces.formalism.IGlobalAttributeFormalism;

import java.util.Collection;

/**
 * Describe an attribute object (always attached to a model object).<br>
 * An attribute can be attached to numerous kind of model objects:
 * <ul>
 * 	<li>arc</li>
 * 	<li>node</li>
 * 	<li>graph</li>
 * </ul>
 */
public interface IAttribute extends IAbstractPropertyChange {

	/** Value update property */
	String VALUE_PROP = "Attribute.ValueUpdate"; //$NON-NLS-1$

	/** The attribute must be selected */
	String SELECT_LIGHT_PROP = "Attribute.SelectLightUpdate"; //$NON-NLS-1$

	/** The attribute must be selected */
	String SELECT_HEAVY_PROP = "Attribute.SelectHeavyUpdate"; //$NON-NLS-1$

	/** The attribute must be unselected */
	String UNSELECT_LIGHT_PROP = "Attribute.UnSelecLighttUpdate"; //$NON-NLS-1$

	/** The attribute must be unselected */
	String UNSELECT_HEAVY_PROP = "Attribute.UnSelectHeavyUpdate"; //$NON-NLS-1$

	/**
	 * @return Whether the current attribute is a leaf or not.
	 */
	boolean isLeaf();
	
	/**
	 * Sets the attribute as a leaf.  The opposite is achieved by adding
	 * child attributes to the attribute.
	 */
	void setLeaf();
	
	/**
	 * @return the attribute name
	 */
	String getName();
	
	/**
	 * @return the attribute value
	 */
	String getValue();
	
	/**
	 * Initialises the value of the attribute based on the children attributes
	 */
	void initialiseValue();

	/**
	 * Fetch the full list of children of the attribute
	 * @return The children of the attribute
	 */
	Collection<IAttribute> getAttributes();
	
	/**
	 * Fetch a child attribute based on its name.
	 * @param attName The name of the child attribute to fetch.
	 * @return The attribute, or null if it does not exist.
	 */
	IAttribute getAttribute(String attName);

	/**
	 * Replace the current children attributes of the element.
	 * @param values The list of new attributes.
	 */
	void setAttributes(Collection<IAttribute> values);

	/**
	 * Add a child attribute to the element.
	 * @param value The child attribute
	 */
	void addAttribute(IAttribute value);
	
	/**
	 * Set a new value for the attribute.
	 * @param value The new value
	 */
	void setValue(String value);
	
	/**
	 * Fetch the object that describe all graphical properties for this attribute.<br>
	 * @return IAttributeGraphicInfo
	 * @see IAttributeGraphicInfo
	 */
	IAttributeGraphicInfo getGraphicInfo();

	/**
	 * @return The element to which the attribute is related
	 */
	IElement getReference();

	/**
	 * @return The element to which the attribute is related
	 */
	IAttribute getParent();

	/**
	 * @return The attribute description given by the formalism
	 */
	IGlobalAttributeFormalism getAttributeFormalism();
}
