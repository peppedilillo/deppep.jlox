/*
 * Generic interface to a callable Lox object: functions, but also classes instantiation.
 */
package deppep.jlox;

import java.util.List;

interface LoxCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
