package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.exceptions.RepeatedConstraintID;
import edu.upc.imp.logics.schema.exceptions.RepeatedPredicateName;
import edu.upc.imp.logics.services.creation.spec.*;

import java.util.*;

public class LogicSchemaBuilder {

    private final Map<ConstraintID, LogicConstraint> logicConstraintById = new HashMap<>();
    private final Map<String, MutablePredicate> predicatesByName = new HashMap<>();
    private final ConstraintIDGenerator constraintIDGenerator;

    public LogicSchemaBuilder() {
        this(new IncrementalConstraintIDGenerator(1));
    }

    public LogicSchemaBuilder(ConstraintIDGenerator constraintIDGenerator) {
        this.constraintIDGenerator = constraintIDGenerator;
    }

    public LogicSchemaBuilder addPredicate(PredicateSpec... predicateSpecs) {
        Arrays.stream(predicateSpecs).forEach(predicateSpec -> this.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        return this;
    }

    public LogicSchemaBuilder addPredicate(String predicateName, int arity) {
        addPredicateIfAbsent(predicateName, arity);
        return this;
    }

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, arity));
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity() != arity) {
            throw new RepeatedPredicateName(predicateName);
        }
    }

    public LogicSchemaBuilder addDerivationRule(DerivationRuleSpec... drs) {
        Arrays.stream(drs).forEach(this::addDerivationRule);
        return this;
    }

    private LogicSchemaBuilder addDerivationRule(DerivationRuleSpec drs) {
        predicatesByName.putIfAbsent(
                drs.getPredicateName(),
                new MutablePredicate(drs.getPredicateName(), drs.getTermSpecList().size()));
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

    public void addLogicConstraint(LogicConstraintSpec... logicConstraintSpecs) {
        Arrays.stream(logicConstraintSpecs).forEach(
                lcs -> {
                    if (lcs instanceof LogicConstraintWithIDSpec withIDSpec) addLogicConstraintWithID(withIDSpec);
                    else if (lcs instanceof LogicConstraintWithoutIDSpec withoutIDSpec)
                        addLogicConstraintWithoutID(withoutIDSpec);
                    else
                        throw new RuntimeException("Unknown LogicConstraintSpec implementation: " + lcs.getClass().getName());
                }
        );
    }

    public LogicSchemaBuilder addLogicConstraintWithID(LogicConstraintWithIDSpec... logicConstraints) {
        Arrays.stream(logicConstraints).forEach(lcs -> {
            ConstraintID constraintID = new ConstraintID(lcs.getId());
            addLogicConstraint(constraintID, lcs);
        });
        return this;
    }

    public LogicSchemaBuilder addLogicConstraintWithoutID(LogicConstraintWithoutIDSpec... logicConstraints) {
        Arrays.stream(logicConstraints).forEach(lcs -> {
            ConstraintID constraintID = constraintIDGenerator.newConstraintID();
            addLogicConstraint(constraintID, lcs);
        });
        return this;
    }

    private void addLogicConstraint(ConstraintID constraintID, LogicConstraintSpec lcs) {
        if (logicConstraintById.containsKey(constraintID)) throw new RepeatedConstraintID(constraintID);
        List<Literal> body = buildBody(lcs.getBody());
        logicConstraintById.put(constraintID, new LogicConstraint(constraintID, body));
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
