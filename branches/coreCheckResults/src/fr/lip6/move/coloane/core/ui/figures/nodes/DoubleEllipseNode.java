package fr.lip6.move.coloane.core.ui.figures.nodes;

import fr.lip6.move.coloane.core.ui.figures.AbstractNodeFigure;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Description d'une figure en forme d'ellipse avec une seconde ellipse à l'intérieur.<br>
 * Cette figure peut être utilisée pour représenter un noeud sur un modèle.
 */
public class DoubleEllipseNode extends AbstractNodeFigure {
	/**
	 * Marge entre les deux ellipses.
	 */
	private static final int MARGIN = 2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void fillShape(Graphics graphics) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds().getResized(-1, -1));
		r.shrink(MARGIN, MARGIN);
		graphics.fillOval(r);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void outlineShape(Graphics graphics) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds());
		r.width--;
		r.height--;
		r.shrink((lineWidth - 1) / 2, (lineWidth - 1) / 2);
		graphics.drawOval(r);
		r.shrink(MARGIN, MARGIN);
		graphics.drawOval(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public final ConnectionAnchor getConnectionAnchor() {
		return new EllipseAnchor(this);
	}

}