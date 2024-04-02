package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.assertions.SchemaTransformationAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SingleDerivationRuleTransformerTest {

    @Nested
    class InputValidation {
        @Test
        void should_throwException_when_generatorIdIsNull() {
            assertThatThrownBy(() -> new SingleDerivationRuleTransformer(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenLogicSchemaIsNull() {
            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            assertThatThrownBy(() -> singleDerivationRuleTransformer.transform(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("LogicSchema cannot be null");
        }

        @Test
        void should_returnEmptySchema_whenInputLogicSchemaIsEmpty() {
            String schemaString = "";
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();

            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);
            assertThat(logicSchemaTransformed).isEmpty();
        }

        @Test
        void should_transformLogicSchema_whenInputLogicSchemaContainsOnlyBasePredicates() {
            LogicSchema logicSchema = new LogicSchema(Set.of(new Predicate("P", 0)), Collections.emptySet());

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();

            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);
            assertThat(logicSchemaTransformed).containsExactlyThesePredicateNames("P");
        }
    }

    @Nested
    class ByProperties {

        @Test
        void should_maintainDerivationRules_whenNotUsedInLogicConstraints() {
            String schemaString = """
                    @1 :- T(x, y), P(x)
                    A(x) :- B(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchema = """
                    @1 :- T(x, y), P(x)
                    A(x) :- B(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchema);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        @Test
        void should_notMaintain_DerivedPredicatesWithMultipleRules() {
            String schemaString = """
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThatThrownBy(() -> logicSchemaTransformed.getPredicateByName("P"))
                    .isInstanceOf(PredicateNotExists.class);
        }

        @Test
        void should_transformLogicSchema_FromOneConstraintWithNDerivationRules_ToNConstraints_withPositiveLiteral() {
            String schemaString = """
                    @1 :- T(x, y), P(x)
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThat(logicSchemaTransformed)
                    .hasConstraintsSize(2);
        }

        @Test
        void should_transformLogicSchema_FromOneConstraintWithNDerivationRules_ToOneConstraint_withNegativeLiteral() {
            String schemaString = """
                    @1 :- T(x, y), not(P(x))
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            // Constraint literal size - 1 + N derivation rules
            assertThat(logicSchemaTransformed).hasConstraintsSize(1);
            ImmutableLiteralsList constraintLiterals = logicSchemaTransformed.getAllLogicConstraints().iterator().next().getBody();
            assertThat(constraintLiterals).hasSize(3);
        }

        @Test
        void should_transformLogicSchema_FromNDerivationsRulesWithSameHeadPredicate_ToNDerivationRulesContainsDifferentHeadPredicates() {
            String schemaString = """
                    @1 :- T(x, y), P(x)
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            Set<String> headPredicates = logicSchemaTransformed.getAllDerivationRules().stream()
                    .map(DerivationRule::getHead)
                    .map(Atom::getPredicateName)
                    .collect(Collectors.toSet());
            assertThat(headPredicates).hasSize(2);
        }
    }

    @Nested
    class ByExample {

        @Test
        void should_notRemoveLogicConstraint_whenOnlyContainsATrueBooleanBuiltInLiteral() {
            String schemaString = """
                    @1 :- TRUE()
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(logicSchema);
        }

        @Test
        void should_notRemoveDerivationRule_whenOnlyContainsATrueBooleanBuiltInLiteral() {
            String schemaString = """
                    P(x) :- TRUE()
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(logicSchema);
        }


        @Test
        void should_notRemoveLogicConstraint_whenOnlyContainsAFalseBooleanBuiltInLiteral() {
            String schemaString = """
                    @1 :- FALSE()
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(logicSchema);
        }

        @Test
        void should_notTransformLogicSchema_whenNotContainsMultipleDerivationsRulesByPredicate() {
            String schemaString = """
                    @1 :- T(x, y), P(x)
                    P(x) :- R(x, y)
                    @2 :- A(x, y), B(x)
                    B(x) :- R(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, false))
                    .isIsomorphicTo(logicSchema);
        }

        @Test
        void should_transformLogicSchema_withPositiveLiterals() {
            String schemaString = """
                    @1 :- T(x, y), P(x)
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchemaString = """
                    @1 :- T(x, y), P_1(x)
                    @2 :- T(x, y), P_2(x)
                    P_1(x) :- R(x, y)
                    P_2(x) :- S(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchemaString);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, true))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        @Test
        void should_transformLogicSchema_withNegativeLiterals() {
            String schemaString = """
                    @1 :- T(x, y), not(P(x))
                    P(x) :- R(x, y)
                    P(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchemaString = """
                    @1 :- T(x, y), not(P_1(x)), not(P_2(x))
                    P_1(x) :- R(x, y)
                    P_2(x) :- S(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchemaString);

            LogicConstraint actualLogicConstraint = logicSchemaTransformed.getAllLogicConstraints().iterator().next();
            LogicConstraint expectedLogicConstraint = expectedLogicSchema.getLogicConstraintByID(new ConstraintID("1"));
            assertThat(actualLogicConstraint.getBody())
                    .isLogicallyEquivalentTo(expectedLogicConstraint.getBody());
        }


        @Test
        void should_transformLogicSchema_withMultipleDerivationRules_definedOverPredicates_WithMultipleDerivationRules_withPositiveLiterals() {
            String schemaString = """
                    @1 :- P(x)
                    P(x) :- A(x, y)
                    P(x) :- B(x, y)
                    A(x, y) :- C(x, y)
                    A(x, y) :- D(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchemaString = """
                    @1 :- P_1(x)
                    @2 :- P_2(x)
                    @3 :- P_3(x)
                    A_1(x, y) :- C(x, y)
                    A_2(x, y) :- D(x, y)
                    P_1(x) :- A_1(x, y)
                    P_2(x) :- A_2(x, y)
                    P_3(x) :- B(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchemaString);
            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, true))
                    .isIsomorphicTo(expectedLogicSchema);
        }

        @Test
        void should_transformLogicSchema_withMultipleDerivationRules_definedOverPredicates_WithMultipleDerivationRules_withNegativeLiterals() {
            String schemaString = """
                    @1 :- Q(x), not(P(x))
                    P(x) :- A(x, y)
                    P(x) :- B(x, y)
                    A(x, y) :- C(x, y)
                    A(x, y) :- D(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchemaString = """
                    @1 :- Q(x), not(P_1(x)), not(P_2(x)), not(P_3(x))
                    P_1(x) :- A_1(x, y)
                    P_2(x) :- A_2(x, y)
                    P_3(x) :- B(x, y)
                    A_1(x, y) :- C(x, y)
                    A_2(x, y) :- D(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchemaString);
            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, true))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }

    @Nested
    class Traceability {

        @Test
        void should_maintainTraceabilityMap_when_transformLogicSchemaCreatesSeveralConstraints() {
            LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                         @1 :- T(x, y), P(x)
                         P(x) :- R(x, y)
                         P(x) :- S(x, y)
                    """);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            SchemaTransformation schemaTransformation = singleDerivationRuleTransformer.executeTransformation(originalSchema);

            SchemaTransformationAssert.assertThat(schemaTransformation)
                    .constraintIDComesFrom("1_1", "1")
                    .constraintIDComesFrom("1_2", "1");
        }

        @Test
        void should_maintainTraceabilityMap_when_transformLogicSchemaDoesNotCreateSeveralConstraints() {
            LogicSchema originalSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                         @1 :- T(x, y), P(x)
                    """);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            SchemaTransformation schemaTransformation = singleDerivationRuleTransformer.executeTransformation(originalSchema);

            SchemaTransformationAssert.assertThat(schemaTransformation)
                    .constraintIDComesFrom("1", "1");
        }
    }

    @Nested
    class PredicateNameSuffixes {

        @Test
        void should_movePrimesToEndPredicateName_whenAddSuffix() {
            String schemaString = """
                    @1 :- T(x, y), not(P''(x))
                    P''(x) :- R(x, y)
                    P''(x) :- S(x, y)
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            SingleDerivationRuleTransformer singleDerivationRuleTransformer = new SingleDerivationRuleTransformer();
            LogicSchema logicSchemaTransformed = singleDerivationRuleTransformer.transform(logicSchema);

            String expectedSchemaString = """
                    @1 :- T(x, y), not(P_1''(x)), not(P_2''(x))
                    P_1''(x) :- R(x, y)
                    P_2''(x) :- S(x, y)
                    """;
            LogicSchema expectedLogicSchema = new LogicSchemaWithIDsParser().parse(expectedSchemaString);

            assertThat(logicSchemaTransformed)
                    .usingIsomorphismOptions(new IsomorphismOptions(false, false, true))
                    .isIsomorphicTo(expectedLogicSchema);
        }

    }
}