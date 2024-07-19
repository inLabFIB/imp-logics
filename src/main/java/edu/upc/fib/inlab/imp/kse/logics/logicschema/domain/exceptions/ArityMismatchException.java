package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

/**
 * Thrown to indicate that the quantity of Term (arity) of an object does not match expected. This could be the case for
 * the classes {@code Atom}, {@code Predicate} or instances of {@code Literal}.
 */
public class ArityMismatchException extends IMPLogicsException {

    /**
     * Constructs an {@code ArityMismatchException} with arguments for the provided and expected arities.
     * <p>
     * The arities are included in this exception's detail message. The exact presentation format of the detail message
     * is unspecified.
     *
     * @param expected expected arity.
     * @param provided provided arity.
     */
    public ArityMismatchException(int expected, int provided) {
        super("Expected arity " + expected + " but provided " + provided);
    }
}
