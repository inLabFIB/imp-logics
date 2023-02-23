package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public class DerivationRuleSpec extends NormalClauseSpec implements LogicElementSpec {
    private final String predicateName;
    private final List<TermSpec> termSpecList;

    public DerivationRuleSpec(String predicateName, List<TermSpec> headTermsSpec, BodySpec body) {
        super(body);
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
