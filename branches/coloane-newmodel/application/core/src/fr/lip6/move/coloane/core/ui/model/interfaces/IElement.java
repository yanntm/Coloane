package fr.lip6.move.coloane.core.ui.model.interfaces;

import java.util.Collection;

public interface IElement extends IAbstractPropertyChange {
	/**
	 * @return unique id.
	 */
	int getId();

	/**
	 * @param name name of the attribute.
	 * @return the IAttribute named attribute or null.
	 */
	IAttribute getAttribute(String name);

	/**
	 * @return Collection of all IAttribute.
	 */
	Collection<IAttribute> getAttributes();

	/**
	 * Put this attribute to this element.
	 * @param name
	 * @param attribute
	 */
	void putAttribute(String name, IAttribute attribute);
}
