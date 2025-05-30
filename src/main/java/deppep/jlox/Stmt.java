/**
 * Implements the syntax grammar:
 *     Block -> List<Stmt> statements;
 *     Class -> Token name, Expr.Variable superclass, List<Stmt.Function> methods;
 *     Expression -> Expr expression;
 *     Function -> Token name, Expr.AnonFunction definition;
 *     If -> Expr condition, Stmt thenBranch, Stmt elseBranch;
 *     Print -> Expr expression;
 *     Return -> Token keyword, Expr value;
 *     While -> Expr condition, Stmt body;
 *     Break -> Token keyword;
 *     Var -> Token name, Expr initializer;
 * automatically generated with `generate_ast.py` on 26/05/25 20:29.
*/
package deppep.jlox;

import java.util.List;


abstract class Stmt {
    interface Visitor<R> {
        R visitBlockStmt(Block expr);
        R visitClassStmt(Class expr);
        R visitExpressionStmt(Expression expr);
        R visitFunctionStmt(Function expr);
        R visitIfStmt(If expr);
        R visitPrintStmt(Print expr);
        R visitReturnStmt(Return expr);
        R visitWhileStmt(While expr);
        R visitBreakStmt(Break expr);
        R visitVarStmt(Var expr);
    }

    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements=statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;
    }

    static class Class extends Stmt {
        Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
            this.name=name;
            this.superclass=superclass;
            this.methods=methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token name;
        final Expr.Variable superclass;
        final List<Stmt.Function> methods;
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

    static class Function extends Stmt {
        Function(Token name, Expr.AnonFunction definition) {
            this.name=name;
            this.definition=definition;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name;
        final Expr.AnonFunction definition;
    }

    static class If extends Stmt {
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition=condition;
            this.thenBranch=thenBranch;
            this.elseBranch=elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
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

    static class Return extends Stmt {
        Return(Token keyword, Expr value) {
            this.keyword=keyword;
            this.value=value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final Token keyword;
        final Expr value;
    }

    static class While extends Stmt {
        While(Expr condition, Stmt body) {
            this.condition=condition;
            this.body=body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Stmt body;
    }

    static class Break extends Stmt {
        Break(Token keyword) {
            this.keyword=keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }

        final Token keyword;
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

