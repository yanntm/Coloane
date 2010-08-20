package fr.lip6.move.coloane.interfaces.objects.result;


/**
 * Describe a special information attached to a model object.<br>
 * This information is usually shown thanks to a red bubble drawn on the model.
 * 
 * @author Florian David
 * @author Jean-Baptiste Voron
 */
public class Tip implements ITip {
	/** The ID of the object to which the tip is attached to */
	private int idObject;
	/** The name of the tip */
	private String name;
	/** The value of the tip */
	private String value;

	/**
	 * Constructor
	 * @param idObject the related model object
	 * @param name The name of the tip
	 * @param value The value of the tip
	 */
	public Tip(int idObject, String name, String value) {
		this.idObject = idObject;
		this.name = name;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getIdObject() {
		return idObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return value;
	}
}
