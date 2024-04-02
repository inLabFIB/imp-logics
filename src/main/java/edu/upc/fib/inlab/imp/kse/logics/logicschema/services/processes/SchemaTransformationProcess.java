package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;

/**
 * A process that, given a logic schema, returns a new schema transformation.
 */
public interface SchemaTransformationProcess {

    /**
     * @param logicSchema not null
     * @return a schemaTransformation
     */
    SchemaTransformation executeTransformation(LogicSchema logicSchema);

}
