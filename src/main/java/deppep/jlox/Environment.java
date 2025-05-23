package deppep.jlox;

import java.util.HashMap;
import java.util.Map;


class Environment {
	final Environment enclosing;
	private final Map<String, Object> values = new HashMap<>();

	// global scope
	Environment() {
		enclosing = null;
	}

	// local scopes
	Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	void define(String name, Object value) {
		/* note we don't check if `name` is in environment already.
		 * this means that this program won't error:
		 * ```var a = "before";
		 * print a; // "before"
		 * var b = "after";
		 * print b; // "after"```
		 * this is no trivial choice. the goal of this is to allow
		 * for a better REPL, where redefining a variable is common
		 * and having error at each redefinition would be annoying */
		values.put(name, value);
	}

	Object getAt(int distance, String name) {
		// don't have to check for the variable to be there: we are assuming
		// that the resolver did its job right. this is delicate, since it makes
		// for a strong coupling between the environment and the resolver.
		return ancestor(distance).values.get(name);
	}

	void assignAt(int distance, Token name, Object value) {
		ancestor(distance).values.put(name.lexeme, value);
	}

	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

	Object get(Token name) {
		if (values.containsKey(name.lexeme))
			return values.get(name.lexeme);

		if (enclosing != null)
			return enclosing.get(name);
		// here we have some freedom. why a RuntimeError? after all
		// it could be detected statically, or we could even go without
		// errors returning null. the reason for having a RuntimeError here
		// is to make it easier for mutually recursive functions,
		// see book page 121 for a nice example.
		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	void assign(Token name, Object value) {
		if (values.containsKey(name.lexeme)) {
			values.put(name.lexeme, value);
			return;
		}

		if (enclosing != null) {
			enclosing.assign(name, value);
			return;
		}

		throw new RuntimeError(name,
				"Undefined variable '" + name.lexeme + "'.");
	}
}
