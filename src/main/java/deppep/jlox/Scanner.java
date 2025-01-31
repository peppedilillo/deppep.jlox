package deppep.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>(); // j. diamond operator
	private int start = 0;
	private int current = 0;
	private int line = 1;  // we keep line number for error reporting
	// for reporting also columns we would keep note of offset since beginning
	// that's  faster than actually using a variable for column number, at least
	// where we actually need to be fast, i.e. when scanning. it will be slower
	// when reporting errors because we will have to actually skip lines up
	// to line, than count columns up to current.
	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("and", TokenType.AND);
		keywords.put("class", TokenType.CLASS);
		keywords.put("else", TokenType.ELSE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("for", TokenType.FOR);
		keywords.put("fun", TokenType.FUN);
		keywords.put("if", TokenType.IF);
		keywords.put("nil", TokenType.NIL);
		keywords.put("or", TokenType.OR);
		keywords.put("print", TokenType.PRINT);
		keywords.put("return", TokenType.RETURN);
		keywords.put("super", TokenType.SUPER);
		keywords.put("this", TokenType.THIS);
		keywords.put("true", TokenType.TRUE);
		keywords.put("var", TokenType.VAR);
		keywords.put("while", TokenType.WHILE);
	}
	
	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			// at beginning of next lexeme
			start = current;  // this represents where present lexeme starts
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", null, line));
		return tokens;
	}

	private void scanToken() {
		char c = advance();
		
		switch (c) {
		case '(': addToken(TokenType.LEFT_PAREN); break;
		case ')': addToken(TokenType.RIGHT_PAREN); break;
		case '{': addToken(TokenType.LEFT_BRACE); break;
		case '}': addToken(TokenType.RIGHT_BRACE); break;
		case ',': addToken(TokenType.COMMA); break;
		case '.': addToken(TokenType.DOT); break;
		case '-': addToken(TokenType.MINUS); break;
		case '+': addToken(TokenType.PLUS); break;
		case ';': addToken(TokenType.SEMICOLON); break;
		case '*': addToken(TokenType.STAR); break;
			
		case '!':
			addToken(match('=') ? TokenType.BANG_EQUAL: TokenType.BANG);
			break;
	    case '=':
			addToken(match('=') ? TokenType.EQUAL_EQUAL: TokenType.EQUAL);
			break;
		case '<':
			addToken(match('=') ? TokenType.LESS_EQUAL: TokenType.LESS);
			break;
		case '>':
			addToken(match('=') ? TokenType.GREATER_EQUAL: TokenType.GREATER);
			break;
		case '/':
			if (match('/')) {
				// this is a comment, and it will go on until end of line
				while (peek() != '\n' && !isAtEnd()) advance();
				// we do not add any token, so start will reset after this
			} else if (match('*')) {
				commentblock();
			} else {
				addToken(TokenType.SLASH);
			}
			break;
			
		case ' ':
		case '\r':
		case '\t':
			// we simply pass over this whitespaces
			break;
		case '\n':
			// newline is special since we have to update the line counter
			line ++;
			break;
			
		case '"': string(); break;
			
		default:
			if (isDigit(c)) {
				number();
			} else if (isAlpha(c)) {
				identifier();
			} else {
				Lox.error(line, "unexpected character.");
			}
			break;
			// we keep scanning since we want to catch as many errors as possible.
			// `error` will raise the hadError flag so we won't execute bullshit
		}
	}

	private void string() {
		while (peek() != '=' && !isAtEnd()) {
			if (peek() == '\n') line++;
			advance();
		}

		if (isAtEnd()) {
			Lox.error(line, "Unterminated string.");
			return;
		}

		advance(); // for the closing `"`.

		// trim surrounding quotes.
		String value = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, value);
	}

	private void number() {
		while (isDigit(peek())) advance();

		if (peek() == '.' && isDigit(peekNext())) {
			advance();

			while (isDigit(peek())) advance();
		}

		addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
	}

	private void identifier() {
		// this is max munching principle, keep if in doubt and take the chunk
		// of code which matches the most characters!
		while (isAlphaNumeric(peek())) advance();
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if (type == null) type = TokenType.IDENTIFIER;
		addToken(type);
	}

	// challenge 4.4
	private void commentblock() {
		int nested = 1;
		
		while (!isAtEnd() && nested > 0) {
			char c = advance();
			
			switch(c) {
			case '\n':
				line++;
				break;
			case '*':
				if (match('/')) nested--;
				break;
			case '/':
				if (match('*')) nested++;
				break;
			default:
				break;
			}
		}
	}
	
	private boolean match(char expected) {
		// for matching double-char tokens.
		// consumes current character when matching.
		if (isAtEnd()) return false;
		if (source.charAt(current) != expected) return false;

		current++;
		return true;
	}

	private char advance() {
		// advance will consume the character
		return source.charAt(current++);
	}
	
	private char peek() {
		// peek is like advance but does not consume the character
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	private char peekNext() {
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}
	// note that the two functions above could have been easily be superseeded by
	// an arbitrary look ahead function taking an int argument. this is nicer,
	// because it tells to the user that we at max do two look-ahead.
	// the more the look-ahead, the slower the scanner.

	private boolean isDigit(char c) {
		// we make our own because the standard library one will consider digits
		// a lot of funny stuff like devanagari (indian) numbers and others.
		return c >= '0' && c <='9';
	}
	
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			(c >= 'A' && c <= 'Z') ||
			c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = source.substring(start, current);
		tokens.add(new Token(type, text, literal, line));
	}
}
