package edu.upc.imp.logics.services.creation.spec;

import java.util.Collections;
import java.util.List;

/**
 * Specification of an ordinary literal.
 */
public class OrdinaryLiteralSpec extends LiteralSpec implements LogicElementSpec {
    private final String predicateName;
    private final List<TermSpec> termsList;
    private final boolean isPositive;

    public OrdinaryLiteralSpec(String predicateName, List<TermSpec> termsList, boolean isPositive) {
        this.predicateName = predicateName;
        this.termsList = Collections.unmodifiableList(termsList);
        this.isPositive = isPositive;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<TermSpec> getTermSpecList() {
        return termsList;
    }

    public boolean isPositive() {
        return isPositive;
    }
}
