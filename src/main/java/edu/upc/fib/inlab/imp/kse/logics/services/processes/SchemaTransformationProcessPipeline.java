package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A pipeline of SchemaTransformationProcessPipeline.
 * This class is useful for chaining several transformations over a logic schema.
 */
public class SchemaTransformationProcessPipeline {

    private final List<SchemaTransformationProcess> transformationProcesses;

    /**
     * @param transformationProcesses not null, neither contains nulls
     */
    public SchemaTransformationProcessPipeline(List<SchemaTransformationProcess> transformationProcesses) {
        if (Objects.isNull(transformationProcesses))
            throw new IllegalArgumentException("TransformationProcesses cannot be null");
        checkDoesNotContainNull(transformationProcesses);

        this.transformationProcesses = transformationProcesses;
    }

    private void checkDoesNotContainNull(List<SchemaTransformationProcess> transformationProcesses) {
        if (transformationProcesses.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("TransformationProcesses cannot contain null");
    }

    /**
     * @param inputLogicSchema not null
     * @return the result of applying the pipeline into the inputLogicSchema
     */
    public SchemaTransformation executeTransformation(LogicSchema inputLogicSchema) {
        checkLogicSchema(inputLogicSchema);
        List<SchemaTransformation> transformations = new LinkedList<>();

        LogicSchema currentLogicSchema = inputLogicSchema;
        for (SchemaTransformationProcess process : transformationProcesses) {
            SchemaTransformation schemaTransformation = process.executeTransformation(currentLogicSchema);
            transformations.add(schemaTransformation);
            currentLogicSchema = schemaTransformation.transformed();
        }

        List<SchemaTraceabilityMap> traceabilityMapList = transformations.stream()
                .map(SchemaTransformation::schemaTraceabilityMap)
                .toList();
        SchemaTraceabilityMap schemaTraceabilityMap = SchemaTraceabilityMap.collapseMaps(traceabilityMapList);
        return new SchemaTransformation(inputLogicSchema, currentLogicSchema, schemaTraceabilityMap);
    }

    private static void checkLogicSchema(LogicSchema inputLogicSchema) {
        if (Objects.isNull(inputLogicSchema)) throw new IllegalArgumentException("Input logic schema cannot be null");
    }

}
