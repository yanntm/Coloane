package interfaces;

/**
 * cette interface est retournée en asynchrone par OpenConnection,
 * qui se trouve dans IAPIConnection.
 * @author KAHOO & UU
 *
 */

public interface IFKVersion {


	/**
	 * Retourne le nom de FrameKit.
	 * @return String
	 */
		String getFkName();
		

		/**
		 * Retourne le numéro de version majeur.
		 * @return int
		 */
		int getFKMajor();
		

		/**
		 * Retourne le numéro de version mineur.
		 * @return int
		 */
		int getFKMinor();
	

	
}
