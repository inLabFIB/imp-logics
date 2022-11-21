package edu.upc.mpi.pipeline;

import edu.upc.mpi.augmented_logicschema.LogicSchemaAugmenter;
import edu.upc.mpi.logicschema.LogicConstraint;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema.LogicSchemaTestHelper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.*;

class PipelineTest extends LogicSchemaTestHelper {


    @Test
    public void testPipelineGetLogicSchemaGetOutput() {
        List<Function<LogicSchema, LogicSchemaProcess>> logicParsersCreators = new LinkedList<>();

        LogicSchema expectedOutputSchema = new LogicSchema();
        LogicSchemaProcess mock = mock(LogicSchemaProcess.class);
        when(mock.getOutputSchema()).thenReturn(expectedOutputSchema);

        logicParsersCreators.add((l) -> mock);
        LogicSchema logicSchema = this.createLogicSchemaWithPositiveUnfolding();
        Pipeline pipeline = new Pipeline(logicSchema, logicParsersCreators);
        pipeline.execute();

        LogicSchema logicSchemaOutput = pipeline.getLogicSchema();
        assertThat(logicSchemaOutput).isEqualTo(expectedOutputSchema);
    }

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
        LogicSchema logicSchema = this.createLogicSchemaWithConstraints("@1 :- P(x), R(x)");
        LogicConstraint originalConstraint = logicSchema.getAllConstraints().get(0);
        LogicSchema finalSchema = this.createLogicSchemaWithConstraints("@2 :- S(x,y)");
        LogicConstraint finalConstraint = finalSchema.getAllConstraints().get(0);

        Function<LogicSchema, LogicSchemaProcess> processCreatorMock = createProcessCreatorMock(originalConstraint, finalConstraint, finalSchema);

        Pipeline pipeline = executePipeline(logicSchema, processCreatorMock);

        Trace trace = pipeline.getTrace(finalConstraint.getID());

        List<LogicConstraint> list = trace.getList();
        assertThat(list).containsExactly(originalConstraint, finalConstraint);
    }

    @SafeVarargs
    private final Pipeline executePipeline(LogicSchema logicSchema, Function<LogicSchema, LogicSchemaProcess>... processors) {
        List<Function<LogicSchema, LogicSchemaProcess>> logicParsersCreators = new LinkedList<>(Arrays.asList(processors));
        Pipeline pipeline = new Pipeline(logicSchema, logicParsersCreators);
        pipeline.execute();
        return pipeline;
    }

    private Function<LogicSchema, LogicSchemaProcess> createProcessCreatorMock(LogicConstraint originalConstraint, LogicConstraint finalConstraint, LogicSchema outputSchema) {
        LogicSchemaProcess processMock = createProcessMock(originalConstraint, finalConstraint, outputSchema);
        return (l) -> processMock;
    }

    private LogicSchemaProcess createProcessMock(LogicConstraint originalConstraint, LogicConstraint finalConstraint, LogicSchema outputSchema) {
        LogicSchemaProcess processMock = mock(LogicSchemaProcess.class);
        when(processMock.getOriginalConstraint(finalConstraint.getID())).thenReturn(originalConstraint);
        when(processMock.getOutputSchema()).thenReturn(outputSchema);
        return processMock;
    }

    @Test
    public void testPipelineReturnsLogicConstraintTraceWithTwoProcesses() {
        LogicSchema logicSchema = this.createLogicSchemaWithConstraints("@1 :- P(x), R(x)");
        LogicConstraint constraint1 = logicSchema.getAllConstraints().get(0);
        LogicSchema logicSchema2 = this.createLogicSchemaWithConstraints("@2 :- S(x,y)");
        LogicConstraint constraint2 = logicSchema2.getAllConstraints().get(0);
        LogicSchema logicSchema3 = this.createLogicSchemaWithConstraints("@3 :- T(x,y,z)");
        LogicConstraint constraint3 = logicSchema3.getAllConstraints().get(0);

        Function<LogicSchema, LogicSchemaProcess> processCreatorMock1 = createProcessCreatorMock(constraint1, constraint2, logicSchema2);
        Function<LogicSchema, LogicSchemaProcess> processCreatorMock2 = createProcessCreatorMock(constraint2, constraint3, logicSchema3);

        Pipeline pipeline = executePipeline(logicSchema, processCreatorMock1, processCreatorMock2);

        Trace trace = pipeline.getTrace(constraint3.getID());

        List<LogicConstraint> list = trace.getList();
        assertThat(list).containsExactly(constraint1, constraint2, constraint3);
    }

    @Test
    public void testPipelineThrowsExceptionWhenLogicConstraintDoesNotExistsInFinalSchema() {
        LogicSchema logicSchema = this.createLogicSchemaWithConstraints("@1 :- P(x), R(x)");
        LogicConstraint originalConstraint = logicSchema.getAllConstraints().get(0);

        Pipeline pipeline = executePipeline(logicSchema, LogicSchemaAugmenter::new);

        assertThatRuntimeException()
                .isThrownBy(() -> pipeline.getTrace(originalConstraint.getID()))
                .withMessage("Logic constraint " + originalConstraint.getID() + " does not appear in output schema");
    }



    @SuppressWarnings("unchecked")
    static <T, P extends T> P spyLambda(Class<T> lambdaType, P lambda) {
        return (P) mock(lambdaType, delegatesTo(lambda));
    }

}