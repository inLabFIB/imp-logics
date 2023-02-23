package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.TermSpec;

import java.util.List;

public class DerivationRuleSpecBuilder extends NormalClauseSpecBuilder<DerivationRuleSpecBuilder> {
    private String predicateName;
    private List<TermSpec> terms;


    public DerivationRuleSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public DerivationRuleSpecBuilder addHead(String predicateName, String... terms) {
        this.predicateName = predicateName;
        this.terms = stringToTermSpecFactory.createTermSpecs(terms);
        return this;
    }

    public DerivationRuleSpec build() {
        return new DerivationRuleSpec(predicateName, terms, bodySpec);
    }
}
