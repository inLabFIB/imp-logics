package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

import java.util.Objects;

public abstract class LogicSchemaTransformationProcess implements LogicProcess, SchemaTransformationProcess {
    @Override
    public LogicSchema execute(LogicSchema logicSchema) {
        checkLogicSchema(logicSchema);
        return executeTransformation(logicSchema).transformed();
    }

    protected void checkLogicSchema(LogicSchema logicSchema) {
        if (Objects.isNull(logicSchema)) {
            throw new IllegalArgumentException("LogicSchema cannot be null");
        }
    }

}
