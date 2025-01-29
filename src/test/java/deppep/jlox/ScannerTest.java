package deppep.jlox;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScannerTest {
    @Test
    public void testBasicExpression() {
        String source = "(hello + world) // a comment";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        TokenType[] expectedTypes = {
            TokenType.LEFT_PAREN,
            TokenType.IDENTIFIER,
            TokenType.PLUS,
            TokenType.IDENTIFIER,
            TokenType.RIGHT_PAREN,
            TokenType.EOF
        };

        String[] expectedLexemes = {
            "(",
            "hello",
            "+",
            "world",
            ")",
            ""
        };

        assertEquals("Number of tokens doesn't match", expectedTypes.length, tokens.size());

        for (int i = 0; i < expectedTypes.length; i++) {
            Token token = tokens.get(i);
            assertEquals("Token type mismatch at position " + i, 
                expectedTypes[i], token.type);
            assertEquals("Lexeme mismatch at position " + i,
                expectedLexemes[i], token.lexeme);
            assertNull("Literal should be null at position " + i, 
                token.literal);
        }
    }
}
