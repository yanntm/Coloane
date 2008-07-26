package fr.lip6.move.coloane.api.observables;

import fr.lip6.move.coloane.api.ApiConnection;

/**
 * Fabrique des objets observables de l'API
 *
 * @author Kahina Bouarab
 * @author Youcef Belattaf
 */
public final class ObservableFactory {

	/** Constructeur privé */
	private ObservableFactory() { }

	/**
	 * Création d'un observable pour l'ouverture de session.<br>
	 * Responsable du passage des menus et des modifications sur ceux-ci
	 * @return L'observable fraîchement créé
	 */
	public static ReceptMenuObservable getNewSessionObservable() {
		return (ReceptMenuObservable) new ReceptMenuObservable();
	}

	/**
	 * Création d'un observable pour la déconnexion brutale de la part de FrameKit.<br>
	 * @return L'observable fraîchement créé
	 */
	public static BrutalInterruptObservable getNewBrutalInterruptObservable() {
		return new BrutalInterruptObservable();
	}

	/**
	 * Création d'un observable pour la déconnexion de la plate-forme
	 * @return L'observable fraîchement créé
	 */
	public static DisconnectObservable getNewCloseConnectionObservable() {
		return new DisconnectObservable();
	}

	/**
	 * Création d'un observable pour la connexion à la plate-forme
	 * @param apiConnection Le gestionnaire de connexion qui doit être averti
	 * @return L'observable fraîchement créé
	 */
	public static ConnectionObservable getNewOpenConnectionObservable(ApiConnection apiConnection) {
		return new ConnectionObservable(apiConnection);
	}

	/**
	 * Création d'un observable pour la réception d'un message en provenance de FrameKit
	 * @return L'observable fraîchement créé
	 */
	public static ReceptMessageObservable getNewSpecialMessageObservable() {
		return new ReceptMessageObservable();
	}

	public static ReceptDialogObservable getNewreceptDialogObservable() {
		return new ReceptDialogObservable();
	}

	public static ReceptResultObservable getNewReceptResultObservable() {
		return new ReceptResultObservable();
	}

	public static ReceptDialogObservable getNewReceptDialogObservable() {
		return new ReceptDialogObservable();
	}
}
