package fr.lip6.move.coloane.core.model;

import fr.lip6.move.coloane.core.model.interfaces.ILink;
import fr.lip6.move.coloane.core.model.interfaces.ILinkableElement;
import fr.lip6.move.coloane.core.model.interfaces.IStickyNote;
import fr.lip6.move.coloane.interfaces.model.IElement;

import java.util.logging.Logger;

/**
 * Link between two elements {@link IElement}
 */
public class LinkModel implements ILink {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/** The sticky note */
	private IStickyNote note;
	/** The element that is linked to the sticky note */
	private ILinkableElement element;

	/**
	 * Constructor.<br>
	 * Create a link between two elements. 
	 * This link is not oriented.
	 * There is no difference between the source and the target.
	 * <br><br>
	 * @param note The sticky note
	 * @param element The element
	 */
	LinkModel(IStickyNote note, ILinkableElement element) {
		LOGGER.fine("Build a link: " + note + "--" + element); //$NON-NLS-1$ //$NON-NLS-2$
		if (note == null || element == null) {
			throw new NullPointerException("Argument must be not null"); //$NON-NLS-1$
		}
		this.note = note;
		this.element = element;
	}

	/** {@inheritDoc} */
	public final ILinkableElement getElement() {
		return element;
	}

	/** {@inheritDoc} */
	public final IStickyNote getNote() {
		return note;
	}

	/** {@inheritDoc} */
	public final void reconnect(IStickyNote newNote, ILinkableElement newElement) {
		note.removeLink(this);
		element.removeLink(this);
		note = newNote;
		element = newElement;
		note.addLink(this);
		element.addLink(this);
	}
}
