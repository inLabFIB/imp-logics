package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.ExpectingConstraintIDException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LogicSchemaWithIDsParserTest {

    @Test
    void should_throwsException_whenParsingConstraint_WithoutConstraintId() {
        String schemaString = " :- p()";

        LogicSchemaWithIDsParser parser = new LogicSchemaWithIDsParser();
        assertThatThrownBy(() -> parser.parse(schemaString))
                .isInstanceOf(ExpectingConstraintIDException.class);
    }

    @Test
    void should_containLogicConstraintWithID_whenParsingConstraint() {
        String schemaString = "@100 :- p()";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("100");
    }

    @Test
    void should_parseWholeSchema_whenDefiningSchema_withSeveralLogicConstraints_andSeveralDerivationRules() {
        String schemaString = """
                            @1 :- WorksIn(E, D), not(Emp(E))
                            @2 :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
                            @3 :- Dept(D), not(MinOneSpecialEmployee(D))
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
                            MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
                """;

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);


        LogicSchemaAssert.assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

}
