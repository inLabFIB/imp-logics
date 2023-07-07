package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.ExpectingConstraintID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class LogicSchemaWithIDsParserTest {

    @Test
    public void should_throwsException_whenParsingConstraint_WithoutConstraintId() {
        String schemaString = " :- p()";

        assertThatThrownBy(() -> new LogicSchemaWithIDsParser().parse(schemaString))
                .isInstanceOf(ExpectingConstraintID.class);
    }

    @Test
    public void should_containLogicConstraintWithID_whenParsingConstraint() {
        String schemaString = "@100 :- p()";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        LogicSchemaAssert.assertThat(logicSchema)
                .containsConstraintID("100");
    }

    @Test
    public void should_parseWholeSchema_whenDefiningSchema_withSeveralLogicConstraints_andSeveralDerivationRules() {
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
