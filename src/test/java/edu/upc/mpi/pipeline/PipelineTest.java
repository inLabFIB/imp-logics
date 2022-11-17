package edu.upc.mpi.pipeline;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.Literal;
import edu.upc.mpi.logicschema.LogicConstraint;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema.LogicSchemaTestHelper;
import edu.upc.mpi.logicschema_normalizer.LogicSchemaNormalizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.*;

class PipelineTest extends LogicSchemaTestHelper {

    @Test
    public void testPipelineExecutesOneTransformation() {
        List<Function<LogicSchema, LogicSchemaProcess>> logicParsersCreators = new LinkedList<>();

        LogicSchemaProcess mock = mock(LogicSchemaProcess.class);

        logicParsersCreators.add((l) -> mock);
        LogicSchema logicSchema = this.createLogicSchemaWithPositiveUnfolding();
        Pipeline pipeline = new Pipeline(logicSchema, logicParsersCreators);
        pipeline.execute();

        verify(mock, times(1)).execute();
    }

    @Test
    public void testPipelineExecutesSeveralTransformations() {

        LogicSchemaProcess processMock1 = mock(LogicSchemaProcess.class);
        Function<LogicSchema, LogicSchemaProcess> processCreator1 = (l) -> processMock1;
        LogicSchema logicSchemaOutput1 = new LogicSchema();
        when(processMock1.getOutputSchema()).thenReturn(logicSchemaOutput1);

        LogicSchemaProcess processMock2 = mock(LogicSchemaProcess.class);
        Function<LogicSchema, LogicSchemaProcess> processCreatorMock2 = spyLambda(Function.class, (l) -> processMock2);

        List<Function<LogicSchema, LogicSchemaProcess>> logicParsersCreators = new LinkedList<>();
        logicParsersCreators.add(processCreator1);
        logicParsersCreators.add(processCreatorMock2);
        LogicSchema logicSchema = this.createLogicSchemaWithPositiveUnfolding();
        Pipeline pipeline = new Pipeline(logicSchema, logicParsersCreators);
        pipeline.execute();

        verify(processMock1, times(1)).execute();
        verify(processCreatorMock2, times(1)).apply(argThat(l -> l == logicSchemaOutput1));
        verify(processMock2, times(1)).execute();
    }

    @Test
    public void testPipelineReturnsLogicConstraintTraceWithOneProcess() {
        LogicSchema logicSchema = new LogicSchema();
        LogicConstraint originalConstraint = this.createBasicLogicConstraint(logicSchema);
        List<Literal> differentBody = originalConstraint.getLiteralsCopied().subList(0, 1);
        LogicConstraint finalConstraint = originalConstraint.copyChangingBody(differentBody);

        Function<LogicSchema, LogicSchemaProcess> processCreatorMock = createProcessCreatorMock(originalConstraint, finalConstraint);

        Pipeline pipeline = executePipeline(logicSchema, processCreatorMock);

        Trace trace = pipeline.getTrace(finalConstraint);

        List<LogicConstraint> list = trace.getList();
        assertThat(list).hasSize(2);
        assertThat(list).first().isEqualTo(originalConstraint);
        assertThat(list).last().isEqualTo(finalConstraint);
    }

    private Pipeline executePipeline(LogicSchema logicSchema, Function<LogicSchema, LogicSchemaProcess>... processors) {
        List<Function<LogicSchema, LogicSchemaProcess>> logicParsersCreators = new LinkedList<>(Arrays.asList(processors));
        Pipeline pipeline = new Pipeline(logicSchema, logicParsersCreators);
        pipeline.execute();
        return pipeline;
    }

    private LogicSchemaProcess createProcessMock(LogicConstraint originalConstraint, LogicConstraint finalConstraint) {
        LogicSchemaProcess processMock = mock(LogicSchemaProcess.class);
        when(processMock.getOriginalConstraint(finalConstraint)).thenReturn(originalConstraint);
        return processMock;
    }

    private Function<LogicSchema, LogicSchemaProcess> createProcessCreatorMock(LogicConstraint originalConstraint, LogicConstraint finalConstraint) {
        LogicSchemaProcess processMock = createProcessMock(originalConstraint, finalConstraint);
        Function<LogicSchema, LogicSchemaProcess> processCreatorMock = (l) -> processMock;
        return processCreatorMock;
    }

    @Test
    public void testPipelineReturnsLogicConstraintTraceWithTwoProcesses() {

    }

    @SuppressWarnings("unchecked")
    static <T, P extends T> P spyLambda(Class<T> lambdaType, P lambda) {
        return (P) mock(lambdaType, delegatesTo(lambda));
    }

}