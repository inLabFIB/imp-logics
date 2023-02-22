package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.LogicSchema;

public class LogicSchemaFactory {
    private final LogicSchemaBuilder logicSchemaBuilder;

    public LogicSchemaFactory() {
        this.logicSchemaBuilder = new LogicSchemaBuilder();
    }

    public LogicSchema createLogicSchema(LogicSchemaSpec logicSchemaSpec) {
        logicSchemaSpec.getPredicateSpecList().forEach(logicSchemaBuilder::addPredicate);
        logicSchemaSpec.getDerivationRuleSpecList().forEach(logicSchemaBuilder::addDerivationRule);
        logicSchemaSpec.getLogicConstraintSpecList().forEach(logicSchemaBuilder::addLogicConstraint);
        return logicSchemaBuilder.build();
    }
}
