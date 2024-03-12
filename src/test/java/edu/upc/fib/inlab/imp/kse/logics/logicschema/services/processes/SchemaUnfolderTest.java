package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism.IsomorphismOptions;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.assertions.SchemaTransformationAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class SchemaUnfolderTest {

    @Nested
    class InputValidationTests {
        @Test
        void should_throwException_whenMultipleStrategyIsNull() {
            assertThatThrownBy(() -> new SchemaUnfolder(null, false))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenSchemaIsNull() {
            SchemaUnfolder schemaUnfolder = new SchemaUnfolder();
            LogicSchema schema = null;
            assertThatThrownBy(() -> schemaUnfolder.unfold(schema))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class UnfoldingTests {
        @Test
        void should_returnEmptySchema_whenSchemaIsEmpty() {
            LogicSchema emptySchema = LogicSchemaMother.createEmptySchema();

            LogicSchema unfoldedEmptySchema = new SchemaUnfolder().unfold(emptySchema);

            assertThat(unfoldedEmptySchema).isEmpty();
        }

        @Test
        void should_maintainDerivationRules_whenDerivationRuleNotUsedInConstraint() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                A(a, b) :- B(a, b)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                A(a, b) :- B(a, b)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Test
        void should_unfoldDerivationRule_whenDerivationRuleHasDerivedLiterals() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                P(x, y) :- R(x, y), S(y)
                                R(a, b) :- T(a, b)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                P(x, y) :- T(x, y), S(y)
                                R(a, b) :- T(a, b)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Test
        void should_unfoldLogicConstraint_whenLogicConstraintHasDerivedLiterals() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                R(a, b) :- T(a, b)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @2 :- T(x, y), S(y)
                                R(a, b) :- T(a, b)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Test
        void should_notUnfoldLogicConstraint_whenDerivedLiteralIsNegated() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- not(R(x, y)), S(x,y)
                                R(a, b) :- T(a, b)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @2 :- not(R(x, y)), S(x,y)
                                R(a, b) :- T(a, b)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Test
        void should_unfoldRecursively_whenDerivedLiteralsDependsOnDerivedLiterals() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y), y < 5
                                R(a, b) :- T(a, b)
                                T(c, d) :- U(c, d, z)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @2 :- U(x, y, z), S(y), y < 5
                                R(a, b) :- U(a, b, z)
                                T(c, d) :- U(c, d, z)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Test
        void should_createMoreThanOneNormalClause_whenDerivedLiteralHasSeveralDerivationRules() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                R(a, b) :- T(a, b)
                                R(a, b) :- U(a, b)
                                S(y) :- S1(y)
                                S(y) :- S2(y)
                            """
            );

            LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

            LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- T(x, y), S1(y)
                                @2 :- T(x, y), S2(y)
                                @3 :- U(x, y), S1(y)
                                @4 :- U(x, y), S2(y)
                                R(a, b) :- T(a, b)
                                R(a, b) :- U(a, b)
                                S(y) :- S1(y)
                                S(y) :- S2(y)
                            """
            );
            assertThat(unfoldedSchema)
                    .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                    .isIsomorphicTo(expectedSchema);
        }

        @Nested
        class UnfoldingWithConstantsInHeadTests {
            @Test
            void should_obtainOneDerivationRule_whenThereIsOneDerivationRule() {
                LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- R(x, y), S(y)
                                    R(a, 1) :- T(a, b)
                                """
                );

                LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- T(x, b), S(y), y=1
                                    R(a, 1) :- T(a, b)
                                """
                );
                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedSchema);
            }

            @Test
            void should_obtainTwoDerivationRules_whenThereAreTwoDerivationRules() {
                LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- R(x, y), S(y)
                                    R(a, 1) :- T(a, b)
                                    R(a, 2) :- TT(a, b)
                                """
                );

                LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- T(x, b), S(y), y=1
                                    P(x, y) :- TT(x, b), S(y), y=2
                                    R(a, 1) :- T(a, b)
                                    R(a, 2) :- TT(a, b)
                                """
                );
                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedSchema);
            }

            @Test
            void should_obtainOneDerivationRuleWithContradiction_whenThereIsOneDerivationRule_NotMatchingConstants() {
                LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- R(x, 2), S(y)
                                    R(a, 1) :- T(a, b)
                                """
                );

                LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- T(x, b), 1=2, S(y)
                                    R(a, 1) :- T(a, b)
                                """
                );
                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedSchema);
            }

            @Test
            void should_obtainTwoDerivationRules_whenThereAreTwoDerivationRules_WithOnlyOneConstantsMatch() {
                LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- R(x, 1), S(y)
                                    R(a, 1) :- T(a, b)
                                    R(a, 2) :- TT(a, b)
                                """
                );

                LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

                LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    P(x, y) :- T(x, b), 1=1, S(y)
                                    P(x, y) :- TT(x, b), 1=2, S(y)
                                    R(a, 1) :- T(a, b)
                                    R(a, 2) :- TT(a, b)
                                """
                );
                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedSchema);
            }
        }

        @Nested
        class UnfoldingWithRepeatedVariablesInHeadTests {
            @Test
            void should_unfold_whenFindingRepeatedVariablesInHead() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), S(x,z)
                                    R(a, b, a) :- T(a, b)
                                """);

                LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(logicSchema);

                LogicSchema expectedUnfoldedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                @1 :- T(x, y), x=z, S(x,z)
                                R(a, b, a) :- T(a, b)
                                    """
                );

                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedUnfoldedSchema);
            }
        }

        @Nested
        class UnfoldingNegatedLiterals {
            @Test
            void should_unfoldNegatedLiterals_whenUsingNegationExtendedParameter() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), not(S(x,z))
                                    S(x,z) :- T(x,z)
                                """);

                LogicSchema unfoldedSchema = new SchemaUnfolder(true)
                        .unfold(logicSchema);

                LogicSchema expectedUnfoldedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), not(T(x,z))
                                    S(x,z) :- T(x,z)
                                """);

                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, false, false))
                        .isIsomorphicTo(expectedUnfoldedSchema);
            }

            @Test
            void should_unfoldNegatedLiterals_withMoreThanOneDefinition_whenUsingNegationExtendedParameter() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), not(S(x,z))
                                    S(x,z) :- T1(x,z), T2(x,z)
                                    S(x,z) :- Q1(x,z), Q2(x,z)
                                """);

                LogicSchema unfoldedSchema = new SchemaUnfolder(true)
                        .unfold(logicSchema);

                LogicSchema expectedUnfoldedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                      @1_1 :- R(x, y, z), not(T1(x,z)), not(Q1(x,z))
                                      @1_2 :- R(x, y, z), not(T1(x,z)), not(Q2(x,z))
                                      @1_3 :- R(x, y, z), not(T2(x,z)), not(Q1(x,z))
                                      @1_4 :- R(x, y, z), not(T2(x,z)), not(Q2(x,z))
                                      S(x,z) :- T1(x,z), T2(x,z)
                                      S(x,z) :- Q1(x,z), Q2(x,z)
                                """
                );

                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedUnfoldedSchema);
            }

            @Test
            void should_unfoldNegatedLiterals_withMoreThanOneDefinitionWithOnlyOneLiteral_whenUsingNegationExtendedParameter() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), not(S(x,z))
                                    S(x,z) :- T(x,z)
                                    S(x,z) :- Q(x,z)
                                """);

                LogicSchema unfoldedSchema = new SchemaUnfolder(true)
                        .unfold(logicSchema);

                LogicSchema expectedUnfoldedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                @1 :- R(x, y, z), not(T(x,z)), not(Q(x,z))
                                S(x,z) :- T(x,z)
                                S(x,z) :- Q(x,z)
                                """
                );

                assertThat(unfoldedSchema)
                        .usingIsomorphismOptions(new IsomorphismOptions(true, true, false))
                        .isIsomorphicTo(expectedUnfoldedSchema);
            }
        }
    }


    @Nested
    class TraceabilityMapTest {

        @Test
        void should_returnOriginalConstraintID_when_unfoldingCreatesSeveralConstraints() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                R(a, b) :- T(a, b)
                                R(a, b) :- U(a, b)
                            """
            );

            SchemaTransformation schemaTransformation = new SchemaUnfolder().executeTransformation(schema);

            SchemaTransformationAssert.assertThat(schemaTransformation)
                    .constraintIDComesFrom("1_1", "1")
                    .constraintIDComesFrom("1_2", "1");
        }

        @Test
        void should_returnOriginalConstraintID_when_unfoldingCreatesOneConstraint() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                            """
            );

            SchemaTransformation schemaTransformation = new SchemaUnfolder().executeTransformation(schema);

            SchemaTransformationAssert.assertThat(schemaTransformation)
                    .constraintIDComesFrom("1", "1");
        }

    }

}