package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import java.util.List;

/**
 * Specification of a Literal.
 */
public abstract class LiteralSpec implements LogicElementSpec {
    public abstract List<TermSpec> getTermSpecList();
}
