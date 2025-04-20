package deppep.jlox;

// challenge 9.3
class BreakException extends RuntimeException {
    BreakException() {
        // see note to `ReturnException`
        super(null, null, false, false);
    }
}
