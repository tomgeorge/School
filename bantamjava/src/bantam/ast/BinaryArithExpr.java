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


/** The abstract <tt>BinaryArithExpr</tt> class represents arithmetic expressions.
  * It can be either plus ('+'), minus ('-'), times ('*'), divide ('/'), or 
  * modulus ('%').  It extends BinaryExpr and contains a lefthand expression and a 
  * righthand expression.  Most of the functionality can be implemented within
  * the visitor method for BinaryExpr, however, some functionality may need to 
  * be implemented in the visitor method for each subclass.
  * @see ASTNode
  * @see BinaryExpr
  * */
public abstract class BinaryArithExpr extends BinaryExpr {
    /** BinaryArithExpr constructor
      * @param lineNum source line number corresponding to this AST node
      * @param leftExpr left operand expression
      * @param rightExpr right operand expression
      * */
    public BinaryArithExpr(int lineNum, Expr leftExpr, Expr rightExpr) { 
	super(lineNum, leftExpr, rightExpr);
    }

    /** Visitor method
      * @param v visitor object
      * @return result of visiting this node
      * @see visitor.Visitor
      * */
    abstract public Object accept(Visitor v);
}
