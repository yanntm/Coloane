package fr.lip6.move.coloane.communications;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;

import fr.lip6.move.coloane.api.Api;
import fr.lip6.move.coloane.communications.objects.Results;

import fr.lip6.move.coloane.interfaces.IApi;
import fr.lip6.move.coloane.interfaces.IComApi;
import fr.lip6.move.coloane.interfaces.IComUi;
import fr.lip6.move.coloane.interfaces.IComMotor;
import fr.lip6.move.coloane.interfaces.IDialogResult;
import fr.lip6.move.coloane.interfaces.IMotorCom;
import fr.lip6.move.coloane.interfaces.IUiCom;
import fr.lip6.move.coloane.interfaces.model.IModel;
import fr.lip6.move.coloane.interfaces.objects.IRootMenuCom;
import fr.lip6.move.coloane.interfaces.objects.IMenuCom;
import fr.lip6.move.coloane.interfaces.objects.IResultsCom;
import fr.lip6.move.coloane.interfaces.objects.IDialogCom;
import fr.lip6.move.coloane.interfaces.objects.IUpdateMenuCom;


import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.menus.RootMenu;
import fr.lip6.move.coloane.ui.dialogs.Dialog;
import fr.lip6.move.coloane.ui.dialogs.DialogResult;
import fr.lip6.move.coloane.ui.model.IModelImpl;

public class Com implements IComUi, IComApi, IComMotor {
	
	/** Une poignee sur l'API de communication avec la plateforme */
	private IApi api = null;
	
	/** Une poignee sur le moteur */
	private IMotorCom motor = null;
	
	/** Une poignee sur l'interface utilisateur */
	private IUiCom ui = null;
	
	/** Le dialogue en cours de construction */
	private Dialog dialog = null;
	
	/** Le menu en cours de construction */
	private RootMenu root = null;
	
	/** L'ensemble de modifications qui doivent etre faites sur les menus */
	private Vector<IUpdateMenuCom> updates = null;
	
	/** TODO : A documenter */
	private Composite parent;
	
	/**
	 * Le constructeur
	 * Le module de communications doit creer un lien avec l'API de communications
	 */
	public Com() {
		this.api = new Api(this);
		this.parent = (Composite) Coloane.getParent();
	}
	
	/**
	 * Permet de rattacher le moteur au module de communications
	 * @param motor
	 */
	public void setMotor(IMotorCom motor) {
		this.motor = motor;
	}
	
	/**
	 * Creer une attache avec l'interface utilisateur
	 * @param IUiCom L'interface de l'interface utilisateur pour le module de communications
	 */
	public void setUi (IUiCom ui) {
		this.ui = ui;
	}
	
	
	/**
	 * Authentification de l'utilisateur
	 * @param login login
	 * @param pass mot de passe
	 * @param ip IP ou est la plateforme FrameKit
	 * @param port Port ou joindre la plateforme FrameKit
	 * @return Booleen indiquant si l'authentification c'est bien passee
	 * @throws Exception exception
	 */
	public boolean authentication(String login, String pass, String ip, int port)  throws Exception {
		try {
			System.out.println("Demande d'authentification : ");
			System.out.println("  Login-> "+login);
			System.out.println("  Pass-> "+pass);
			System.out.println("  IP-> "+ip);
			System.out.println("  Port-> " +port);
			
			// Connexion � la plateforme
			boolean retour = api.openConnexion(login, pass, ip, port, Coloane.getParam("API_NAME"), Coloane.getParam("API_VERSION"));
			if (retour) {
				System.out.println("Retour authentification OK");
			} else {
				System.err.println("Retour authentification KO");
			}
			return retour;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * Connecte un modele a la plate-forme
	 * @param modele Le modele
	 * @return booleen selon que la connexion s'est bien passee ou pas
	 * @throws Exception
	 */
	public boolean openSession(IModelImpl model) throws Exception {
		if (model == null) {
            throw new NullPointerException();
        }
		try {
			System.out.println("Demande de connexion du modele");

			// Recuperation du nom de la session courante
			String sessionName = motor.getSessionManager().getCurrentSession().getName();
			System.out.println("Nom de la session : "+sessionName);
			
			// Recuperation du nom du formalime de la session courante
			String formalismName = model.getFormalism().getName();
			System.out.println("Nom du formalisme : "+formalismName);
			
			// Demande de l'ouverture de session a l'API
			Boolean retour = api.openSession(sessionName, motor.getSessionManager().getCurrentSession().getModel().getDate(), formalismName);
			if (retour) {
				System.out.println("Connexion r�ussie !");
				return true;
			} else {
				System.err.println("Echec de la connexion !");
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * Deconnecte un modele (Demande en provenance de Coloane)
	 * @param modele Le modele a deconnecter
	 * @return boolean Si la deconnexion c'est bien passee
     * @throws Exception exception
	 */
	public boolean closeSession() throws Exception {
		try {
			System.out.println("Demande de deconnexion du modele");
			
			if (motor.getSessionManager().getCurrentSession() == null) {
				return false;
			}

			// On sauvegarde les noms des menus a supprimer
			RootMenu menuServiceName = motor.getSessionManager().getCurrentSession().getServicesMenu();
			RootMenu menuAdminName = motor.getSessionManager().getCurrentSession().getAdminMenu();
			
			// Si la deconnexion du cote API se passe bien
			if (api.closeCurrentSession()) {			
				
				// Suppression du menu de services
				if (menuServiceName != null)
					this.ui.removeMenu(menuServiceName.getName());
				
				// Suppression du menu d'administration
				if (menuAdminName != null)
					this.ui.removeMenu(menuAdminName.getName());
				
                System.out.println("Deconnexion reussie !");
				return true;
			} else {
                System.err.println("Echec de la deconnexion !");
				return false;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Deconnexion brutale de tous les modeles (demande de FrameKit)
	 * Cette deconnexion est provoquee par un KO ou un FC
	 */
	public void closeAllSessions() {
		System.out.println("Demande de deconnexion de tout les modeles");
		motor.getSessionManager().destroyAllSessions();		
	}
	
	
	/**
	 * Demande de service a la plateforme FrameKit
	 * @param rootMenuName Nom de la racine du menu
	 * @param parentName Nom du pere du service demande
	 * @param serviceName Nom du service demande (nom de la feuille cliquee)
	 */
	public void askForService(String rootMenuName, String parentName, String serviceName) {
		// Grisage du menu de services
		this.ui.changeMenuStatus(rootMenuName,false);
		
		// Requete a l'API
		this.api.askForService(rootMenuName, parentName, serviceName);
	}
	
	
	/**
	 * Affichage d'un message dans l'interface utilisateur (Vue History)
	 * @param message Message a afficher dans la console 
	 */
	public void printHistoryMessage(String message) {
		this.ui.printHistoryMessage(message);
	}
	
	
	/**
	 * Affichage d'un message dans l'interface utilisateur (Vue State)
	 * @param message Message a afficher dans la console 
	 */
	public void printStateMessage(String message) {
		this.ui.printStateMessage(message);
	}
	
	
	/** 
	 * Affichage des menus construit a partir des commandes CAMI 
	 * @param menu La racine du menu a afficher
	 */
	public void drawMenu(IRootMenuCom rootMenuCom) {		
		try {
			// Transformation des menus
			root = new RootMenu(rootMenuCom.getRootMenuName());
			
			for (int j=0; j < rootMenuCom.getListMenu().size(); j++) {
				IMenuCom menuCom = rootMenuCom.getListMenu().get(j);
				root.addMenu(menuCom.getServiceName(), menuCom.getFatherName(), menuCom.isEnabled());
			}
			
			// Demande d'affichage a l'UI
			parent.getDisplay().asyncExec(new Runnable(){
				public void run(){
					ui.drawMenu(root);
				}
			});
		} catch (Exception e) {
			System.err.println("Impossible de construire le menu");
			e.printStackTrace();
		}
		
	}
	
	
	/** 
	 * Affichage des menus construit a partir des commandes CAMI 
	 * @param updates La racine du menu a afficher
	 */
	public void updateMenu(Vector<IUpdateMenuCom> updatesMenu) {
		this.updates = updatesMenu;
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				ui.updateMenu(updates);
			}
		});
	}
	
	
	/**
	 * Affichag d'une boite de dialogue
	 * @param dialog La boite de dialogue entierement definie
	 */
	public void drawDialog(IDialogCom dialogCom) {
		
		// Transformation du dialogue
		dialog = new Dialog(dialogCom);
		
		// Affichage de la boite de dialogue
		parent.getDisplay().asyncExec(new Runnable(){
			public void run(){
				ui.drawDialog(dialog);
			}
		});
	}
	
	
	/**
	 * Recupere les informations de la boite de dialogue
	 * @results Les resultats sous forme d'objets
	 */
	public void getDialogAnswers(DialogResult results) {
		if (!this.api.getDialogAnswers((IDialogResult)results)) {
			System.err.println("La transmission des reponses de la boite de dialogue a echouee");
		}
	}
	
	/**
	 * Affichage des resultats d'un service
	 * @param serviceName Le nom du service auquel sont rattaches les resultats
	 * @param result L'objet contenant tous les resultats
	 */
	public void setResults(String serviceName, IResultsCom resultsCom) {

		if ((serviceName != "") && (resultsCom != null)) {
			// Transformation des resultats
			Results results = new Results(resultsCom);
			this.ui.setResults(serviceName,results);
		} else {
			this.ui.setResults(serviceName,null);
			this.ui.printResults();
		}
	}
	
	/**
	 * Affichage des resultats transmis par l'API
	 * Cette methode doit etre appelee apres la methode setResults
	 */
	public void printResults() {
		this.ui.printResults();
	}
	
	/**
	 * Afichage d'un message de FrameKit
	 * @param type Le type de message
	 * @param text Le texte du message
	 * @param specialType Un indicateur
	 */
	public void setUiMessage(int type, String text, int specialType) {
		Coloane.showWarningMsg(text);
	}
	
	
	/**
	 * Informe FK que le modele a ete mis a jour
	 * @param dateUpdate La date de derniere mise a jour du modele
	 */
	public void toUpdate (int dateUpdate) {
		this.api.changeModeleDate(dateUpdate);
	}
	
	
	/**
	 * Recupere le modele
	 * @return IModel Le modele en cours
	 * @see IModel
	 */
	public IModel getModel() {
		return this.motor.getSessionManager().getCurrentSession().getModel().getGenericModel();
	}
	
	
	/**
	 * Demande la creation d'un nouveau modele a partir des inputs de FK
	 * @param Le modele construit par l'api de communication
	 */
	public void setNewModel(IModel model) {		
		this.motor.setNewModel(model);
	}
	
	
	/**
	 * Permet de noter le modele comme sale ou propre apres communication avec la plate-forme
	 * @param boolean true indique que le modele a ete sali et doit donc etre mis a jour
	 */
	public void setModelDirty(boolean state) {
		this.motor.getSessionManager().getCurrentSession().getModel().setDirty(state);
	}
	
	
	/**
	 * Retourne l'etat de fraicheur actuel du modele
	 * @return boolean Indicateur de fraicheur
	 */
	public boolean getDirtyState() {
		boolean rep = this.motor.getSessionManager().getCurrentSession().getModel().isDirty();
		return rep;
	}
	
	
	/** Retourne la date de derniere modification du modele
	 * @return int Date
	 */
	public int getDateModel() {
		return this.motor.getSessionManager().getCurrentSession().getModel().getDate();
	}
}
