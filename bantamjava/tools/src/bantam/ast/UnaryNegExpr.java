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


/** The <tt>UnaryNegExpr</tt> class represents arithmetic negation ('-') expressions.
  * It extends unary expressions so it contains an expression.  Since this class is 
  * similar to other subclasses most of the functionality can be implemented in 
  * the visitor method for the parent class.
  * @see ASTNode
  * @see Expr
  * */
public class UnaryNegExpr extends UnaryExpr {
    /** UnaryNegExpr constructor
      * @param lineNum source line number corresponding to this AST node
      * @param expr expression for complementing
      * */
    public UnaryNegExpr(int lineNum, Expr expr) {
	super(lineNum, expr);
    }

    /** Get the operation name
      * @return op name
      * */
    public String getOpName() {
	return "-";
    }

    /** Get the operation type
      * @return op type
      * */
    public String getOpType() {
	return "int";
    }

    /** Get the operand type
      * @return operand type
      * */
    public String getOperandType() {
	return "int";
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
