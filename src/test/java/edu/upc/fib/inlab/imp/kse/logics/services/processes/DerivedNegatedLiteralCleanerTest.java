package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;


@Disabled
class DerivedNegatedLiteralCleanerTest {

    @Nested
    class NoTermsInHeadTests {
        @Nested
        class LogicConstraintCleaning {
            @Test
            public void should_removeDerivedNegatedLiteral_whenDefinitionRuleContainsSingleRule_withSingleLiteral() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(Derived())
                        Derived() :- A()
                        """);

                LogicSchema cleanedSchema = new DerivedNegatedLiteralCleaner().clean(logicSchema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(A())
                        Derived() :- A()
                        """);

                LogicSchemaAssert.assertThat(cleanedSchema).hasSameStructureAs(expectedSchema);
            }

            public static Stream<Arguments> literalsAndItsNegation() {
                return Stream.of(
                        Arguments.of("A()", "not(A())"),
                        Arguments.of("not(A())", "A()"),
                        Arguments.of("TRUE()", "FALSE()"),
                        Arguments.of("FALSE()", "TRUE"),
                        Arguments.of("1 < 2", "1 >= 2"),
                        Arguments.of("1 <= 2", "1 > 2"),
                        Arguments.of("1 = 2", "1 <> 2"),
                        Arguments.of("1 <> 2", "1 = 2"),
                        Arguments.of("1 >= 2", "1 < 2"),
                        Arguments.of("1 > 2", "1 <= 2"),
                        Arguments.of(List.of(), 0)
                );
            }

            @ParameterizedTest
            @MethodSource("literalsAndItsNegation")
            public void should_removeDerivedNegatedLiteral_whenDefinitionRuleContainsSingleRule_withSingleNegatedLiteral(String literal, String negatedLiteral) {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        "@1 :- P(x), not(Derived())\n" +
                                "Derived() :- " + literal
                );

                LogicSchema cleanedSchema = new DerivedNegatedLiteralCleaner().clean(logicSchema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        "@1 :- P(x), " + negatedLiteral + "\\n" +
                                "Derived() :- " + literal
                );

                LogicSchemaAssert.assertThat(cleanedSchema).hasSameStructureAs(expectedSchema);
            }

            @Test
            public void should_removeDerivedNegatedLiteral_whenDefinitionRuleContainsSingleRule_withMultipleLiterals() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(Derived())
                        Derived() :- A(), B()
                        """);

                LogicSchema cleanedSchema = new DerivedNegatedLiteralCleaner().clean(logicSchema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(A())
                        @2 :- P(x), not(B())
                        Derived() :-  A(), B()
                        """);

                LogicSchemaAssert.assertThat(cleanedSchema).hasSameStructureAs(expectedSchema);
            }

            @Test
            public void should_removeDerivedNegatedLiteral_whenThereAreSeveralDefinitionRules_withSingleLiteral() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(Derived())
                        Derived() :- A()
                        Derived() :- B()
                        """);

                LogicSchema cleanedSchema = new DerivedNegatedLiteralCleaner().clean(logicSchema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(A()), not(B())
                        Derived() :- A()
                        Derived() :- B()
                        """);

                LogicSchemaAssert.assertThat(cleanedSchema).hasSameStructureAs(expectedSchema);
            }

            @Test
            public void should_removeDerivedNegatedLiteral_whenThereAreSeveralDefinitionRules_withSeveralLiterals() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(Derived())
                        Derived() :- A1(), A2(), A3()
                        Derived() :- B1(), B2()
                        """);

                LogicSchema cleanedSchema = new DerivedNegatedLiteralCleaner().clean(logicSchema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs("""
                        @1 :- P(x), not(A1()), not(B1())
                        @2 :- P(x), not(A1()), not(B2())
                        @3 :- P(x), not(A2()), not(B1())
                        @4 :- P(x), not(A2()), not(B2())
                        @5 :- P(x), not(A3()), not(B1())
                        @6 :- P(x), not(A3()), not(B2())
                        Derived() :- A1(), A2(), A3()
                        Derived() :- B1(), B2()
                        """);

                LogicSchemaAssert.assertThat(cleanedSchema).hasSameStructureAs(expectedSchema);
            }
        }
    }
}