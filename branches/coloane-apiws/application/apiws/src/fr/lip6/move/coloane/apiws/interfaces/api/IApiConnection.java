package fr.lip6.move.coloane.apiws.interfaces.api;

import fr.lip6.move.coloane.apiws.interfaces.observers.IAskDialogObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IChangeSessionObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.ICloseConnectionObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.ICloseSessionObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IErrorMessagerObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IExecutServiceObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IOpenConnectionObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IOpenSessionObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.ITraceMessageObserver;
import fr.lip6.move.coloane.apiws.interfaces.observers.IWarningMessageObserver;
import fr.lip6.move.coloane.apiws.interfaces.session.IApiSession;

public interface IApiConnection {

	/**
	 * Initialiser l'adresse IP du serveur
	 * @return true, si l'initialisation a reussie, false sinon
	 */
	public boolean setIpServer();

	/**
	 * Initialiser le port du serveur
	 * @return true, si l'initialisation a reussie, false sinon
	 */
	public boolean setPortServer();

	/**
	 * Initialiser le login
	 * @return true, si l'initialisation a reussie, false sinon
	 */
	public boolean setLogin();

	/**
	 * Initialiser le password
	 * @return true, si l'initialisation a reussie, false sinon
	 */
	public boolean setPassword();
	
	/**
	 * Ajouter un observateur pour les evenements d'ouverture de connexion
	 * @param o l'observateur qui sera notifier pour les evenements d'ouverture de connexion
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setOpenConnectionObserver(IOpenConnectionObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements d'ouverture de session
	 * @param o l'observateur qui sera notifier pour les evenements d'ouverture de session
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setOpenSessionObsever(IOpenSessionObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de changement de session
	 * @param o l'observateur qui sera notifier pour les evenements de changement de session
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setChangeSessionObserver(IChangeSessionObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements d'execution de service
	 * @param o l'observateur qui sera notifier pour les evenements d'execution de service
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setExecutServiceObserver(IExecutServiceObserver o, boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de fermeture de session
	 * @param o l'observateur qui sera notifier pour les evenements de fermeture de session
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setCloseSessionObserver(ICloseSessionObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de fermeture de connexion
	 * @param o l'observateur qui sera notifier pour les evenements de fermeture de connexion
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setCloseConnectionObserver(ICloseConnectionObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de demande de boite de dialogue
	 * @param o l'observateur qui sera notifier pour les evenements de demande de boite de dialogue
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setAskDialogObserver(IAskDialogObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de reception des messages de trace
	 * @param o l'observateur qui sera notifier pour les evenements de reception des messages de trace
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setTraceMessageObserver(ITraceMessageObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de reception de message d'avertissement
	 * @param o l'observateur qui sera notifier pour les evenement de reception d'avertissement
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setWarningMessageObserver(IWarningMessageObserver o,boolean createThread);
	
	/**
	 * Ajouter un observateur pour les evenements de reception de message d'erreur
	 * @param o l'observateur qui sera notifier pour les evenement de reception de message d'erreur
	 * @param createThread definie s'il faut creer un thread pour la notification
	 * @return true, si le l'observateur a bien etait ajouter, false sinon
	 */
	public boolean setErrorMessageObserver(IErrorMessagerObserver o,boolean createThread);
	
	/**
	 * Ouvre une connexion avec le wrapper
	 * @return true, si l'ouverture a reussie, false sinon
	 */
	public boolean openConnection();
	
	/**
	 * Ferme une connexion avec le wrapper
	 * @return true, si la fermeture a reussie, false sinon
	 */
	public boolean CloseConnection();
	
	/**
	 * Creer une session
	 * @return une session
	 */
	public IApiSession getApiSession();
	
}
