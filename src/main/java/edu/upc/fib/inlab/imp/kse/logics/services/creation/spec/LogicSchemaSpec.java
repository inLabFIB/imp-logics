package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> Specification of a whole logic schema, that is, a specification of a set of predicates,
 * logic constraints, and derivation rules. </p>
 *
 * <p> A LogicSchemaSpec can only deal with logic constraints including an id, or logic constraints
 * not including an id, but not both at the same time.
 * Hence, when instantiating a LogicSchemaSpec, we must declare which type
 * of LogicConstraintSpec we are going to use.
 * </p>
 *
 * @param <T> kind of LogicConstraintSpec this LogicSchemaSpec will work with
 */
public class LogicSchemaSpec<T extends LogicConstraintSpec> implements LogicElementSpec {
    private final List<PredicateSpec> predicateSpecList;
    private final List<DerivationRuleSpec> derivationRuleSpecList;
    private final List<T> logicConstraintSpecList;

    public LogicSchemaSpec() {
        predicateSpecList = new LinkedList<>();
        derivationRuleSpecList = new LinkedList<>();
        logicConstraintSpecList = new LinkedList<>();
    }

    public void addPredicateSpecs(PredicateSpec... predicateSpecs) {
        this.predicateSpecList.addAll(Arrays.asList(predicateSpecs));
    }

    public void addPredicateSpecs(List<PredicateSpec> predicateSpecs) {
        this.predicateSpecList.addAll(predicateSpecs);
    }

    public void addDerivationRuleSpecs(DerivationRuleSpec... derivationRuleSpecs) {
        this.derivationRuleSpecList.addAll(Arrays.asList(derivationRuleSpecs));
    }

    public void addDerivationRuleSpecs(List<DerivationRuleSpec> derivationRuleSpecs) {
        this.derivationRuleSpecList.addAll(derivationRuleSpecs);
    }

    @SafeVarargs
    public final void addLogicConstraintSpecs(T... logicConstraintSpecs) {
        this.logicConstraintSpecList.addAll(Arrays.asList(logicConstraintSpecs));
    }

    public final void addLogicConstraintSpecs(List<T> logicConstraintSpecs) {
        this.logicConstraintSpecList.addAll(logicConstraintSpecs);
    }

    public List<PredicateSpec> getPredicateSpecList() {
        return Collections.unmodifiableList(predicateSpecList);
    }

    public List<DerivationRuleSpec> getDerivationRuleSpecList() {
        return Collections.unmodifiableList(derivationRuleSpecList);
    }

    public List<T> getLogicConstraintSpecList() {
        return Collections.unmodifiableList(logicConstraintSpecList);
    }
}
