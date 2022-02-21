package edu.upc.mpi.tpch;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema_normalizer.LogicSchemaNormalizer;
import edu.upc.mpi.parser.LogicSchemaParser;
import org.junit.*;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;

public class MPILogicsIntegrationTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    //Main objective: check time consumption of the whole process(parse + normalise + augment)
    @Test
    public void shouldParseAugmentAndNormalise_whenAssertionsFileIsLoaded() throws URISyntaxException {
        // Arrange -> load assertion to logic file
        Path assertions = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("logicSchemaTINTIN.txt")).toURI());
        assert Files.exists(assertions);


        // Action
        // execute parser
        LogicSchemaParser parser = new LogicSchemaParser(assertions.toFile());
        parser.parse();
        LogicSchema schema = parser.getLogicSchema();

        // augment
        LogicSchemaAugmenter augmenter = new LogicSchemaAugmenter(schema);
        augmenter.augment();
        schema = augmenter.getAugmentedLogicSchema();

        // normalise
        LogicSchemaNormalizer normalizer = new LogicSchemaNormalizer(schema);
        normalizer.normalize();
        schema = normalizer.getNormalizedLogicSchema();

        // Assert (should be improved)
        assertNotNull(schema.getAllConstraints());
    }
}
