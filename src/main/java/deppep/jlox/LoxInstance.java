package deppep.jlox;

import java.util.Map;
import java.util.HashMap;

class LoxInstance {
    private LoxClass klass;
    private Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        // the fact that method lookup comes after field lookup means that
        // fields will eventually shadow methods
        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return this.klass + " instance";
    }
}
