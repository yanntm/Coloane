/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.tools.layout;

import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IAttribute;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.interfaces.model.requests.IRequest;
import fr.lip6.move.coloane.tools.graphviz.GraphViz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Point;

/**
 * A utility class that offers "Layout" that modifies in place the given graph.<br>
 * Graphviz settings (preferences) are used.
 * 
 * @author Yann Thierry-Mieg
 */
public final class GraphLayout {

	/** 
	 * Hide constructor : functionality is all static.
	 */
	private GraphLayout() { }

	/**
	 * The main user function : apply dot layout to the provided Graph instance.
	 * @param graph the graph to be updated with new positions.
	 * @param monitor the progress monitor to inform
	 * @return the list of position commands to execute
	 * @throws CoreException if dot file generation failed (typically dot misconfiguration)
	 * @throws IOException if dot file malformed or inexistant (timeout ?)
	 */
	public static List<IRequest> layout(IGraph graph, IProgressMonitor monitor) throws CoreException, IOException {
		monitor.subTask("Rewriting the current graph as a DOT compatible graph");
		StringBuffer sb = new StringBuffer();
		Map<String, List<INode>> clusters = new HashMap<String, List<INode>>();
		sb.append("digraph G {\n");
		sb.append("   concentrate = true; \n");
		for (INode node : graph.getNodes()) {
			// One line per node
			sb.append("    " + getDotID(node) + " ;\n");
			addNode(clusters, node);
		}
		for (IArc arc : graph.getArcs()) {
			// One line per arc
			sb.append(getDotID(arc.getSource()) + " -> " + getDotID(arc.getTarget()) + "[label=ID" + arc.getId() + " ] ;\n");
		}
		if (!clusters.isEmpty()) {
			for (Entry<String, List<INode> > e : clusters.entrySet()) {
				sb.append("  subgraph \"cluster" + e.getKey() + "\" { \n");
				for (INode node : e.getValue()) {
					sb.append("    " + getDotID(node) + " ;\n");
				}
				sb.append("  }\n");
			}
		}
		sb.append("}");
		monitor.worked(1);


		// Call Graphviz
		monitor.subTask("Calling DOT tool");
		InputStream dotOutput = GraphViz.generate(new ByteArrayInputStream(sb.toString().getBytes()),
				"plain", // format to basic annotated positions
				new Point(20, 20));
		monitor.worked(1);
		monitor.subTask("Parsing graph objects positions");
		List<IRequest>requests = DotParser.parseGraphPositions(dotOutput, graph);
		monitor.worked(1);
		return requests;

	}

	/** Defines clusters if possible.
	 * Relies on an attribute named "component" in the node.
	 * If it exists, the node will be attached to a cluster bearing that name.
	 * @param clusters a mapping giving for each cluster its list of nodes
	 * @param node a node to be added
	 */
	private static void addNode(Map<String, List<INode>> clusters, INode node) {
		IAttribute comp = node.getAttribute("component");
		if (comp == null) {
			return;
		}
		String cname = comp.getValue();
		if (cname.equals("")) {
			return;
		}
		List<INode> nodes;
		if (!clusters.containsKey(cname)) {
			nodes = new ArrayList<INode>();
			clusters.put(cname, nodes);
		} else {
			nodes = clusters.get(cname);
		}
		nodes.add(node);
	}

	/**
	 * Convert an integer node ID to a string passed to dot as object ID
	 * e.g. 12 -> "ID12"
	 * @param node the node
	 * @return the string ID
	 */
	private static String getDotID(INode node) {
		return "ID" + node.getId();
	}
}
