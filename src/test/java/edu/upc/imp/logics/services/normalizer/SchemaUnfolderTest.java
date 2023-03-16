package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.utils.LogicSchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class SchemaUnfolderTest {

    @Test
    public void should_throwException_whenSchemaIsNull() {
        assertThatThrownBy(() -> new SchemaUnfolder().unfold(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Nested
    class UnfoldingTests {
        @Test
        public void should_returnEmptySchema_whenSchemaIsEmpty() {
            LogicSchema emptySchema = LogicSchemaMother.createEmptySchema();

            LogicSchema unfoldedEmptySchema = new SchemaUnfolder().unfold(emptySchema);

            LogicSchemaAssert.assertThat(unfoldedEmptySchema).isEmpty();
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
            LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
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
            LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
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
            LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
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
            LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
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
            LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
        }
    }

}