package edu.upc.imp.logics.specification;

import java.util.List;

public class DerivationRuleSpec extends NormalClauseSpec {
    private final String predicateName;
    private final List<TermSpec> termSpecList;

    public DerivationRuleSpec(String predicateName, List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        super(bodySpec);
        this.predicateName = predicateName;
        this.termSpecList = termSpecList;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public List<TermSpec> getTermSpecList() {
        return termSpecList;
    }
}
