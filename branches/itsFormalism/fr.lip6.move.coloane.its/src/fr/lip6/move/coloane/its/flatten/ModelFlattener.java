package fr.lip6.move.coloane.its.flatten;


import fr.lip6.move.coloane.core.model.GraphModelFactory;
import fr.lip6.move.coloane.interfaces.exceptions.ModelException;
import fr.lip6.move.coloane.interfaces.formalism.IElementFormalism;
import fr.lip6.move.coloane.interfaces.formalism.IGraphFormalism;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.its.CompositeTypeDeclaration;
import fr.lip6.move.coloane.its.Concept;
import fr.lip6.move.coloane.its.TypeDeclaration;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.draw2d.ColorConstants;

/** A class to flatten an ITS model once fr.lip6.move.coloane.its links are resolved.
 * Produces a new model instance.
 * @author Yann
 *
 */
public final class ModelFlattener {

	private IGraph flatModel;
	private Map<String, Map<INode, INode>> idsPerInstance;
	private List<List<ResolvedTrans>> emptyEffect;

	/**
	 * Constructor.
	 */
	public ModelFlattener() {
		// we build one transition node in the flat net for each possible sync instantiation
		// To initialize cartesian products in calls to cumulateLabelEffect
		emptyEffect = new ArrayList<List<ResolvedTrans>>();
		emptyEffect.add(new ArrayList<ResolvedTrans>());
	}

	/**
	 * Flatten a model and build a representation of the flat model.
	 * @param root the composite to flatten
	 * @throws ModelException if cannot create TPN graph.
	 */
	public void doFlatten(CompositeTypeDeclaration root) throws ModelException {
		flatModel = new GraphModelFactory().createGraph("Time Petri Net");
		idsPerInstance = new HashMap<String, Map<INode, INode>>();
		try {
			flatten(root, "");
			for (String label : root.getLabels()) {
				// obtain effects of all public syncs
				List< List<ResolvedTrans> > tset = cumulateLabelEffect(root, label, "", emptyEffect);
				buildTransitions(tset, label);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Flatten a model and build a representation of the flat model.
	 * @param ctd the composite to flatten
	 * @param prefix the prefix of this instance, e.g. "P1.P2."
	 * @throws ModelException if cannot create graph.
	 */
	private void flatten(CompositeTypeDeclaration ctd, String prefix) throws ModelException {
		// we build one transition node in the flat net for each possible sync instantiation
		// obtain effects of all private ("" label) syncs
		// scan for instances and get private events of each instance

		// grab the appropriate formalism elements to analyze an ITS Composite
		IGraphFormalism formalism = ctd.getGraph().getFormalism().getMasterGraph();
		IElementFormalism inst = formalism.getElementFormalism("instance");

		/** Scan through the Nodes to find all instances and recursively flatten them */
		Collection<INode> nodes = ctd.getGraph().getNodes();
		for (INode node : nodes) {
			// An instance
			if (node.getNodeFormalism().equals(inst)) {
				String instName = node.getAttribute("name").getValue();
				String instConcept = node.getAttribute("type").getValue();

				Concept concept = ctd.getConcept(instConcept);
				TypeDeclaration t = concept.getEffective();
				if (t instanceof CompositeTypeDeclaration) {
					CompositeTypeDeclaration ctd2 = (CompositeTypeDeclaration) t;
					flatten(ctd2, prefix + "." + instName);
				} else {
					flatten(t, prefix + "." + instName);
				}
			}
		}


		// obtain effects of all private ("" label) syncs
		List< List<ResolvedTrans> > tset = cumulateLabelEffect(ctd, "", prefix, emptyEffect);
		// create corresponding effect in the resulting net
		buildTransitions(tset, "");


	}

	/** instantiate a transition for each set in tset
	 * 
	 * @param tset the set of sets of concrete transitions to bind this behavior to.
	 * @param label the label of the resulting action
	 * @throws ModelException if graph creation fail
	 */
	private void buildTransitions(List<List<ResolvedTrans>> tset, String label) throws ModelException {
		for (List<ResolvedTrans> effectSet : tset) {
			INode t = flatModel.createNode("transition");

			if ("".equals(label)) {
				StringBuffer sb = new StringBuffer();
				for (ResolvedTrans rt : effectSet) {
					sb.append(rt);
					sb.append(",");
				}
				// kill trailing comma
				sb.deleteCharAt(sb.length() - 1);

				t.getAttribute("label").setValue(sb.toString());
				t.getAttribute("visibility").setValue("private");
				t.getGraphicInfo().setBackground(ColorConstants.lightBlue);

			} else {
				t.getAttribute("label").setValue(label);
				t.getAttribute("visibility").setValue("public");
				t.getGraphicInfo().setBackground(ColorConstants.green);

			}

			for (ResolvedTrans rt : effectSet) {
				for (IArc a : rt.getTransition().getIncomingArcs()) {
					INode place = a.getSource();
					INode flatPlace = idsPerInstance.get(rt.getInstance()).get(place);
					IArc resultArc = flatModel.createArc(a.getArcFormalism().getName(), flatPlace, t);
					// copy arc properties
					for (IAttribute att : a.getAttributes()) {
						resultArc.getAttribute(att.getName()).setValue(att.getValue());
					}
				}
				for (IArc a : rt.getTransition().getOutgoingArcs()) {
					INode place = a.getTarget();
					INode flatPlace = idsPerInstance.get(rt.getInstance()).get(place);
					IArc resultArc = flatModel.createArc(a.getArcFormalism().getName(), t, flatPlace);
					// copy arc properties
					for (IAttribute att : a.getAttributes()) {
						resultArc.getAttribute(att.getName()).setValue(att.getValue());
					}
				}
			}
		}

	}


	/** Add to tset the effects of the label lab the instance represented by Node inst.
	 * 
	 * @param ctd the type of the composite
	 * @param lab the label to add
	 * @param prefix the instance prefix
	 * @param tset the set of syncs under construction
	 * @return an updated set of set of effects
	 */
	private List<List<ResolvedTrans>> cumulateLabelEffect(CompositeTypeDeclaration ctd, String lab,
			String prefix, List<List<ResolvedTrans>> tset) {
		// grab the appropriate formalism elements to analyze an ITS Composite
		IGraphFormalism formalism = ctd.getGraph().getFormalism().getMasterGraph();

		IElementFormalism sync = formalism.getElementFormalism("synchronization");
		/** Scan through the Nodes to find all instances and recursively flatten them */
		Collection<INode> nodes = ctd.getGraph().getNodes();
		// scan through the nodes looking for syncs bearing the label "lab"
		List<INode> matchingSyncs = new ArrayList<INode>();
		for (INode node : nodes) {
			// A synchronization
			if (node.getNodeFormalism().equals(sync)) {
				String syncLabel = node.getAttribute("label").getValue();
				if (lab.equals(syncLabel)) {
					// A sync with the right label
					matchingSyncs.add(node);
				}
			}
		}
		// now add effect of each sync with matching label
		List<List<ResolvedTrans>> resultSet = new ArrayList<List<ResolvedTrans>>();
		for (INode matchSync : matchingSyncs) {
			// Each sync creates a new effect = a list of list of synchronized TPN transitions
			List<List<ResolvedTrans>> effectSet = new ArrayList<List<ResolvedTrans>>();
			// initially suppose we have no effect (i.e. 1 empty effect for cartesian product)
			effectSet.add(new ArrayList<ResolvedTrans>());

			for (IArc arc : matchSync.getIncomingArcs()) {
				// Grab the node "instance"
				INode instance = arc.getSource();
				TypeDeclaration instType = ctd.getConcept(instance.getAttribute("type").getValue()).getEffective();
				String instName = instance.getAttribute("name").getValue();
				// Parse the labels field
				String labels = arc.getAttribute("labels").getValue();
				StringTokenizer st = parseLabels(labels);

				while (st.hasMoreTokens()) {
					// foreach label
					String curLabel = st.nextToken();
					effectSet = cumulateLabelEffect(instType, instance, curLabel, prefix + "." + instName, effectSet);
				}
			}
			// repeat for outgoing arcs, since they are undirected
			for (IArc arc : matchSync.getOutgoingArcs()) {
				// Grab the node "instance"
				INode instance = arc.getTarget();
				TypeDeclaration instType = ctd.getConcept(instance.getAttribute("type").getValue()).getEffective();
				String instName = instance.getAttribute("name").getValue();
				// Parse the labels field
				String labels = arc.getAttribute("labels").getValue();
				StringTokenizer st = parseLabels(labels);

				while (st.hasMoreTokens()) {
					// foreach label
					String curLabel = st.nextToken();
					effectSet = cumulateLabelEffect(instType, instance, curLabel, prefix + "." + instName, effectSet);
				}
			}

			// compute the product with tset and add to global effects computed
			// creates N*M effects where N is size of effectSet and M size of tset argument to recursive call
			for (List<ResolvedTrans> effect : effectSet) {
				for (List<ResolvedTrans> initialEffect : tset) {
					List<ResolvedTrans> resultEffect = new ArrayList<ResolvedTrans>(effect);
					resultEffect.addAll(initialEffect);
					resultSet.add(resultEffect);
				}
			}

		}
		return resultSet;
	}

	/**
	 * parse an arc label to get the labels.
	 * e.g. "take;put"
	 * @param labels the label of the arc
	 * @return the {@link StringTokenizer} that recognizes the labels
	 */
	private static StringTokenizer parseLabels(String labels) {
		String labs = labels;
		labs = labels.replace(" ", "");
		labs = labels.replace("\t", "");
		labs = labels.replace("\n", "");

		return new StringTokenizer(labs, ";");
	}

	/** Add to tset the effects of the label lab the instance represented by Node inst.
	 * 
	 * @param type the type of the instance (a TPN)
	 * @param inst the instance node in the original net(s)
	 * @param lab the label to add
	 * @param tset the set of syncs under construction
	 * @param prefix the instance prefix of this transition
	 * @return the set of set of effects under construction
	 */
	private List<List<ResolvedTrans>> cumulateLabelEffect(TypeDeclaration type, INode inst, String lab,
			String prefix, List<List<ResolvedTrans>> tset) {

		// Test for recursion composite case
		if (type instanceof CompositeTypeDeclaration) {
			CompositeTypeDeclaration ctd = (CompositeTypeDeclaration) type;
			return cumulateLabelEffect(ctd, lab, prefix, tset);
		}
		// We are now sure this is a TPN
		// grab the appropriate formalism elements to analyze an ITS Composite
		IGraphFormalism formalism = type.getGraph().getFormalism().getMasterGraph();
		IElementFormalism trans = formalism.getElementFormalism("transition");

		/** Scan through the Nodes to find all trans bearing this label and collect them in matchingTrans */
		List<INode> matchingTrans = new ArrayList<INode>();

		Collection<INode> nodes = type.getGraph().getNodes();
		for (INode node : nodes) {
			// A transition
			if (node.getNodeFormalism().equals(trans)) {
				String label = node.getAttribute("label").getValue();
				// Check if label matches
				if (label.equals(lab)) {
					// select this transition
					matchingTrans.add(node);
				}
			}
		}

		/** now do the cartesian product of effects into tset.
		 * This is simply a nested loop, creates N*M entries in result */
		List<List<ResolvedTrans>> result = new ArrayList<List<ResolvedTrans>>();
		for (INode transition : matchingTrans) {
			for (List<ResolvedTrans> syncSet : tset) {
				// A new entry in the result created by copy
				List<ResolvedTrans> resSet = new ArrayList<ResolvedTrans>(syncSet);
				// add this transition
				resSet.add(new ResolvedTrans(prefix, transition));
				result.add(resSet);
			}
		}
		return result;
	}

	/**
	 * Flatten a TPN using the provided prefix
	 * @param t the tpn to flatten
	 * @param prefix the instance prefix P1.P2.
	 * @throws ModelException in case of graph creation problems
	 */
	private void flatten(TypeDeclaration t, String prefix) throws ModelException {

		// grab the appropriate formalism elements to analyze an ITS Composite
		IGraphFormalism formalism = t.getGraph().getFormalism().getMasterGraph();
		IElementFormalism place = formalism.getElementFormalism("place");
		IElementFormalism trans = formalism.getElementFormalism("transition");
		// to store node mapping of places
		Map<INode, INode> ids = new HashMap<INode, INode>();
		idsPerInstance.put(prefix, ids);

		/** Scan through the Nodes to find all places and flatten them */
		Collection<INode> nodes = t.getGraph().getNodes();
		for (INode node : nodes) {
			// A place
			if (node.getNodeFormalism().equals(place)) {
				String placeName = node.getAttribute("name").getValue();
				String marking = node.getAttribute("marking").getValue();
				// create a new place in the flat model
				INode p = flatModel.createNode("place");
				p.getGraphicInfo().setBackground(ColorConstants.yellow);
				ids.put(node, p);

				String newPName = prefix + "." + placeName;

				p.getAttribute("name").setValue(newPName);
				p.getAttribute("marking").setValue(marking);
			}
		}

		/** Scan through the Nodes to find all private transitions and flatten them */
		for (INode node : nodes) {
			// A transition
			if (node.getNodeFormalism().equals(trans)) {
				boolean isPublic = "public".equals(node.getAttribute("visibility").getValue());
				if (!isPublic) {
					// a "private" transition
					String transName = node.getAttribute("label").getValue();

					// create a new transition in the flat model
					INode newt = flatModel.createNode("transition");
					newt.getGraphicInfo().setBackground(ColorConstants.lightBlue);

					String tname = prefix + "." + transName;
					newt.getAttribute("label").setValue(tname);
					//handle arcs
					for (IArc a : node.getIncomingArcs()) {
						// find the source place mapped in the flat model
						INode newSource = ids.get(a.getSource());
						IArc arc;
						arc = flatModel.createArc(a.getArcFormalism().getName(), newSource, newt);
						flatModel.addArc(arc);
					}
					for (IArc a : node.getOutgoingArcs()) {
						// find the source place mapped in the flat model
						INode newSource = ids.get(a.getTarget());
						IArc arc;
						arc = flatModel.createArc(a.getArcFormalism().getName(), newt, newSource);
						flatModel.addArc(arc);
					}

				}
			}
		}

	}

	/**
	 * Accessor for the model built during invocation of flattenModel
	 * @return the built flat TPN graph or null if not computed.
	 */
	public IGraph getFlatModel() {
		return flatModel;
	}
}