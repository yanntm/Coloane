package interfaces;

/**
 * cette interface est retournée en asynchrone par OpenSession 
 * qui se trouve dans IAPIOpenSession.
 * @author KAHOO & UU
 *
 */

public interface IFKInfo {

	
	/**
	 * Retourne le nom du service.
	 * @return String
	 */
	String getNameService();    
	
	
	/**
	 * Retourne des informations relatives au service.
	 * @return String
	 */
	String getAboutService();        
	
	
	/**
	 * Retourne l'incrmental.
	 * @return String
	 */
	String getIncremental();         
	
	
	/**
	 * Retourne le resultat calculé .
	 *  (1) si fait précédemment
	 *  (2) non
	 * @return String
	 */
	String getResultatCalcule();    

}
