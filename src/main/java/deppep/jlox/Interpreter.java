package deppep.jlox;

import java.util.ArrayList;
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
	final Environment globals = new Environment();
	private Environment environment = globals;

	Interpreter() {
		// this is an example of a native function
		globals.define("clock", new LoxCallable() {  // crazy java syntax: the value is an anonymous class
			@Override
			public int arity() {
				return 0;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return (double)System.currentTimeMillis() / 1000.0;
			}
		});
	}

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
	public Object visitLogicalExpr(Expr.Logical expr) {
		Object left = evaluate(expr.left);

		// short-circuiting logic. as few as possible evaluations are made and
		// we return as soon as possible.
		if (expr.operator.type == TokenType.OR) {
			if (isTruthy(left)) return left;
		} else {
			if (!isTruthy(left)) return left;
		}

		return evaluate(expr.right);
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

			// challenge 7.2
			if (left instanceof String) {
			 	return (String)left + stringify(right);
			}
			if (right instanceof String) {
			 	return stringify(left) + (String)right;
			}

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
	public Object visitCallExpr(Expr.Call expr) {
		Object callee = evaluate(expr.callee);

		List <Object> arguments = new ArrayList<>();
		// another subtletly here: if argument evaluation has side effect on
		// the argument yet to be parsed, the order arguments are evaluated could
		// be visible to the user.
		for (Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}

		if (!(callee instanceof LoxCallable)) {
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");
		}
		LoxCallable function = (LoxCallable)callee;
		// checks function's arity against number of arguments actually passed
		if (arguments.size() != function.arity()) {
			throw new RuntimeError(expr.paren,
					"Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
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
		Object var = environment.get(expr.name);

		// challenge 8.2: raise a runtime error when an uninitialzed variable is accessed
		if (var == null)
			throw new RuntimeError(expr.name,
					"Uninitialized variable '" + expr.name.lexeme + "'.");
		return var;
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
	public Void visitFunctionStmt(Stmt.Function stmt) {
		LoxFunction function = new LoxFunction(stmt);
		environment.define(stmt.name.lexeme, function);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		if (isTruthy(evaluate(stmt.condition))) {
			execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = evaluate(stmt.expression);
		System.out.println(stringify(value));
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if (stmt.value != null) {
			value = evaluate(stmt.value);
		}

		throw new ReturnException(value);
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
	public Void visitWhileStmt(Stmt.While stmt) {
		while (isTruthy(evaluate(stmt.condition))) {
			try {
				execute(stmt.body);
			} catch (BreakException _) {
				break;
			}
		}
		return null;
	}

	// challenge 9.3
	@Override
	public Void visitBreakStmt(Stmt.Break stmt) {
		throw new BreakException();
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

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		executeBlock(stmt.statements, new Environment(environment));
		return null;
	}

	void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;
		try {
			this.environment = environment;

			for (Stmt statement : statements) {
				execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}
}
