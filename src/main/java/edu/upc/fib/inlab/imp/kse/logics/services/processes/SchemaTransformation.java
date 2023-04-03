package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

/**
 * Class that remembers the transformation applied from an original LogicSchema to a transformedLogicSchema.
 * Currently, it remembers which logic constraintID comes from what constraintID.
 */
public record SchemaTransformation(LogicSchema original, LogicSchema transformed,
                                   SchemaTraceabilityMap schemaTraceabilityMap) {

    public ConstraintID getOriginalConstraintID(ConstraintID transformedConstraintID) {
        return schemaTraceabilityMap.getOriginalConstraintID(transformedConstraintID);
    }

}
