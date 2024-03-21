package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicSchemaSpec;

/**
 * <p> Class responsible for creating a logic schema for a given logic schema specification.
 * The factory will ensure that there is a Predicate for each predicate name used in the specification.
 * That is, if a logic constraint or derivation rule specification uses a predicate name "P", which
 * has not been specified as a predicate, the factory will automatically create such predicate P. </p>
 *
 * <p> The factory must work with either LogicConstraintSpecWithIDs, or LogicConstraintSpecWithoutIDs, but not
 * both at the same time.</p>
 *
 * @param <T> kind of LogicConstraintSpec this class works with
 */
public class LogicSchemaFactory<T extends LogicConstraintSpec> {
    private final ConstraintIDGenerator<T> constraintIDGenerator;

    public static LogicSchemaFactory<LogicConstraintWithIDSpec> defaultLogicSchemaWithIDsFactory() {
        return new LogicSchemaFactory<>(new UseSpecIDGenerator());
    }

    public static LogicSchemaFactory<LogicConstraintWithoutIDSpec> defaultLogicSchemaWithoutIDsFactory() {
        return new LogicSchemaFactory<>(new IncrementalConstraintIDGenerator());

    }

    public LogicSchemaFactory(ConstraintIDGenerator<T> constraintIDGenerator) {
        this.constraintIDGenerator = constraintIDGenerator;
    }

    public LogicSchema createLogicSchema(LogicSchemaSpec<T> logicSchemaSpec) {
        LogicSchemaBuilder<T> logicSchemaBuilder = new LogicSchemaBuilder<>(constraintIDGenerator);
        logicSchemaSpec.getPredicateSpecList().forEach(predicateSpec -> logicSchemaBuilder.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        logicSchemaSpec.getDerivationRuleSpecList().forEach(logicSchemaBuilder::addDerivationRule);
        logicSchemaSpec.getLogicConstraintSpecList().forEach(logicSchemaBuilder::addLogicConstraint);
        return logicSchemaBuilder.build();
    }
}
