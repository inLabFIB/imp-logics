package edu.upc.fib.inlab.imp.kse.logics.services.processes;


import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

public interface SchemaTransformationProcess {

    SchemaTransformation executeTransformation(LogicSchema logicSchema);

}
