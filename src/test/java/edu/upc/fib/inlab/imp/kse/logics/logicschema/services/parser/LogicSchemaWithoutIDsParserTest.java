package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.NotExpectingConstraintID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LogicSchemaWithoutIDsParserTest {

    @Test
    void should_throwsException_whenParsingConstraint_WithoutConstraintId() {
        String schemaString = " :- p()";

        LogicSchema logicSchema = new LogicSchemaWithoutIDsParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .hasConstraintsSize(1);
    }

    @Test
    void should_containLogicConstraintWithID_whenParsingConstraint() {
        String schemaString = "@100 :- p()";

        LogicSchemaWithoutIDsParser parser = new LogicSchemaWithoutIDsParser();
        assertThatThrownBy(() -> parser.parse(schemaString))
                .isInstanceOf(NotExpectingConstraintID.class);
    }

    @Test
    void should_parseWholeSchema_whenDefiningSchema_withSeveralLogicConstraints_andSeveralDerivationRules() {
        String schemaString = """
                            :- WorksIn(E, D), not(Emp(E))
                            :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
                            :- Dept(D), not(MinOneSpecialEmployee(D))
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
                """;

        LogicSchema logicSchema = new LogicSchemaWithoutIDsParser().parse(schemaString);


        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy")
                .hasConstraintsSize(3);

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

}
