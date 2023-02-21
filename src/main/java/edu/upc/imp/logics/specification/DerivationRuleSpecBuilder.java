package edu.upc.imp.logics.specification;

import java.util.LinkedList;
import java.util.List;

public class DerivationRuleSpecBuilder {
    private final StringToTermSpecFactory stringToTermSpecFactory;
    private String predicateName;
    private List<TermSpec> terms;
    private final List<LiteralSpec> bodySpec = new LinkedList<>();


    public DerivationRuleSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    public DerivationRuleSpecBuilder addHead(String predicateName, String... terms) {
        this.predicateName = predicateName;
        this.terms = stringToTermSpecFactory.createTermSpecs(terms);
        return this;
    }

    public DerivationRuleSpecBuilder addOrdinaryLiteral(String predicateName, String... terms) {
        bodySpec.add(new OrdinaryLiteralSpec(predicateName, stringToTermSpecFactory.createTermSpecs(terms), true));
        return this;
    }

    public DerivationRuleSpec build() {
        return new DerivationRuleSpec(predicateName, terms, bodySpec);
    }
}
