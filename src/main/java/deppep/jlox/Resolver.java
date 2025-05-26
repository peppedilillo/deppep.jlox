package deppep.jlox;

import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.Map;


class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    //  why boolean? we will set values to `false` when a variable is only declared,
    // and set it to true when the variables is defined.
    private final Stack<Map<String, VarInfo>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    // challenge 9.3
    private LoopType currentLoop = LoopType.NONE;

    // challenge 11.3
    // rather than storing a single value, we are storing a record, transforming our
    // variable map in a sort of table
    private record VarInfo(Token keyword, boolean initialized, boolean accessed) {}

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER
    }

    private enum LoopType {
        NONE,
        LOOP
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        declare(stmt.name);
        define(stmt.name);

        if (stmt.superclass != null && stmt.superclass.name == stmt.name) {
            // will take care of non-sense like `class Bagel < Bagel {};`
            Lox.error(stmt.superclass.name, "A class can't inherit from itself");
        }
        if (stmt.superclass != null) {
            resolve(stmt.superclass);
        }

        beginScope();
        scopes.peek().put("this", new VarInfo(stmt.name, true, true));

        for (Stmt.Function method: stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method.definition, declaration);
        }

        endScope();
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        // note we have no control flow here: both branches are resolved because
        // either one could be reached at runtime.
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can only return from inside a function.");
        }
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.keyword, "Can't return a value from initializer.");
            }
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        currentLoop = LoopType.LOOP;
        resolve(stmt.body);
        currentLoop = LoopType.NONE;
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (currentLoop != LoopType.LOOP) {
            Lox.error(stmt.keyword, "Can't break outside of a loop.");
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }


    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument: expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitTernaryExpr(Expr.Ternary expr) {
        resolve(expr.left);
        resolve(expr.middle);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt.definition, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitAnonFunctionExpr(Expr.AnonFunction expr) {
        resolveFunction(expr, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty()) {
            VarInfo var = scopes.peek().get(expr.name.lexeme);
            if (var != null) {
                if (!var.initialized) {
                    /* this bit will raise an error when the variable is accessed in its own
                     * initializer, and take care of code like this:
                     * `var a = "outer"; { var a = a; }`
                     * the alternative is either to initialize the innermost `a` to `outer`.
                     * but, if so, why? we could just avoid the line altogether.
                     * alternatively we could initialize the innermost `a` to `nil` but, again,
                     * why? wouldn't it just be better to initialize it directly to the nil value?
                     * we choose to give a hint to the user, and return a compile-time error. */
                    Lox.error(expr.name, "Can't read local variable in its own initializer.");
                } else if (!var.accessed) {
                    // challenge 11.3
                    scopes.peek().put(expr.name.lexeme, new VarInfo(var.keyword, var.initialized, true));
                }
            }
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    void resolve(List<Stmt> statements) {
        for (Stmt statement: statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Expr.AnonFunction function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param: function.params) {
            declare(param);
            define(param);
        }

        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        Map<String, VarInfo> scope = scopes.pop();

        // challenge 11.3
        // ending a scope we check if each variable in it was ever accessed,
        // and, if not, we report an error.
        for (Map.Entry<String, VarInfo> entry : scope.entrySet()) {
            VarInfo var = entry.getValue();
            if (!var.accessed) {
                Lox.error(var.keyword,
                        "Variable was defined but never accessed.");
            }
        }
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, VarInfo> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, new VarInfo(name, false, false));
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, new VarInfo(name, true, false));
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                // `size - 1 - i` will be 0 if we are in the innermost scope
                // 1 if we are in the first outermost scope and so on..
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}
