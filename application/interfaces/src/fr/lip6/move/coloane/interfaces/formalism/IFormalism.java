package fr.lip6.move.coloane.interfaces.formalism;

import fr.lip6.move.coloane.interfaces.model.INode;

/**
 * Describe a formalism.<br>
 * Here, the formalism is used to describe all nodes, arcs and attributes that can be used in a model.<br>
 * Some constraints can also be defined :
 * <ul>
 * 	<li>constraints between nodes</li>
 * 	<li>constraints on node actions</li>
 * </ul>
 */
public interface IFormalism {

	/**
	 * Tells if the arc creation is allowed or not
	 * @param source The source node
	 * @param target The target node
	 * @param arcFormalism The arc formalism
	 * @return <code>true</code> if the creation is allowed
	 */
	boolean isLinkAllowed(INode source, INode target, IArcFormalism arcFormalism);

	/**
	 * Tells if the action is allowed on this particular node
	 * @param node The node
	 * @return <code>true</code> if the action is allowed
	 */
	boolean isActionAllowed(INode node);

	/**
	 * @return The formalism ID
	 */
	String getId();

	/**
	 * @return The formalism name
	 */
	String getName();

	/**
	 * @return The formalism ID for FK platform (kept for historical reason)
	 * @deprecated
	 */
	String getFKName();

	/**
	 * @return The image associated to this formalism
	 */
	String getImageName();

	/**
	 * @return The root graph that is using this formalism
	 */
	IGraphFormalism getRootGraph();
}
