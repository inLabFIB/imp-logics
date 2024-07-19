package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A pipeline of SchemaTransformationProcessPipeline. This class is useful for chaining several transformations over a
 * logic schema.
 */
public class SchemaTransformationProcessPipeline extends LogicSchemaTransformationProcess {

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
    @Override
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
        List<SchemaTraceabilityMap> reversedList = new LinkedList<>(traceabilityMapList);
        Collections.reverse(reversedList); //reverting list to have the newest maps (the map from the last transformation,
        //which contains the final constraintIDs) first.
        //So that, when we collapse them, we obtain a map from the final constraints
        //to the original ones.
        SchemaTraceabilityMap schemaTraceabilityMap = SchemaTraceabilityMap.collapseMaps(reversedList);
        return new SchemaTransformation(inputLogicSchema, currentLogicSchema, schemaTraceabilityMap);
    }

}
