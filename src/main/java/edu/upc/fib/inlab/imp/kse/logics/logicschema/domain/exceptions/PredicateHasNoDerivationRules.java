package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

public class PredicateHasNoDerivationRules extends RuntimeException {
    public PredicateHasNoDerivationRules(Predicate predicate) {
        super("Predicate " + predicate.getName() + " has no derivation rules");
    }
}
