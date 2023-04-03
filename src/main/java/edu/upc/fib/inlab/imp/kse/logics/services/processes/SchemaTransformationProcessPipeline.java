package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SchemaTransformationProcessPipeline {

    private final List<SchemaTransformationProcess> transformationProcesses;

    public SchemaTransformationProcessPipeline(List<SchemaTransformationProcess> transformationProcesses) {
        this.transformationProcesses = transformationProcesses;
    }

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
