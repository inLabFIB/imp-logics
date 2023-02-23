package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.Predicate;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class LogicSchemaAssert extends AbstractAssert<LogicSchemaAssert, LogicSchema> {
    public LogicSchemaAssert(LogicSchema logicSchema) {
        super(logicSchema, LogicSchemaAssert.class);
    }

    public static LogicSchemaAssert assertThat(LogicSchema actual) {
        return new LogicSchemaAssert(actual);
    }

    public LogicSchemaAssert containsPredicate(String predicateName, int arity) {
        Assertions.assertThat(actual.getAllPredicates())
                .anySatisfy(predicate -> PredicateAssert.assertThat(predicate)
                        .hasName(predicateName)
                        .hasArity(arity));
        return this;
    }

    public LogicSchemaAssert containsConstraintID(String constraintID) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .anySatisfy(constraint -> LogicConstraintAssert.assertThat(constraint)
                        .hasID(constraintID));
        return this;
    }

    public LogicSchemaAssert containsExactlyThesePredicateNames(String... predicateNames) {
        Assertions.assertThat(actual.getAllPredicates())
                .map(Predicate::getName)
                .containsExactlyInAnyOrder(predicateNames);
        return this;
    }

    public LogicSchemaAssert containsExactlyTheseConstraintIDs(String... constraintIDs) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .map(LogicConstraint::getID)
                .map(ConstraintID::id)
                .containsExactlyInAnyOrder(constraintIDs);
        return this;
    }

    public void hasConstraintsSize(int size) {
        Assertions.assertThat(actual.getAllLogicConstraints()).hasSize(size);
    }
}
