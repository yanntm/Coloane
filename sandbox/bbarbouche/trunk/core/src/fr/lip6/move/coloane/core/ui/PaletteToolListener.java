package fr.lip6.move.coloane.core.ui;

import org.eclipse.gef.palette.PaletteListener;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * Un ecouteur pour la palette<br>
 * Cet ecouteur permet de detecter l'outil en cours d'utilisation.
 * @see Editor#createPaletteViewerProvider()
 */
public class PaletteToolListener implements PaletteListener {

	public final void activeToolChanged(PaletteViewer viewer, ToolEntry tool) {
		tool.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED, Boolean.FALSE);
	}

}