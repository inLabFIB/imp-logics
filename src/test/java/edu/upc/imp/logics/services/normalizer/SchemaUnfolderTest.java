package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.assertions.LogicSchemaAssert;
import edu.upc.imp.logics.schema.utils.LogicSchemaMother;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
class SchemaUnfolderTest {

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
                        """
        );
        LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
    }

    @Test
    public void should_unfoldRecursively_whenDerivedLiteralsDependsOnDerivedLiterals() {
        LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                """
                            @1 :- R(x, y), S(y)
                            R(a, b) :- T(a, b)
                            T(c, d) :- U(c, d, z)
                        """
        );

        LogicSchema unfoldedSchema = new SchemaUnfolder().unfold(schema);

        LogicSchema expectedSchema = LogicSchemaMother.buildLogicSchemaWithIDs(
                """
                            @2 :- T(x, y, z), S(y)
                        """
        );
        LogicSchemaAssert.assertThat(unfoldedSchema).isLogicallyEquivalentTo(expectedSchema);
    }

}