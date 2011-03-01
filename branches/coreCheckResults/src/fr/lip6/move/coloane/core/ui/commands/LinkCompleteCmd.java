package fr.lip6.move.coloane.core.ui.commands;

import fr.lip6.move.coloane.core.model.LinkModel;
import fr.lip6.move.coloane.core.model.StickyNoteModel;
import fr.lip6.move.coloane.core.model.interfaces.ILink;
import fr.lip6.move.coloane.core.model.interfaces.ILinkableElement;
import fr.lip6.move.coloane.core.model.interfaces.IStickyNote;

import org.eclipse.gef.commands.Command;

/**
 * Create (finalize) the creation of a link between a model element ({@link ILinkableElement}) and a sticky note ({@link StickyNoteModel})
 * 
 * @author Jean-Baptiste Voron
 * @author Clément Démoulins
 */
public class LinkCompleteCmd extends Command {

	/** The sticky note */
	private IStickyNote note = null;
	/** The model element linked to the sticky note */
	private ILinkableElement element;

	/** The link */
	private ILink link;
	
	/**
	 * Constructor
	 * @param graph The current graph
	 * @param note The sticky note
	 * @param element the element attached to the note
	 */
	public LinkCompleteCmd(IStickyNote note, ILinkableElement element) {
		this.note = note;
		this.element = element;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		// @see Constructor... if no note has been identified, the source or the target is not the good type
		if (this.note == null) {
			return false;
		}
		return true;
	};

	/** {@inheritDoc} */
	@Override
	public final void execute() {
		this.link = new LinkModel(note, element);
		redo();
	}

	/** {@inheritDoc} */
	@Override
	public final void redo() {
		this.link.connect();
	}

	/** {@inheritDoc} */
	@Override
	public final void undo() {
		this.link.disconnect();
	}
}