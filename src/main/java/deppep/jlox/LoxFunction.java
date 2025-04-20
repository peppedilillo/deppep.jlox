package deppep.jlox;

import java.util.List;


class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    LoxFunction(Stmt.Function declaration, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // note that each function will come with its own environment, which gets created
        // dynamically at _call_ time, not at _declaration_ time.
        // this enables recursion, which otherwise would not be possible.
        // functions also 'encapsulate' its parameters meaning that these are not visible
        // anywhere else in the code.
        Environment environment = new Environment(closure);
        // we walk the declaration, binding arguments (values) to parameters (lexeme, symbols)
        for (int i=0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        // return statement are implemented as exceptions (as we do with break)
        try {
            // `executeBlock` will set the interpreter's environment to the function's one
            // and execute the declaration body in it.
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException returnValue) {
            return returnValue.value;
        }
        // just before returning, executeBlock will reset the interpreter environment to
        // the one of the callee, the function environment being discarded
        return null;
    }

    @Override
    public int arity() {
        // safe, because `visitCallExpr` from the interpreter checks that we the
        // arguments list has the same length of parameters.
        return declaration.params.size();
    }

    @Override
    public String toString() {
        // with this `print add;` will output `<fn add>`
        return "<fn " + declaration.name.lexeme + ">";
    }
}
