package fr.lip6.move.coloane.motor.formalism;

import fr.lip6.move.coloane.exceptions.ColoaneException;
import fr.lip6.move.coloane.interfaces.exceptions.ModelException;
import fr.lip6.move.coloane.interfaces.exceptions.SyntaxErrorException;
import fr.lip6.move.coloane.interfaces.model.IModel;
import fr.lip6.move.coloane.main.Coloane;
import fr.lip6.move.coloane.model.Model;
import fr.lip6.move.coloane.motor.formalism.defs.PetriNets;
import fr.lip6.move.coloane.motor.formalism.defs.PrefixNets;
import fr.lip6.move.coloane.motor.formalism.defs.ReachabilityGraph;
import fr.lip6.move.coloane.ui.model.IModelImpl;
import fr.lip6.move.coloane.ui.model.ModelImplAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Classe du gestionnaire de formalismes.
 * C'est ici que sont definis les formalismes.
 */
public class FormalismManager {

	/** Liste des formalismes disponibles. */
	private static ArrayList<Formalism> listOfFormalisms;

	/**
	 * Constructeur de la classe FormalismsManager
	 * Constitution de la liste des formalismes
	 */
	public FormalismManager() {
		listOfFormalisms = new ArrayList<Formalism>();
		listOfFormalisms.add(new PetriNets());
		listOfFormalisms.add(new PrefixNets());
		listOfFormalisms.add(new ReachabilityGraph());
	}

	/**
	 * Charger un formalisme a partir de son nom
	 * @param formalismName Nom du formalisme
	 * @return Formalism Le formalisme souhaite ou null si le formalisme n'existe pas
	 * @see Formalism
	 */
	public final Formalism loadFormalism(String formalismName) {
		for (Formalism form : listOfFormalisms) {
			if (formalismName.equalsIgnoreCase(form.getName())) {
				return form;
			}
		}
		return null;
	}

	/**
	 * Cette methode retourne un formalisme a partir d'une extension utilisee lors de la sauvegarde d'un modele
	 * @param extension Extension recherchee
	 * @return Formalism
	 */
	public final Formalism getFormalismByExtension(String extension) {
		for (Formalism form : listOfFormalisms) {
			if (extension.equals(form.getExtension())) {
				return form;
			}
		}
		return null;
	}

	/**
	 * Retourne la liste des formalismes disponibles
	 * @return listOfFormalism
	 */
	public final ArrayList getListOfFormalisms() {
		return listOfFormalisms;
	}

	/**
	 * Importe un modele en determinant son formalisme en fonction de l'extension du fichier
	 * @param fileName nom de fchier a importer
	 * @return le model adapter correspondant
	 * @throws Exception leve d'exception si le fichier n'est pas valide
	 */
	public final IModelImpl importModel(String fileName) throws ColoaneException {

		// Determination du formalism avec l'extension
		StringTokenizer file = new StringTokenizer(fileName, "."); //$NON-NLS-1$
		String fext = file.nextToken(); // Debut du nom
		fext = file.nextToken(); // Extension

		Formalism formalism = getFormalismByExtension(fext);

		// On verifie qu'un formalisme existe bien pour cette extension
		if (formalism == null) {
			throw new ColoaneException(Coloane.getTranslate().getString("motor.formalism.FormalismManager.192")); //$NON-NLS-1$
		}

		IModel genericModel = null;
		try {
			genericModel = new Model(new File(fileName));
		} catch (SyntaxErrorException e) {
			throw new ColoaneException(Coloane.getTranslate().getString("motor.formalism.FormalismManager.193")); //$NON-NLS-1$
		} catch (ModelException e) {
			throw new ColoaneException(Coloane.getTranslate().getString("motor.formalism.FormalismManager.193")); //$NON-NLS-1$
		}

		// On indique au modele l'identifiant maximal
		// Cette etape est importante, si on souhaite ajouter de nouveaux noeuds et arcs au modele
		genericModel.setMaxId(genericModel.getMaxId());

		return new ModelImplAdapter(genericModel, formalism);
	}

	/**
	 * Export un modeladapter en model en fonction de son formalisme
	 * @param modelAdapter le model a enregistrer
	 * @param fileName nom du fichier desire sans les extentions
	 * @throws Exception
	 */

	public final void exportModel(IModelImpl modelAdapter, String fileName) throws ColoaneException {

		if (fileName.equalsIgnoreCase("") || fileName == null) { //$NON-NLS-1$
			throw new ColoaneException(Coloane.getTranslate().getString("motor.formalism.FormalismManager.195")); //$NON-NLS-1$
		}

		// Extention du formalism
		String ext = modelAdapter.getFormalism().getExtension();
		if (ext == null) {
			throw new ColoaneException(Coloane.getTranslate().getString("motor.formalism.FormalismManager.196")); //$NON-NLS-1$
		}
		// Creation du fichier
		FileOutputStream wr;
		try {
			wr = new FileOutputStream(new File(fileName + "." + ext));
		} catch (FileNotFoundException e1) {
			throw new ColoaneException("");
		}  //$NON-NLS-1$
		BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(wr));

		// Traduction du modele entier
		try {
			String[] cami = modelAdapter.getGenericModel().translate();
			for (int i = 0; i < cami.length; i++) {
				buff.write(cami[i]);
				buff.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			buff.flush();
			wr.flush();
			buff.close();
			wr.close();
		} catch (IOException e) {
			throw new ColoaneException("");
		}
	}
}