package fr.lip6.move.coloane.core.ui.properties.sections;

import fr.lip6.move.coloane.core.ui.model.IModelImpl;

/**
 * Section qui affiche les attributs du model.
 */
public class ModelSection extends AbstractElementSection<IModelImpl> {

	@Override
	public final void refresh() {
		if (!isDisposed()) {
			refreshControls(
					getElement().getFormalism().getName(),
					getElement().getFormalism().getListOfAttribute());

			refreshContent();
			redraw();
		}
	}

}
