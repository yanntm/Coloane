package fr.lip6.move.coloane.core.motor.formalisms.elements;

import fr.lip6.move.coloane.interfaces.formalism.IComputedAttributeFormalism;
import fr.lip6.move.coloane.interfaces.model.IAttributeFormatter;

import org.eclipse.draw2d.geometry.Point;

/**
 * This class represents a special attribute characteristics.<br>
 * A <b>computed attribute</b> which value is computed according to values of other attributes.<br>
 * Each base element has to maintain its own list of computed attributes.
 * This attribute is never stored !
 * 
 * @author Jean-Baptiste Voron
 */
public class ComputedAttributeFormalism implements IComputedAttributeFormalism {
	/** Name */
	private String name;

	/** The default value */
	private String defaultValue = null;

	/** Should the attribute be displayed even if its value matches the default one? */
	private boolean isDefaultValueDrawable = false;

	/** Bold? */
	private boolean bold = false;

	/** Italic? */
	private boolean italic = false;

	/** Font size? */
	private int size = 10;
	
	/** Delta Location */
	private Point delta = new Point(0,0);

	/** The Java class used to format the attribute value */
	private Class< ? > formatter = null;

	/**
	 * Build an attribute
	 * @param name The attribute name
	 * @param defaultValue The default value
	 * @param isDefaultValueDrawable Should the default value be displayed ?
	 * @param formatter The Java class responsible for the formatting of the attribute
	 */
	public ComputedAttributeFormalism(String name, String defaultValue, boolean isDefaultValueDrawable, Class< ? > formatter) {
		this(name, defaultValue, isDefaultValueDrawable);
		this.formatter = formatter;
	}

	/**
	 * Build an attribute
	 * @param name The attribute name
	 * @param defaultValue The default value
	 * @param isDefaultValueDrawable Should the default value be displayed ?
	 */
	public ComputedAttributeFormalism(String name, String defaultValue, boolean isDefaultValueDrawable) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.isDefaultValueDrawable = isDefaultValueDrawable;
	}

	/** {@inheritDoc} */
	public final String getName() { return this.name; }

	/** {@inheritDoc} */
	public final String getDefaultValue() {
		if (defaultValue != null) { return defaultValue; }
		return ""; //$NON-NLS-1$
	}

	/** {@inheritDoc} */
	public final boolean isBold() {
		return bold;
	}

	/**
	 * Set the bold status of the attribute
	 * @param bold <code>true</code> if the attribute has to be displayed in bold
	 */
	public final void setBold(boolean bold) {
		this.bold = bold;
	}

	/** {@inheritDoc} */
	public final boolean isItalic() {
		return italic;
	}

	/**
	 * Set the italic status of the attribute
	 * @param italic <code>true</code> if the attribute has to be displayed in italic
	 */
	public final void setItalic(boolean italic) {
		this.italic = italic;
	}

	/** {@inheritDoc} */
	public final Integer getFontSize() {
		return this.size;
	}

	/**
	 * Set the default font size of the attribute
	 * @param size font-size
	 */
	public final void setSize(String size) {
		this.size = Integer.valueOf(size);
	}
		
	/** {@inheritDoc} */
	public final boolean isDefaultValueDrawable() {
		return this.isDefaultValueDrawable;
	}
	
	/**
	 * <b>Delta Location</b> is used to specify the relative position of the attribute according to the parent element.<br>
	 * @param x X coordinate of the Delta Location.
	 */
	public final void setXDelta(int x) {
		this.delta.x = x;
	}
	
	/**
	 * <b>Delta Location</b> is used to specify the relative position of the attribute according to the parent element.<br>
	 * @param y Y coordinate of the Delta Location.
	 */
	public final void setYDelta(int y) {
		this.delta.y = y;
	}
	
	/** {@inheritDoc} */
	public final Point getDeltaLocation() {
		return this.delta;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IAttributeFormatter getAttributeFormatter() {
		try {
			return (IAttributeFormatter) formatter.getConstructor(new Class< ? >[0]).newInstance(new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
