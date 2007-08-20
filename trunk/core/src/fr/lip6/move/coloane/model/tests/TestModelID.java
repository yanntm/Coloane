package fr.lip6.move.coloane.model.tests;

import fr.lip6.move.coloane.interfaces.exceptions.ModelException;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.model.Arc;
import fr.lip6.move.coloane.model.Model;
import fr.lip6.move.coloane.model.Node;

import java.util.Vector;

import junit.framework.TestCase;

/*Scenario de test:
 * Ajout d'un arc de facon correcte ou non aleatoirement
 * Tous les 8 tours, un ou plusieurs arcs sont ajoutes a la liste des arcs a retirer
 * les arcs presents dans la liste sont alors supprimes
 * Tous les 15 tours 1 noeud choisi aleatoirement est supprime.
 * Le nombre de tour effectue est specifie par la variable "max_tour"
 * Le nombre d'arc a supprimer par est specifie par la variable "nb_remove"
 * */

public class TestModelID extends TestCase {
	static final int MAXLOOP = 500;
	static final int NBREMOVE = 2;
	static final int RANDOM = 4;

	private int tour = 1;

	private Model model = new Model();

	private int idNode1, idNode2, idArc;

	public final void switchArcOK(IArc arc) {

		int actionOK = (int) (Math.random() * RANDOM);
		int i;

		INode node1 = new Node("Node");
		INode node2 = new Node("Node");

		try {
			switch(actionOK) {
				// Ajout de 2 noeuds relies par un arc
				case 0:
					arc.setStartingNode(node1);
					arc.setEndingNode(node2);

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addNode(node1);
					idNode1 = node1.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idNode1)));
					assertEquals(idNode1, model.getMaxId());

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addNode(node2);
					idNode2 = node2.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idNode2)));
					assertEquals(idNode2, model.getMaxId());

					assertEquals(node1, model.getANode(idNode1));
					assertEquals(node2, model.getANode(idNode2));

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addArc(arc);
					idArc = arc.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idArc)));
					assertEquals(idArc, model.getMaxId());

					System.out.println("Ajout de l'arc " + arc.getId() + " et des noeuds " + idNode1 + " et " + idNode2 + "\n");
					break;

					// Ajout d'un noeud source relie a un noeud deja existant
				case 1:
					if (model.getListOfNodeSize() == 0) {
						break;
					}

					i = (int) (Math.random() * model.getListOfNodeSize());

					arc.setStartingNode(node1);
					arc.setEndingNode(model.getNthNode(i));

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addNode(node1);
					idNode1 = node1.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idNode1)));
					assertEquals(idNode1, model.getMaxId());

					assertEquals(node1, model.getANode(idNode1));

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addArc(arc);
					idArc = arc.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idArc)));
					assertEquals(idArc, model.getMaxId());

					break;

					// Ajout d'un noeud cible relie a un noeud deja present dans le modele
				case 2:

					if (model.getListOfNodeSize() == 0) {
						break;
					}

					i = (int) (Math.random() * model.getListOfNodeSize());

					arc.setStartingNode(model.getNthNode(i));
					arc.setEndingNode(node2);

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addNode(node2);
					idNode2 = node2.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idNode2)));
					assertEquals(idNode2, model.getMaxId());

					assertEquals(node2, model.getANode(idNode2));

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addArc(arc);
					idArc = arc.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idArc)));
					assertEquals(idArc, model.getMaxId());

					break;

				case 3:

					if (model.getListOfNodeSize() == 0) {
						break;
					}

					i = (int) (Math.random() * model.getListOfNodeSize());

					arc.setStartingNode(model.getNthNode(i));
					i = (int) (Math.random() * model.getListOfNodeSize());
					arc.setEndingNode(model.getNthNode(i));

					assertFalse(model.getListOfId().contains(Integer.valueOf(model.getMaxId() + 1)));
					model.addArc(arc);
					idArc = arc.getId();
					assertTrue(model.getListOfId().contains(Integer.valueOf(idArc)));
					assertEquals(idArc, model.getMaxId());

					break;

				default:
					break;
			}
		} catch (ModelException e) {
			System.out.println(e.toString());
		}

	}

	/* Cas ou l'ajout de l'arc est impossible */
	public final void switchArcNull(IArc arc) {

		int actionNull = (int) (Math.random() * RANDOM);
		int i;

		INode node1 = new Node("Node");
		INode node2 = new Node("Node");

		try {
			switch(actionNull) {
				// Ajout d'un arc dont les noeuds ne sont present dans le modele
				case 0:

					arc.setStartingNode(node1);
					arc.setEndingNode(node2);

					idNode1 = node1.getId();
					idNode2 = node2.getId();

					assertTrue(node1 != model.getANode(idNode1));
					assertTrue(node2 != model.getANode(idNode2));

					model.addArc(arc);
					break;

				// Ajout d'un arc dont le noeud source n'est pas present dans le modele
				case 1:
					if (model.getListOfNodeSize() == 0) {
						break;
					}

					i = (int) (Math.random() * model.getListOfNodeSize());

					arc.setStartingNode(node1);
					arc.setEndingNode(model.getNthNode(i));

					idNode1 = node1.getId();
					assertTrue(model.getANode(idNode1) == null);

					model.addArc(arc);
					break;

					// Ajout d'un arc dont le noeud cible n'est pas present dans le modele
				case 2:
					if (model.getListOfNodeSize() == 0) {
						break;
					}

					i = (int) (Math.random() * model.getListOfNodeSize());

					arc.setStartingNode(model.getNthNode(i));
					arc.setEndingNode(node2);

					idNode2 = node2.getId();
					assertTrue(model.getANode(idNode2) == null);

					model.addArc(arc);
					break;

				case 3:
					model.addArc(arc);
					break;

				default:
					break;

			}
		} catch (ModelException e) {
			System.out.println(e.toString());
		}
	}

	public final void testScenarioID() {

		int actionAlea;

		while (tour <= MAXLOOP) {
			actionAlea = (int) (Math.random() * 2);
			IArc arc = new Arc("Arc");
			System.out.println("Tour:" + tour);

			if ((tour % 8) == 0) {
				int ind = 0;
				int i = 0;

				while (i < NBREMOVE) {
					if (model.getListOfArcSize() == 0) {
						System.out.println("Aucun Arc a retirer\n");
						break;
					}

					ind = (int) (Math.random() * model.getListOfArcSize());
					arc = model.getNthArc(ind);
					if (!(arc == null)) {

						idArc = arc.getId();
						System.out.println("arc_id to remove=" + idArc + "\n");
						INode node1 = arc.getStartingNode();
						INode node2 = arc.getEndingNode();
						idNode1 = node1.getId();
						idNode2 = node2.getId();

						assertTrue(model.getListOfId().contains(Integer.valueOf(idArc)));
						try {
							model.removeArc(arc);
						} catch (ModelException e) {
							fail(e.toString());
						}

						/* arc plus present dans la liste des arcs du modele */
						assertTrue(model.getAnArc(idArc) == null);

						/*
						 * arc plus present dans la liste des noeuds de sa cible
						 * et de sa source
						 */
						assertFalse(node1.getListOfOutputArc().contains(arc));
						assertFalse(node2.getListOfInputArc().contains(arc));

						/* arc plus present dans la liste des identifiants */
						assertFalse(model.getListOfId().contains(Integer.valueOf(idArc)));

						i = i + 1;
					}
				}
			} else {
				if ((tour % 15) == 0 && !(model.getListOfNodeSize() == 0)) {
					int idNode;
					int ind = 0;
					INode nodeRemove;
					ind = (int) (Math.random() * model.getListOfNodeSize());
					Vector<IArc> outArc;
					Vector<IArc> inArc;

					nodeRemove = model.getNthNode(ind);
					idNode = nodeRemove.getId();

					outArc = new Vector<IArc>(nodeRemove.getListOfOutputArc());
					inArc = new Vector<IArc>(nodeRemove.getListOfInputArc());

					try {
						model.removeNode(nodeRemove);
					} catch (ModelException e) {
						fail(e.toString());
					}

					assertTrue(model.getANode(idNode) == null);

					for (int i = 0; i < outArc.size(); i++) {
						IArc a = outArc.get(i);
						INode n = a.getEndingNode();
						int aId = a.getId();
						assertTrue(model.getAnArc(aId) == null);
						assertFalse(n.getListOfInputArc().contains(a));
					}

					for (int i = 0; i < inArc.size(); i++) {
						IArc a = inArc.get(i);
						INode n = a.getStartingNode();
						int aId = a.getId();
						assertTrue(model.getAnArc(aId) == null);
						assertFalse(n.getListOfOutputArc().contains(a));
					}
				} else {
					// Ajout autorise
					if (actionAlea == 0) {
						System.out.println("Cas : Ajout autorise");
						switchArcOK(arc);
						if (!(model.getListOfNodeSize() == 0)) {

							INode node1 = arc.getStartingNode();
							INode node2 = arc.getEndingNode();
							idNode1 = arc.getStartingNode().getId();
							idNode2 = arc.getEndingNode().getId();
							idArc = arc.getId();

							assertTrue(model.getAnArc(idArc) != null);
							assertTrue(node1.getListOfOutputArc().contains(arc));
							assertTrue(node2.getListOfInputArc().contains(arc));
							// System.out.println("ADD
							// tour:"+tour+"\narc_id:"+id_arc+"\nid_node1:"+id_node1+"
							// id_node2:"+id_node2+"\nMaxId:"+model.getMaxId()+"\n");
						} else {
							System.out.println("ListOfNode vide: l'ajout d'un arc ne peut s'effectuer\n");
						}
					} else {
						// Action non autorisee
						System.out.println("Cas : Ajout non conforme");
						switchArcNull(arc);
						if (!(model.getListOfNodeSize() == 0)) {
							idArc = arc.getId();
							assertTrue(model.getAnArc(idArc) == null);
						} else {
							System.out.println("ListOfNode vide\n");
						}
					}
				}
			}
			tour++;
		}
		System.out.println("FIN DES TESTS!");
	}
}
