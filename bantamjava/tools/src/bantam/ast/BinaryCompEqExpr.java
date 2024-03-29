/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/

package ast;

import visitor.*;


/** The <tt>BinaryCompEqExpr</tt> class represents equivalence expressions.  It 
  * extends comparison expressions so it contains a lefthand expression and a 
  * righthand expression.  Since this class is similar to other subclasses most of 
  * the functionality can be implemented in the visitor method for the parent class.
  * @see ASTNode
  * @see BinaryCompExpr
  * */
public class BinaryCompEqExpr extends BinaryCompExpr {
    /** BinaryCompEqExpr constructor
      * @param lineNum source line number corresponding to this AST node
      * @param leftExpr left operand expression
      * @param rightExpr right operand expression
      * */
    public BinaryCompEqExpr(int lineNum, Expr leftExpr, Expr rightExpr) { 
	super(lineNum, leftExpr, rightExpr);
    }

    /** Get the operation name
      * @return op name
      * */
    public String getOpName() {
	return "==";
    }

    /** Get the operation type
      * @return op type
      * */
    public String getOpType() {
	return "boolean";
    }

    /** Get the operand type
      * @return operand type
      * */
    public String getOperandType() {
	return null;
    }

    /** Visitor method
      * @param v visitor object
      * @return result of visiting this node
      * @see visitor.Visitor
      * */
    public Object accept(Visitor v) {
	return v.visit(this);
    }
}
