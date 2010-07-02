package fr.lip6.move.coloane.main;

import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.lip6.move.coloane.communications.Com;
import fr.lip6.move.coloane.motor.Motor;
import fr.lip6.move.coloane.ui.UserInterface;
import fr.lip6.move.coloane.ui.model.IModelImpl;

public class Coloane extends AbstractUIPlugin {

	public static ResourceBundle traduction = null;
	private static Coloane plugin;
	private Com com = null;	
	private Motor motor = null;
	private UserInterface ui = null;

	public Coloane () throws Exception {
		plugin = this;
		try {
			traduction = ResourceBundle.getBundle("resources/LNG");
		} catch (Exception e) {
			System.err.println("Fichier de langue pas trouve !");
			System.err.println("Erreur : "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Methode de lancement du plugin
	 * C'est la premiere methode a etre appelee lors du chargement d'une classe du plugin
	 * @param context Parametre systeme fourni par Eclipse
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		try {

			System.out.println(traduction.getString("main.Coloane.0"));
			//System.out.println("-- Initialisation du plugin Coloane --");
			
			// Pour afficher la version et le numero de build 
			String bundleLocation = getBundle().getLocation();
			// Pour supprimer le update "@"
			bundleLocation = bundleLocation.substring(bundleLocation.indexOf("@")+1);
			Manifest mf;
			System.out.println(traduction.getString("main.Coloane.1"));
			if (bundleLocation.endsWith(".jar")) {	
				JarFile coreJar = new JarFile(bundleLocation);
				mf = coreJar.getManifest();
				Attributes atts = mf.getMainAttributes();
				
				System.out.println(traduction.getString("main.Coloane.2") + atts.getValue("Implementation-Version"));
				System.out.println(traduction.getString("main.Coloane.3") + atts.getValue("Implementation-Build"));
			} else {
				System.out.println(traduction.getString("main.Coloane.4"));
			}
			
			

			// Initialisation de l'interface graphique
			ui = new UserInterface();
			if (ui == null) {
				System.err.println("Erreur lors du chargement de l'interface utilisateur");				
			}

			// Initialisation du moteur
			motor = new Motor();
			if (motor == null) {
				System.err.println("Erreur lors du chargement du module moteur");
			}

			// Initialisation de la partie communications
			com = new Com();
			if (com == null) {
				System.err.println("Erreur lors du chargement du module de communications");
			}

			// Creation des liens
			com.setUi(ui);
			com.setMotor(motor);

			motor.setCom(com);

			ui.setCom(com);
			ui.setMotor(motor);

		} catch (Exception e) {
			System.err.println("Erreur : "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Permet de recuperer le plugin
	 * @return le plugin
	 */
	public static Coloane getDefault() {
		return plugin;
	}

	/**
	 * Permet de recuperer le workspace
	 * @return le workspace
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Le methode de fin de vie du plugin
	 * @param context Parametre systeme fourni par Eclipse
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}


	/**
	 * Recupere le parametre dans le fichier de configuration
	 * @param key L'identifiant du parametre
	 * @return String le parametre demande
	 */
	public static String getParam(String key) {
		try {
			return Platform.getResourceBundle(getDefault().getBundle()).getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * Notifier le changement du modele de la session courrante
	 */
	public static void notifyModelChange(IModelImpl model) {
		if (model != null) {
			int dateUpdate = model.modifyDate();
			if ((dateUpdate != 0) && (getDefault().getMotor().getSessionManager().getCurrentSession() != null)) {
				System.out.println("OK pour l'update");
				plugin.com.toUpdate(dateUpdate);
			}
		}
	}

	/**
	 * Affiche un message d'erreur sous forme de boite de dialogue
	 * @param msg Le message a afficher
	 */
	public static void showErrorMsg(String msg) {
		MessageDialog.openError(getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Coloane",msg);

	}

	/**
	 * Affiche un message d'avertissment sous forme de boite de dialogue
	 * @param msg Message a afficher
	 */
	public static void showWarningMsg(String msg) {
		MessageDialog.openWarning(getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Coloane",msg);
	}

	/**
	 * Donne la main sur le module de communication
	 * @return Com le module de communication
	 */
	public Com getCom() {
		return com;
	}

	/**
	 * Donne la main sur le module de communication
	 * @return Com le module de communication
	 */
	public Motor getMotor() {
		return motor;
	}

	/**
	 * TODO: A documenter
	 */
	public static Composite getParent () {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}