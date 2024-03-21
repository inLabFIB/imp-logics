package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;


import java.util.List;
import java.util.Objects;

/**
 * Specification of a built-in literal.
 */
public class BuiltInLiteralSpec extends LiteralSpec {
    private final String operator;
    private final List<TermSpec> termSpecs;

    public BuiltInLiteralSpec(String operator, List<TermSpec> termSpecs) {
        if (Objects.isNull(operator)) throw new IllegalArgumentException("Operator cannot be null");
        if (Objects.isNull(termSpecs)) throw new IllegalArgumentException("TermSpecs cannot be null");
        this.operator = operator;
        this.termSpecs = termSpecs;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public List<TermSpec> getTermSpecList() {
        return termSpecs;
    }
}
