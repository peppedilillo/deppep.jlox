package deppep.jlox;

class ReturnException extends RuntimeException {
    final Object value;

    ReturnException(Object value) {
        // this will essentially remove message and disable stack tracing which
        // causes overhead without bringing any benefit
        super(null, null, false, false);
        this.value = value;
    }
}
