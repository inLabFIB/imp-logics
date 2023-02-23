package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public class BuiltInLiteralSpec extends LiteralSpec implements LogicElementSpec {
    private final String operator;
    private final List<TermSpec> termSpecs;

    public BuiltInLiteralSpec(String operator, List<TermSpec> termSpecs) {
        this.operator = operator;
        this.termSpecs = termSpecs;
    }

    public String getOperator() {
        return operator;
    }

    public List<TermSpec> getTermSpecs() {
        return termSpecs;
    }
}
