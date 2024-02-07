package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.CapitalConstantsTermTypeCriteria;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogicSchemaParserTest {

    @Nested
    class PredicateAndVariableSyntaxTests {
        @Test
        void should_allowPredicateNames_withSymbols() {
            String schemaString = "@1 :- p:$?_s(a)";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            assertThat(logicSchema)
                    .containsPredicate("p:$?_s", 1);
        }

        @Test
        void should_allowTermNames_withSymbols() {
            String schemaString = "@1 :- p(a:$?_s)";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            assertThat(logicSchema.getAllLogicConstraints())
                    .hasSize(1);

            LogicConstraint logicConstraint = logicSchema.getAllLogicConstraints().stream().findFirst().orElseThrow();

            assertThat(logicConstraint.getBody())
                    .hasSize(1)
                    .first()
                    .hasPredicate("p", 1)
                    .hasVariable(0, "a:$?_s");
        }
    }

    @Test
    void should_fail_whenSchemaContainsErrorSyntax() {
        String schemaString = """
                            "@1 :- q(x),
                            q(x) :- p(x, y), r(y)
                """;

        LogicSchemaWithIDsParser logicSchemaWithIDsParser = new LogicSchemaWithIDsParser();
        assertThatThrownBy(() -> logicSchemaWithIDsParser.parse(schemaString));
    }

    @Test
    void should_containPredicate_whenPredicateAppearsInConstraint() {
        String schemaString = "@1 :- p()";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsPredicate("p", 0);
    }

    @Test
    void should_containPredicate_whenPredicateAppearsInConstraint_withNonZeroArity() {
        String schemaString = "@1 :- p(x, y)";

        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

        assertThat(logicSchema)
                .containsPredicate("p", 2);
    }

    @Test
    void should_containConstraint_whenConstraintIsDefined() {
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
    void should_containConstraint_whenConstraintIsDefined_withOrdinaryLiteral_withNonZeroArity() {
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
    void should_containDerivationRule_whenDerivationRuleIsDefined_withOrdinaryLiteral_withNonZeroArity() {
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
    void should_containBodyWithSeveralLiterals_whenBodyIsDefined_withSeveralLiterals() {
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
    void should_containTwoDerivationRules_whenTwoDerivationRuleAreDefined_forTheSamePredicate() {
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
    void should_parseWholeSchema_whenDefiningSchema_withSeveralLogicConstraints_andSeveralDerivationRules() {
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
    void should_parseOnlyNewSchema_whenInvokingParserTwice() {
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
        void should_containBooleanBuiltInLiteral_whenBodyContainsBooleanString(String booleanString, boolean booleanValue) {
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
        void should_containCustomBuiltInLiteral_whenConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("myCustomBuiltInPredicate"))
            ).parse(schemaString);

            assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraint)
                    .containsCustomBuiltInLiteral("myCustomBuiltInPredicate");
        }

        @Test
        void should_notContainCustomBuiltInLiteral_whenNotConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("anotherPredicateName"))
            ).parse(schemaString);

            assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            assertThat(logicConstraint)
                    .containsOrdinaryLiteral("myCustomBuiltInPredicate", 0);
        }
    }

    @Nested
    class ConstantAndVariableParsingTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "0", "1", "01", "1000",
                "1.0", "0.0", "1000.0", ".000001",
                "1.0e-10", "1.0e10", "1.0e+10",
                "1.0E-10", "1.0E10", "1.0E+10"
        })
        void should_parseConstant_whenTermIsNumber(String numberString) {
            String schemaString = "@1 :- P(" + numberString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, numberString);
        }

        @ParameterizedTest
        @ValueSource(strings = {"''", "'1'", "'a'", "'abc'", "'Escape \\' test'", "'Hello \n World'"})
        void should_parseConstant_whenTermIsSingleQuotes(String constantString) {
            String schemaString = "@1 :- P(" + constantString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, constantString);
        }

        @ParameterizedTest
        @ValueSource(strings = {"\"\"", "\"1\"", "\"a\"", "\"abc\"", "\"Escape \\\" test\"", "\"Hello \n World\""})
        void should_parseConstant_whenTermIsDoubleQuotes(String constantString) {
            String schemaString = "@1 :- P(" + constantString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, constantString);
        }

        @Test
        void should_parseConstant_whenTermIsID_andCriteriaMakesItConstant() {
            String schemaString = "@1 :- P(A)";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser(new CapitalConstantsTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of())).parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, "A");
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "a'", "a123"})
        void should_parseVariable_whenTermIsID_andCriteriaMakesItVariable(String variableString) {
            String schemaString = "@1 :- P(" + variableString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser(new CapitalConstantsTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of())).parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasVariable(0, variableString);
        }
    }
}
