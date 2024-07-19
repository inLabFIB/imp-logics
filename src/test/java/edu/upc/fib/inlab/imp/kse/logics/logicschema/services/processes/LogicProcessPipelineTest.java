package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LogicProcessPipelineTest {

    @Test
    void should_returnInputLogicSchema_when_processPipelineIsEmpty() {
        List<LogicProcess> logicProcesses = List.of();

        LogicSchema inputSchema = LogicSchemaMother.createEmptySchema();

        LogicProcessPipeline pipeline = new LogicProcessPipeline(logicProcesses);
        LogicSchema logicSchemaOutput = pipeline.execute(inputSchema);

        assertThat(logicSchemaOutput).isEqualTo(inputSchema);
    }

    @Test
    void should_returnLogicSchemaExpected_when_processPipelineContainsOneProcess() {
        List<LogicProcess> logicProcesses = new LinkedList<>();

        LogicSchema schema = LogicSchemaMother.createEmptySchema();
        LogicSchema expectedOutputSchema = LogicSchemaMother.createEmptySchema();

        LogicProcess mock = mockLogicProcess(schema, expectedOutputSchema);
        logicProcesses.add(mock);

        LogicProcessPipeline pipeline = new LogicProcessPipeline(logicProcesses);
        LogicSchema logicSchemaOutput = pipeline.execute(schema);

        assertThat(logicSchemaOutput).isEqualTo(expectedOutputSchema);
    }

    private static LogicProcess mockLogicProcess(LogicSchema inputSchema, LogicSchema outputSchema) {
        LogicProcess mock = mock(LogicProcess.class);
        when(mock.execute(inputSchema)).thenReturn(outputSchema);
        return mock;
    }

    @ParameterizedTest(name = "should execute {0} transformation process")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void should_executeNTransformation_processPipelineContainsNProcess(int numberOfProcesses) {

        LogicSchema schema = LogicSchemaMother.createEmptySchema();

        List<LogicProcess> mocks = IntStream.range(0, numberOfProcesses)
                .mapToObj(i -> mockLogicProcess(schema, schema))
                .toList();

        LogicProcessPipeline pipeline = new LogicProcessPipeline(mocks);
        pipeline.execute(schema);

        assertThat(mocks).hasSize(numberOfProcesses);
        mocks.forEach(mock -> verify(mock, times(1)).execute(any(LogicSchema.class)));
    }

    @Nested
    class InputValidationTests {

        @Test
        void should_ThrowException_when_processPipelineIsNull() {
            assertThatThrownBy(() -> new LogicProcessPipeline(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_ThrowException_when_processPipelineContainsNull() {
            List<LogicProcess> processes = new LinkedList<>();
            processes.add(null);
            assertThatThrownBy(() -> new LogicProcessPipeline(processes))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_ThrowException_when_inputSchemaIsNull() {
            LogicProcessPipeline pipeline = new LogicProcessPipeline(List.of());
            assertThatThrownBy(() -> pipeline.execute(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

}