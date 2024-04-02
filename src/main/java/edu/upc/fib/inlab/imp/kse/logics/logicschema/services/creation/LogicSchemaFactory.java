package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicSchemaSpec;

import java.util.Set;

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
        return createLogicSchema(logicSchemaSpec, Set.of());
    }

    public LogicSchema createLogicSchema(LogicSchemaSpec<T> logicSchemaSpec, Set<Predicate> relationalSchema) {
        return new LogicSchemaBuilder<>(constraintIDGenerator, relationalSchema)
                .addAllPredicates(logicSchemaSpec.getPredicateSpecList())
                .addAllDerivationRules(logicSchemaSpec.getDerivationRuleSpecList())
                .addAllLogicConstraints(logicSchemaSpec.getLogicConstraintSpecList())
                .build();
    }
}
