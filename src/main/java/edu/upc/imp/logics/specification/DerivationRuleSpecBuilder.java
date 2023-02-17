package edu.upc.imp.logics.specification;

import java.util.LinkedList;
import java.util.List;

public class DerivationRuleSpecBuilder {
    private final StringToTermFactory stringToTermFactory;
    private String predicateName;
    private List<TermSpec> terms;
    private final List<LiteralSpec> bodySpec = new LinkedList<>();


    public DerivationRuleSpecBuilder(StringToTermFactory stringToTermFactory) {
        this.stringToTermFactory = stringToTermFactory;
    }

    public DerivationRuleSpecBuilder addHead(String predicateName, String... terms) {
        this.predicateName = predicateName;
        this.terms = stringToTermFactory.createTerms(terms);
        return this;
    }

    public DerivationRuleSpecBuilder addOrdinaryLiteral(String predicateName, String... terms) {
        bodySpec.add(new OrdinaryLiteralSpec(predicateName, stringToTermFactory.createTerms(terms), true));
        return this;
    }

    public DerivationRuleSpec build() {
        return new DerivationRuleSpec(predicateName, terms, bodySpec);
    }
}
