package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.LinkedList;
import java.util.List;

import static edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTransformationAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class SchemaTransformationProcessPipelineTest {

    @Nested
    class InputValidationTests {
        @Test
        public void should_throwException_when_SchemaTransformationProcessListIsNull() {
            assertThatThrownBy(() -> new SchemaTransformationProcessPipeline(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_when_SchemaTransformationProcessContainsNull() {
            List<SchemaTransformationProcess> processList = new LinkedList<>();
            processList.add(null);
            assertThatThrownBy(() -> new SchemaTransformationProcessPipeline(processList))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_when_inputLogicSchemaIsNull() {
            SchemaTransformationProcessPipeline pipeline = new SchemaTransformationProcessPipeline(List.of());
            assertThatThrownBy(() -> pipeline.executeTransformation(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Test
    public void should_invokeTheServiceInInputOrder() {
        LogicSchema inputSchema = LogicSchemaMother.createEmptySchema();
        LogicSchema unfoldedSchema = LogicSchemaMother.createEmptySchema();
        SchemaTransformationProcess process1 = mockProcess(new SchemaUnfolder(), inputSchema, unfoldedSchema);

        LogicSchema transformedSchema = LogicSchemaMother.createEmptySchema();
        SchemaTransformationProcess process2 = mockProcess(new SingleDerivationRuleTransformer(), unfoldedSchema, transformedSchema);

        LogicSchema sortedSchema = LogicSchemaMother.createEmptySchema();
        SchemaTransformationProcess process3 = mockProcess(new BodySorter(), transformedSchema, sortedSchema);

        LogicSchema cleanedSchema = LogicSchemaMother.createEmptySchema();
        SchemaTransformationProcess process4 = mockProcess(new PredicateCleaner(), sortedSchema, cleanedSchema);

        SchemaTransformationProcessPipeline pipeline = new SchemaTransformationProcessPipeline(List.of(
                process1, process2, process3, process4
        ));
        pipeline.executeTransformation(inputSchema);

        InOrder servicesInvokedInOrder = inOrder(process1, process2, process3, process4);
        servicesInvokedInOrder.verify(process1).executeTransformation(inputSchema);
        servicesInvokedInOrder.verify(process2).executeTransformation(unfoldedSchema);
        servicesInvokedInOrder.verify(process3).executeTransformation(transformedSchema);
        servicesInvokedInOrder.verify(process4).executeTransformation(sortedSchema);
    }

    private static SchemaTransformationProcess mockProcess(SchemaTransformationProcess process, LogicSchema inputSchema, LogicSchema outputSchema) {
        SchemaTransformationProcess mockProcess = spy(process);
        SchemaTransformation outputTransformation = new SchemaTransformation(inputSchema, outputSchema, new SchemaTraceabilityMap());
        doReturn(outputTransformation).when(mockProcess).executeTransformation(inputSchema);
        return mockProcess;
    }

    @Nested
    class TraceabilityTests {
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
                    .constraintIDComesFrom("1_1", "1")
                    .constraintIDComesFrom("1_2", "1");
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

}
