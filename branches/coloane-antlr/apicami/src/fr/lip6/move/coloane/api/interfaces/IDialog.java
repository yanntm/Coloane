package fr.lip6.move.coloane.api.interfaces;

public interface IDialog {

//	 Types du dialogue
	static final int  DLG_STANDARD = 1;
	static final int  DLG_WARNING = 2;
	static final int  DLG_ERROR = 3;
	static final int  DLG_INTERACTIVE = 4;


	/* Saisie autorisee */
	int INPUT_AUTHORIZED = 1;
	/* Saisie interdite */
	int INPUT_FORBIDDEN = 2;
	/* Saisie autorisee et echappement possible */
	int INPUT_AND_ABORT_AUTHORIZED = 5;

	/* Affichage mono-ligne */
	int SINGLE_LINE = 1;
	/* Affichage multi-ligne avec selection simple */
	int MULTI_LINE_WITH_SINGLE_SELECTION = 2;
	/* Affichage multi-ligne avec selection multiple */
	int MULTI_LINE_WITH_MULTI_SELECTION = 5;


	/**
	 * Indique l'identifiant de la boite de dialogue.<br>
	 * Chaque boite de dialogue creee par la plate-forme possede un identifiant.
	 * @return L'identifiant
	 */
	public int getId();


	//saisie autorisée, interdite, abort.
	/**
	 * Indique le type de saisie autorise<br>
	 * Les valeurs possibles sont presentees dans IDialogCom
	 * @return Le type de saisie de la boite de dialogue
	 */
	public int getInputType();


	public String[] getDialogButtons();

	/**
	 * Indique le titre de la boite de dialogue
	 * @return Le titre a afficher en tant que titre de la boite de dialogue
	 */
	public String getDialogTitle();


	/**
	 * Indique le message a afficher dans la boite de dialogue
	 * @return La chaine de caractere a afficher dans la boite de dialogue
	 */
     public String getDialogMessage();

	/**
	 * Indique le message d'aide associe avec la boite de dialogue
	 * @return La chaine de caracteres contenant le message d'aide
	 */
	public String getHelp();


	/**
	 * Indique si la saisie a le droit d'etre multilignes
	 * @return Un indicateur
	 * @see IDialog
	 */
	public int getMultiLine();


	/**
	 * nous donne les lignes de la saisie .
	 * @return les lignes.
	 */
	public String[] getLines();

	/** Action de l'utilisateur sur la fenêtre
	/* buttonNumber : numéro du bouton appuyé
	/* responseMultiLine : sélections de l'utilisateur.
	*/
	public String[] setDialogResponse(int buttonNumber, int[] responseMultiLine);

}
