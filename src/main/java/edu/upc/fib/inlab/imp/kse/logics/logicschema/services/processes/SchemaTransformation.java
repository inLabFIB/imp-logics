package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;

import java.util.Objects;

/**
 * Class that remembers the transformation applied from an original LogicSchema to a transformedLogicSchema. Currently,
 * it remembers which logic constraintID comes from what constraintID.
 */
public record SchemaTransformation(LogicSchema original, LogicSchema transformed,
                                   SchemaTraceabilityMap schemaTraceabilityMap) {

    /**
     * @param transformedConstraintID not null
     * @return the original constraintID of the transformedConstraintID
     */
    public ConstraintID getOriginalConstraintID(ConstraintID transformedConstraintID) {
        if (Objects.isNull(transformedConstraintID))
            throw new IllegalArgumentException("TransformedConstraintID cannot be null");
        return schemaTraceabilityMap.getOriginalConstraintID(transformedConstraintID);
    }

}
