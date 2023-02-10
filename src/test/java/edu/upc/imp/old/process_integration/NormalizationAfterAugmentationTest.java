package edu.upc.imp.old.process_integration;

import edu.upc.imp.old.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.imp.old.logicschema.LogicSchema;
import edu.upc.imp.old.logicschema.NormalClause;
import edu.upc.imp.old.logicschema.OrdinaryLiteral;
import edu.upc.imp.old.logicschema.Predicate;
import edu.upc.imp.old.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.imp.old.parser.LogicSchemaParser;
import edu.upc.imp.old.pipeline.LogicSchemaProcess;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NormalizationAfterAugmentationTest {

    @Test
    public void testUnfoldingsForComplexConstraints() {
        String logicSchemaString = "% Constraints\n" +
                "  @1 :- A(_0), not(??aux1(_0))\n" +
                "% DerivationRules\n" +
                "  ??aux1(_0) :- B(_0), C(_0), not(??aux2(_0))\n" +
                "  ??aux2(_0) :- D(_0), E(_0), not(??aux3(_0))\n" +
                "  ??aux3(_0) :- F(_0), G(_0)\n";
        LogicSchemaParser parser = new LogicSchemaParser(logicSchemaString);
        parser.parse();
        LogicSchema logicSchema = parser.getLogicSchema();

        LogicSchemaProcess augmenter = new LogicSchemaAugmenter(logicSchema);
        augmenter.execute();
        LogicSchema augmentedSchema = augmenter.getOutputSchema();

        LogicSchemaProcess normalizer = new LogicSchemaNormalizer(augmentedSchema);
        normalizer.execute();
        LogicSchema normalizedSchema = normalizer.getOutputSchema();

        assertNoPositiveDerivedLiterals(normalizedSchema);
        assertNoMultipleDerivationRulesForOnePredicate(normalizedSchema);
    }

    private void assertNoPositiveDerivedLiterals(LogicSchema logicSchema) {
        for (NormalClause nc : logicSchema.getAllNormalClauses()) {
            for (OrdinaryLiteral ol : nc.getOrdinaryLiterals()) {
                assertTrue(ol.isBase() || ol.isNegated());
            }
        }
    }

    private void assertNoMultipleDerivationRulesForOnePredicate(LogicSchema logicSchema) {
        for (Predicate p : logicSchema.getAllPredicates()) {
            if (p.getDefinitionRules().size() > 1) System.out.println(p);
            assertTrue(p.getDefinitionRules().size() <= 1);
        }
    }
}
