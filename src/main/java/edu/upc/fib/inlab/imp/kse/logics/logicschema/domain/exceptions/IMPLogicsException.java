package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

/**
 * General exception for IMP Logics library. All custom exceptions in the project are subclasses of this class.
 * <p>
 * {@code IMPLogicsException} is a subclass of {@code RuntimeException} so it, and all its subclasses, are
 * <em>unchecked exceptions</em>
 */
public class IMPLogicsException extends RuntimeException {

    /**
     * Constructs a new runtime exception with {@code null} as its detail message.
     */
    public IMPLogicsException() {
        super();
    }

    /**
     * Constructs a new IMP Logics exception with the specified detail message.
     *
     * @param message   the detail message. The detail message is saved for
     *                  later retrieval by the {@link #getMessage()} method.
     */
    public IMPLogicsException(String message) {
        super(message);
    }
}
