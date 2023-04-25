package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

public abstract class LogicSchemaTransformationProcess implements LogicProcess, SchemaTransformationProcess {
    @Override
    public LogicSchema execute(LogicSchema logicSchema) {
        return executeTransformation(logicSchema).transformed();
    }

    @Override
    public abstract SchemaTransformation executeTransformation(LogicSchema logicSchema);
}
