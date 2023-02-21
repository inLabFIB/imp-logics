package edu.upc.imp.logics.specification;

import java.util.LinkedList;
import java.util.List;

public class LogicConstraintSpecBuilder {
    private final StringToTermFactory stringToTermFactory;
    private final List<LiteralSpec> bodySpec = new LinkedList<>();
    private String id;

    public LogicConstraintSpecBuilder(StringToTermFactory stringToTermFactory) {
        this.stringToTermFactory = stringToTermFactory;
    }

    public LogicConstraintSpecBuilder addConstraintId(String id) {
        this.id = id;
        return this;
    }

    public LogicConstraintSpecBuilder addOrdinaryLiteral(String predicateName, String... terms) {
        bodySpec.add(new OrdinaryLiteralSpec(predicateName, stringToTermFactory.createTerms(terms), true));
        return this;
    }

    public LogicConstraintSpec build() {
        return new LogicConstraintSpec(id, bodySpec);
    }
}
