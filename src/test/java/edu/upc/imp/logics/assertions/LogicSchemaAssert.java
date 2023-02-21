package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.LogicSchema;
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
}
