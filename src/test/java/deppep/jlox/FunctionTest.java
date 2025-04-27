package deppep.jlox;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class FunctionTest {

    @Test
    public void fibonacciTest() {
        String source =
                "fun fib(n) {" +
                        "  if (n <= 1) return n;" +
                        "  return fib(n - 2) + fib(n - 1);" +
                        "}" +
                        "fib(6);";

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Interpreter interpreter = new Interpreter();
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        // reset error flags which we will check later.
        Lox.hadError = false;
        Lox.hadRuntimeError = false;

        assertTrue("First statement should be a function definition", statements.get(0) instanceof Stmt.Function);
        interpreter.execute(statements.get(0));

        assertFalse("Parsing error occurred", Lox.hadError);
        assertFalse("Runtime error during definition", Lox.hadRuntimeError);

        assertTrue("Second statement should be an expression statement", statements.get(1) instanceof Stmt.Expression);
        Stmt.Expression exprStmt = (Stmt.Expression) statements.get(1);
        assertTrue("Expression should be a function call", exprStmt.expression instanceof Expr.Call); // [cite: 52]

        Object result = null;
        try {
            result = interpreter.evaluate(exprStmt.expression);
        } catch (RuntimeError e) {
            fail("Runtime error during function evaluation: " + e.getMessage());
        }

        assertFalse("Runtime error during evaluation", Lox.hadRuntimeError);

        assertNotNull("Result should not be null", result);
        assertTrue("Result should be a Double", result instanceof Double);
        assertEquals("Fibonacci(6) should return 8.0", 8.0, (Double) result, 0.001);
    }
}