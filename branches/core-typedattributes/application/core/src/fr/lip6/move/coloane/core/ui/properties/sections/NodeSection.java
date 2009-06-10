package fr.lip6.move.coloane.core.ui.properties.sections;

import fr.lip6.move.coloane.interfaces.model.INode;

/**
 * Section qui affiche les attributs du noeud.
 */
public class NodeSection extends AbstractElementSection<INode> {

	/** {@inheritDoc} */
	@Override
	public final void refresh() {
		if (!isDisposed()) {
			refreshControls(
					getElements().get(0).getNodeFormalism().getName(),
					getElements().get(0).getNodeFormalism().getAttributes());

			refreshContent();
			redraw();
		}
	}

}
