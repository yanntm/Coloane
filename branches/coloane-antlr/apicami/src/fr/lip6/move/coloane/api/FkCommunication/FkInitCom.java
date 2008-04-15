package fr.lip6.move.coloane.api.FkCommunication;

import java.io.IOException;

import fr.lip6.move.coloane.api.interfaces.IListener;
import fr.lip6.move.coloane.api.interfaces.ISpeaker;

/**
 * 
 * @author UU && KAHOO
 *	
 * Cette classe construit
 *  - la socket de communcation avec FK et la connecte 
 * 	- un objet Listner
 *  - un objet Speaker
 *  
 *  et renvoie les interfaces  ISpeaker et IListener
 */

public class FkInitCom {
	
	/**
	 * Fabrique un objet Listner
	 * @return retourne une interface IListener
	 */
	private static IListener getFkComListener(FkComLowLevel lowLevel){
		return new Listener(lowLevel);
	}
	
	/**
	 * Fabrique un objet Speaker
	 * @return retourne l'interface ISpeaker
	 */
	private static ISpeaker getFkComSpeaker(FkComLowLevel lowLevel){
		return new Speaker(lowLevel);
	}
	
	
	/**
	 * 
	 * Cette méthode 
	 * @param ip adresse Ip de la machine hébérgeant FrameKit
	 * @param port d'écoute de FrameKit
	 * @return paire d'objet contenant le Speaker et le Listener
	 * @throws IOException
	 */
	
	public static Pair<ISpeaker, IListener> initCom(String ip, int port) throws IOException{
		
		/** Créer une nouvelle connexion */
		// pas besoin de traiter la IOException
		FkComLowLevel lowLevel = new FkComLowLevel(ip, port);
		
		Pair<ISpeaker, IListener> pair = new Pair<ISpeaker, IListener> ();
		
		/** Créer un speaker */
		pair.speaker  = getFkComSpeaker(lowLevel);
		
		/** Créer un speaker */
		pair.listener = getFkComListener(lowLevel);
		
		
		return pair;
					
	}
	
	
}
