package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.LogicSchema;

import java.util.Objects;

public class LogicSchemaFactory {
    private final LogicSchemaBuilder logicSchemaBuilder;

    public LogicSchemaFactory(LogicSchemaBuilder logicSchemaBuilder) {
        if(Objects.isNull(logicSchemaBuilder)) throw new IllegalArgumentException("LogicSchemaBuilder cannot be null");
        this.logicSchemaBuilder = logicSchemaBuilder;
    }

    public LogicSchema buildLogicSchema(LogicSchemaSpecification logicSchemaSpecification) {
        for(PredicateSpec predicateSpec: logicSchemaSpecification.getPredicateSpecList()){
            logicSchemaBuilder.addPredicate(predicateSpec);
        }

        for(DerivationRuleSpec derivationRuleSpec: logicSchemaSpecification.getDerivationRuleSpecList()){
            logicSchemaBuilder.addDerivationRuleSpec(derivationRuleSpec);
        }

        for(LogicConstraintSpec logicConstraintSpec: logicSchemaSpecification.getLogicConstraintSpecList()){
            logicSchemaBuilder.addLogicConstraint(logicConstraintSpec);
        }

        return logicSchemaBuilder.build();
    }
}
