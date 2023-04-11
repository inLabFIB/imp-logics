package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.DefaultTermTypeCriteria;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class LogicSchemaParserTest {

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsPredicate("p", 0);
    }

    @Test
    public void should_containPredicate_whenPredicateAppearsInConstraint_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsPredicate("p", 2);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", 0);
    }

    @Test
    public void should_containConstraint_whenConstraintIsDefined_withOrdinaryLiteral_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsConstraintID("1");

        LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));
        assertThat(logicConstraint)
                .hasID("1")
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");
    }

    @Test
    public void should_containDerivationRule_whenDerivationRuleIsDefined_withOrdinaryLiteral_withNonZeroArity() {
        String schemaString = "q(x) :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        assertThat(derivationRulesList).hasSize(1);
        DerivationRule derivationRule = derivationRulesList.get(0);
        assertThat(derivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");
    }

    @Test
    public void should_containBodyWithSeveralLiterals_whenBodyIsDefined_withSeveralLiterals() {
        String schemaString = "q(x) :- p(x, y), r(y)";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        assertThat(derivationRulesList).hasSize(1);
        DerivationRule derivationRule = derivationRulesList.get(0);
        assertThat(derivationRule)
                .hasBodySize(2)
                .containsOrdinaryLiteral("p", "x", "y")
                .containsOrdinaryLiteral("r", "y");
    }

    @Test
    public void should_containTwoDerivationRules_whenTwoDerivationRuleAreDefined_forTheSamePredicate() {
        String schemaString = """
                q(x) :- p(x, y)
                q(x) :- r(x)
                """;

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        List<DerivationRule> derivationRulesList = logicSchema.getDerivationRulesByPredicateName("q");
        assertThat(derivationRulesList).hasSize(2);
        DerivationRule firstDerivationRule = derivationRulesList.get(0);
        assertThat(firstDerivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("p", "x", "y");

        DerivationRule secondDerivationRule = derivationRulesList.get(1);
        assertThat(secondDerivationRule)
                .hasBodySize(1)
                .containsOrdinaryLiteral("r", "x");
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


        assertThat(logicSchema)
                .containsExactlyThesePredicateNames(
                        "Dept", "Rich", "WorksIn", "Emp", "Manages", "CrucialDept", "MinOneSpecialEmployee", "Happy")
                .containsExactlyTheseConstraintIDs("1", "2", "3");

        List<DerivationRule> derivationRules = logicSchema.getDerivationRulesByPredicateName("MinOneSpecialEmployee");
        assertThat(derivationRules).hasSize(2);
    }

    @Test
    public void should_parseOnlyNewSchema_whenInvokingParserTwice() {
        //Arrange
        String schemaString1 = "P(x) :- Q(x, y)";
        LogicSchemaWithIDsParser logicSchemaWithIDsParser = new LogicSchemaWithIDsParser();
        logicSchemaWithIDsParser.parse(schemaString1);

        //Action
        String schemaString2 = "R(x) :- S(x, y)";
        LogicSchema logicSchema2 = logicSchemaWithIDsParser.parse(schemaString2);
        assertThat(logicSchema2).containsExactlyThesePredicateNames("R", "S");
    }

    @Nested
    class BooleanBuiltInLiteralTest {
        @ParameterizedTest
        @MethodSource("booleanValues")
        public void should_containBooleanBuiltInLiteral_whenBodyContainsBooleanString(String booleanString, boolean booleanValue) {
            String schemaString = "@1 :- " + booleanString + "()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraint)
                    .containsBooleanBuiltInLiteral(booleanValue);
        }

        private static Stream<Arguments> booleanValues() {
            return Stream.of(
                    Arguments.of("TRUE", true),
                    Arguments.of("FALSE", false)
            );
        }
    }

    @Nested
    class CustomBuiltInLiteralTest {

        @Test
        public void should_containCustomBuiltInLiteral_whenConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new DefaultTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("myCustomBuiltInPredicate"))
            ).parse(schemaString);

            assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraint)
                    .containsCustomBuiltInLiteral("myCustomBuiltInPredicate");
        }

        @Test
        public void should_notContainCustomBuiltInLiteral_whenNotConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new DefaultTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("anotherPredicateName"))
            ).parse(schemaString);

            assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraint)
                    .containsOrdinaryLiteral("myCustomBuiltInPredicate", 0);
        }
    }
}
