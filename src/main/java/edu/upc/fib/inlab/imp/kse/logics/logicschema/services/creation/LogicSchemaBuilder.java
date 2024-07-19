package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedConstraintIDException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.RepeatedPredicateNameException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;

import java.util.*;

/**
 * Builder for a logic schema. The builder will ensure that there is a Predicate for each predicate name used in the
 * specification. That is, if a logic constraint or derivation rule specification uses a predicate name "P", which has
 * not been specified as a predicate, the builder will automatically create such predicate P.
 *
 * <p> The builder must work with either LogicConstraintSpecWithIDs, or LogicConstraintSpecWithoutIDs, but not
 * both at the same time.</p>
 *
 * @param <T> kind of LogicConstraintSpec this class works with
 */
public class LogicSchemaBuilder<T extends LogicConstraintSpec> {

    private final Map<ConstraintID, LogicConstraint> logicConstraintById = new HashMap<>();
    private final Map<String, Predicate> predicatesByName = new HashMap<>();
    private final ConstraintIDGenerator<T> constraintIDGenerator;


    public LogicSchemaBuilder(ConstraintIDGenerator<T> constraintIDGenerator) {
        this.constraintIDGenerator = constraintIDGenerator;
    }

    public LogicSchemaBuilder(ConstraintIDGenerator<T> constraintIDGenerator, Set<Predicate> predicates) {
        this.constraintIDGenerator = constraintIDGenerator;
        for (Predicate pred : predicates) {
            this.predicatesByName.put(pred.getName(), pred);
        }
    }

    public static LogicSchemaBuilder<LogicConstraintWithoutIDSpec> defaultLogicSchemaWithoutIDsBuilder() {
        return new LogicSchemaBuilder<>(new IncrementalConstraintIDGenerator());
    }

    public static LogicSchemaBuilder<LogicConstraintWithIDSpec> defaultLogicSchemaWithIDsBuilder() {
        return new LogicSchemaBuilder<>(new UseSpecIDGenerator());
    }

    public LogicSchemaBuilder<T> addDerivationRule(DerivationRuleSpec... drs) {
        Arrays.stream(drs).forEach(this::addDerivationRule);
        return this;
    }

    private void addDerivationRule(DerivationRuleSpec drs) {
        predicatesByName.putIfAbsent(
                drs.getPredicateName(),
                new MutablePredicate(drs.getPredicateName(), drs.getTermSpecList().size()));
        Query query = buildQuery(drs.getTermSpecList(), drs.getBody());
        MutablePredicate mutablePredicate = (MutablePredicate) predicatesByName.get(drs.getPredicateName());
        mutablePredicate.addDerivationRule(query);
    }

    private Query buildQuery(List<TermSpec> termSpecList, List<LiteralSpec> bodySpec) {
        ImmutableTermList headTerms = TermSpecToTermFactory.buildTerms(termSpecList);
        ImmutableLiteralsList body = buildBody(bodySpec);
        return QueryFactory.createQuery(headTerms, body);
    }

    private ImmutableLiteralsList buildBody(List<LiteralSpec> bodySpec) {
        addPredicatesFromBody(bodySpec);
        return new BodyBuilder(new LiteralFactory(predicatesByName)).addLiterals(bodySpec).build();
    }

    private void addPredicatesFromBody(List<LiteralSpec> bodySpec) {
        for (LiteralSpec literalSpec : bodySpec) {
            if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
                int numberOfTerms = olSpec.getTermSpecList().size();
                addPredicateIfAbsent(olSpec.getPredicateName(), numberOfTerms);
            } else if (!(literalSpec instanceof BuiltInLiteralSpec)) {
                throw new IMPLogicsException("Unrecognized literalSpec " + literalSpec.getClass().getName());
            }
        }
    }

    @SafeVarargs
    public final LogicSchemaBuilder<T> addLogicConstraint(T... logicConstraintSpecs) {
        return addLogicConstraint(Arrays.stream(logicConstraintSpecs).toList());
    }

    public final LogicSchemaBuilder<T> addLogicConstraint(Collection<T> logicConstraintSpecs) {
        logicConstraintSpecs.forEach(lcs -> {
            ConstraintID constraintID = constraintIDGenerator.newConstraintID(lcs);
            addLogicConstraint(constraintID, lcs);
        });
        return this;
    }

    public LogicSchemaBuilder<T> addDerivationRule(Collection<DerivationRuleSpec> derivationRules) {
        derivationRules.forEach(this::addDerivationRule);
        return this;
    }

    public LogicSchema build() {
        Set<Predicate> predicates = new LinkedHashSet<>(predicatesByName.values());
        Set<LogicConstraint> constraints = new LinkedHashSet<>(logicConstraintById.values());
        return new LogicSchema(predicates, constraints);
    }

    public LogicSchemaBuilder<T> addAllDerivationRules(List<DerivationRuleSpec> newRules) {
        newRules.forEach(this::addDerivationRule);
        return this;
    }

    public LogicSchemaBuilder<T> addAllLogicConstraints(List<T> newConstraints) {
        newConstraints.forEach(this::addLogicConstraint);
        return this;
    }

    public LogicSchemaBuilder<T> addAllPredicates(List<PredicateSpec> allPredicates) {
        allPredicates.forEach(this::addPredicate);
        return this;
    }

    private void addPredicateIfAbsent(String predicateName, int arity) {
        checkRepeatedNameWithDifferentArity(predicateName, arity);
        predicatesByName.putIfAbsent(predicateName, new MutablePredicate(predicateName, arity));
    }

    public LogicSchemaBuilder<T> addPredicate(PredicateSpec... predicateSpecs) {
        Arrays.stream(predicateSpecs).forEach(predicateSpec -> this.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        return this;
    }

    private void checkRepeatedNameWithDifferentArity(String predicateName, int arity) {
        if (predicatesByName.containsKey(predicateName)
                && predicatesByName.get(predicateName).getArity() != arity) {
            throw new RepeatedPredicateNameException(predicateName);
        }
    }

    public LogicSchemaBuilder<T> addPredicate(String predicateName, int arity) {
        addPredicateIfAbsent(predicateName, arity);
        return this;
    }

    private void addLogicConstraint(ConstraintID constraintID, LogicConstraintSpec lcs) {
        if (logicConstraintById.containsKey(constraintID)) throw new RepeatedConstraintIDException(constraintID);
        ImmutableLiteralsList body = buildBody(lcs.getBody());
        logicConstraintById.put(constraintID, new LogicConstraint(constraintID, body));
    }
}
