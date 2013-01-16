/*******************************************************************************
 * Copyright (c) 2013 Jesper Steen Moller and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *								bug 382727 - [1.8][compiler] Lambda expression parameters and locals cannot shadow variables from context
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;

public class LambdaScope extends MethodScope {

	protected LambdaExpression expression;

	public LambdaScope(BlockScope parent, LambdaExpression expression) {
		super(LAMBDA_SCOPE, parent, parent.referenceContext(), parent.methodScope().isStatic);
		this.expression = expression;
	}

	public TypeBinding expectedResultType() {
		return this.expression.expectedResultType();
	}
}
