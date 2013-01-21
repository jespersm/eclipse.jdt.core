/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public abstract class FunctionalLiteral extends MagicLiteral {

	private TypeBinding expectedType;
	private MethodBinding singleMethod;
	private boolean allowedInContext = false;
	
	public FunctionalLiteral(int s , int e) {
		super(s,e);
	}

	public void setExpectedType(TypeBinding expectedType) {
		this.expectedType = expectedType;
	}

	public void computeConstant() {

		this.constant = Constant.NotAConstant;
	}

	public int nullStatus(FlowInfo flowInfo) {
		return FlowInfo.NON_NULL;
	}

	public Object reusableJSRTarget() {
		return TypeBinding.NULL;
	}

	/**
	 * Must examine the expected type and pick the method which matches the method
	 * 
	 * @param argumentCount The number of arguments
	 * @param reporter Where to show prpblem markers 
	 * @return The method in the relevant expected functional interface 
	 */
	protected MethodBinding resolveFunctionalMethod(int argumentCount, ProblemReporter reporter) {
		if (! (this.expectedType instanceof ReferenceBinding)) {
			reporter.targetTypeIsNotAFunctionalInterface(this);
			return null;
		}
		ReferenceBinding referenceBinding = (ReferenceBinding)this.expectedType;
		MethodBinding methods[] = referenceBinding.methods();
		
		// TODO: This ignores inheritance
		if (methods == null || methods.length != 1) {
			reporter.targetTypeIsNotAFunctionalInterface(this);
			return null;
		}
		this.singleMethod = methods[0];
		return this.singleMethod;
	}

	public TypeBinding expectedType() {
		return this.expectedType;
	}
	
	public TypeBinding expectedResultType() {
		return this.singleMethod != null ? this.singleMethod.returnType : null; 
	}

	public TypeBinding literalType(BlockScope blockScope) {
		return expectedType();
	}

	/**
	 * Code generation for the null literal
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 * @param valueRequired boolean
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		int pc = codeStream.position;
		if (valueRequired) {
			codeStream.aconst_null(); // TODO: Real code
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

	/**
	 * Sets a flag to allow a functional interface at this expression.
	 */
	public void allowFunctionalInterface() {
		this.allowedInContext  = true; // default is to do nothing
	}
	
	public boolean checkContext(BlockScope scope) {
		if (! this.allowedInContext) {
			// Knowledge of context should have been set beforehand
			scope.problemReporter().illegalContextForFunctionalExpression(this);
			return false;
		}
		return true;
	}
	
}
