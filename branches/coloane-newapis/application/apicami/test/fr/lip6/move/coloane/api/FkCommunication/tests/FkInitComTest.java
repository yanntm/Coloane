package fr.lip6.move.coloane.api.FkCommunication.tests;

import java.io.IOException;

import fr.lip6.move.coloane.api.communications.FkInitCom;
import fr.lip6.move.coloane.api.communications.ComObjects;
import fr.lip6.move.coloane.api.interfaces.IListener;
import fr.lip6.move.coloane.api.interfaces.ISpeaker;

import junit.framework.TestCase;

/**
 *
 * @author kahoo & uu
 * Classe de test de FkInitCom
 */

public class FkInitComTest extends TestCase {

	/** Adresse Ip de la machine à contacter */
	private final static String IP_ADRESS = "127.0.0.1" ;

	/** Port d'écoute de FrameKit */
	private final static int PORT = 7001;


	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * test l'initialisation de la connexion de la socket
	 * @throws IOException
	 */
	public void testInitCom() throws IOException {
		ComObjects<ISpeaker, IListener> pair = FkInitCom.initCom(this.IP_ADRESS, this.PORT, null);
		/* Tester que les listener et speaker sont bien créés */
		this.assertNotNull(pair.speaker);
		this.assertNotNull(pair.listener);
	}

}
