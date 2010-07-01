package fr.lip6.move.coloane.tools.stats;

import java.util.ArrayList;

import java.util.List;

import fr.lip6.move.coloane.core.extensions.IColoaneAction;
import fr.lip6.move.coloane.core.results.Result;
import fr.lip6.move.coloane.core.results.SubResult;
import fr.lip6.move.coloane.interfaces.model.IArc;
import fr.lip6.move.coloane.interfaces.model.IGraph;
import fr.lip6.move.coloane.interfaces.model.INode;
import fr.lip6.move.coloane.interfaces.objects.result.IResult;

/**
 * Very basic tool that provides some statistics about the current model
 * 
 * @author Jean-Baptiste Voron
 * @author Florian David
 */
public class StatAction implements IColoaneAction {

	/**
	 * {@inheritDoc}
	 */
	public List<IResult> run(IGraph model) {
		ArrayList<INode> nodes = new ArrayList<INode>(model.getNodes());
		ArrayList<IArc> arcs = new ArrayList<IArc>(model.getArcs());

		Result result = new Result("Statistics", model);
		SubResult subresNodes = new SubResult("Places and Transitions", "Show all nodes");
		SubResult subresArcs = new SubResult("Arcs", "Show all arcs");

		// Add all nodes as objects designation for the first sub-result
		for(int i = 0; i < nodes.size(); i++) { subresNodes.addObjectDesignation(nodes.get(i)); }
		// Add all arcs as objects designation for the second sub-result
		for(int i = 0; i < arcs.size(); i++) { subresArcs.addObjectDesignation(arcs.get(i)); }

		
		SubResult subresNodeList = new SubResult("Places and Transitions", "Node list");
		SubResult subresArcList = new SubResult("Arcs", "Arc list");

		for(int i = 0; i < nodes.size(); i++) { subresNodeList.addObjectOutline(nodes.get(i)); }
		for(int i = 0; i < arcs.size(); i++) { subresArcList.addObjectOutline(arcs.get(i));	}

		result.addTextualResult("Number of nodes:",String.valueOf(nodes.size()));
		result.addTextualResult("Number of arcs:",String.valueOf(arcs.size()));

		result.addSubResult(subresNodes);
		result.addSubResult(subresArcs);
		result.addSubResult(subresNodeList);
		result.addSubResult(subresArcList);

		ArrayList<IResult> resultsList = new ArrayList<IResult>();
		resultsList.add(result);
		return resultsList;
	}
}