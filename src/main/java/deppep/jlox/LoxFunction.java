package deppep.jlox;

import java.util.List;


class LoxFunction implements LoxCallable {
    private final Token name;
    private final Expr.AnonFunction definition;
    private final Environment closure;

    LoxFunction(Token name, Expr.AnonFunction definition, Environment closure) {
        this.name = name;
        this.definition = definition;
        this.closure = closure;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(name, definition, environment);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // note that each function will come with its own environment, which gets created
        // dynamically at _call_ time, not at _declaration_ time.
        // this enables recursion, which otherwise would not be possible.
        // functions also 'encapsulate' its parameters meaning that these are not visible
        // anywhere else in the code.
        Environment environment = new Environment(closure);
        // we walk the definition, binding arguments (values) to parameters (lexeme, symbols)
        for (int i=0; i < definition.params.size(); i++) {
            environment.define(definition.params.get(i).lexeme, arguments.get(i));
        }

        // return statement are implemented as exceptions (as we do with break)
        try {
            // `executeBlock` will set the interpreter's environment to the function's one
            // and execute the definition body in it.
            interpreter.executeBlock(definition.body, environment);
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
        return definition.params.size();
    }

    @Override
    public String toString() {
        if (name == null) {
            return "<fn anonymous>";
        }
        // with this `print add;` will output `<fn add>`
        return "<fn " + name.lexeme + ">";
    }
}
