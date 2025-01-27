/**
 * A recursive descent, abstact syntax tree parser implementing the grammar:
 *     expression    -> equality;
 *     equality      -> comparison (("!=" | "==") comparison)*;
 *     comparison    -> term ((">" | ">=" | "<" | "<=") term)*;
 *     term          -> factor (("-" | "+") factor)*;
 *     factor        -> unary (("/" | "*") unary)*;
 *     unary         -> ("!" | "-") unary
 *                   |  primary;
 *     primary       -> NUMBER | STRING | "true" | "false" | "nil"
 *                   |  "(" expression ")";
*/
package deppep.jlox;

import java.util.List;
import deppep.jlox.TokenType;

class Parser {
	private final List<Token> tokens;
	private int current = 0;

	Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	private Expr expression() {
		return equality();
	}

	private Expr equality() {
		Expr expr = comparison();
		while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
			// note that if we had Expr.Binary(left, operator, expr);
			// the rule would be right associative.
			expr = new Expr.Binary(expr, operator, right);
		}
		// for example consider this:
		// A == B == C == D
		// after the loop we end up with expr being like:
		// Expr.Binary(Expr.Binary(Expr.Binary(A, "==", B), "==", C), "==", D)
		return expr;
	}

	private Expr comparison() {
		// note the code being practically equal to that of `equality`
		// this is because the production rule _is_ formally equal to that!
		Expr expr = term();

		while (match(TokenType.GREATER,
					 TokenType.GREATER_EQUAL,
					 TokenType.LESS,
					 TokenType.LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = factor();

		// match arguments are in order of precedence,
		// first addition, then subtraction
		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = unary();

		while (match(TokenType.SLASH, TokenType.STAR)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Expr.Unary(operator, right);
		}
		
		// note that in this case right recursion implements
		// right associativity. why not using recursion with left-associative
		// rules like the Binarys before? the book says recursion does not
		// play good on the left. my explanation on this is that we can tail
		// recur on the right, but not on the left, hence left recursion may
		// cause stack overflows. however keep in mind this is my idea, i am
		// not sure about the actual reason for this.
		return primary();
	}

	private Expr primary() {
		// the highest level of precedence, or the bottom of the grammar
		if (match(TokenType.FALSE)) return new Expr.Literal(false);
		if (match(TokenType.TRUE)) return new Expr.Literal(true);
		if (match(TokenType.NIL)) return new Expr.Literal(null);
		if (match(TokenType.NUMBER, TokenType.STRING)) return new Expr.Literal(previous().literal);
		if (match(TokenType.LEFT_PAREN)) {
			Expr expr = expression();  // here we go again..
			// if we don't get a ')' we got an error
			consume(TokenType.RIGHT_PAREN, "Expect ')' after expression");
			return new Expr.Grouping(expr);
		}
	}

	private boolean match(TokenType... types) {
		// as with the lexer, will consume the token
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private boolean check(TokenType type) {
		// does not consume.
		if (isAtEnd()) return false;
		return peek().type == type;
	}

	private Token advance() {
		// again, as with lexer, it consumes
		if (!isAtEnd()) current++;
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type == TokenType.EOF;
	}

	private Token peek() {
		// does not consume
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}
	
}
