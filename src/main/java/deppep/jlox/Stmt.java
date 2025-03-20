/**
 * Implements the syntax grammar:
 *     Expression -> Expr expression
 *     Print -> Expr expression
 *     Var -> Token name, Expr initializer
 * automatically generated with `generate_ast.py` on 20/03/25 23:00.
*/
package deppep.jlox;


abstract class Stmt {
    interface Visitor<R> {
        R visitExpressionStmt(Expression expr);
        R visitPrintStmt(Print expr);
        R visitVarStmt(Var expr);
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

    static class Var extends Stmt {
        Var(Token name, Expr initializer) {
            this.name=name;
            this.initializer=initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final Token name;
        final Expr initializer;
    }

    abstract <R> R accept(Visitor<R> visitor);
}

