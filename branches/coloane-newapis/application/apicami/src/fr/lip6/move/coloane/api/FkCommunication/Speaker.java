package fr.lip6.move.coloane.api.FkCommunication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import fr.lip6.move.coloane.api.cami.CamiGenerator;
import fr.lip6.move.coloane.api.interfaces.IDialog;
import fr.lip6.move.coloane.api.interfaces.IDialogAnswer;
import fr.lip6.move.coloane.api.interfaces.IModel;
import fr.lip6.move.coloane.api.interfaces.ISpeaker;

/**
 *
 * @author UU & Kahoo
 * Le rôle de cette classe est d'offrir une interface de haut niveau
 * aux modules de API pour envoyer des commandes à FrameKit.
 */

public class Speaker implements ISpeaker{

	/** objet de communication bas niveau */
	private FkComLowLevel fkll;
	Logger logger;

	/**
	 *
	 * @param lowLevel Un objet de type FkComLowLevel pour qui Speaker
	 * 			délèguera les écritures sur la socket.
	 */

	public Speaker(FkComLowLevel lowLevel) {
		this.fkll = lowLevel;
		this.logger = Logger.getLogger("fr.lip6.move.coloane.api");
	}


	/**
	 * @param rootName nom du service principal
	 * @param menuName Menu de service
	 * @param serviceName nom du service invoqué
	 * @throws IOException
	 */

	public void askForService(String rootName, String menuName, String serviceName) throws IOException {

		/** fabrique la commande et envoie de la commande DT */
		byte[] cmdToSend = CamiGenerator.generateCmdDT();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/** generation et envoi de la première commande PQ */
		cmdToSend = CamiGenerator.generateCmdPQ(rootName, menuName);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/** generation et envoi de la deuxième commande PQ */
		cmdToSend = CamiGenerator.generateCmdPQ(rootName, serviceName);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/** generation et envoi de la deuxième commande FT */
		cmdToSend = CamiGenerator.generateCmdFT();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));


	}


	public void askForService(String rootName, String menuName, String serviceName, String date) throws IOException {

		/** generation de la commande MS pour envoi de la date */
		byte[] cmdToSend = CamiGenerator.generateCmdMS(date);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/** envoi des commandes askForService sans la date */
		askForService(rootName, menuName, serviceName);

	}

	/**
	 * Ferme la connexion avec FrameKit
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		/** fabrique la commande FC */
		byte[] cmdToSend = CamiGenerator.generateCmdFC();
		/** envoie de la commande FC */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));
	}

	/**
	 * Clore la session
	 * @param continueProcessing
	 * @throws IOException
	 */
	public void closeSession(boolean continueProcessing) throws IOException {


		/** fabrique la commande FS */
		byte[] cmdToSend = CamiGenerator.generateCmdFS(continueProcessing);
		/** envoie de la commande FS */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));
	}


	/** initie la connexion avec FrameKit
	 * @param login
	 * @param password
	 * @throws IOException
	 */
	public void startCommunication(String login, String password) throws IOException {

		/** fabrique la commande SC */
		byte[] cmdToSend = CamiGenerator.generateCmdSC(login, password);
		/** envoie de la commande SC */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

	}

	/**
	 * @param uiName  Nom de l'interface utilisateur (client)
	 * @param uiVersion version du client
	 * @param login login déja passé dans la commande SC de la connexion
	 * 		  gardé pour des raisons de compatibilité ascendante
	 */
	public void openConnection(String uiName, String uiVersion, String login) throws IOException {

		/** fabrique la commande OC */
		byte[] cmdToSend = CamiGenerator.generateCmdOC(uiName,uiVersion, login);
		/** envoie de la commande SC */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

	}


	/**
	 *   demande a ISpeaker d'envoyer a FK l'ouverture d'une session.
	 *   @param le nom de la session.
	 *   @param date de dernière modification du modèle.
	 *   @param formalisme de la session
	 *   @param	interlocuteur (outil invoqué)
	 *   @param mode batch ou interactif
	 *   @throws IOException
	 *
 	 */
	public void openSession(String sessionName, String date,
			String sessionFormalism, String interlocutor, int mode) throws IOException {

		/* envoi de la commande OS */
		byte[] cmdToSend = CamiGenerator.generateCmdOS(sessionName, date, sessionFormalism);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));


		/* envoi de la commande DI */
		cmdToSend = CamiGenerator.generateCmdDI();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/* envoi de la commande CI */
		cmdToSend = CamiGenerator.generateCmdCI(interlocutor, mode);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/* envoi de la commande FI */
		cmdToSend = CamiGenerator.generateCmdFI();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

	}

	/**
	 * suspension de la session
	 * @throws IOException
	 *
	 */
	public void resumeSession(String sessionName) throws IOException {
		/* envoi de la commande FI */
		byte[] cmdToSend = CamiGenerator.generateCmdRS(sessionName);
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));


	}


	

	/**
	 * Envoi du modèle
	 * @param m modèle à envoyer
	 * @throws IOException
	 * @throws IOException
	 */
	public void sendModel(IModel m) throws IOException {


		/** transformer le modèle en cami */
		ArrayList<byte[]> camiModel;
		camiModel =  CamiGenerator.generateCamiModel(m);

		/** envoyer un DB : debut transmission du modele */
		byte[] cmdToSend = CamiGenerator.generateCmdDB();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

		/** envoyer le coeur du modele */
		for(int i=0; i<camiModel.size(); i++){
			this.fkll.writeCommand(camiModel.get(i));
			
			this.logger.finer("[CO-->FK] : " + new String(camiModel.get(i), 4, camiModel.get(i).length - 4));
		}

		/** envoyer un FB : fin de transmission du modele */
		cmdToSend = CamiGenerator.generateCmdFB();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));

	}


	/**
	 * @throws IOException
	 *
	 */

	public void suspendSession() throws IOException {
		/** fabrique la commande SS */
		byte[] cmdToSend = CamiGenerator.generateCmdSS();
		/** envoie de la commande SS */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));
	}


	public void invalidModel() throws IOException {
		
		/** fabrique la commande SS */
		byte[] cmdToSend = CamiGenerator.generateCmdQQ();
		/** envoie de la commande SS */
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));
	}


	public void sendDialogResponse(IDialogAnswer dialogAnswer) throws IOException {
	     /** envoi de la commande DP */
		byte[] cmdToSend = CamiGenerator.generateCmdDP();
		this.fkll.writeCommand(cmdToSend);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend, 4, cmdToSend.length - 4));
		
		/***** le coeur de la reponse a la boite de dialogue*/
        ArrayList<byte[]> camiDialog;
        camiDialog = CamiGenerator.generateCmdDialogAnswer(dialogAnswer);
    	for(int i=0; i<camiDialog.size(); i++){
			this.fkll.writeCommand(camiDialog.get(i));
			
			this.logger.finer("[CO-->FK] : " + new String(camiDialog.get(i), 4, camiDialog.get(i).length - 4));
		}
    	 /** envoi de la commande DP */
		byte[] cmdToSend2 = CamiGenerator.generateCmdFP();
		this.fkll.writeCommand(cmdToSend2);
		this.logger.finer("[CO-->FK] : " + new String(cmdToSend2, 4, cmdToSend2.length - 4));
	}
	
		}
