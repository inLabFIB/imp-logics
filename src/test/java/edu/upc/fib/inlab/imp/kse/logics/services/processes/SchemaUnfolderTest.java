package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class SchemaUnfolderTest {

    @Nested
    class InputValidationTests {
        @Test
        public void should_throwException_whenMultipleStrategyIsNull() {
            assertThatThrownBy(() -> new SchemaUnfolder(null, ImmutableLiteralsList.KindOfUnfolding.STANDARD))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenKindOfUnfoldingIsNull_withTwoParams() {
            assertThatThrownBy(() -> new SchemaUnfolder(new SuffixMultipleConstraintIDGenerator(), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenKindOfUnfoldingIsNull_withOneParam() {
            assertThatThrownBy(() -> new SchemaUnfolder(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenSchemaIsNull() {
            SchemaUnfolder unfolder = new SchemaUnfolder();
            assertThatThrownBy(() -> unfolder.unfold(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class UnfoldingTests {
        @Test
        public void should_returnEmptySchema_whenSchemaIsEmpty() {
            LogicSchema emptySchema = LogicSchemaMother.createEmptySchema();

            LogicSchema unfoldedEmptySchema = new SchemaUnfolder().unfold(emptySchema);

            assertThat(unfoldedEmptySchema).isEmpty();
        }

        @Test
        public void should_unfoldDerivationRule_whenDerivationRuleHasDerivedLiterals() {
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
            assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }

        @Test
        public void should_unfoldLogicConstraint_whenLogicConstraintHasDerivedLiterals() {
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
            assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }

        @Test
        public void should_notUnfoldLogicConstraint_whenDerivedLiteralIsNegated() {
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
            assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }

        @Test
        public void should_unfoldRecursively_whenDerivedLiteralsDependsOnDerivedLiterals() {
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
            assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }

        @Test
        public void should_createMoreThanOneNormalClause_whenDerivedLiteralHasSeveralDerivationRules() {
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
            assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }

        @Nested
        class UnfoldingWithConstantsInHeadTests {
            @Test
            public void should_obtainOneDerivationRule_whenThereIsOneDerivationRule() {
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
                assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
            }

            @Test
            public void should_obtainTwoDerivationRules_whenThereAreTwoDerivationRules() {
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
                assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
            }

            @Test
            public void should_obtainOneDerivationRuleWithContradiction_whenThereIsOneDerivationRule_NotMatchingConstants() {
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
                assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
            }

            @Test
            public void should_obtainTwoDerivationRules_whenThereAreTwoDerivationRules_WithOnlyOneConstantsMatch() {
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
                assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
            }
        }

        @Nested
        class UnfoldingWithRepeatedVariablesInHeadTests {
            @Test
            public void should_unfold_whenFindingRepeatedVariablesInHead() {
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

                LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedUnfoldedSchema);
            }
        }

        @Nested
        class UnfoldingNegatedLiterals {
            @Test
            public void should_unfoldNegatedLiterals_whenUsingNegationExtendedParameter() {
                LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                    @1 :- R(x, y, z), not(S(x,z))
                                    S(x,z) :- T(x,z)
                                """);

                LogicSchema unfoldedSchema = new SchemaUnfolder(ImmutableLiteralsList.KindOfUnfolding.NEGATION_EXTENDED)
                        .unfold(logicSchema);

                LogicSchema expectedUnfoldedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                        """
                                @1 :- R(x, y, z), not(T(x,z))
                                S(x,z) :- T(x,z)
                                    """
                );

                LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedUnfoldedSchema);
            }
        }
    }


    @Nested
    class TraceabilityMapTest {

        @Test
        public void should_returnOriginalConstraintID_when_unfoldingCreatesSeveralConstraints() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                                R(a, b) :- T(a, b)
                                R(a, b) :- U(a, b)
                            """
            );

            SchemaTransformation schemaTransformation = new SchemaUnfolder().unfoldTransformation(schema);

            assertThat(schemaTransformation)
                    .constraintIDComesFrom("1", "1_1")
                    .constraintIDComesFrom("1", "1_2");
        }

        @Test
        public void should_returnOriginalConstraintID_when_unfoldingCreatesOneConstraint() {
            LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                    """
                                @1 :- R(x, y), S(y)
                            """
            );

            SchemaTransformation schemaTransformation = new SchemaUnfolder().unfoldTransformation(schema);

            assertThat(schemaTransformation)
                    .constraintIDComesFrom("1", "1");
        }

    }

}