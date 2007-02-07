package fr.lip6.move.coloane.interfaces.model;

import java.util.Vector;
import java.io.Serializable;

/**
 * Cette classe decrit un arc generique d'un modele.<br>
 * Un arc se compose des elements suivants :
 * <ul>
 * 	<li> Un type regroupant des informations destinees au formalisme </li>
 *	<li> Un identifiant unique le designant dans un modele</li>
 *	<li> Un positionnement (X,Y)</li>
 *	<li> Une liste d'attributs </li>
 *	<li> Un noeud de depart (source)</li>
 *	<li> Un noeud d'arrivee (cible)</li>
 * </ul>
 * Quelques contraintes de construction sont attachees a l'arc :
 * <ul>
 * 	<li>Un arc possede obligatoirement un noeud source et un noeud cible.</li> 
 * </ul>
 * @see INode
 * @see IAttribute
 * @see IArc
 */
public abstract class Arc extends Base implements IArc, Serializable { 
    
    /** Utilise lors de la deserialization afin de s'assurer que les versions des classes Java soient concordantes. */
    private static final long serialVersionUID = 1L;

    /** Type de l'arc */
    protected String arcType;
    
    /** Identifiant de l'arc */
    protected int id;
    
    /** Position absolue horizontale depuis le bord gauche de la fenetre d'affichage du modele. */
    protected int xPosition;

    /** Position absolue verticale depuis le bord haut de la fenetre d'affichage du modele. */
    protected int yPosition;

    /** Vecteur contenant l'ensemble des objets de type Attribut de l'arc.
     * @see IAttribute
     */
    private Vector<IAttribute> listOfAttr;

    /** Noeud d'entree de l'arc. */
    protected INode startingNode;

    /** Noeud de sortie de l'arc. */
    protected INode endingNode;

    /**
     * Constructeur de la classe Arc. 
     * @param arcType Type de l'arc
     * @param id Identifant unique de l'arc
     */
    public Arc(String arcType, int id) {
        this.arcType = arcType;
        this.id = setUniqueId(id);
        this.xPosition = 0;
        this.yPosition = 0;
        this.listOfAttr = new Vector<IAttribute>();
        this.startingNode = null;
        this.endingNode = null;
    }
    
    /**
     * Constructeur de la classe Arc. 
     * @param arcType Type de l'arc
     */
    public Arc(String arcType) {
        this.arcType = arcType;
        this.id = getUniqueId();
        this.xPosition = 0;
        this.yPosition = 0;
        this.listOfAttr = new Vector<IAttribute>();
        this.startingNode = null;
        this.endingNode = null;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getArcType()
	 */
    public String getArcType() {
        return this.arcType;
    }
    
    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#setPosition(int, int)
	 */
    public void setPosition(int x, int y) {
        if (x > 0 && y > 0) {
            this.xPosition = x;
            this.yPosition = y;
        }
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getId()
	 */
	public int getId() {
	    return this.id;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getXPosition()
	 */
    public int getXPosition() {
        return this.xPosition;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getYPosition()
	 */
    public int getYPosition() {
        return this.yPosition;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#setStartingNode(fr.lip6.move.coloane.interfaces.model.Node)
	 */
    public void setStartingNode(INode node) {
        if (this.startingNode == null) {
            this.startingNode = node;
        }
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#setEndingNode(fr.lip6.move.coloane.interfaces.model.Node)
	 */
    public void setEndingNode(INode node) {
        if (this.endingNode == null) {
            this.endingNode = node;
        }
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getStartingNode()
	 */
    public INode getStartingNode() {
        return this.startingNode;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getEndingNode()
	 */
    public INode getEndingNode() {
        return this.endingNode;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#addAttribute(fr.lip6.move.coloane.interfaces.model.Attribute)
	 */
    public void addAttribute(IAttribute attribute) {
    	this.listOfAttr.addElement(attribute);
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#removeAttribute(fr.lip6.move.coloane.interfaces.model.Attribute)
	 */
    public void removeAttribute(IAttribute attribute) {
        try {
            this.listOfAttr.remove(attribute);
        } catch (ArrayIndexOutOfBoundsException e) {
        	e.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#removeAttribute(int)
	 */
    public void removeAttribute(int index) {
        try {
            this.listOfAttr.remove(index);
        } catch (ArrayIndexOutOfBoundsException e) {
        	e.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getListOfAttrSize()
	 */
    public int getListOfAttrSize() {
        return this.listOfAttr.size();
    }
    
    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getListOfAttr()
	 */
    public Vector<IAttribute> getListOfAttr() {
        return this.listOfAttr;
    }
    
    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#getNthAttr(int)
	 */
    public IAttribute getNthAttr(int index) {
        IAttribute attr = null;
        try {
            attr = (IAttribute) this.listOfAttr.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return attr;
    }

    /* (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.model.IArc#translateToCAMI()
	 */
    public abstract String[] translate();
}

