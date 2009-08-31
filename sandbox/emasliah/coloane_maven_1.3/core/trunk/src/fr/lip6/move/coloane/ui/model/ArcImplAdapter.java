package fr.lip6.move.coloane.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import fr.lip6.move.coloane.exceptions.BuildException;
import fr.lip6.move.coloane.interfaces.exceptions.SyntaxErrorException;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.objects.IPosition;
import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.model.Arc;
import fr.lip6.move.coloane.model.Attribute;
import fr.lip6.move.coloane.motor.formalism.AttributeFormalism;
import fr.lip6.move.coloane.motor.formalism.ElementBase;
import fr.lip6.move.coloane.motor.formalism.Formalism;

/**
 * Description de l'adapteur pour un arc du modele.
 * L'arc adapte est generique. L'adapteur fourni les mehodes et structure utiles pour GEF.
 * Cet adapteur doit gerer la coherence entre l'arc generique et l'arc augemente.
 * @see IArcImpl
 */

public class ArcImplAdapter extends AbstractModelElement implements IArcImpl, IElement {

	/** Id pour la serialisation */
	private static final long serialVersionUID = 1L;

	/** Noeud source repris depuis l'arc generique */
	private INodeImpl source;

	/** Noeud cible repris depuis l'arc generique */
	private INodeImpl target;

	/** Booleen pour l'etat de la connexion */
	private boolean isConnected;

	/** Arc generique a adapter */
	private IArc arc;

	private IArcGraphicInfo graphicInfo;

	/** Element de base du formalisme associe au noeud */
	private ElementBase elementBase;

	/** Le modele augemente qui contient cet arc augemente */
	private IModelImpl modelAdapter;

	/**
	 * Constructeur <br>
	 * Creation d'un adaptateur pour un arc generique deja existant
	 * @param arc Arc generique pour l'adaptateur
	 * @param source Noeud source
	 * @param target Noeud cible
	 * @param base Element de base du formalisme
	 * @throws BuildException 
	 */
	public ArcImplAdapter(IArc arc, INodeImpl source, INodeImpl target, ElementBase base) throws BuildException {
		this.elementBase = base;		
		this.arc = arc;

		// Creation de la liste des attributs
		this.setProperties(arc);

		// Connecte l'arc augmente(modifie les noeud adaptes)
		// !! Il faut absolument executee cette methode a la fin du constructeur !
		this.reconnect(source,target);

		this.graphicInfo = new ArcGraphicInfo(this);
	}


	/** 
	 * Constructeur<br>
	 * Creation d'un arc a partir des deux noeuds source et cible.
	 * @param source Noeud source
	 * @param target Noeud cible
	 * @param base Element de base du formalisme         
	 * @throws BuildException 
	 */
	public ArcImplAdapter(INodeImpl source, INodeImpl target, ElementBase base) throws BuildException {
		this.elementBase = base;

		// Creation de l'arc generique
		this.arc = new Arc(base.getName());

		// Creation de la liste des attributs
		this.setProperties();

		// Connecte l'arc (modifie les noeud adaptes)
		// !! Il faut absolument executee cette methode a la fin du constructeur !
		this.reconnect(source, target);

		this.graphicInfo = new ArcGraphicInfo(this);
	}

	/**
	 * Creation des attributs generique prevus par le formalisme
	 * Creation des attributs adaptes correspondant a ces attributs generiques
	 * Tous les attributs sont initialises aux valeurs par defaut prevus par le formalisme
	 */
	private void setProperties() {
		this.properties.clear();

		// Parcours de tout les attributs du formalisme
		Iterator iterator = this.elementBase.getListOfAttribute().iterator();

		while (iterator.hasNext()) {
			AttributeFormalism attributeFormalism = (AttributeFormalism) iterator.next();

			/* Creation de l'attribut generique */
			IAttribute attribute = new Attribute(attributeFormalism.getName(),new String(attributeFormalism.getDefaultValue()),arc.getId());
			this.arc.addAttribute(attribute);

			/* Creation de l'attribut adapte */
			IAttributeImpl attributeAdapter = new AttributeImplAdapter(attribute,attributeFormalism,this);

			/* Ajout de cet attribut dans la liste des propriete pour la vue GEF */
			this.properties.put(attributeAdapter.getId(), attributeAdapter);
		}
	}


	/**
	 * Affectation des attributs corrects (ceux contenu dans le modele generique)
	 * Creation des attribut generiques manquants et attributs adaptes correspondants
	 * Cela peut �tre utile lorsq'un modele est lu depuis un fichier.
	 * @param node Le noeud generique qui vient d'etre augemente
	 */
	private void setProperties(IArc arc) {

		// Parcours de tous les attributs du formalisme
		Iterator iterator = this.elementBase.getListOfAttribute().iterator();

		while (iterator.hasNext()) {
			AttributeFormalism attributeFormalism = (AttributeFormalism) iterator.next();

			IAttributeImpl attributeAdapter = null;
			IAttribute attribute = null;

			// On parcours tous les attributs generique deja definis dans notre arc generique
			// On cherche l'attribut dans notre noeud generique qui correspond a l'attibut prevu par le formalisme (courant)
			boolean find = false;
			for (int i = 0; (i < arc.getListOfAttrSize()) && !find; i++) {

				// Si l'attribut du formalisme est bien decrit dans notre modele... On cree l'adapteur
				// Pas besoin de creer un nouvel attribut dans le modele !
				attribute = arc.getNthAttr(i);
				if (attributeFormalism.getName().equalsIgnoreCase(attribute.getName())) {
					attributeAdapter = new AttributeImplAdapter(attribute, attributeFormalism,this);
					find = true;
				}
			}

			// Si aucun attribut generique dans notre modele ne correspond a celui du formalisme... alors notre modele n'est pas complet
			// Il faut donc creer un attribut generique et un adapteur pour cet attribut du formalisme
			if (!find) {
				attribute = new Attribute(attributeFormalism.getName(), new String(attributeFormalism.getDefaultValue()), 1);
				attributeAdapter = new AttributeImplAdapter(attribute, attributeFormalism,this);
				this.arc.addAttribute(attribute);
			}

			this.properties.put(attributeAdapter.getId(), attributeAdapter);
		}
	} 

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getId()
	 */
	public int getId() {
		return this.getGenericArc().getId();
	}

	public IArcGraphicInfo getGraphicInfo() {
		return this.graphicInfo;
	}


	/**
	 * Retourne la liste des attributs qui peuvent etre affiches sur l'editeur
	 * @return Le liste des attributs
	 */
	private List<IAttributeImpl> getDrawableAttributes() {
		List<IAttributeImpl> list = new ArrayList<IAttributeImpl>();
		Iterator iterator = this.properties.values().iterator();    	
		while (iterator.hasNext()) {
			IAttributeImpl att = (IAttributeImpl)iterator.next();
			if (!(att.getValue().equals(att.getDefaultValue())) && att.isDrawable()) {
				list.add(att);
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#setAttributesSelected(boolean, boolean)
	 */
	public void setAttributesSelected(boolean state) {
		List<IAttributeImpl> list = this.getDrawableAttributes();
		for (IAttributeImpl att : list) {
			att.setSelect(state);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IElement#getAttributes()
	 */
	public List<IElement> getAttributes() {
		List<IElement> list = new ArrayList<IElement>();

		// Ajout des attributs "personnels" du noeud
		List<IAttributeImpl> attributes  = this.getDrawableAttributes();
		for (IAttributeImpl a : attributes) {
			list.add((IElement) a);
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getContextMenus()
	 */
	public Collection getContextMenus() {
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getSource()
	 */
	public INodeImpl getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getTarget()
	 */
	public INodeImpl getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#reconnect()
	 */
	public void reconnect() throws BuildException {
		if (!isConnected) {
			this.source.addOutputArc(this);
			this.target.addInputArc(this);
			isConnected = true;
		}
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#reconnect(fr.lip6.move.coloane.ui.model.INodeImpl, fr.lip6.move.coloane.ui.model.INodeImpl)
	 */
	public void reconnect(INodeImpl source, INodeImpl target) throws BuildException {
		this.disconnect();

		if (source == null || target == null) {
			throw new BuildException(Coloane.traduction.getString("ui.model.ArcImplAdapter.0")); //$NON-NLS-1$
		}

		// On indique a l'arc generique quels sont ces sources et cibles
		this.arc.setStartingNode(source.getGenericNode());
		this.arc.setEndingNode(target.getGenericNode());

		this.source = source;
		this.target = target;

		// Meme indication pour l'arc adapte
		this.reconnect();
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#disconnect()
	 */
	public void disconnect() {
		if (isConnected) {

			// Action sur l'arc generique
			this.arc.setStartingNode(null);
			this.arc.setEndingNode(null);

			// Action sur l'arc adapte
			this.source.removeArc(this);
			this.target.removeArc(this);

			// On indique la deconnexion			
			this.source = null;
			this.target = null;

			isConnected = false;
		}
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getGenericArc()
	 */
	public IArc getGenericArc() {
		return this.arc;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getFormalism()
	 */
	public Formalism getFormalism() {
		return this.elementBase.getFormalism();
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getModelAdapter()
	 */
	public IModelImpl getModelAdapter() {
		return modelAdapter;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#setModelAdapter(fr.lip6.move.coloane.ui.model.IModelImpl)
	 */
	public void setModelAdapter(IModelImpl modelAdapter) {
		this.modelAdapter = modelAdapter;
	}


	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return super.getPropertyDescriptors();

	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getArcValue()
	 */
	public String getArcValue() {
		String valeur = ""; //$NON-NLS-1$
		for (int i = 0; i < this.arc.getListOfAttrSize(); i++) {
			if (this.arc.getNthAttr(i).getName().equalsIgnoreCase("valuation")) { //$NON-NLS-1$
				valeur = this.arc.getNthAttr(i).getValue();
				break;
			}
		}
		return valeur;
	}

	/* (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getArcLabel()
	 */
	public String getArcLabel() {
		String valeur = ""; //$NON-NLS-1$
		for (int i = 0; i < this.arc.getListOfAttrSize(); i++) {
			if (this.arc.getNthAttr(i).getName().equalsIgnoreCase("label")) { //$NON-NLS-1$
				valeur = this.arc.getNthAttr(i).getValue();
				break;
			}
		}
		return valeur;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#addInflexPoint(org.eclipse.draw2d.geometry.Point, int)
	 */
	public void addInflexPoint(Point p, int index) {
		try {
			this.arc.addPI(p.x, p.y, index);
			firePropertyChange(ArcImplAdapter.INFLEXPOINT_PROP, null,this);
		} catch (SyntaxErrorException e) {
			System.err.println("Imopssible d'ajouter le point d'inflexion");
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#removeInflexPoint(int)
	 */
	public void removeInflexPoint(int index) {
		try {
			this.arc.removePI(index);
			firePropertyChange(ArcImplAdapter.INFLEXPOINT_PROP, null,this);
		} catch (SyntaxErrorException e) {
			System.err.println("Imopssible de supprimer le point d'inflexion");
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#modifyInflexPoint(int, org.eclipse.draw2d.geometry.Point)
	 */
	public void modifyInflexPoint(int index, Point p) {
		this.arc.modifyPI(index, p.x, p.y);
		firePropertyChange(ArcImplAdapter.INFLEXPOINT_PROP, null,this);
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getInflexPoint(int)
	 */
	public Point getInflexPoint(int index) {
		IPosition p = this.arc.getNthPI(index);
		return new Point(p.getXPosition(),p.getYPosition());
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#getInflexPoints()
	 */
	public List<Bendpoint> getInflexPoints() {
		List<Bendpoint> bendPoints = new ArrayList<Bendpoint>(); 
		for (IPosition p : this.arc.getListOfPI()) {
			bendPoints.add(new AbsoluteBendpoint(p.getXPosition(),p.getYPosition()));
		}
		return bendPoints;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.ui.model.IArcImpl#updateAttributesPosition()
	 */
	public void updateAttributesPosition() {

		// Calcul du nouveau point milieu
		Point newMiddlePoint = this.graphicInfo.findMiddlePoint();

		// Position actuelle
		Point oldMiddlePoint = this.graphicInfo.getMiddlePoint();

		// Calcul du decalage
		int deltaX = newMiddlePoint.x - oldMiddlePoint.x;
		int deltaY = newMiddlePoint.y - oldMiddlePoint.y;

		// Mise a jour des coordonnees des attributs
		for (IAttributeImpl attr : this.getDrawableAttributes()) {
			Point attrLocation = attr.getGraphicInfo().getLocation();
			attr.getGraphicInfo().setLocation(attrLocation.x+deltaX, attrLocation.y+deltaY);
		}

		this.graphicInfo.updateMiddlePoint();
	}
}