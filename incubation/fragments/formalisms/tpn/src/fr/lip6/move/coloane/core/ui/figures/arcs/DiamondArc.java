package fr.lip6.move.coloane.core.ui.figures.arcs;

import fr.lip6.move.coloane.core.ui.figures.AbstractArcFigure;
import fr.lip6.move.coloane.core.ui.figures.IArcFigure;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

/**
 * Test or Reset arc definition.<br>
 * This arc has a colored diamond shape at its target end.
 *
 * @author Yann Thierry-Mieg
 */
public class DiamondArc extends AbstractArcFigure implements IArcFigure {
	private Color color;

	/**
	 * Constructor
	 * @param color The color used for the diamond
	 */
	public DiamondArc(Color color) {
		this.color = color;
		setTargetDecoration(buildDecoration());
	}

	/**
	 * Create the decoration that will be used for the target-side of the arc
	 * @return the decoration
	 */
	private PolygonDecoration buildDecoration() {
		PolygonDecoration decoration = new PolygonDecoration();

		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-1, 1);
		decorationPointList.addPoint(-2, 0);
		decorationPointList.addPoint(-1, -1);

		decoration.setTemplate(decorationPointList);
		decoration.setFill(true);
		decoration.setBackgroundColor(color);

		return decoration;
	}
}