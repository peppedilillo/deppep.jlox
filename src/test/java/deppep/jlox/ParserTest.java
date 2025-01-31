package deppep.jlox;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ParserTest {
    @Test
    public void Calculator0Test() {
        String input = "1.0 + 2.0";
        String expected = "(+ 1.0 2.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator1Test() {
        String input = "1.0 + (2.0 + 3.2)";
        String expected = "(+ 1.0 (group (+ 2.0 3.2)))";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator2Test() {
        String input = "1.0 + (2.0 / 3.2)";
        String expected = "(+ 1.0 (group (/ 2.0 3.2)))";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator3Test() {
        String input = "1.0 + (2.0 + 3.2) + 5.0";
        String expected = "(+ (+ 1.0 (group (+ 2.0 3.2))) 5.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator4Test() {
        String input = "1.0 + (2.0 + 3.2) + 5.0 * 4.0";
        String expected = "(+ (+ 1.0 (group (+ 2.0 3.2))) (* 5.0 4.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator5Test() {
        String input = "--1.0";
        String expected = "(- (- 1.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        Expr expr = new Parser(tokens).parse();
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }
}
