package deppep.jlox;


class Token {
	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line;

	Token(TokenType type, String lexeme, Object literal, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;  // this is for ERROR REPORTING
		// more sophisticated error reporting will also show the column.
		// when noting the column is often better to note an `offset` since
		// file beginning, and the length of the lexeme, because the scanner
		// calculates them anyway and they can be converted back to a line
		// and column.
	}

	public String toString() {
		return type + " " + lexeme + " " + literal;
	}
}
