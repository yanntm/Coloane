package fr.lip6.move.coloane.projects.its.expression;

/**
 * A substraction.
 * @author Yann
 *
 */
public final class Minus extends NaryExpression implements IntegerExpression {

	/**
	 * {@inheritDoc}
	 */
	public int evaluate(IEvaluationContext context) {
		int total = 0;
		boolean first = true;
		for (IntegerExpression expr : getChildren()) {
			if (first) {
				total = expr.evaluate(context);
				first = false;
			} else {
				total -= expr.evaluate(context);
			}
		}
		return total;
	}

}