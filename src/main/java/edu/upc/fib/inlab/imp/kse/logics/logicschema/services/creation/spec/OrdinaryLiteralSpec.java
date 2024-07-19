package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Specification of an ordinary literal.
 */
public class OrdinaryLiteralSpec implements LiteralSpec {
    private final String predicateName;
    private final List<TermSpec> termsList;
    private final boolean isPositive;

    public OrdinaryLiteralSpec(String predicateName, List<TermSpec> termsList) {
        this(predicateName, termsList, true);
    }

    public OrdinaryLiteralSpec(String predicateName, List<TermSpec> termsList, boolean isPositive) {
        if (isNull(predicateName)) throw new IllegalArgumentException("Predicate name cannot be null");
        if (isNull(termsList)) throw new IllegalArgumentException("Terms list cannot be null");
        this.predicateName = predicateName;
        this.termsList = Collections.unmodifiableList(termsList);
        this.isPositive = isPositive;
    }

    public String getPredicateName() {
        return predicateName;
    }

    @Override
    public List<TermSpec> getTermSpecList() {
        return termsList;
    }

    public boolean isPositive() {
        return isPositive;
    }
}
