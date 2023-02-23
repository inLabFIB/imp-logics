package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.imp.logics.services.creation.spec.LogicSchemaSpec;

public class LogicSchemaFactory<T extends LogicConstraintSpec> {
    private final LogicSchemaBuilder<T> logicSchemaBuilder;

    public static LogicSchemaFactory<LogicConstraintWithIDSpec> defaultLogicSchemaWithIDsFactory() {
        return new LogicSchemaFactory<>(new UseSpecIDGenerator());
    }

    public static LogicSchemaFactory<LogicConstraintWithoutIDSpec> defaultLogicSchemaWithoutIDsFactory() {
        return new LogicSchemaFactory<>(new IncrementalConstraintIDGenerator());

    }

    public LogicSchemaFactory(ConstraintIDGenerator<T> constraintIDGenerator) {
        logicSchemaBuilder = new LogicSchemaBuilder<>(constraintIDGenerator);
    }

    public LogicSchema createLogicSchema(LogicSchemaSpec<T> logicSchemaSpec) {
        logicSchemaSpec.getPredicateSpecList().forEach(predicateSpec -> logicSchemaBuilder.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        logicSchemaSpec.getDerivationRuleSpecList().forEach(logicSchemaBuilder::addDerivationRule);
        logicSchemaSpec.getLogicConstraintSpecList().forEach(logicSchemaBuilder::addLogicConstraint);
        return logicSchemaBuilder.build();
    }
}
