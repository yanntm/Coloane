package fr.lip6.move.coloane.core.model;

import fr.lip6.move.coloane.interfaces.formalism.IAttributeFormalism;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IElement;
import fr.lip6.move.coloane.interfaces.model.core.ICoreElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the attributes of an IElement.
 * @see ICoreElement
 */
public abstract class AbstractElement extends AbstractPropertyChange implements ICoreElement {
	/**
	 * Map of attributes, the key is the name of the attributes.
	 */
	private Map<String, IAttribute> attributes = new HashMap<String, IAttribute>();

	private IElement parent;

	AbstractElement(IElement parent, List<IAttributeFormalism> attributes) {
		this.parent = parent;
		if (attributes != null) {
			for (IAttributeFormalism attr : attributes) {
				this.attributes.put(
						attr.getName(),
						new AttributeModel(this, attr));
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.ui.model.interfaces.IElement#getAttribute(java.lang.String)
	 */
	public final IAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.ui.model.interfaces.IElement#getAttributes()
	 */
	public final Collection<IAttribute> getAttributes() {
		return attributes.values();
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.ui.model.interfaces.IElement#getDrawableAttributes()
	 */
	public final Collection<IAttribute> getDrawableAttributes() {
		ArrayList<IAttribute> drawables = new ArrayList<IAttribute>();
		for (IAttribute attr : attributes.values()) {
			if (attr.isDrawable()) {
				drawables.add(attr);
			}
		}
		return drawables;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.ui.model.interfaces.IElement#putAttribute(java.lang.String, fr.lip6.move.coloane.core.ui.model.interfaces.IAttribute)
	 */
	public final void putAttribute(String name, IAttribute attribute) {
		attributes.put(name, attribute);
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.ui.model.interfaces.IElement#getParent()
	 */
	public final IElement getParent() {
		return parent;
	}
}
