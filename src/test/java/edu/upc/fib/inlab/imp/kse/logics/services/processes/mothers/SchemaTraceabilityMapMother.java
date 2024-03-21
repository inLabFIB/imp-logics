package edu.upc.fib.inlab.imp.kse.logics.services.processes.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.SchemaTraceabilityMap;

import java.util.Map;

public class SchemaTraceabilityMapMother {
    public static SchemaTraceabilityMap create(Map<String, String> finalToOriginMap) {
        SchemaTraceabilityMap result = new SchemaTraceabilityMap();
        finalToOriginMap.forEach((finalID, originalID) ->
                result.addConstraintIDOrigin(new ConstraintID(finalID), new ConstraintID(originalID)));
        return result;
    }

    public static SchemaTraceabilityMap createEmptyMap() {
        return create(Map.of());
    }
}
