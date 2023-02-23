package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.imp.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.imp.logics.services.creation.spec.*;

import java.util.*;

public class LogicSchemaBuilder {

    private final Map<ConstraintID, LogicConstraint> logicConstraintById = new HashMap<>();
    private final Map<String, MutablePredicate> predicatesByName = new HashMap<>();

    public LogicSchemaBuilder addPredicate(PredicateSpec predicateSpec) {
        this.addPredicate(predicateSpec.name(), predicateSpec.arity());
        return this;
    }

    public LogicSchemaBuilder addPredicate(PredicateSpec... predicateSpecs) {
        Arrays.stream(predicateSpecs).forEach(this::addPredicate);
        return this;
    }

    public LogicSchemaBuilder addPredicate(String predicateName, int arity) {
        addPredicateIfAbsent(predicateName, arity);
        return this;
    }

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, new Arity(arity)));
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity().getNumber() != arity) {
            throw new RepeatedPredicateName(predicateName);
        }
    }

    public LogicSchemaBuilder addDerivationRule(DerivationRuleSpec drs) {
        predicatesByName.putIfAbsent(
                drs.getPredicateName(),
                new MutablePredicate(drs.getPredicateName(), new Arity(drs.getTermSpecList().size())));
        Query query = buildQuery(drs.getTermSpecList(), drs.getBody());
        MutablePredicate mutablePredicate = predicatesByName.get(drs.getPredicateName());
        mutablePredicate.addDerivationRule(query);
        return this;
    }

    public LogicSchemaBuilder addDerivationRules(DerivationRuleSpec... drs) {
        Arrays.stream(drs).forEach(this::addDerivationRule);
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

    public LogicSchemaBuilder addLogicConstraints(LogicConstraintSpec... logicConstraints) {
        Arrays.stream(logicConstraints).forEach(this::addLogicConstraint);
        return this;
    }

    private List<Literal> buildBody(List<LiteralSpec> bodySpec) {
        addPredicatesFromBody(bodySpec);
        return new BodyBuilder(predicatesByName).addLiterals(bodySpec).build();
    }

    private void addPredicatesFromBody(List<LiteralSpec> bodySpec) {
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                int numberOfTerms = olSpec.getTermSpecList().size();
                addPredicateIfAbsent(olSpec.getPredicateName(), numberOfTerms);
            } else throw new RuntimeException("Unrecognized literalSpec " + literalSpec.getClass().getName());
        }
    }

    public LogicSchema build() {
        Set<Predicate> predicates = new HashSet<>(predicatesByName.values());
        Set<LogicConstraint> constraints = new HashSet<>(logicConstraintById.values());
        return new LogicSchema(predicates, constraints);
    }
}
