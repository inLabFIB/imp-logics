package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.*;

/**
 * Specification of a derivation rule.
 */
public class DerivationRuleSpec extends NormalClauseSpec implements LogicElementSpec {
    private final String predicateName;
    private final List<TermSpec> termSpecList;

    public DerivationRuleSpec(String predicateName, List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        this(predicateName, termSpecList, new BodySpec(bodySpec));
    }

    public DerivationRuleSpec(String predicateName, List<TermSpec> headTermsSpec, BodySpec body) {
        super(body);
        if (Objects.isNull(predicateName)) throw new IllegalArgumentException("Predicate name cannot be null");
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (Objects.isNull(headTermsSpec)) throw new IllegalArgumentException("Head terms cannot be null");

        this.predicateName = predicateName;
        this.termSpecList = headTermsSpec;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<TermSpec> getTermSpecList() {
        return termSpecList;
    }

    @Override
    public Set<String> getAllVariableNames() {
        Set<String> result = new LinkedHashSet<>(getAllHeadVariableNames());
        result.addAll(new BodySpec(getBody()).getAllVariableNames());
        return result;
    }

    public Set<String> getAllHeadVariableNames() {
        Set<String> result = new HashSet<>();
        for (TermSpec termSpec : termSpecList) if (termSpec instanceof VariableSpec) result.add(termSpec.getName());
        return result;
    }
}
