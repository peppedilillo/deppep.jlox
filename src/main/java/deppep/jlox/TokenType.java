package deppep.jlox;


enum TokenType {
	// single char tokens
	LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
	COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
	COLON, QUESTION,

	// one or two character tokens
	BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER,
	GREATER_EQUAL, LESS, LESS_EQUAL,

	// literals
	IDENTIFIER, STRING, NUMBER,

	// keywords
	AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
	PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,
	BREAK,  // challenge 9.3

	EOF
}
