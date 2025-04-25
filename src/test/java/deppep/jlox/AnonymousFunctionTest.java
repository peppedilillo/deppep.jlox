package deppep.jlox;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;


public class AnonymousFunctionTest {
    @Test
    public void AnonymousFunctionExpressionStatementTest() {
        String input = "fun() {};"; //
        List<Token> tokens = new Scanner(input).scanTokens();
        List<Stmt> stmts = new Parser(tokens).parse(); // [cite: 175, 221, 224]

        // Expect exactly one statement [cite: 221]
        assertEquals("Expected one statement", 1, stmts.size());
        // Expect that statement to be an ExpressionStatement [cite: 355, 361, 367]
        assertTrue("Statement should be an ExpressionStatement", stmts.get(0) instanceof Stmt.Expression);

        Stmt.Expression exprStmt = (Stmt.Expression) stmts.get(0);
        // Expect the inner expression to be an AnonFunction [cite: 53, 57, 75, 301]
        assertTrue("Expression within statement should be an AnonFunction", exprStmt.expression instanceof Expr.AnonFunction);
    }
}
