package fr.lip6.move.coloane.core.motor.formalisms.elements;

/**
 * Cette classe représente les caracteristiques d'un attribut d'un élément de formalisme.<br>
 * Un attribut est une caractéristique d'un élément de base.<br>
 * Chaque élément du formalisme maintient une liste de ses attributs.
 */
public class Attribute {
	/** Nom de l'attribut. */
	private String name;

	/** Attribut est-il multilignes. */
	private boolean multiline;

	/** L'attribut est-il affichable. */
	private boolean drawable;

	/** Valeur par défaut de l'attribut. */
	private String defaultValue = null;

	/** Ordre d'affichage dans la fenetre des proprietes */
	private int order;

	/**
	 * Construit un nouvel attribut
	 * @param order L'ordre d'affichage dans la fenêtre de propiétés
	 * @param name Le nom de l'attribut.
	 * @param drawable L'information est elle affichable a l'ecran ?
	 * @param multiline L'attribut est il multi-lignes ?
	 */
	public Attribute(int order, String name, boolean drawable, boolean multiline) {
		this.name = name;
		this.drawable = drawable;
		this.multiline = multiline;
		this.order = order;
	}

	/**
	 * Construit un nouvel attribut (avec une valeur par defaut)
	 * @param order L'ordre d'affichage dans la fenetre des proprietes
	 * @param name Le nom de l'attribut.
	 * @param drawable L'information est elle affichable a l'ecran ?
	 * @param multiline L'attribut est il multi-lignes ?
	 * @param defaultValue La valeur par defaut de l'attribut.
	 */
	public Attribute(int order, String name, boolean drawable, boolean multiline, String defaultValue) {
		this(order,name,drawable,multiline);
		this.defaultValue = defaultValue;
	}


	/**
	 * @return Le nom de l'attribut
	 */
	public final String getName() { return this.name; }

	/**
	 * @return L'indicateur qui permet de savoir si un attribut est affichable dans l'éditeur grapique
	 */
	public final boolean isDrawable() { return drawable; }

	/**
	 * @return L'indicateur qui permet de savoir si un attribut peut être réparti sur plusieurs lignes
	 */
	public final boolean isMultiLine() { return multiline; }

	/**
	 * @return La valeur par défaut de l'attribut
	 */
	public final String getDefaultValue() {
		if (defaultValue != null) { return defaultValue; }
		return ""; //$NON-NLS-1$
	}

	/**
	 * @return L'index d'affichage à l'intérieur de la fenêtre de propriété
	 */
	public final int getOrder() { return this.order; }
}
