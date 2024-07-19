package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;

/**
 * A process that, given a logic schema, returns a new logic schema.
 * <p>
 * A LogicProcess does not trace where the resulting LogicSchema comes from. If you are interested in remembering, for
 * instance, which constraintID comes from what constraintID consider using SchemaTransformationProcess.
 *
 * @see SchemaTransformationProcess
 */
public interface LogicProcess {

    /**
     * execute the transformation
     *
     * @param logicSchema, usually not null
     * @return a new logicSchema
     */
    LogicSchema execute(LogicSchema logicSchema);

}
