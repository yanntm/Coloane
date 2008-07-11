package fr.lip6.move.coloane.apiws.session;

import java.util.HashMap;
import java.util.Hashtable;

import fr.lip6.move.coloane.apiws.interfaces.session.ISessionController;
import fr.lip6.move.coloane.apiws.interfaces.session.ISessionStateMachine;
import fr.lip6.move.coloane.interfaces.api.exceptions.ApiException;
import fr.lip6.move.coloane.interfaces.api.session.IApiSession;
import fr.lip6.move.wrapper.ws.WrapperStub.Session;

public class SessionController implements ISessionController {
	
	/**
	 * Represent la session active
	 */
	private IApiSession activeSession;
	

	/**
	 * Represent l'ensemle des sessions
	 */
	private Hashtable<String, IApiSession> listSessions;
	
	/**
	 * Represent la liste des observables
	 */
	private HashMap<Integer, Object> listObservables;
	
	/**
	 * Represent la correspondance entre le nom d'une session et son identifiant
	 */
	private Hashtable<String, String> correspondenceNameToIdSession;
	
	public SessionController(HashMap<Integer, Object> listObservables){
		this.activeSession = null;
		this.listSessions = new Hashtable<String, IApiSession>();
		this.listObservables = listObservables;
		
		this.correspondenceNameToIdSession = new Hashtable<String, String>();
	}

	public IApiSession getActiveSession() {
		return activeSession;
	}

	public boolean isActivateSession(IApiSession s) {
		return activeSession.getIdSession().equals(s.getIdSession());
	}
	
	public boolean addSession(IApiSession s) {
		listSessions.put(s.getIdSession(),s);
		return true;
	}

	public boolean removeSession(IApiSession s) {
		listSessions.remove(s.getIdSession());
		return false;
	}

	public boolean openSession(IApiSession s) throws ApiException {
		try{
			return  this.suspendSession(activeSession);
		}catch (ApiException e){
			throw new ApiException("Impossible d'ouvrire une session -> "+e.getMessage());
		}
	}

	public boolean suspendSession(IApiSession session) throws ApiException {
		ApiSession s = (ApiSession) session;
		
		if (s ==null){
			return true;
		}
		if (s.getSessionStateMachine().getState() == ISessionStateMachine.IDLE_STATE){
			return true;
		}
		throw new ApiException("Impossible de suspendre la session: idSession="+s.getIdSession()+" etat="+s.getSessionStateMachine().getState()+" activeSession="+isActivateSession(s));
	}

	public boolean resumeSession(IApiSession session) throws ApiException {
		ApiSession s = (ApiSession) session;
		if (s.getSessionStateMachine().getState() == ISessionStateMachine.SUSPEND_SESSION_STATE){
			return true;
		}
		throw new ApiException("Impossible de reprondre la session: idSession="+s.getIdSession()+" etat="+s.getSessionStateMachine().getState()+" activeSession="+isActivateSession(s));
	}

	public boolean closeSession(IApiSession session) throws ApiException {
		ApiSession s = (ApiSession) session;
		
		if (s.getSessionStateMachine().getState() == ISessionStateMachine.IDLE_STATE || s.getSessionStateMachine().getState() == ISessionStateMachine.SUSPEND_SESSION_STATE){
			return true;
		}
		throw new ApiException("Impossible de fermer la session: idSession="+s.getIdSession()+" etat="+s.getSessionStateMachine().getState()+" activeSession="+isActivateSession(s));
	}

	public boolean askForService(IApiSession session) throws ApiException {
		ApiSession s = (ApiSession) session;
		
		if (isActivateSession(s) && s.getSessionStateMachine().getState() == ISessionStateMachine.IDLE_STATE){
			return true;
		}
		throw new ApiException("Impossible de demander un service sur la session: idSession="+s.getIdSession()+" etat="+s.getSessionStateMachine().getState()+" activeSession="+isActivateSession(s));
	}

	public void notifyEndOpenSession(IApiSession opened) {
		// TODO Auto-generated method stub
		
	}

	public void notifyEndResult() {
		// TODO Auto-generated method stub
		
	}

	public void notifyEndSuspendSession(IApiSession suspended) {
		// TODO Auto-generated method stub
		
	}

	public void notifyEndResumeSession(IApiSession resumed) {
		// TODO Auto-generated method stub
		
	}
	
	public void notifyEndChangeSession(IApiSession suspended,
			Session sessionToResumed) {
		// TODO Auto-generated method stub
		
	}

	public void notifyEndCloseSession(IApiSession closed,
			Session sessionToResumed) {
		// TODO Auto-generated method stub
		
	}

	public void notifyWaitingForModel() {
		// TODO Auto-generated method stub
		
	}

	public void notifyWaitingForResult() {
		// TODO Auto-generated method stub
		
	}

}
