package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.DerivationRuleSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * Builder to facilitate the creation of DerivationRuleSpec.
 */
public class DerivationRuleSpecBuilder extends NormalClauseSpecBuilder<DerivationRuleSpecBuilder> {
    private String predicateName;
    private List<TermSpec> headTerms;

    /**
     * Creates a new DerivationRuleSpecBuilder using a DefaultStringToTermSpecFactory to distinguish the kind of Term to
     * instantiate for the given term names.
     */
    public DerivationRuleSpecBuilder() {
        this(new AllVariableTermTypeCriteria());
    }

    public DerivationRuleSpecBuilder(TermTypeCriteria termTypeCriteria) {
        super(termTypeCriteria);
    }

    public DerivationRuleSpecBuilder addHead(String predicateName, String... terms) {
        this.predicateName = predicateName;
        this.headTerms = stringToTermSpecFactory.createTermSpecs(terms);
        return this;
    }

    public DerivationRuleSpecBuilder addHead(String predicateName, List<TermSpec> terms) {
        this.predicateName = predicateName;
        this.headTerms = new LinkedList<>(terms);
        return this;
    }

    public DerivationRuleSpec build() {
        return new DerivationRuleSpec(predicateName, headTerms, bodySpec);
    }
}
