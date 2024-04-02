package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.List;
import java.util.Objects;

/**
 * Specification of a derivation rule.
 */
public class DerivationRuleSpec extends NormalClauseSpec implements LogicElementSpec {
    private final String predicateName;
    private final List<TermSpec> termSpecList;

    public DerivationRuleSpec(String predicateName, List<TermSpec> headTermsSpec, BodySpec body) {
        super(body);
        if (Objects.isNull(predicateName)) throw new IllegalArgumentException("Predicate name cannot be null");
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (Objects.isNull(headTermsSpec)) throw new IllegalArgumentException("Head terms cannot be null");

        this.predicateName = predicateName;
        this.termSpecList = headTermsSpec;
    }

    public DerivationRuleSpec(String predicateName, List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        this(predicateName, termSpecList, new BodySpec(bodySpec));
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<TermSpec> getTermSpecList() {
        return termSpecList;
    }
}
