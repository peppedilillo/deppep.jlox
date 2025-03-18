package deppep.jlox;

import org.junit.Test;


import static org.junit.Assert.*;

public class InterpreterTest {
    @Test
    public void InterpreterBinaryTest() {
        Interpreter interpreter = new Interpreter();

        Expr.Binary expression = new Expr.Binary(
                new Expr.Literal(6.0),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Literal(111.0)
        );
        Double result = (Double) interpreter.visitBinaryExpr(expression);
        assertEquals(666., result, 0.001);
    }

    @Test
    public void InterpreterTernaryTest() {
        Interpreter interpreter = new Interpreter();

        Expr.Ternary expression = new Expr.Ternary(
                new Expr.Binary(
                        new Expr.Literal(12.0),
                        new Token(TokenType.GREATER, ">", null, 1),
                        new Expr.Literal(2.0)
                ),
                new Token(TokenType.QUESTION, "?", null, 1),
                new Expr.Literal(5.0),
                new Token(TokenType.COLON, ":", null, 1),
                new Expr.Literal(10.0)
        );
        Double result = (Double) interpreter.visitTernaryExpr(expression);
        assertEquals(5., result, 0.001);
    }
}
