package fr.lip6.move.coloane.ui.views;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import fr.lip6.move.coloane.ui.model.AbstractModelElement;
import fr.lip6.move.coloane.ui.model.INodeGraphicInfo;
import fr.lip6.move.coloane.ui.model.NodeImplAdapter;

public class NodeFigure extends Figure implements INodeFigure, HandleBounds {

	private IFigure figure;
	private IFigure zone;
	
	private Label nameLabel;
	private Label valueLabel;
	private Label domainLabel;
	
	private INodeGraphicInfo nodeGraphInfo;

	/**
	 * ??
	 * @param element
	 */
	public NodeFigure (AbstractModelElement element) {
		if (element instanceof NodeImplAdapter) {
			createNodeFigure((NodeImplAdapter) element);
		}
	}
	
	/**
	 * Creation de la figure associee a un noeud
	 * @param node Le modele enrichi du noeud
	 */
	private void createNodeFigure(NodeImplAdapter node) {
		
		// Recupere les options graphiques definies pour le formalisme
		nodeGraphInfo = node.getGraphicInfo();
		
		FlowLayout layout = new FlowLayout();
		layout.setMajorAlignment(FlowLayout.ALIGN_LEFTTOP);
		setLayoutManager(layout);	
		layout.setHorizontal(true);
		
		// Le cas d'un place ou d'un etat simple
		if (nodeGraphInfo.getFigureStyle() == INodeGraphicInfo.FIG_CIRCLE) {
			figure = new Ellipse();
			figure.setForegroundColor(ColorConstants.black);
			figure.setSize(16, 16);
			
			if (nodeGraphInfo.isFilled()) {
				figure.setBackgroundColor(ColorConstants.black);
			}
			add(figure);
			
		// Le cas d'un etat initial
		} else if (nodeGraphInfo.getFigureStyle() == INodeGraphicInfo.FIG_DBLCIRCLE) {
			figure = new Ellipse();
			figure.setForegroundColor(ColorConstants.black);
			figure.setSize(16, 16);
						
			IFigure figure2 = new Ellipse();
			figure2.setForegroundColor(ColorConstants.black);
			figure2.setSize(12, 12);
			figure2.setLocation(new Point(2,2));
			figure.add(figure2);
			
			add(figure);
			
		// Le cas d'une queue
		} else if (nodeGraphInfo.getFigureStyle() == INodeGraphicInfo.FIG_QUEUE) {
			figure = new RoundedRectangle();
			figure.setSize(16, 8);
			figure.setForegroundColor(ColorConstants.black);
			add(figure);	
			
		// Le reste des cas (transition)
		} else {
			figure = new RectangleFigure();
			figure.setSize(24, 8);
			figure.setForegroundColor(ColorConstants.black);
			
			if (nodeGraphInfo.isFilled()) {
				figure.setBackgroundColor(ColorConstants.black);
			}			
			add(figure);
		}
		
		// Creation de la zone des labels
		zone = new Figure();
		FlowLayout labelLayout = new FlowLayout();
		labelLayout.setHorizontal(false);
		zone.setLayoutManager(labelLayout);
		add(zone);
		
		
		// Creation des labels
		nameLabel = new Label("");
		Font nameFont = new Font(null,"arial",11,SWT.BOLD);
		nameLabel.setFont(nameFont);
		
		valueLabel = new Label("");
		Font valueFont = new Font(null,"arial",10,SWT.ITALIC);
		valueLabel.setFont(valueFont);
		
		domainLabel = new Label("");
		Font domainFont = new Font(null,"arial",10,SWT.NORMAL);
		domainLabel.setFont(domainFont);
		
		zone.add(nameLabel);
		zone.add(valueLabel);
		zone.add(domainLabel);
	}
	
	/**
	 * Indique les bornes reelles de la figure.
	 */
	public Rectangle getHandleBounds() {
		return new Rectangle(nodeGraphInfo.getLocation(),nodeGraphInfo.getSize());
	}
	
	/**
	 * Retourne le symbole de la figure.<br>
	 * Il faut consid�rer ce symbole lorsqu'on essaie de creer des connexions
	 */
	public IFigure getSymbol () {
		return figure;
	}

	/**
	 * Modifie la valeur du label nom attache
	 * @param name
	 */
	public void setNodeName(String name) {
		nameLabel.setText(name);
	}
	
	/**
	 * Modifie la valeur du label value attache
	 * @param value
	 */
	public void setNodeValue(String value) {
		valueLabel.setText(value);	
	}
	
	/**
	 * Modifie la valeur du label domaine attache
	 * @param domain
	 */
	public void setNodeDomain(String domain) {
		domainLabel.setText(domain);	
	}
}