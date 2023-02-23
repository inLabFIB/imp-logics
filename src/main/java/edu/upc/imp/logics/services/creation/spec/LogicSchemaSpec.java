package edu.upc.imp.logics.services.creation.spec;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LogicSchemaSpec implements LogicElementSpec {
    private final List<PredicateSpec> predicateSpecList;
    private final List<DerivationRuleSpec> derivationRuleSpecList;
    private final List<LogicConstraintSpec> logicConstraintSpecList;

    public LogicSchemaSpec() {
        predicateSpecList = new LinkedList<>();
        derivationRuleSpecList = new LinkedList<>();
        logicConstraintSpecList = new LinkedList<>();
    }

    public void addPredicateSpecs(PredicateSpec... predicateSpecs) {
        this.predicateSpecList.addAll(Arrays.asList(predicateSpecs));
    }

    public void addDerivationRuleSpecs(DerivationRuleSpec... derivationRuleSpecs) {
        this.derivationRuleSpecList.addAll(Arrays.asList(derivationRuleSpecs));
    }

    public void addLogicConstraintSpecs(LogicConstraintSpec... logicConstraintSpecs) {
        this.logicConstraintSpecList.addAll(Arrays.asList(logicConstraintSpecs));
    }

    public List<PredicateSpec> getPredicateSpecList() {
        return Collections.unmodifiableList(predicateSpecList);
    }

    public List<DerivationRuleSpec> getDerivationRuleSpecList() {
        return Collections.unmodifiableList(derivationRuleSpecList);
    }

    public List<LogicConstraintSpec> getLogicConstraintSpecList() {
        return Collections.unmodifiableList(logicConstraintSpecList);
    }
}
