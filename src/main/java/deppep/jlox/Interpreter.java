package deppep.jlox;

import java.util.List;


// Lox objects are stored in Java's objects. Hence the interpreter return these.
// kinda cool that the difference between expression and statements is higlighted
// well in this declaration: expression returns values (Java's Object in our
// implementation); statements do not return values (Void) but have side-effects.
public class Interpreter implements Expr.Visitor<Object>,
						 // a technique note on the next line. For some obscure reason
						 // Java do not let you return `void` as generic type. You have
						 // to return Void, which is a "boxed void" implementation alike
						 // `Int` is for `int`.
						 Stmt.Visitor<Void> {
	private Environment environment = new Environment();
	
	void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement: statements) {
				execute(statement);
			}
		} catch (RuntimeError error) {
			Lox.runtimeError(error);
		}
	}

	// expr interface
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return evaluate(expr.expression);
	}

	// challenge 6.2: extends
	@Override
	public Object visitTernaryExpr(Expr.Ternary expr) {
		Object left = evaluate(expr.left);
		if (isTruthy(left)) {
			Object middle = evaluate(expr.middle);
			return middle;
		}
		Object right = evaluate(expr.right);
		return right;
	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		// note that we are evaluating left to right.
		// this means that if `left` has a side effect on `right`
		// that will take place! we will also check the type after having
		// evaluated both. this is not trivial, we could have evaluated
		// the first, checked it, then do the same with the second.
		Object left = evaluate(expr.left);
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case TokenType.GREATER:
			checkNumberOperands(expr.operator, left, right);
			return (double)left > (double)right;
		case TokenType.GREATER_EQUAL:
			checkNumberOperands(expr.operator, left, right);
			return (double)left >= (double)right;
		case TokenType.LESS:
			checkNumberOperands(expr.operator, left, right);
			return (double)left < (double)right;
		case TokenType.LESS_EQUAL:
			checkNumberOperands(expr.operator, left, right);
			return (double)left <= (double)right;
		case TokenType.MINUS:
			checkNumberOperands(expr.operator, left, right);
			return (double)left - (double)right;
		case TokenType.PLUS:
			// in Lox the `+` operator is overloaded to support
			// both addition and string concatenation.
			if (left instanceof Double && right instanceof Double) {
				return (double)left + (double)right;
			}

			if (left instanceof String && right instanceof String) {
				return (String)left + (String)right;
			}
		  
			/** challenge 7.2.
			// I complete it but comment because i don't like it
			// implements string concatenation between inhomogenous types
			if (left instanceof String) {
			 	return (String)left + stringify(right);
			}
			
			if (right instanceof String) {
			 	return stringify(left) + (String)right;
			}
			*/
			throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
		case TokenType.SLASH:
			checkNumberOperands(expr.operator, left, right);

			if ((double)right == 0)
				throw new RuntimeError(expr.operator, "Division by zero.");
			return (double)left / (double)right;
		case TokenType.STAR:
			checkNumberOperands(expr.operator, left, right);
			return (double)left * (double)right;
		case TokenType.BANG_EQUAL:
			return !isEqual(left, right);
		case TokenType.EQUAL_EQUAL:
			return isEqual(left, right);
		case TokenType.COMMA:
			return right;
		}

		// unreachable
		return null;
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = evaluate(expr.right);

		switch (expr.operator.type) {
		case TokenType.BANG:
			return !isTruthy(right);
		case TokenType.MINUS:
			checkNumberOperand(expr.operator, right);
			return -(double)right;
		}

		// unreachable
		return null;
	}

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		return environment.get(expr.name);
	}

	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (left instanceof Double && right instanceof Double) return;
		throw new RuntimeError(operator, "Operands must be numbers.");
	}
	
	// here we decide what truth value is given to a certain object
	// type. Lox follows ruby in this: null and false are "falsey", they
	// evaluate to false. all the other are "truthy", they evaluate to true.
	private boolean isTruthy(Object object) {
		if (object == null) return false;
		if (object instanceof Boolean) return (boolean)object;
		return true;
	}

	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null) return false;

		return a.equals(b);
	}

	private String stringify(Object object) {
		if (object == null) return "nil";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
					text = text.substring(0, text.length() - 2);
				}
			return text;
		}

		return object.toString();
	}

	private Object evaluate(Expr expr){
		return expr.accept(this);
	}


	// Stmt interface
	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		evaluate(stmt.expression);
		// note we return null. This is requrired by `Void`.
		// see note at top.
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = evaluate(stmt.initializer);
		}

		// if no initializer, a variable is set to null
		environment.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		Object value = evaluate(expr.value);
		environment.assign(expr.name, value);
		return value;
	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}
}
