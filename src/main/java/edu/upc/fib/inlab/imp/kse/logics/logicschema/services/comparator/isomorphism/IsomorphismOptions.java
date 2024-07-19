package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

public record IsomorphismOptions(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed,
                                 boolean changingDerivedPredicateNameAllowed) {
    /**
     * Default options os Isomorphism. It permits changing the variableNames, the order of the literals, and the name of
     * the predicate names.
     */
    public IsomorphismOptions() {
        this(true, true, true);
    }

    public IsomorphismOptions(IsomorphismOptions options) {
        this(options.changeVariableNamesAllowed, options.changeLiteralOrderAllowed, options.changingDerivedPredicateNameAllowed);
    }
}
