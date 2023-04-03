package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

import java.util.*;

public class SchemaTraceabilityMap {
    private final Map<ConstraintID, ConstraintID> constraintToOrigConstraintIDMap = new HashMap<>();

    public static SchemaTraceabilityMap collapseMaps(List<SchemaTraceabilityMap> maps) {

        List<SchemaTraceabilityMap> reverseList = new LinkedList<>(maps);
        Collections.reverse(reverseList);

        return reverseList.stream().reduce(new SchemaTraceabilityMap(), SchemaTraceabilityMap::joinMap);
    }

    private static SchemaTraceabilityMap joinMap(SchemaTraceabilityMap previous, SchemaTraceabilityMap current) {
        if (previous.constraintToOrigConstraintIDMap.isEmpty()) return current;
        previous.constraintToOrigConstraintIDMap.forEach((finalId, originalId) -> {
            ConstraintID newOriginalId = current.getOriginalConstraintID(originalId);
            previous.addConstraintIDOrigin(finalId, newOriginalId);
        });
        return previous;
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
