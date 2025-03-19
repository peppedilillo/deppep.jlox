/**
 * Implements the syntax grammar:
 *     Expression -> Expr expression
 *     Print -> Expr expression
 * automatically generated with `generate_ast.py` on 19/03/25 22:11.
*/
package deppep.jlox;


abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression expr);
        R visitPrintStmt(Print expr);
    }

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression=expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }

    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression=expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final Expr expression;
    }

    abstract <R> R accept(Visitor<R> visitor);
}

