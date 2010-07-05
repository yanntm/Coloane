package checkerConditions;

import fr.lip6.move.coloane.interfaces.formalism.IAttributeChecker;

/**
 * Check that the model contains authors names
 * 
 * @author Jean-Baptiste Voron
 * @author Florian David
 *
 */
public class GraphAuthorsChecker implements IAttributeChecker {

	/**
	 * {@inheritDoc}
	 */
	public boolean performCheck(String value) {		
		// False if this attribute is an empty string
		return !value.isEmpty();
	}
}
