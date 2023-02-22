package edu.upc.imp.logics.specification;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LogicSchemaSpecification {
    private final List<PredicateSpec> predicateSpecList;
    private final List<DerivationRuleSpec> derivationRuleSpecList;
    private final List<LogicConstraintSpec> logicConstraintSpecList;

    public LogicSchemaSpecification() {
        predicateSpecList = new LinkedList<>();
        derivationRuleSpecList = new LinkedList<>();
        logicConstraintSpecList = new LinkedList<>();
    }

    public void addPredicateSpecification(PredicateSpec predicateSpec){
        this.predicateSpecList.add(predicateSpec);
    }

    public void addDerivationRuleSpec(DerivationRuleSpec derivationRuleSpec){
        this.derivationRuleSpecList.add(derivationRuleSpec);
    }

    public void addLogicConstraintSpec(LogicConstraintSpec logicConstraintSpec){
        this.logicConstraintSpecList.add(logicConstraintSpec);
    }
    public List<PredicateSpec> getPredicateSpecList() {
        return Collections.unmodifiableList(predicateSpecList);
    }

    public List<DerivationRuleSpec> getDerivationRuleSpecList() {
        return Collections.unmodifiableList(derivationRuleSpecList);
    }

    public List<LogicConstraintSpec> getLogicConstraintSpecList() {
        return  Collections.unmodifiableList(logicConstraintSpecList);
    }
}
