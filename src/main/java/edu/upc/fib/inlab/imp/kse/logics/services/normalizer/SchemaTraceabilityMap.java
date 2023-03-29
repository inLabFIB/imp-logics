package edu.upc.fib.inlab.imp.kse.logics.services.normalizer;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchemaTraceabilityMap {
    private final Map<ConstraintID, ConstraintID> constraintToOrigConstraintIDMap = new HashMap<>();

    public static SchemaTraceabilityMap collapseMaps(SchemaTraceabilityMap... maps) {

        SchemaTraceabilityMap resultMap = new SchemaTraceabilityMap();

        SchemaTraceabilityMap lastMap = maps[maps.length - 1];
        lastMap.constraintToOrigConstraintIDMap.forEach((finalId, originalId) -> {
            for (int i = maps.length - 1; i >= 0; --i) {
                originalId = maps[i].getOriginalConstraintID(originalId);
            }
            resultMap.addConstraintIDOrigin(finalId, originalId);
        });

        return resultMap;
    }

    public void addConstraintIDOrigin(ConstraintID constraintID, ConstraintID originalID) {
        if (Objects.isNull(constraintID)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
        if (Objects.isNull(originalID)) {
            throw new IllegalArgumentException("Original constraintID cannot be null");
        }
        constraintToOrigConstraintIDMap.put(constraintID, originalID);
    }

    public ConstraintID getOriginalConstraintID(ConstraintID constraintID) {
        if (Objects.isNull(constraintID)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
        return constraintToOrigConstraintIDMap.get(constraintID);
    }
}
