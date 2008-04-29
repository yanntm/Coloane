package fr.lip6.move.coloane.api.interfaces;

/**
 * cette interface represente celle du session controller
 * @author kahoo && UU
 *
 */

public interface ISessionController {

	/**
	 * nous retourne la session active.
	 * @return la session active.
	 */
	public IApiSession getActiveSession();

	/**
	 * nous rajoute une session dans ma liste de sessions.
	 * @param la session à rajouter.
	 * @return true si c'est fait, false sinon.
	 */
	public boolean addSession(IApiSession s);

	/**
	 * nous supprime une session dans ma liste de sessions.
	 * @param la session a supprimer.
	 * @return true si c'est fait, false sinon.
	 */
	public boolean removeSession(IApiSession s);

	/**
	 * nous dit si notre session est active ou pas.
	 * @param la session à verifier.
	 * @return true, si vraiment c'est la session active, false sinon.
	 */
	public boolean ifActivateSession(IApiSession s);

    /**
     * nous permet de suspendre la session.
     * @param la session à suspendre.
     * @return true, si suspendue, false sinon.
     */
	public boolean suspendSession(IApiSession s);

	/**
	 * nous permet de reprendre la session.
	 * @param la session a reprendre.
	 * @return true, si la session a été reprise , false sinon.
	 */
	public boolean resumeSession(IApiSession s);

	/**
	 * session demande a session controller s'il a le droit d'ouvrir une session ou pas.
	 * @param s la session qu'on veut ouvrir.
	 * @return vraie si c'est ok, false sinon.
	 * @throws InterruptedException
	 */
	public boolean openSession(IApiSession s) throws InterruptedException;
}
