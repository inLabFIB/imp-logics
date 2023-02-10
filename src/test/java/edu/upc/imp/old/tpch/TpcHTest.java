package edu.upc.imp.old.tpch;

import edu.upc.imp.old.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.imp.old.logicschema.DerivationRule;
import edu.upc.imp.old.logicschema.LogicConstraint;
import edu.upc.imp.old.logicschema.LogicSchema;
import edu.upc.imp.old.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.imp.old.parser.LogicSchemaParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TpcHTest {

    @Test
    public void test() throws URISyntaxException {
        File tpchSchemaFile = new File(ClassLoader.getSystemResource("tpc-h.txt").toURI());
        File tpchAugmentedSchemaFile = new File(ClassLoader.getSystemResource("tpc-h_augmented.txt").toURI());
        File tpchNormalizedSchemaFile = new File(ClassLoader.getSystemResource("tpc-h_normalized.txt").toURI());

        assert tpchSchemaFile.exists() : "File " + tpchSchemaFile.getAbsolutePath() + " does not exists";
        assert tpchAugmentedSchemaFile.exists() : "File " + tpchSchemaFile.getAbsolutePath() + " does not exists";
        assert tpchNormalizedSchemaFile.exists() : "File " + tpchSchemaFile.getAbsolutePath() + " does not exists";

        LogicConstraint.reset();

        System.out.println("Parsing files");
        LogicSchemaParser parser = new LogicSchemaParser(tpchSchemaFile);
        parser.parse();
        LogicSchema inputSchema = parser.getLogicSchema();

        LogicSchemaParser parserAug = new LogicSchemaParser(tpchAugmentedSchemaFile);
        parserAug.parse();
        LogicSchema expectedAugmentedSchema = parserAug.getLogicSchema();

        LogicSchemaParser parserNorm = new LogicSchemaParser(tpchNormalizedSchemaFile);
        parserNorm.parse();
        LogicSchema expectedNormalizedSchema = parserNorm.getLogicSchema();

        System.out.println("Augmenting schema");
        LogicSchemaAugmenter augmenter = new LogicSchemaAugmenter(inputSchema);
        augmenter.augment();
        LogicSchema augmentedSchema = augmenter.getAugmentedLogicSchema();
        checkEqualityOfSchemas(augmentedSchema, expectedAugmentedSchema);

        System.out.println("Normalizing schema");
        LogicSchemaNormalizer normalizer = new LogicSchemaNormalizer(augmentedSchema);
        normalizer.normalize();
        LogicSchema normalizedSchema = normalizer.getNormalizedLogicSchema();
        checkEqualityOfSchemas(normalizedSchema, expectedNormalizedSchema);
    }

    private void checkEqualityOfSchemas(LogicSchema obtainedSchema, LogicSchema expectedSchema) {
        List<LogicConstraint> expectedConstraints = expectedSchema.getAllConstraints();
        List<DerivationRule> expectedDerivationRules = expectedSchema.getAllDerivationRules();
        List<LogicConstraint> obtainedConstraints = obtainedSchema.getAllConstraints();
        List<DerivationRule> obtainedDerivationRules = obtainedSchema.getAllDerivationRules();

        assertEquals(obtainedConstraints.size(), expectedConstraints.size());
        assertEquals(obtainedDerivationRules.size(), expectedDerivationRules.size());

        for (LogicConstraint lc : obtainedConstraints) {
            System.out.println(lc);
            assertTrue(expectedConstraints.contains(lc));
        }
        for (DerivationRule dr : obtainedDerivationRules) {
            System.out.println(dr);
            assertTrue(expectedDerivationRules.contains(dr));
        }
    }
}
