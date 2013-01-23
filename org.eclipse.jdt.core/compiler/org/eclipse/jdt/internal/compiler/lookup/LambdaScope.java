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
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;

public class LambdaScope extends MethodScope {

	protected LambdaExpression expression;
	public MethodBinding bodyBinding;

	public LambdaScope(BlockScope parent, LambdaExpression expression) {
		super(LAMBDA_SCOPE, parent, parent.referenceContext(), parent.methodScope().isStatic);
		this.expression = expression;
	}

	public TypeBinding expectedResultType() {
		return this.expression.expectedResultType();
	}
	
	/**
	 * Error management:
	 * 		keep null for all the errors that prevent the method to be created
	 * 		otherwise return a correct method binding (but without the element
	 *		that caused the problem) : i.e. Incorrect thrown exception
	 */
	public MethodBinding createMethod() {
		if (this.bodyBinding != null) return this.bodyBinding;
		
		SourceTypeBinding declaringClass = referenceType().binding;
		int modifiers = ExtraCompilerModifiers.AccUnresolved;
		this.bodyBinding =
			new MethodBinding(modifiers, "lambda$0".toCharArray(), null, null, null, declaringClass); //$NON-NLS-1$
	
		// TODO: Check that arguments have types, they must be compatible with the
		// target method's.
	
		// Hmm, I suppose the type parameters for lambdas are anything but trivial, and should copy any
		// 
		TypeParameter[] typeParameters = null; // TODO: Compute it here
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=324850, If they exist at all, process type parameters irrespective of source level.
	    if (typeParameters == null || typeParameters.length == 0) {
	    	this.bodyBinding.typeVariables = Binding.NO_TYPE_VARIABLES;
		} else {
			this.bodyBinding.typeVariables = createTypeVariables(typeParameters, this.bodyBinding);
			this.bodyBinding.modifiers |= ExtraCompilerModifiers.AccGenericSignature;
		}
		return this.bodyBinding;
	}
	
	/**
	 *  Answer the reference type of this scope.
	 * It is the nearest enclosing type of this scope.
	 */
	public TypeDeclaration referenceType() {
		return this.classScope().referenceContext;
	}	
}
