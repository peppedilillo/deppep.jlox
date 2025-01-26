package deppep.jlox;

// chapter 5, challenge 3: define a visitor class for our syntax tree classes
// that takes an expression, converts it to RPN and returns the resulting string
class RpnPrinter implements Expr.Visitor<String> {
    String print(Expr expr) { return expr.accept(this); }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return reverse(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return reverse("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return reverse(expr.operator.lexeme, expr.right);
    }

    private String reverse(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        for (Expr expr: exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        builder.append(name);

        return builder.toString();
    }
}
