package deppep.jlox;

import java.util.HashMap;
import java.util.Map;


class Environment {
	private final Map<String, Object> values = new HashMap<>();

	void define(String name, Object value) {
		// note we don't check if `name` is in environment already.
		// this means that this program won't error:
		//
		// var a = "before";
		// print a; // "before"
		// var b = "after";
		// print b; // "after"
		//
		// this is no trivial choice. the goal of this is to allow
		// for a better REPL, where redefining a variable is common
		// and having error at each redefinition would be annoying
		values.put(name, value);
	}

	Object get(Token name) {
		if (values.containsKey(name.lexeme)) {
			return values.get(name.lexeme);
		}

		// here we have some freedom. why a RuntimeError? after all
		// it could be detected statically, or we could even go without
		// errors returning null. the reason for having a RuntimeError here
		// is to make it easier for mutually recursive functions,
		// see book page 121 for a nice example.
		throw new RuntimeError(name,
							   "Undefined variable '" + name.lexeme + "'.");
	}
}
