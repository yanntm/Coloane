package fr.lip6.move.coloane.ui.model;

import java.io.Serializable;

import org.eclipse.draw2d.geometry.Point;

public class AttributeGraphicInfo implements IAttributeGraphicInfo, Serializable {

	/** Id pour la serialisation */
	private static final long serialVersionUID = 1L;
	
	/** Le noeud enrichi */
	private final IAttributeImpl attribute;
	
	/** Les coordonees */
	private int x = 0;
	private int y = 0;

	/** 
	 * Constructeur
	 * @param attributeImpl L'attribut enrichi
	 */
	public AttributeGraphicInfo(IAttributeImpl attribute) {
		this.attribute = attribute;
		this.x = attribute.getGenericAttribute().getXPosition();
		this.y = attribute.getGenericAttribute().getYPosition();
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IAttributeGraphicInfo#getLocation()
	 */
	public Point getLocation() {
		return new Point(this.x,this.y);
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IAttributeGraphicInfo#setLocation(int, int)
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		
		// Mise a jour du noeud generique
		this.attribute.getGenericAttribute().setPosition(x, y);
		
		// Lever un evenement
		((AttributeImplAdapter)this.attribute).firePropertyChange(AttributeImplAdapter.LOCATION_PROP,null,new Point(x, y));				
	}
}

