package fr.lip6.move.coloane.core.communications;

import fr.lip6.move.coloane.api.main.Api;
import fr.lip6.move.coloane.core.interfaces.IComMotor;
import fr.lip6.move.coloane.core.interfaces.IComUi;
import fr.lip6.move.coloane.core.interfaces.IMotorCom;
import fr.lip6.move.coloane.core.interfaces.IUiCom;
import fr.lip6.move.coloane.core.main.Coloane;
import fr.lip6.move.coloane.core.menus.RootMenu;
import fr.lip6.move.coloane.core.ui.dialogs.AuthenticationInformation;
import fr.lip6.move.coloane.core.ui.dialogs.DrawDialog;
import fr.lip6.move.coloane.core.ui.model.IModelImpl;
import fr.lip6.move.coloane.interfaces.IApi;
import fr.lip6.move.coloane.interfaces.IComApi;
import fr.lip6.move.coloane.interfaces.IDialogResult;
import fr.lip6.move.coloane.interfaces.model.IModel;
import fr.lip6.move.coloane.interfaces.objects.IDialogCom;
import fr.lip6.move.coloane.interfaces.objects.IMenuCom;
import fr.lip6.move.coloane.interfaces.objects.IResultsCom;
import fr.lip6.move.coloane.interfaces.objects.IRootMenuCom;
import fr.lip6.move.coloane.interfaces.objects.IUpdateMenuCom;

import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;

public final class Com implements IComUi, IComApi, IComMotor {

	/** Une poignee sur l'API de communication avec la plateforme */
	private IApi api = null;

	/** Une poignee sur le moteur */
	private IMotorCom motor = null;

	/** Une poignee sur l'interface utilisateur */
	private IUiCom ui = null;

	/** Le menu en cours de construction */
	private RootMenu root = null;

	/** L'ensemble de modifications qui doivent etre faites sur les menus */
	private Vector<IUpdateMenuCom> updates = null;

	/** Conteneur graphique de haut niveau */
	private Composite parent;

	/** L'instance du singleton : Com */
	private static Com instance;

	/**
	 * Le constructeur en private
	 * Le module de communications doit creer un lien avec l'API de communications
	 * Pour eviter les doublonson utilise le pattern <b>Singleton</b>
	 * @see #getInstance()
	 */
	private Com() {
		this.api = Api.getInstance(this);
		this.parent = (Composite) Coloane.getParent();
	}

	/**
	 * Retourne le module de communications
	 * @return Com Le module de communications
	 */
	public static synchronized Com getInstance() {
		if (instance == null) { instance = new Com(); }
		return instance;
	}

	/**
	 * Permet de rattacher le moteur au module de communications
	 * @param theMotor Le module moteur
	 */
	public void setMotor(IMotorCom theMotor) {
		this.motor = theMotor;
	}

	/**
	 * Permet d'attacher l'interface utilisateur au module de communications
	 * @param theUi L'interface utilisateur
	 */
	public void setUi(IUiCom theUi) {
		this.ui = theUi;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.interfaces.IComMotor#authentication(fr.lip6.move.coloane.core.ui.dialogs.AuthenticationInformation, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean authentication(AuthenticationInformation authInformation, IProgressMonitor monitor) {
		Coloane.getLogger().fine("Demande d'authentification"); //$NON-NLS-1$

		monitor.beginTask("Connecting...", 2);

		// Connexion a la plateforme
		boolean retour = this.api.openConnection(authInformation.getLogin(), authInformation.getPass(), authInformation.getIp(), authInformation.getPort(), Coloane.getParam("API_NAME"), Coloane.getParam("API_VERSION")); //$NON-NLS-1$ //$NON-NLS-2$

		monitor.worked(1);
		monitor.setTaskName("Logging...");

		// Log du resultat
		if (retour) { Coloane.getLogger().fine("Authentification OK"); } else { Coloane.getLogger().warning("Authentification KO"); } //$NON-NLS-1$ //$NON-NLS-2$

		monitor.worked(1);
		monitor.done(); // Pas d'asynchronisme... L'authentification est terminee
		return retour;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.interfaces.IComMotor#openSession(fr.lip6.move.coloane.core.ui.model.IModelImpl, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean openSession(IModelImpl model, IProgressMonitor monitor) {
		Coloane.getLogger().fine("Connexion d'un modele"); //$NON-NLS-1$

		monitor.beginTask("Connecting current model...", 3);

		// Si le modele est nul on ne peut rien faire
		if (model == null) {
			Coloane.getLogger().warning("Aucun modele a connecter"); //$NON-NLS-1$
			return false;
		}

		monitor.worked(1);
		monitor.setTaskName("Fetching information about the model");

		// Recuperation du nom de la session courante
		String sessionName = motor.getSessionManager().getCurrentSessionName();
		// Recuperation du nom du formalime de la session courante
		String formalismName = model.getFormalism().getName();

		// Demande de l'ouverture de session a l'API
		boolean retour = api.openSession(sessionName, model.getDate(), formalismName);

		monitor.worked(1);
		monitor.setTaskName("Receiving available services");

		// Log du resultat
		if (retour) { Coloane.getLogger().fine("Connexion reussie !"); } else { Coloane.getLogger().warning("Echec de la connexion"); } //$NON-NLS-1$ //$NON-NLS-2$

		// Au commencement, un modele est toujours propre
		model.setDirty(false);

		return retour;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.interfaces.IComMotor#closeSession(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean closeSession(IProgressMonitor monitor) {
		// On verifie qu'il y a bien une sesssion courante
		if (motor.getSessionManager().getCurrentSession() == null) { return false; }

		monitor.beginTask("Disconnecting current model...", 2);

		// Deconnexion du cote de l'API
		boolean retour = api.closeCurrentSession();

		monitor.worked(1);
		return retour; // Pas de done puisque l'action est asynchrone
	}

	/**
	 * Deconnexion brutale de tous les modeles (a la demande de FrameKit)<br>
	 * Cette deconnexion est provoquee par un KO ou un FC
	 */
	public void closeAllSessions() {
		Coloane.getLogger().fine("Framekit demande la deconnexion de tous les modeles");
		motor.getSessionManager().destroyAllSessions();
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.core.interfaces.IComMotor#askForService(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void askForService(String rootMenuName, String referenceName, String serviceName, IProgressMonitor monitor) {
		Coloane.getLogger().fine("Demande de service : " + serviceName); //$NON-NLS-1$

		// Requete a l'API
		monitor.beginTask("Asking the platform...", IProgressMonitor.UNKNOWN);
		api.askForService(rootMenuName, referenceName, serviceName);
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.IComApi#setTaskDescription(java.lang.String, java.lang.String)
	 */
	public void setTaskDescription(String service, String description) {
		motor.setTaskDescription(service, description);
	}

	/**
	 * Affichage d'un message dans l'interface utilisateur (Vue History)
	 * @param message Message a afficher dans la console
	 */
	public void printHistoryMessage(String message) {
		Coloane.getLogger().finest("Affichage dans l'historique : " + message); //$NON-NLS-1$
		this.ui.printHistoryMessage(message);
	}

	/**
	 * Affichage des menus construit a partir des commandes CAMI
	 * @param menu La racine du menu a afficher
	 */
	public void drawMenu(IRootMenuCom rootMenuCom) {
		Coloane.getLogger().fine("Affichage des menus"); //$NON-NLS-1$

		try {
			// Transformation des menus
			root = new RootMenu(rootMenuCom.getRootMenuName());
			for (IMenuCom menuCom : rootMenuCom.getListMenu()) {
				root.addMenu(menuCom.getServiceName(), menuCom.getFatherName(), menuCom.isEnabled());
			}

			// Demande d'affichage a l'UI
			parent.getDisplay().asyncExec(new Runnable() {
				public void run() { ui.drawMenu(root); }
			});
		} catch (Exception e) {
			Coloane.getLogger().warning("Unable to build the model"); //$NON-NLS-1$
		}

	}

	/**
	 * Affichage des menus construit a partir des commandes CAMI
	 * @param updates La racine du menu a afficher
	 */
	public void updateMenu(Vector<IUpdateMenuCom> updatesMenu) {
		Coloane.getLogger().fine("Mise a jour des menus"); //$NON-NLS-1$
		this.updates = updatesMenu;
		parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ui.updateMenu(motor.getConcernedSession(), updates);
			}
		});
	}

	/**
	 * Affichage d'une boite de dialogue
	 * @param dialogCom La boite de dialogue entierement definie
	 */
	public void drawDialog(IDialogCom dialog) {
		/* Affichage de la boite de dialogue dans une thread dediee */
		parent.getDisplay().asyncExec(new DrawDialog(dialog));
	}

	/**
	 * Recupere les informations de la boite de dialogue
	 * @results Les resultats sous forme d'objets
	 */
	public void sendDialogAnswers(IDialogResult results) {
		this.api.getDialogAnswers((IDialogResult) results);
	}

	/**
	 * Affichage des resultats d'un service
	 * @param serviceName Le nom du service auquel sont rattaches les resultats
	 * @param result L'objet contenant tous les resultats
	 */
	public void setResults(String serviceName, IResultsCom resultsCom) {
		Coloane.getLogger().fine("Preparation des resultats pour le service : " + serviceName); //$NON-NLS-1$
		if ((serviceName != "") && (resultsCom != null)) { //$NON-NLS-1$
			this.ui.setResults(serviceName, resultsCom);
		} else {
			this.ui.setResults(serviceName, null);
			this.ui.printResults();
		}
	}

	/**
	 * Affichage des resultats transmis par l'API
	 * Cette methode doit etre appelee apres la methode setResults
	 */
	public void printResults() {
		Coloane.getLogger().fine("Affichage des resultats"); //$NON-NLS-1$
		this.ui.printResults();
	}

	/**
	 * Recupere le modele pour le transmettre a l'API
	 * @return IModel Le modele en cours
	 * @see IModel
	 */
	public IModel sendModel() {
		Coloane.getLogger().fine("Transmission d'un modele a la plateforme"); //$NON-NLS-1$
		return this.motor.getSessionManager().getCurrentSessionModel().getGenericModel();
	}


	/**
	 * Demande la creation d'un nouveau modele a partir des inputs de FK<br>
	 * En general, l'API est responsable de cet appel !
	 * @param Le modele construit par l'api de communication
	 */
	public void setNewModel(IModel model) {
		Coloane.getLogger().fine("Reception d'un nouveau modele"); //$NON-NLS-1$
		this.motor.setNewModel(model);
	}


	/**
	 * Informe FK que le modele a ete mis a jour
	 * @param dateUpdate La date de derniere mise a jour du modele
	 */
	public void toUpdate(int dateUpdate) {
		Coloane.getLogger().fine("Le modele doit etre mis a jour du cote de la plateforme"); //$NON-NLS-1$
		this.api.changeModeleDate(dateUpdate);
	}

	/**
	 * Retourne l'etat de fraicheur actuel du modele
	 * @return boolean Indicateur de fraicheur
	 */
	public boolean getDirtyState() {
		boolean state = this.motor.getSessionManager().getCurrentSessionModel().isDirty();
		if (state) { Coloane.getLogger().fine("Le modele est actuellement SALE"); } else { Coloane.getLogger().fine("Le modele est actuellement PROPRE"); } //$NON-NLS-1$ //$NON-NLS-2$
		return this.motor.getSessionManager().getCurrentSessionModel().isDirty();
	}

	/**
	 * Retourne la date de derniere modification du modele
	 * @return int Date
	 */
	public int getDateModel() {
		return this.motor.getSessionManager().getCurrentSessionModel().getDate();
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.IComApi#setEndOpenSession()
	 */
	public void setEndOpenSession() {
		Coloane.getLogger().finer("Fin de la demande d'ouverture de session (connexion)");
		motor.endService();
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.IComApi#setEndCloseSession()
	 */
	public void setEndCloseSession() {
		Coloane.getLogger().finer("Fin de la demande de fermeture de session (deconnexion)");
		motor.endService();
	}

	/*
	 * (non-Javadoc)
	 * @see fr.lip6.move.coloane.interfaces.IComApi#setEndService()
	 */
	public void setEndService() {
		Coloane.getLogger().finer("Fin du service recu et transmis par l'API");
		motor.endService();
	}
}
