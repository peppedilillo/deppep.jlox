package deppep.jlox;

import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;


public class BlockCommentTest {
    @Test
    public void testCommentBlocks() throws IOException {
        // just a comment on this bullshit because java docs are UTTERLY obscure.
        // what happens here when I run `mvn test` is that maven copies all resources and
        // classes to the build directory `target`, and add target to the PATH.
        // then java looks into the path and finds the file maven just copied.
        String source;
        try (InputStream inputStream = BlockCommentTest.class.getResourceAsStream("/comment_blocks.lox")) {
            if (inputStream == null) fail("Could not find test resource: /comment_blocks.lox");
            source = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Define expected tokens
        Object[][] expected = {
                {TokenType.IDENTIFIER, "n", null},
                {TokenType.EQUAL, "=", null},
                {TokenType.NUMBER, "10", 10.0},
                {TokenType.IDENTIFIER, "m", null},
                {TokenType.EQUAL, "=", null},
                {TokenType.IDENTIFIER, "n", null},
                {TokenType.PLUS, "+", null},
                {TokenType.NUMBER, "2", 2.0},
                {TokenType.IDENTIFIER, "res", null},
                {TokenType.EQUAL, "=", null},
                {TokenType.IDENTIFIER, "n", null},
                {TokenType.PLUS, "+", null},
                {TokenType.IDENTIFIER, "m", null},
                {TokenType.IDENTIFIER, "res", null},
                {TokenType.EQUAL_EQUAL, "==", null},
                {TokenType.NUMBER, "22", 22.0},
                {TokenType.EOF, "", null}
        };

        assertEquals("Number of tokens doesn't match",
                expected.length, tokens.size());

        for (int i = 0; i < expected.length; i++) {
            Token token = tokens.get(i);
            TokenType expectedType = (TokenType) expected[i][0];
            String expectedLexeme = (String) expected[i][1];
            Object expectedLiteral = expected[i][2];

            assertEquals("Token type mismatch at position " + i,
                    expectedType, token.type);
            assertEquals("Lexeme mismatch at position " + i,
                    expectedLexeme, token.lexeme);
            assertEquals("Literal mismatch at position " + i,
                    expectedLiteral, token.literal);
        }
    }
}