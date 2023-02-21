package edu.upc.imp.logics.parser;

import edu.upc.imp.logics.assertions.LogicConstraintAssert;
import edu.upc.imp.logics.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.schema.LogicConstraint;
import edu.upc.imp.logics.schema.LogicSchema;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
public class LogicSchemaParserTest {

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsPredicate("p", 0);
    }

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsPredicate("p", 2);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", 0);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined_withOrdinaryLiteral_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        LogicConstraintAssert.assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");
    }

}
