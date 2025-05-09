package deppep.jlox;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ExpressionParsingTest {
    @Test
    public void Calculator0Test() {
        String input = "1.0 + 2.0;";
        String expected = "(+ 1.0 2.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator1Test() {
        String input = "1.0 + (2.0 + 3.2);";
        String expected = "(+ 1.0 (group (+ 2.0 3.2)))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator2Test() {
        String input = "1.0 + (2.0 / 3.2);";
        String expected = "(+ 1.0 (group (/ 2.0 3.2)))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator3Test() {
        String input = "1.0 + (2.0 + 3.2) + 5.0;";
        String expected = "(+ (+ 1.0 (group (+ 2.0 3.2))) 5.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator4Test() {
        String input = "1.0 + (2.0 + 3.2) + 5.0 * 4.0;";
        String expected = "(+ (+ 1.0 (group (+ 2.0 3.2))) (* 5.0 4.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator5Test() {
        String input = "--1.0;";
        String expected = "(- (- 1.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void Calculator6Test() {
        String input = "1.0 + --1.0;";
        String expected = "(+ 1.0 (- (- 1.0)))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void CommaOperatorTest() {
        String input = "1.0, 1.0 + 2.0;";
        String expected = "(, 1.0 (+ 1.0 2.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void TernaryTest1() {
        String input = "1.0 ? 2.0 : 3.0;";
        String expected = "(?: 1.0 2.0 3.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void TernaryTest2() {
        String input = "1.0 ? 2.0 ? 3.0 : 4.0 : 5.0;";
        String expected = "(?: 1.0 (?: 2.0 3.0 4.0) 5.0)";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }

    @Test
    public void TernaryTest3() {
        String input = "1.0 ? 2.0 : 3.0 ? 4.0 : 5.0;";
        String expected = "(?: 1.0 2.0 (?: 3.0 4.0 5.0))";
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse();
        Expr expr = ((Stmt.Expression)stmts.getFirst()).expression;
        String actual = new AstPrinter().print(expr);

        assertEquals("strings are not equal", expected, actual);
    }
}
