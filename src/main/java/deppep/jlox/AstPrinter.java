package deppep.jlox;

// since this implements the visitors interface, it should
// implement visit methods for all expressions types
class AstPrinter implements Expr.Visitor<String> {
	String print(Expr expr) {
		return expr.accept(this);
	}

	@Override
	public String visitAssignExpr(Expr.Assign expr) {
		return parenthesize(expr.name + "= ", expr.value);
	}

	// challenge 6.2
	@Override
	public String visitTernaryExpr(Expr.Ternary expr) {
		return parenthesize(expr.first.lexeme + expr.second.lexeme, expr.left, expr.middle, expr.right);
	}

	@Override
	public String visitBinaryExpr(Expr.Binary expr) {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitGroupingExpr(Expr.Grouping expr) {
		return parenthesize("group", expr.expression);
	}

	@Override
	public String visitLiteralExpr(Expr.Literal expr) {
		if (expr.value == null) return "nil";
		return expr.value.toString();
	}

	@Override
	public String visitLogicalExpr(Expr.Logical expr) {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitSetExpr(Expr.Set expr) {
		return parenthesize("set " + expr.name.lexeme + " on", expr.object, expr.value);
	}

	@Override
	public String visitThisExpr(Expr.This expr) {
		return "this";
	}

	@Override
	public String visitVariableExpr(Expr.Variable expr) {
		return parenthesize("var", expr);
	}

	@Override
	public String visitCallExpr(Expr.Call expr) {
		return parenthesize("fun " + expr.callee + ":", expr.arguments.toArray(Expr[]::new));
	}

	@Override
	public String visitGetExpr(Expr.Get expr) {
		return parenthesize(expr.object + "." + expr.name );
	}

	@Override
	public String visitUnaryExpr(Expr.Unary expr) {
		return parenthesize(expr.operator.lexeme, expr.right);
	}

	@Override
	public String visitAnonFunctionExpr(Expr.AnonFunction expr) {
		return parenthesize("fun anonymous");
	}

	private String parenthesize(String name, Expr... exprs) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expr expr: exprs) {
			builder.append(" ");
			builder.append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}
}
