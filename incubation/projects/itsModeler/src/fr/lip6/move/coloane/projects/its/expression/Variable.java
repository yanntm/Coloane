package fr.lip6.move.coloane.projects.its.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An integer variable.
 * @author Yann
 *
 */
public final class Variable implements IntegerExpression, IVariable {

	private String name;

	/**
	 * Ctor.
	 * @param name of the variable
	 */
	public Variable(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public int evaluate(IEvaluationContext context) {
		return context.getVariableValue(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IntegerExpression> getChildren() {
		return new ArrayList<IntegerExpression>();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// CHECKSTYLE OFF
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		// CHECKSTYLE ON
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Variable other = (Variable) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariable> supportingVariables() {
		Set<IVariable> set = new HashSet<IVariable>();
		set.add(this);
		return set;
	}

}