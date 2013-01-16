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
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class LambdaExpression extends NullLiteral {  // For the time being.
	Argument [] arguments;
	Statement body;
	LambdaScope scope;
	TypeDeclaration typeDeclaration;
	private TypeBinding expectedType;
	private MethodBinding singleMethod;
	
	public LambdaExpression(Argument [] arguments, Statement body) {
		super(0, 0);
		this.arguments = arguments;
		this.body = body;
	}
	
	public void setExpectedType(TypeBinding expectedType) {
		this.expectedType = expectedType;
	}

	public TypeBinding resolveType(BlockScope blockScope) {
		if (this.expectedType == null) {
			this.scope.problemReporter().polyExpressionInIllegalContext(this);
		}
		this.singleMethod = resolveFunctionalMethod(this.arguments != null ? this.arguments.length : 0);
		if (this.scope == null && this.body != null) {
			this.scope = new LambdaScope(blockScope, this);
		}
		if (this.arguments != null) {
			for (int i = 0, length = this.arguments.length; i < length; i++) {
				this.arguments[i].setElidedType(this.singleMethod.parameters[i]);
				this.arguments[i].resolve(this.scope);
//				this.scope.addLocalVariable(this.arguments[i].binding);
			}
		}
		// Must examine poly-type and pick the right one
		
		if (this.body != null) {
			this.body.resolve(this.scope);
		}
		
		return super.resolveType(this.scope);
	}
	
	private MethodBinding resolveFunctionalMethod(int numberOfArguments) {
		if (! (this.expectedType instanceof ReferenceBinding)) {
			this.scope.problemReporter().polyExpressionInIllegalContext(this);
			return null;
		}
		ReferenceBinding referenceBinding = (ReferenceBinding)this.expectedType;
		MethodBinding methods[] = referenceBinding.methods();
		
		if (methods.length != 1) {
			this.scope.problemReporter().polyExpressionInIllegalContext(this);
			return null;
		}
		return methods[0];
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

	public TypeBinding expectedResultType() {
		return this.singleMethod != null ? this.singleMethod.returnType : null; 
	}
}
