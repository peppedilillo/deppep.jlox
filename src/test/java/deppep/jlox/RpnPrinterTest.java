package deppep.jlox;

import org.junit.Test;
import static org.junit.Assert.*;


public class RpnPrinterTest {
    @Test
    public void RpnPrinterTest() {
        Expr expression = new Expr.Binary(
                new Expr.Binary(
                        new Expr.Literal(1),
                        new Token(TokenType.PLUS, "+", null, 1),
                        new Expr.Literal(2)
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Binary(
                        new Expr.Literal(4),
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(3)
                )
        );

        assertEquals("strings are not equal",
                "1 2 + 4 3 - *",
                new RpnPrinter().print(expression));
    };
}
