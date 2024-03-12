package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;

/**
 * Specification of a Literal.
 */
public interface LiteralSpec extends LogicElementSpec {
    List<TermSpec> getTermSpecList();
}
