package fr.lip6.move.coloane.core.motor.session;

import fr.lip6.move.coloane.core.communications.Com;
import fr.lip6.move.coloane.core.menus.RootMenu;
import fr.lip6.move.coloane.core.results.ResultTreeList;
import fr.lip6.move.coloane.interfaces.api.exceptions.ApiException;
import fr.lip6.move.coloane.interfaces.api.session.IApiSession;
import fr.lip6.move.coloane.interfaces.model.IGraph;

import java.util.logging.Logger;

/**
 * Definition d'une session
 */
public class Session implements ISession {
	/** Le logger pour la classe */
	private static final Logger LOG = Logger.getLogger("fr.lip6.move.coloane.core"); //$NON-NLS-1$

	/** Nom de la session */
	private String name;

	/** Le graph associe */
	private IGraph graph;

	/** Le ResultTreeList associé */
	private ResultTreeList results;

	/** Arborescence du menu administration */
	private RootMenu adminMenu;

	/** Arborescence du menu et des services de la session */
	private RootMenu menu;

	/** Status de la session */
	private int status;

	/** Objet session de l'api */
	private IApiSession apiSession;


	/**
	 * Constructeur<br>
	 * Tous les champs sont initialisés.<br>
	 * Le nom ne doit pas etre nul.
	 * @param name Nom de la session
	 */
	public Session(String name) {
		if (name == null) {
			throw new NullPointerException("name cannot be null"); //$NON-NLS-1$
		}
		this.name = name;
		this.graph = null;
		this.status = ISession.CLOSED;
		this.results = null;
		this.adminMenu = null;
		this.menu = null;
	}

	/** {@inheritDoc} */
	public final void suspend() {
		LOG.finer("Suspension de la session " + name); //$NON-NLS-1$
		if (status == ISession.CONNECTED) {
			status = ISession.SUSPENDED;
		}
	}

	/** {@inheritDoc} */
	public final void resume() {
		LOG.finer("Reprise de la session " + name); //$NON-NLS-1$
		if (status == ISession.SUSPENDED) {
			status = ISession.CONNECTED;
		}
	}

	/** {@inheritDoc} */
	public final void destroy() {
		this.menu = null;
		this.adminMenu = null;
		this.status = ISession.CLOSED;
	}

	/** {@inheritDoc} */
	public final String getName() {
		return name;
	}

	/** {@inheritDoc} */
	public final IGraph getGraph() {
		return this.graph;
	}

	/** {@inheritDoc} */
	public final void setModel(IGraph model) {
		this.graph = model;
	}

	/** {@inheritDoc} */
	public final RootMenu getAdminMenu() {
		return adminMenu;
	}

	/** {@inheritDoc} */
	public final void setAdminMenu(RootMenu admin) {
		this.adminMenu = admin;
	}

	/** {@inheritDoc} */
	public final RootMenu getServicesMenu() {
		return menu;
	}

	/** {@inheritDoc} */
	public final void setServicesMenu(RootMenu root) {
		this.menu = root;
	}

	/** {@inheritDoc} */
	public final int getStatus() {
		return status;
	}

	/** {@inheritDoc} */
	public final void setStatus(int status) {
		this.status = status;
	}

	/** {@inheritDoc} */
	public final ResultTreeList getServiceResults() {
		if (results == null) {
			this.results = new ResultTreeList();
		}
		return results;
	}

	/** {@inheritDoc} */
	public final boolean connect() {
		try {
			apiSession = Com.getInstance().createApiSession();
			apiSession.openSession(graph.getDate(), graph.getFormalism().getFKName(), name);
		} catch (ApiException e) {
			LOG.warning("Problème lors de la connection de la session : " + e); //$NON-NLS-1$
			return false;
		}
		setStatus(ISession.CONNECTED);
		return true;
	}

	/** {@inheritDoc} */
	public final boolean disconnect() {
		try {
			if (apiSession != null) {
				apiSession.closeSession();
				apiSession = null;
			}
		} catch (ApiException e) {
			LOG.warning("Problème lors de la déconnexion de la session : " + e); //$NON-NLS-1$
			return false;
		}
		setStatus(ISession.CLOSED);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return name;
	}
}
