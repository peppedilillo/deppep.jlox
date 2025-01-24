package deppep.jlox;

import org.junit.Test;
import static org.junit.Assert.*;


public class AstPrinterTest {
    @Test
    public void AstPrinterTest() {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67))
        );

        assertEquals("strings are not equal",
                "(* (- 123) (group 45.67))",
                new AstPrinter().print(expression));
    };
}
