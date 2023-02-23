package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.spec.LogicSchemaSpec;

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
