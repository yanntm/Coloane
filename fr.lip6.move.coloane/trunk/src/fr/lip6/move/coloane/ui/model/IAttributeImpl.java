package fr.lip6.move.coloane.ui.model;

import fr.lip6.move.coloane.interfaces.model.IAttribute;

/**
 * Interface des attributs d'un element du modele (noeud ou arc). <br>
 * Ses attributs sont :
 * <ul>
 * 	<li>id : nom unique (dans le projet) de la propriete
 * 	<li>displayName : chaine de characteres a afficher dans le PropertyView d'Eclipse
 * 	<li>propType : type (boolean, integer, string, list)
 * 	<li>value : valeur de la propriete
 * 	<li>validation : ??
 * 	<li>validation message : ??
 * </ul> 
 */

public interface IAttributeImpl {
	
	/** ID pour la propriete lorsqu'un changement de valeur */
	public static final String VALUE_PROP = "Attribute.ValueUpdate";
	
	/** ID pour la propriete lorsqu'un changement de la position */
	public static final String LOCATION_PROP = "Attribute.Location";
	
    /** ID pour la propriete lorsque l'attribut doit etre selectionne */
    public static final String SELECT_LIGHT_PROP = "Attribute.SelectLightUpdate";
    
    /** ID pour la propriete lorsque l'attribut doit etre selectionne */
    public static final String SELECT_HEAVY_PROP = "Attribute.SelectHeavyUpdate";
    
    /** ID pour la propriete lorsque l'attribut doit etre selectionne */
    public static final String UNSELECT_LIGHT_PROP = "Attribute.UnSelecLighttUpdate";

    /** ID pour la propriete lorsque l'attribut doit etre selectionne */
    public static final String UNSELECT_HEAVY_PROP = "Attribute.UnSelectHeavyUpdate";

    /**
	 * Retourne l'identifiant de l'attribut
	 * @return
	 */
	public String getId();
	
	/**
	 * Retourne la chaine qui sera affichee sur la fenetre PropertiesView d'Eclipse
	 * @return String
	 */
	public String getDisplayName();
	
	/**
	 * Retourne la valeur de l'attribut
	 * @return Object
	 */
	public String getValue();

	/**
	 * Inidique la nouvelle valeur pour l'attribut generique
	 * @param oldValue L'ancienne valeur de l'attribut
	 * @param newValue La nouvelle valeur de l'attribut
	 */
	public void setValue(String oldValue, String newValue);
	
	/**
	 * Indicateur d'affichage
	 * @return true si l'attribut doit etre affiche
	 */
	public boolean isDrawable();
	
	/**
	 * Indicateur d'attribut multiligne
	 * @return true si l'attribut est multiligne
	 */
	public boolean isMultiline();
	
	/**
	 * Retourne l'objet contenant les informations graphiques concernant cet attribut
	 * @return IAttributeGraphicInfo
	 * @see IAttributeGraphicInfo
	 */
	public IAttributeGraphicInfo getGraphicInfo();
	
	/**
	 * Retourne l'attribut generique attache a l'attribut augmente
	 * @return IAttribute L'atribut generique
	 */
	public IAttribute getGenericAttribute();
	
	/**
	 * Retourne la valeur par defaut de l'attribut
	 * @return La valeur par defaut
	 */
	public String getDefaultValue();
	
	/**
	 * TODO : A documenter
	 * @return 
	 */
	public boolean getValidation();
	
	/**
	 * TODO : A documenter
	 * @return Le message
	 */
	public String getValidationMessage();
	
	/**
	 * Retourne la reference de l'element proprietaire de cet attribut.
	 * Un IElement peut etre caste en INodeImpl ou IArcImpl
	 * @return
	 */
	public IElement getReference();
	
	/**
	 * Leve un evenement pour demander la mise en valeur de l'attribut.
	 * @param state Selection / Deselection
	 */
	public void setSelect(boolean state);
	
	/**
	 * Leve un evenement pour demander la mise en valeur de l'attribut.
	 * Deux mises en valeur sont possible :
	 * <ul>
	 * 	<li>Une legere qui correspond a un survol de l'objet reference</li>
	 * 	<li>Une lourde qui correspond a un clic sur l'objet referent</li>
	 * </ul>
	 * @param light Type de mie en valeur
	 * @param state Selection / Deselection
	 */
	public void setSelect(boolean light, boolean state);
}
