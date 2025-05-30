/**
 * Implements the syntax grammar:
 *     Assign -> Token name, Expr value;
 *     Ternary -> Expr left, Token first, Expr middle, Token second, Expr right;
 *     Binary -> Expr left, Token operator, Expr right;
 *     Set -> Expr object, Token name, Expr value;
 *     Super -> Token keyword, Token method;
 *     This -> Token keyword;
 *     Unary -> Token operator, Expr right;
 *     Call -> Expr callee, Token paren, List<Expr> arguments;
 *     Get -> Expr object, Token name;
 *     AnonFunction -> List<Token> params, List<Stmt> body;
 *     Grouping -> Expr expression;
 *     Literal -> Object value;
 *     Logical -> Expr left, Token operator, Expr right;
 *     Variable -> Token name;
 * automatically generated with `generate_ast.py` on 28/05/25 17:53.
*/
package deppep.jlox;

import java.util.List;


abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitTernaryExpr(Ternary expr);
        R visitBinaryExpr(Binary expr);
        R visitSetExpr(Set expr);
        R visitSuperExpr(Super expr);
        R visitThisExpr(This expr);
        R visitUnaryExpr(Unary expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);
        R visitAnonFunctionExpr(AnonFunction expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitVariableExpr(Variable expr);
    }

    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name=name;
            this.value=value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }

    static class Ternary extends Expr {
        Ternary(Expr left, Token first, Expr middle, Token second, Expr right) {
            this.left=left;
            this.first=first;
            this.middle=middle;
            this.second=second;
            this.right=right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTernaryExpr(this);
        }

        final Expr left;
        final Token first;
        final Expr middle;
        final Token second;
        final Expr right;
    }

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left=left;
            this.operator=operator;
            this.right=right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Set extends Expr {
        Set(Expr object, Token name, Expr value) {
            this.object=object;
            this.name=name;
            this.value=value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object;
        final Token name;
        final Expr value;
    }

    static class Super extends Expr {
        Super(Token keyword, Token method) {
            this.keyword=keyword;
            this.method=method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Token keyword;
        final Token method;
    }

    static class This extends Expr {
        This(Token keyword) {
            this.keyword=keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final Token keyword;
    }

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator=operator;
            this.right=right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee=callee;
            this.paren=paren;
            this.arguments=arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;
        final Token paren;
        final List<Expr> arguments;
    }

    static class Get extends Expr {
        Get(Expr object, Token name) {
            this.object=object;
            this.name=name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object;
        final Token name;
    }

    static class AnonFunction extends Expr {
        AnonFunction(List<Token> params, List<Stmt> body) {
            this.params=params;
            this.body=body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAnonFunctionExpr(this);
        }

        final List<Token> params;
        final List<Stmt> body;
    }

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression=expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value=value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left=left;
            this.operator=operator;
            this.right=right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Variable extends Expr {
        Variable(Token name) {
            this.name=name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }

    abstract <R> R accept(Visitor<R> visitor);
}

