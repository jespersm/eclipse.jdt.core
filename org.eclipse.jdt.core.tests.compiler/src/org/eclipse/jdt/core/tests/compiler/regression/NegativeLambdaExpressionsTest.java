/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
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
package org.eclipse.jdt.core.tests.compiler.regression;

import junit.framework.Test;
public class NegativeLambdaExpressionsTest extends AbstractRegressionTest {

static {
//	TESTS_NAMES = new String[] { "test380112e"};
//	TESTS_NUMBERS = new int[] { 50 };
//	TESTS_RANGE = new int[] { 11, -1 };
}
public NegativeLambdaExpressionsTest(String name) {
	super(name);
}
public static Test suite() {
	return buildMinimalComplianceTestSuite(testClass(), F_1_8);
}

// https://bugs.eclipse.org/bugs/show_bug.cgi?id=382818, ArrayStoreException while compiling lambda
public void test001() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface I {\n" +
				"  void foo(int x, int y);\n" +
				"}\n" +
				"public class X {\n" +
				"  public static void main(String[] args) {\n" +
				"    int x, y;\n" +
				"    I i = () -> {\n" +
				"      int z = 10;\n" +
				"    };\n" +
				"    i++;\n" +
				"  }\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 10)\n" + 
			"	i++;\n" + 
			"	^^^\n" + 
			"Type mismatch: cannot convert from I to int\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=382841, ClassCastException while compiling lambda
public void test002() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface I {\n" +
				" void foo(int x, int y);\n" +
				"}\n" +
				"public class X {\n" +
				"  public static void main(String[] args) {\n" +
				"    int x, y;\n" +
				"    I i = (p, q) -> {\n" +
				"      int r = 10;\n" +
				"    };\n" +
				"    i++;\n" +
				"  }\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 10)\n" + 
			"	i++;\n" + 
			"	^^^\n" + 
			"Type mismatch: cannot convert from I to int\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=382841, ClassCastException while compiling lambda
public void test003() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface I {\n" +
				" void foo(int x, int y);\n" +
				"}\n" +
				"public class X {\n" +
				"  public static void main(String[] args) {\n" +
				"    int x, y;\n" +
				"    I i = null, i2 = (p, q) -> {\n" +
				"      int r = 10;\n" +
				"    }, i3 = null;\n" +
				"    i++;\n" +
				"  }\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 10)\n" + 
			"	i++;\n" + 
			"	^^^\n" + 
			"Type mismatch: cannot convert from I to int\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383046, syntax error reported incorrectly on syntactically valid lambda expression
public void test004() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface IX {\n" +
				"    public int foo();\n" +
				"}\n" +
				"public class X {\n" +
				"     IX i = () -> 42;\n" +
				"     int x\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	int x\n" + 
			"	    ^\n" + 
			"Syntax error, insert \";\" to complete FieldDeclaration\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383085 super::identifier not accepted.
public void test005() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface IX{\n" +
				"	public void foo();\n" +
				"}\n" +
				"public class X {\n" +
				"	IX i = super::toString;\n" +
				"   Zork z;\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	Zork z;\n" + 
			"	^^^^\n" + 
			"Zork cannot be resolved to a type\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383046, syntax error reported incorrectly on *syntactically* valid reference expression
public void test006() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"interface One{}\n" +
				"interface Two{}\n" +
				"interface Three{}\n" +
				"interface Four{}\n" +
				"interface Five{}\n" +
				"interface Blah{}\n" +
				"interface Outer<T1,T2>{interface Inner<T3,T4>{interface Leaf{ <T> void method(); } } }\n" +
				"interface IX{\n" +
				"	public void foo();\n" +
				"}\n" +
				"public class X {\n" +
				"	IX i = Outer<One, Two>.Inner<Three, Four>.Deeper<Five, Six<String>>.Leaf::<Blah, Blah>method;\n" +
				"   int x\n" +
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 13)\n" + 
			"	int x\n" + 
			"	    ^\n" + 
			"Syntax error, insert \";\" to complete FieldDeclaration\n" + 
			"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383096, NullPointerException with a wrong lambda code snippet
public void test007() {
	this.runNegativeTest(
			new String[] {
					"X.java",
					"interface I { int a(int a); }\n" +
					"public class X {\n" +
					"    void foo() {\n" +
					"            I t1 = f -> {{};\n" +
					"            I t2 = (x) -> 42;\n" +
					"        } \n" +
					"        }\n" +
					"}\n",
				},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	} \n" + 
			"	^\n" + 
			"Syntax error, insert \";\" to complete BlockStatements\n" + 
			"----------\n" /* expected compiler log */,
			true /* perform statement recovery */);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383949,  Explicit this parameter illegal in lambda expressions
public void test008() {
	this.runNegativeTest(
			new String[] {
					"X.java",
					"interface I {\n" +
					"  int foo(X x);\n" +
					"}\n" +
					"public class X {\n" +
					"  public static void main(String[] args) {\n" +
					"    I i = (X this) -> 10;  \n" +
					"  }\n" +
					"}\n",
				},
				"----------\n" + 
				"1. ERROR in X.java (at line 6)\n" + 
				"	I i = (X this) -> 10;  \n" + 
				"	         ^^^^\n" + 
				"Lambda expressions cannot declare a this parameter\n" + 
				"----------\n");
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=383949,  Explicit this parameter illegal in lambda expressions
public void test009() {
	this.runNegativeTest(
			new String[] {
					"X.java",
					"import java.awt.event.ActionListener;\n" +
					"interface I {\n" +
					"    void doit(String s1, String s2);\n" +
					"}\n" +
					"public class X {\n" +
					"  public void test1(int x) {\n" +
					"    ActionListener al = (public xyz) -> System.out.println(xyz); \n" +
					"    I f = (abstract final s, @Nullable t) -> System.out.println(s + t); \n" +
					"  }\n" +
					"}\n",
				},
				"----------\n" + 
				"1. ERROR in X.java (at line 7)\n" + 
				"	ActionListener al = (public xyz) -> System.out.println(xyz); \n" + 
				"	                            ^^^\n" + 
				"Syntax error, modifiers and annotations are not allowed for the lambda parameter xyz as its type is elided\n" + 
				"----------\n" + 
				"2. ERROR in X.java (at line 8)\n" + 
				"	I f = (abstract final s, @Nullable t) -> System.out.println(s + t); \n" + 
				"	                      ^\n" + 
				"Syntax error, modifiers and annotations are not allowed for the lambda parameter s as its type is elided\n" + 
				"----------\n" + 
				"3. ERROR in X.java (at line 8)\n" + 
				"	I f = (abstract final s, @Nullable t) -> System.out.println(s + t); \n" + 
				"	                                   ^\n" + 
				"Syntax error, modifiers and annotations are not allowed for the lambda parameter t as its type is elided\n" + 
				"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=381121,  [] should be accepted in reference expressions.
public void test010A() {
	this.runNegativeTest(
			new String[] {
					"X.java",
					"interface I {\n" +
					"	Object foo(int [] ia);\n" +
					"}\n" +
					"public class X {\n" +
					"	I i = (int [] ia) -> {\n" +
					"		      return ia.clone();\n" +
					"	      };\n" +
					"	I i2 = int[]::clone;\n" +
					"	Zork z;\n" +
					"}\n",
				},
				"----------\n" + 
				"1. ERROR in X.java (at line 9)\n" + 
				"	Zork z;\n" + 
				"	^^^^\n" + 
				"Zork cannot be resolved to a type\n" + 
				"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=381121,  [] should be accepted in reference expressions.
public void test010B() {
	this.runNegativeTest(
			new String[] {
					"X.java",
					"interface I {\n" +
					"	Object foo(int [] ia);\n" +
					"}\n" +
					"public class X {\n" +
					"	I i = (int [] ia) -> ia.clone();\n" +
					"	I i2 = int[]::clone;\n" +
					"	Zork z;\n" +
					"}\n",
				},
				"----------\n" + 
				"1. ERROR in X.java (at line 7)\n" + 
				"	Zork z;\n" + 
				"	^^^^\n" + 
				"Zork cannot be resolved to a type\n" + 
				"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=382727,  Lambda expression parameters and locals cannot shadow variables from context
public void test011() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  void foo(int x, int y);\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    int x, y;\r\n" + 
			"    I i = (x, y) -> { // Error: x,y being redeclared\r\n" + 
			"      int args = 10; //  Error args is being redeclared\r\n" + 
			"    };\r\n" + 
			"  }\r\n" + 
			"}"}, 
			"----------\n" +
			"1. ERROR in X.java (at line 7)\r\n" + 
			"	I i = (x, y) -> { // Error: x,y being redeclared\r\n" + 
			"	       ^\r\n" + 
			"Duplicate local variable x\r\n" + 
			"----------\r\n" + 
			"2. ERROR in X.java (at line 7)\r\n" + 
			"	I i = (x, y) -> { // Error: x,y being redeclared\r\n" + 
			"	          ^\r\n" + 
			"Duplicate local variable y\r\n" + 
			"----------\r\n" + 
			"3. ERROR in X.java (at line 8)\r\n" + 
			"	int args = 10; //  Error args is being redeclared\r\n" + 
			"	    ^^^^\r\n" + 
			"Duplicate local variable args\r\n" + 
			"" +
			"----------\n");
				
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=382701,  Implement semantic analysis of Lambda expressions & Reference expressions
public void test012() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  int foo(int x, int y);\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    int x = 2;\r\n" + 
			"    I i = (a, b) -> {\r\n" + 
			"      return 42.0 + a + args.length; // Type mismatch: cannot convert from double to int\r\n" + 
			"    };\r\n" + 
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 8)\n" + 
			"	return 42.0 + a + args.length; // Type mismatch: cannot convert from double to int\n" + 
			"	       ^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"Type mismatch: cannot convert from double to int\n" + 
			"----------\n");
				
}


// https://bugs.eclipse.org/bugs/show_bug.cgi?id=384687 [1.8] Wildcard type arguments should be rejected for lambda and reference expressions
public void test013A() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"class Action<K> {\r\n" + 
			"  static <T1> int fooMethod(Object x) { return 0; }\r\n" + 
			"}\r\n" + 
			"interface I {\r\n" + 
			"  int foo(Object x);\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    I functional = Action::<?>fooMethod;\r\n" + 
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 9)\n" + 
			"	I functional = Action::<?>fooMethod;\n" + 
			"	                        ^\n" + 
			"Wildcard is not allowed at this location\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=384687 [1.8] Wildcard type arguments should be rejected for lambda and reference expressions
public void test013B() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"class Action<K> {\r\n" + 
			"  int foo(Object x, Object y, Object z) { return 0; }\r\n" + 
			"}\r\n" + 
			"interface I {\r\n" + 
			"  void foo(Object x);\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    Action<Object> exp = new Action<Object>();\r\n" + 
			"    int x,y,z;\r\n" + 
			"    I len6 = foo->exp.<?>method(x, y, z);\r\n" + 
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 11)\n" + 
			"	I len6 = foo->exp.<?>method(x, y, z);\n" + 
			"	                   ^\n" + 
			"Wildcard is not allowed at this location\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=382702 - [1.8][compiler] Lambda expressions should be rejected in disallowed contexts
public void test014() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  int foo1(String x);\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" +
			"    System.out.println(\"Lambda in illegal context: \" + (() -> \"Illegal Lambda\"));\r\n" +
			"    System.out.println(\"Method Reference in illegal context: \" + System::exit);\r\n" +
			"    System.out.println(\"Constructor Reference in illegal context: \" + String::new);\r\n" +
			"    I sam1 = (x) -> x.length(); // OK\r\n" +
			"    I sam2 = ((String::length)); // OK\r\n" +
			"    I sam3 = (Math.random() > 0.5) ? String::length : String::hashCode; // OK\r\n" +
			"    I sam4 = (I)(String::length); // OK\r\n" +
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	System.out.println(\"Lambda in illegal context: \" + (() -> \"Illegal Lambda\"));\n" + 
			"	                                                   ^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"Functional expressions may not be used here (only allowed in assignments, casts, and as parameters)\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 7)\n" + 
			"	System.out.println(\"Method Reference in illegal context: \" + System::exit);\n" + 
			"	                                                             ^^^^^^^^^^^^\n" + 
			"Functional expressions may not be used here (only allowed in assignments, casts, and as parameters)\n" + 
			"----------\n" + 
			"3. ERROR in X.java (at line 8)\n" + 
			"	System.out.println(\"Constructor Reference in illegal context: \" + String::new);\n" + 
			"	                                                                  ^^^^^^^^^^^^\n" + 
			"Functional expressions may not be used here (only allowed in assignments, casts, and as parameters)\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=398734 - [1.8][compiler] Lambda expression type or return type should be checked against the target functional interface method's result type
public void test015A() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  String foo();\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    I i = () -> 42;\r\n" + 
			"    I i2 = () -> \"Hello, Lambda\";\r\n" + 
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	I i = () -> 42;\n" + 
			"	            ^^\n" + 
			"Type mismatch: cannot convert from int to String\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=398734 - [1.8][compiler] Lambda expression type or return type should be checked against the target functional interface method's result type
public void test015B() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  String foo();\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\r\n" + 
			"    I i = () -> {\r\n" +
			"      return 42;\r\n" +
			"    };\r\n" + 
			"    I i2 = () -> {\r\n" +
			"      return \"Hello, Lambda as a block!\";\r\n" +
			"    };\r\n" + 
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 7)\n" + 
			"	return 42;\n" + 
			"	       ^^\n" + 
			"Type mismatch: cannot convert from int to String\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=398734 - [1.8][compiler] Lambda expression type or return type should be checked against the target functional interface method's result type
public void test015C() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"public class X {\r\n" + 
			"  int data = 0;\r\n" +
			"  public void makeLambdas() {\r\n" + 
			"    Runnable r1 = () -> System.out.println(\"side effect\");  // OK\r\n" +
			"    Runnable r2 = () -> data = 42; // OK, side effect\r\n" +
			"    Runnable r3 = () -> data++; // OK, side effect\r\n" +
			"    Runnable r4 = () -> ++data; // OK, side effect\r\n" +
			"    Runnable r5 = () -> data += 3; // OK, side effect\r\n" +
			"    Runnable r6 = () -> new X(); // may have side effects\r\n" +
			"    Runnable r7 = () -> new X(); // may have side effects\r\n" +
			"    Runnable r8 = () -> \"Dead\";  // Dead: Literal\r\n" +
			"    Runnable r9 = () -> 2 + 2;  // Dead: No side effects\r\n" +
			"    Runnable r10 = () -> data; // Dead: Just a field reference\r\n" +
			"  }\r\n" + 
			"}"},
			"----------\n" + 
			"1. WARNING in X.java (at line 11)\n" + 
			"	Runnable r8 = () -> \"Dead\";  // Dead: Literal\n" + 
			"	                    ^^^^^^\n" + 
			"Lambda expression has no effect and returns void\n" + 
			"----------\n" + 
			"2. WARNING in X.java (at line 12)\n" + 
			"	Runnable r9 = () -> 2 + 2;  // Dead: No side effects\n" + 
			"	                    ^^^^^\n" + 
			"Lambda expression has no effect and returns void\n" + 
			"----------\n" + 
			"3. WARNING in X.java (at line 13)\n" + 
			"	Runnable r10 = () -> data; // Dead: Just a field reference\n" + 
			"	                     ^^^^\n" + 
			"Lambda expression has no effect and returns void\n" + 
			"----------\n");
}

//https://bugs.eclipse.org/bugs/show_bug.cgi?id=398734 - [1.8][compiler] Lambda expression type or return type should be checked against the target functional interface method's result type
public void test015D() {
	this.runNegativeTest(
			new String[] {
			"X.java",
			"interface I {\r\n" + 
			"  int baz();\r\n" + 
			"}\r\n" + 
			"public class X {\r\n" + 
			"  public static void main(String[] args) {\n" + 
			"    I i1 = () -> {\n" + 
			"      System.out.println(\"No return\");\n" + 
			"    }; // Error: Lambda block should return value\n" + 
			"\n" + 
			"    I i2 = () -> {\n" + 
			"      if (Math.random() < 0.5) return 42;\n" + 
			"    }; // Error: Lambda block doesn't always return a value\n" + 
			"    I i3 = () -> {\n" + 
			"      return 42;\n" + 
			"      System.out.println(\"Dead!\");\n" + 
			"    }; // Error: Lambda block has dead code\n" + 
			"  }\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	I i1 = () -> {\n" + 
			"      System.out.println(\"No return\");\n" + 
			"    }; // Error: Lambda block should return value\n" + 
			"	       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"This method must return a result of type int\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 10)\n" + 
			"	I i2 = () -> {\n" + 
			"      if (Math.random() < 0.5) return 42;\n" + 
			"    }; // Error: Lambda block doesn\'t always return a value\n" + 
			"	       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"This method must return a result of type int\n" + 
			"----------\n" + 
			"3. ERROR in X.java (at line 15)\n" + 
			"	System.out.println(\"Dead!\");\n" + 
			"	^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n" + 
			"Unreachable code\n" + 
			"----------\n");
}

public static Class testClass() {
	return NegativeLambdaExpressionsTest.class;
}
}