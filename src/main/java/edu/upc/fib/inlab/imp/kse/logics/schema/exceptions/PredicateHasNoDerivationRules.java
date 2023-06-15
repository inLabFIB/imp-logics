package edu.upc.fib.inlab.imp.kse.logics.schema.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;

public class PredicateHasNoDerivationRules extends RuntimeException {
    public PredicateHasNoDerivationRules(Predicate predicate) {
        super("Predicate " + predicate.getName() + " has no derivation rules");
    }
}
