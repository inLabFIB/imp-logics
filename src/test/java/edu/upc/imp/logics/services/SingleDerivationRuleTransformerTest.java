package edu.upc.imp.logics.services;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SingleDerivationRuleTransformerTest {

    @Nested
    class ByProperties {
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

            LogicSchemaAssert.assertThat(logicSchemaTransformed)
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
            LogicSchemaAssert.assertThat(logicSchemaTransformed).hasConstraintsSize(1);
            ImmutableLiteralsList constraintLiterals = logicSchemaTransformed.getAllLogicConstraints().iterator().next().getBody();
            ImmutableLiteralsListAssert.assertThat(constraintLiterals).hasSize(3);
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

            LogicSchemaAssert.assertThat(logicSchemaTransformed).isLogicallyEquivalentTo(logicSchema);
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

            LogicSchemaAssert.assertThat(logicSchemaTransformed).isLogicallyEquivalentTo(expectedLogicSchema);
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
            ImmutableLiteralsListAssert.assertThat(actualLogicConstraint.getBody())
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
            LogicSchemaAssert.assertThat(logicSchemaTransformed).isLogicallyEquivalentTo(expectedLogicSchema);
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
            LogicSchemaAssert.assertThat(logicSchemaTransformed).isLogicallyEquivalentTo(expectedLogicSchema);
        }

    }
}