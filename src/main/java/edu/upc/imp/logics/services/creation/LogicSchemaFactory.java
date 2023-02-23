package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.imp.logics.services.creation.spec.LogicSchemaSpec;

public class LogicSchemaFactory<T extends LogicConstraintSpec> {
    private final LogicSchemaBuilder logicSchemaBuilder;

    public LogicSchemaFactory(ConstraintIDGenerator constraintIDGenerator) {
        logicSchemaBuilder = new LogicSchemaBuilder(constraintIDGenerator);
    }

    public LogicSchemaFactory() {
        this.logicSchemaBuilder = new LogicSchemaBuilder();
    }

    public LogicSchema createLogicSchema(LogicSchemaSpec<T> logicSchemaSpec) {
        logicSchemaSpec.getPredicateSpecList().forEach(predicateSpec -> logicSchemaBuilder.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        logicSchemaSpec.getDerivationRuleSpecList().forEach(logicSchemaBuilder::addDerivationRule);
        logicSchemaSpec.getLogicConstraintSpecList().forEach(logicSchemaBuilder::addLogicConstraint);
        return logicSchemaBuilder.build();
    }
}
