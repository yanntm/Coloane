package fr.lip6.move.coloane.motor.formalism.defs;

import fr.lip6.move.coloane.motor.formalism.ArcFormalism;
import fr.lip6.move.coloane.motor.formalism.AttributeFormalism;
import fr.lip6.move.coloane.motor.formalism.ElementBase;
import fr.lip6.move.coloane.motor.formalism.Formalism;
import fr.lip6.move.coloane.motor.formalism.NodeFormalism;
import fr.lip6.move.coloane.motor.formalism.Rule;
import fr.lip6.move.coloane.ui.model.IArcGraphicInfo;
import fr.lip6.move.coloane.ui.model.IAttributeGraphicInfo;
import fr.lip6.move.coloane.ui.model.INodeGraphicInfo;

public class PetriNets extends Formalism {

	private static final String NAME = "AMI-Net";
	private static final String IMG = "/resources/icons/ami.gif";
	private static final String EXTENSION = "petri";

	private static final int PLACE_WIDTH = 16;
	private static final int PLACE_HEIGHT = 16;
	private static final int TRANSITION_WIDTH = 24;
	private static final int TRANSITION_HEIGHT = 8;
	private static final int QUEUE_WIDTH = 16;
	private static final int QUEUE_HEIGHT = 8;

	/**
	 * Constructeur du formalisme
	 */
	public PetriNets() {
		super(NAME, IMG, EXTENSION);

		int i = 1;

		addAttributeFormalism(new AttributeFormalism(i++, "declaration", IAttributeGraphicInfo.NOR, true, true));
		addAttributeFormalism(new AttributeFormalism(i++, "author(s)", IAttributeGraphicInfo.NOR, true, true));
		addAttributeFormalism(new AttributeFormalism(i++, "version", IAttributeGraphicInfo.NOR, true, false, "0,0"));
		addAttributeFormalism(new AttributeFormalism(i++, "project", IAttributeGraphicInfo.NOR, true, true));
		addAttributeFormalism(new AttributeFormalism(i++, "title", IAttributeGraphicInfo.NOR, true, true));
		addAttributeFormalism(new AttributeFormalism(i++, "date", IAttributeGraphicInfo.NOR, true, false));
		addAttributeFormalism(new AttributeFormalism(i++, "code", IAttributeGraphicInfo.NOR, true, true));
		addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));

		/* Creation ajout des differents elements de base d'un Reseau de Petri :
		 * <ul>
		 *   <li>Place</li>
		 *   <li>Transition</li>
		 *   <li>Arc</li>
		 *   <li>Arc inhibiteur</li>
		 * </ul>
		 */

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// La place:
		ElementBase elt = new NodeFormalism("place", "Place", this, INodeGraphicInfo.FIG_CIRCLE, PLACE_WIDTH, PLACE_HEIGHT, false); //$NON-NLS-1$ //$NON-NLS-2$
		elt.addAttributeFormalism(new AttributeFormalism(i++, "name", IAttributeGraphicInfo.L1, true, false));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "domain", IAttributeGraphicInfo.L2, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "marking", IAttributeGraphicInfo.NOR, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "component", false, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/place16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/place24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// La transition:
		elt = new NodeFormalism("transition", "Transition", this, INodeGraphicInfo.FIG_RECT, TRANSITION_WIDTH, TRANSITION_HEIGHT, false); //$NON-NLS-1$ //$NON-NLS-2$
		elt.addAttributeFormalism(new AttributeFormalism(i++, "name", IAttributeGraphicInfo.L1, true, false));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "guard", IAttributeGraphicInfo.NOR, true, true, "true"));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "priority", IAttributeGraphicInfo.NOR, true, true, "0"));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "delay", IAttributeGraphicInfo.NOR, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "action", false, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/transition16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/transition24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// La transition immediate:
		elt = new NodeFormalism("immediate transition", "I. Transition", this, INodeGraphicInfo.FIG_RECT, TRANSITION_WIDTH, TRANSITION_HEIGHT, true); //$NON-NLS-1$ //$NON-NLS-2$
		elt.addAttributeFormalism(new AttributeFormalism(i++, "name", IAttributeGraphicInfo.L1, true, false));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "guard", IAttributeGraphicInfo.NOR, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "priority", IAttributeGraphicInfo.NOR, true, true, "1"));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "weight", IAttributeGraphicInfo.NOR, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/transitionimmediate16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/transitionimmediate24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// La queue:
		elt = new NodeFormalism("queue", "Queue", this, INodeGraphicInfo.FIG_QUEUE, QUEUE_WIDTH, QUEUE_HEIGHT, true); //$NON-NLS-1$ //$NON-NLS-2$
		elt.addAttributeFormalism(new AttributeFormalism(i++, "name", IAttributeGraphicInfo.L1, true, false));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "domain", IAttributeGraphicInfo.L2, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "marking", IAttributeGraphicInfo.NOR, true, true));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/queue16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/queue24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// L'arc
		elt = new ArcFormalism("arc", "Arc", this, IArcGraphicInfo.FIG_ARC_SIMPLE); //$NON-NLS-1$ //$NON-NLS-2$

		elt.addAttributeFormalism(new AttributeFormalism(i++, "valuation", IAttributeGraphicInfo.NOR, true, true, "1"));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/arc16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/arc24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Remise a zero de l'indicateur d'affichage
		i = 1;

		// L'arc hinibiteur
		elt = new ArcFormalism("inhibitor arc", "Inhibitor Arc", this, IArcGraphicInfo.FIG_ARC_INHIBITOR); //$NON-NLS-1$ //$NON-NLS-2$
		elt.addAttributeFormalism(new AttributeFormalism(i++, "valuation", IAttributeGraphicInfo.NOR, true, true, "1"));
		elt.addAttributeFormalism(new AttributeFormalism(i++, "note", false, true));
		elt.setAddrIcone16("/resources/icons/arcinhibiteur16.png"); //$NON-NLS-1$
		elt.setAddrIcone24("/resources/icons/arcinhibiteur24.png"); //$NON-NLS-1$

		addElementBase(elt);

		// Ajout des regles gerant le formalisme, ces regles definissent ce qu'on ne peut pas faire.
		// Interdit place - place
		addRule(new Rule(this.getNodeFormalism("place"), this.getNodeFormalism("place")));
		// Interdit queue - queue
		addRule(new Rule(this.getNodeFormalism("queue"), this.getNodeFormalism("queue")));
		// Interdit transition - transition
		addRule(new Rule(this.getNodeFormalism("transition"), this.getNodeFormalism("transition")));
		// Interdit transition immediate - transition immediate
		addRule(new Rule(this.getNodeFormalism("immediate transition"), this.getNodeFormalism("immediate transition")));
		// Interdit transition - transition immediate
		addRule(new Rule(this.getNodeFormalism("transition"), this.getNodeFormalism("immediate transition")));
		// Interdit transition immediate - transition
		addRule(new Rule(this.getNodeFormalism("immediate transition"), this.getNodeFormalism("transition")));
		// Interdit transition queue - place
		addRule(new Rule(this.getNodeFormalism("queue"), this.getNodeFormalism("place")));
		// Interdit transition place - queue
		addRule(new Rule(this.getNodeFormalism("place"), this.getNodeFormalism("queue")));
	}
}