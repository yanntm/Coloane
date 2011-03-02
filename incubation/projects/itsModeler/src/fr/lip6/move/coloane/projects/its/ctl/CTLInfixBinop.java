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
 *   Yann THIERRY-MIEG (LIP6)
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.projects.its.ctl;

/**
 * A binary operator that is noted in infix form.
 * Overloads the toString mechanism.
 * @author Yann
 *
 */
public abstract class CTLInfixBinop extends CTLBinaryOp {

	/**
	 * a OP b
	 * @param left a
	 * @param right b
	 */
	public CTLInfixBinop(CTLFormula left, CTLFormula right) {
		super(left, right);
	}

	/**
	 * Formats the operator between the operands + adds braces to operands.
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "(" + getLeft() + ")" + getOperator() + "(" + getRight() + ")";
	}
}
