package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.imp.logics.schema.exceptions.RepeatedPredicateName;

import java.util.*;

public class LogicSchemaBuilder {

    private final Map<ConstraintID, LogicConstraint> logicConstraintById = new HashMap<>();
    private final Map<String, MutablePredicate> predicatesByName = new HashMap<>();

    public LogicSchemaBuilder addPredicate(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, new Arity(arity)));
        return this;
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity().getNumber() != arity) {
            throw new RepeatedPredicateName(predicateName);
        }
    }

    public LogicSchemaBuilder addDerivationRuleSpec(DerivationRuleSpec drs) {
        predicatesByName.putIfAbsent(
                drs.getPredicateName(),
                new MutablePredicate(drs.getPredicateName(), new Arity(drs.getTermSpecList().size())));
        Query query = buildQuery(drs.getTermSpecList(), drs.getBody());
        MutablePredicate mutablePredicate = predicatesByName.get(drs.getPredicateName());
        mutablePredicate.addDerivationRule(query);
        return this;
    }

    private Query buildQuery(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        List<Term> headTerms = TermSpecToTermFactory.buildTerms(termSpecList);
        List<Literal> body = buildBody(bodySpec);
        return new Query(headTerms, body);
    }

    public LogicSchemaBuilder addLogicConstraint(LogicConstraintSpec lcs) {
        ConstraintID constraintID = new ConstraintID(lcs.getId());
        if (logicConstraintById.containsKey(constraintID)) throw new RepeatedConstraintID(constraintID);
        List<Literal> body = buildBody(lcs.getBody());
        logicConstraintById.put(constraintID, new LogicConstraint(constraintID, body));
        return this;
    }

    private List<Literal> buildBody(List<LiteralSpec> bodySpec) {
        List<Literal> body = new LinkedList<>();
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                body.add(buildOrdinaryLiteral(olSpec));
            } else throw new RuntimeException("Unrecognized literalSpec " + literalSpec.getClass().getName());
        }
        return body;
    }

    private Literal buildOrdinaryLiteral(OrdinaryLiteralSpec olSpec) {
        List<Term> terms = TermSpecToTermFactory.buildTerms(olSpec.getTermSpecList());
        predicatesByName.putIfAbsent(olSpec.getPredicateName(), new MutablePredicate(olSpec.getPredicateName(), new Arity(terms.size())));
        Predicate predicate = predicatesByName.get(olSpec.getPredicateName());
        return new OrdinaryLiteral(new Atom(predicate, terms), olSpec.isPositive());
    }

    public LogicSchema build() {
        Set<Predicate> predicates = new HashSet<>(predicatesByName.values());
        Set<LogicConstraint> constraints = new HashSet<>(logicConstraintById.values());
        return new LogicSchema(predicates, constraints);
    }

}
