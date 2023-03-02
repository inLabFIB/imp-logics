package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.DerivationRuleSpec;
import edu.upc.imp.logics.services.creation.spec.TermSpec;

import java.util.List;

/**
 * Builder to facilitate the creation of DerivationRuleSpec.
 */
public class DerivationRuleSpecBuilder extends NormalClauseSpecBuilder<DerivationRuleSpecBuilder> {
    private String predicateName;
    private List<TermSpec> terms;

    /**
     * Creates a new DerivationRuleSpecBuilder using a DefaultStringToTermSpecFactory to distinguish
     * the kind of Term to instantiate for the given term names.
     */
    public DerivationRuleSpecBuilder() {
        this(new DefaultTermTypeCriteria());
    }

    public DerivationRuleSpecBuilder(TermTypeCriteria termTypeCriteria) {
        super(termTypeCriteria);
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
