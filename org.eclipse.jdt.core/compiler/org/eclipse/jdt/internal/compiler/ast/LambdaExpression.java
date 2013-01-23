/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
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
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LambdaScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class LambdaExpression extends FunctionalLiteral {
	Argument [] arguments;
	Statement body;
	LambdaScope scope;
	TypeDeclaration typeDeclaration;
	
	public LambdaExpression(Argument [] arguments, Statement body) {
		super(0, 0);
		this.arguments = arguments;
		this.body = body;
	}
	
	public TypeBinding resolveType(BlockScope blockScope) {
		if (this.scope == null) {
			this.scope = new LambdaScope(blockScope, this);
		}
		MethodBinding method = null;
		if (checkContext(blockScope)) {
			int formalArgumentCount = this.arguments != null ? this.arguments.length : 0;
			method = resolveFunctionalMethod(formalArgumentCount, this.scope.problemReporter());
			if (this.arguments != null && method != null) {
				for (int i = 0, length = this.arguments.length; i < length; i++) {
					this.arguments[i].setElidedType(method.parameters[i]);
					this.arguments[i].resolve(this.scope);
				}
			}
			// Must examine poly-type and pick the right one
		}		
		if (this.body != null) {
			if (this.body instanceof Expression) {
				Expression expression = (Expression) this.body;
				if (method != null && method.returnType != null) {
					expression.setExpectedType(method.returnType);
					TypeBinding expressionType = expression.resolveType(this.scope);
					checkExpressionResult(method.returnType, expression, expressionType);
				}
			} else {
				// if non-void, check that a value is returned
				this.body.resolve(this.scope);
			}
		}
		
		return super.resolveType(this.scope);
	}

	void checkExpressionResult(TypeBinding lambdaResultType, Expression expression, TypeBinding expressionType) {
		// this is copied from ReturnStatement::resolve
		if (lambdaResultType == TypeBinding.VOID) {
			final boolean mayHaveEffects[] = new boolean[] {false};
			expression.traverse(new ASTVisitor() {
				public boolean visit(ArrayAllocationExpression     theExpression, BlockScope unusedScope)	 { return markEffects(); }
				public boolean visit(AllocationExpression          theExpression, BlockScope unusedScope) { return markEffects(); }
				public boolean visit(Assignment                    theExpression, BlockScope unusedScope) { return markEffects(); }
				public boolean visit(CompoundAssignment            theExpression, BlockScope unusedScope) { return markEffects(); }
				public boolean visit(MessageSend                   theExpression, BlockScope unusedScope) { return markEffects(); }
				public boolean visit(PostfixExpression             theExpression, BlockScope unusedScope)  { return markEffects(); }
				public boolean visit(PrefixExpression              theExpression, BlockScope unusedScope)  { return markEffects(); }
				public boolean visit(QualifiedAllocationExpression theExpression, BlockScope unusedScope) { return markEffects(); }

				private boolean markEffects() {
					mayHaveEffects[0] = true;
					return false;
				}
			}, this.scope);
			if (! mayHaveEffects[0]) {
				this.scope.problemReporter().lambdaExpressionHasNoEffect(expression);
			}
			return; // anything goes
		}
		if (lambdaResultType != expressionType) // must call before computeConversion() and typeMismatchError()
			this.scope.compilationUnitScope().recordTypeConversion(lambdaResultType, expressionType);
		if (expression.isConstantValueOfTypeAssignableToType(expressionType, lambdaResultType)
				|| expressionType.isCompatibleWith(lambdaResultType)) {
	
			expression.computeConversion(this.scope, lambdaResultType, expressionType);
			if (expressionType.needsUncheckedConversion(lambdaResultType)) {
			    this.scope.problemReporter().unsafeTypeConversion(expression, expressionType, lambdaResultType);
			}
			if (expression instanceof CastExpression
					&& (expression.bits & (ASTNode.UnnecessaryCast|ASTNode.DisableUnnecessaryCastCheck)) == 0) {
				CastExpression.checkNeedForAssignedCast(this.scope, lambdaResultType, (CastExpression) expression);
			}
			return;
		} else if (isBoxingCompatible(expressionType, lambdaResultType, expression, this.scope)) {
			expression.computeConversion(this.scope, lambdaResultType, expressionType);
			if (expression instanceof CastExpression
					&& (expression.bits & (ASTNode.UnnecessaryCast|ASTNode.DisableUnnecessaryCastCheck)) == 0) {
				CastExpression.checkNeedForAssignedCast(this.scope, lambdaResultType, (CastExpression) expression);
			}			return;
		}
		if ((lambdaResultType.tagBits & TagBits.HasMissingType) == 0) {
			// no need to complain if return type was missing (avoid secondary error : 220967)
			this.scope.problemReporter().typeMismatchError(expressionType, lambdaResultType, expression, null);
		}
	}
	
	public void traverse(
			ASTVisitor visitor,
			BlockScope blockScope) {

			if (visitor.visit(this, blockScope)) {
				if (this.arguments != null) {
					int argumentsLength = this.arguments.length;
					for (int i = 0; i < argumentsLength; i++)
						this.arguments[i].traverse(visitor, this.scope);
				}

				if (this.body != null) {
					this.body.traverse(visitor, this.scope);
				}
			}
			visitor.endVisit(this, blockScope);
		}

	public StringBuffer printExpression(int tab, StringBuffer output) {
		int parenthesesCount = (this.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT;
		String suffix = ""; //$NON-NLS-1$
		for(int i = 0; i < parenthesesCount; i++) {
			output.append('(');
			suffix += ')';
		}
		output.append('(');
		if (this.arguments != null) {
			for (int i = 0; i < this.arguments.length; i++) {
				if (i > 0) output.append(", "); //$NON-NLS-1$
				this.arguments[i].print(0, output);
			}
		}
		output.append(") -> " ); //$NON-NLS-1$
		this.body.print(this.body instanceof Block ? tab : 0, output);
		return output.append(suffix);
	}

}
