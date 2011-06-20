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

import fr.lip6.move.coloane.core.model.interfaces.ILocatedElement;
import fr.lip6.move.coloane.core.model.interfaces.ISpecialState;
import fr.lip6.move.coloane.core.ui.files.IModelHandler;
import fr.lip6.move.coloane.core.ui.rulers.EditorGuide;
import fr.lip6.move.coloane.core.ui.rulers.EditorRulerProvider;
import fr.lip6.move.coloane.interfaces.formalism.IAttributeFormalism;
import fr.lip6.move.coloane.interfaces.formalism.IElementFormalism;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IAttributeGraphicInfo;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.ILocationInfo;

import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

/**
 * Describe an object model attribute
 *
 * @author Jean-Baptiste Voron
 */
public class AttributeModel extends AbstractPropertyChange implements IAttribute, ILocatedElement, ISpecialState {
	/** The main logger */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/** The reference model element */
	private IElement reference;
	
	/** The parent of the element */
	private IAttribute parent;

	/** The formalism of the attribute */
	private IAttributeFormalism attributFormalism;

	/** Graphical guides to help the positioning of the attribute */
	private EditorGuide horizontalGuide;
	private EditorGuide verticalGuide;

	/** Attribute name */
	private final String name;

	/** Attribute value */
	private String value = ""; //$NON-NLS-1$

	/** Attribute child attributes */
	private Map<String, IAttribute> children = new HashMap<String, IAttribute>();
	
	/** All the graphical information about this attribute */
	private IAttributeGraphicInfo graphicInfo = new AttributeGraphicInfo(this);

	/**
	 * Constructor
	 *
	 * @param reference The element to which this attribute is associated
	 * @param elementFormalism The properties of the element which will contain this attribute
	 */
	public AttributeModel(IElement reference, IElementFormalism elementFormalism, String name) {
		this.reference = reference;
		this.parent = null;
		this.addPropertyChangeListener((PropertyChangeListener) reference);
		
		for (IAttributeFormalism a: elementFormalism.getAttributes()){
			if (a.getName().equals(name)) {
				this.attributFormalism = a;
				break;
			}
		}
		
		if (attributFormalism == null) {
			this.name = ""; //$NON-NLS-1$
		} else {
			this.name = attributFormalism.getName();
		}

	}
	
	/**
	 * Constructor
	 *
	 * @param reference The element to which this attribute is associated
	 * @param attributeFormalism The properties of the attribute which will contain this attribute (given by the formalism)
	 */
	public AttributeModel(IElement reference, IAttribute parent, IAttributeFormalism attributeFormalism, String name) {
		LOGGER.finest("Build an attribute: " + attributeFormalism.getName() + " for #" + reference.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		this.reference = reference;
		this.parent = parent;	
		this.addPropertyChangeListener((PropertyChangeListener) reference);
		
		for (IAttributeFormalism a: attributeFormalism.getAttributes()){
			if (a.getName().equals(name)) {
				this.attributFormalism = a;
				break;
			}
		}
		
		if (attributFormalism == null) {
			this.name = ""; //$NON-NLS-1$
		} else {
			this.name = attributFormalism.getName();
		}
	}

	/** {@inheritDoc} */
	public final String getName() {
		return name;
	}

	/** {@inheritDoc} */
	public final IAttributeGraphicInfo getGraphicInfo() {
		return graphicInfo;
	}

	/** {@inheritDoc} */
	public final ILocationInfo getLocationInfo() {
		return this.graphicInfo;
	}

	/** {@inheritDoc} */
	public final IElement getReference() {
		return reference;
	}
	
	/** {@inheritDoc} */
	public final IAttribute getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	public final IAttributeFormalism getAttributeFormalism() {
		return attributFormalism;
	}

	/** {@inheritDoc} */
	public final EditorGuide getGuide(int orientation) {
		if (orientation == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			return this.horizontalGuide;
		} else {
			return this.verticalGuide;
		}
	}

	/** {@inheritDoc} */
	public final void setGuide(EditorGuide guide) {
		if (guide.getOrientation() == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			this.horizontalGuide = guide;
		} else {
			this.verticalGuide = guide;
		}
	}

	/** {@inheritDoc} */
	public final void removeGuide(int orientation) {
		if (orientation == EditorRulerProvider.HORIZONTAL_ORIENTATION) {
			this.horizontalGuide = null;
		} else {
			this.verticalGuide = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return "Attribute: " + name + "= " + value + " [" + reference + "])"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/** {@inheritDoc} */
	public final void setSpecialState(boolean state) {
		firePropertyChange(SPECIAL_STATE_CHANGE, null, state);
	}

	/** {@inheritDoc} */
	public boolean isLeaf() {
		return children.isEmpty();
	}

	/** {@inheritDoc} */
	public void setLeaf() {
		children.clear();
	}

	/** {@inheritDoc} */
	public final String getValue() {
		return value;
	}
	
	/**
	 * Parses the value attribute and, if it contains a properly formed description
	 * of acceptable inner attributes, adds them to the current attribute
	 * If the syntax for entering attributes is to be modified, change this function and the InnerAttributeParser function
	 */
	private void parseValue(){
		try {
			// Build the parsing factory & Parse
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(this.getValue())), new InnerAttributeParser(this));
		} catch (Exception e) {
			//this will empty the attribute list
			this.setLeaf();
		}
	}
	
	/** {@inheritDoc} */
	public final void setValue(String value) {
		String oldValue = this.value;
		// Warn the controller about the change only if it is necessary
		if (!oldValue.equals(value)) {
			this.value = value;
			//parse the value to see if there are contained attributes
			parseValue();
			firePropertyChange(IAttribute.VALUE_PROP, oldValue, value);
		}
	}

	/** {@inheritDoc} */
	public Collection<IAttribute> getAttributes() {
		return children.values();
	}

	/** {@inheritDoc} */
	public IAttribute getAttribute(String attName) {
		return children.get(attName);
	}

	/** {@inheritDoc} */
	public void setAttributes(Collection<IAttribute> values) {
		Map<String, IAttribute> oldValues = this.children;
		for (IAttribute attr : values) {
			this.children.put(attr.getName(), attr);
		}
		firePropertyChange(IAttribute.VALUE_PROP, oldValues, this.children);
	}

	/** {@inheritDoc} */
	public void addAttribute(IAttribute value) {
		Map<String, IAttribute> oldValues = this.children;
		children.put(value.getName(), value);
		firePropertyChange(IAttribute.VALUE_PROP, oldValues, this.children);
	}

	/** {@inheritDoc} */
	public void initialiseValue() {
		String oldValue = value;
		value = ""; //$NON-NLS-1$
		for (IAttribute a : children.values())
			value += buildXMLString(a);
		firePropertyChange(IAttribute.VALUE_PROP, oldValue, value);
	}
	
	/**
	 * Builds the XML representation of the attribute
	 * @param attribut The attribute to build a representation of
	 * @return The XML representation
	 */
	String buildXMLString(IAttribute attribut){
		String val = ""; //$NON-NLS-1$
		String balise = attribut.getName();
		val+="<" + IModelHandler.ATTRIBUTE_MARKUP + " " + IModelHandler.ATTRIBUTE_NAME_MARKUP + "='"+balise+"'";  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		val+=" " + IModelHandler.ATTRIBUTE_X_MARKUP + "='"+attribut.getGraphicInfo().getLocation().x+"'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		val+=" " + IModelHandler.ATTRIBUTE_Y_MARKUP + "='"+attribut.getGraphicInfo().getLocation().y+"'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		val+=">"; //$NON-NLS-1$
		val+=attribut.getValue();
		for(IAttribute att: attribut.getAttributes()){
			val+=buildXMLString(att);
		}
		val+="</" + IModelHandler.ATTRIBUTE_MARKUP + ">";	 //$NON-NLS-1$ //$NON-NLS-2$
		
		return val;
	}
}