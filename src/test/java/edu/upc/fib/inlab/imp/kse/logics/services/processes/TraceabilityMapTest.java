package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert.assertThat;

public class TraceabilityMapTest {

    @Test
    public void should_returnOriginalConstraintID_when_pipelineCreatesSeveralConstraints() {
        LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                """
                            @1 :- R(x, y), S(y)
                            R(a, b) :- T(a, b)
                            R(a, b) :- U(a, b)
                        """
        );

        SchemaTransformationProcessPipeline pipeline = normalizePipeline();
        SchemaTransformation schemaTransformation = pipeline.executeTransformation(schema);

        assertThat(schemaTransformation)
                .constraintIDComesFrom("1", "1_1")
                .constraintIDComesFrom("1", "1_2");
    }

    @Test
    public void should_returnOriginalConstraintID_when_processionCreatesOneConstraint() {
        LogicSchema schema = LogicSchemaMother.buildLogicSchemaWithIDs(
                """
                            @1 :- R(x, y), S(y)
                        """
        );

        SchemaTransformationProcessPipeline pipeline = normalizePipeline();
        SchemaTransformation schemaTransformation = pipeline.executeTransformation(schema);

        assertThat(schemaTransformation)
                .constraintIDComesFrom("1", "1");
    }

    private static SchemaTransformationProcessPipeline normalizePipeline() {
        return new SchemaTransformationProcessPipeline(List.of(
                new SchemaUnfolder(), new SingleDerivationRuleTransformer(), new BodySorter(), new PredicateCleaner()
        ));
    }

}