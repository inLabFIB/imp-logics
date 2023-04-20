package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;

import java.util.*;

/**
 * This class is responsible to recall which constraintsID (from one schema) comes from what constraintID (from an
 * original schema) when applying a logic process
 */
public class SchemaTraceabilityMap {
    private final Map<ConstraintID, ConstraintID> constraintToOrigConstraintIDMap = new HashMap<>();

    protected static SchemaTraceabilityMap collapseMaps(List<SchemaTraceabilityMap> maps) {
        List<SchemaTraceabilityMap> reverseList = new LinkedList<>(maps);
        Collections.reverse(reverseList);
        return reverseList.stream().reduce(new SchemaTraceabilityMap(), (previous, current) -> joinMap(current, previous));
    }

    public static SchemaTraceabilityMap joinMap(SchemaTraceabilityMap previous, SchemaTraceabilityMap current) {
        //TODO: create a new SchemaTraceabilityMap to return
        if (current.constraintToOrigConstraintIDMap.isEmpty()) return previous;
        current.constraintToOrigConstraintIDMap.forEach((finalId, originalId) -> {
            ConstraintID newOriginalId = previous.getOriginalConstraintID(originalId);
            current.addConstraintIDOrigin(finalId, newOriginalId);
        });
        return current;
    }

    /**
     * Adds the mapping between the constraintID and the originalID
     *
     * @param constraintID not null
     * @param originalID   not null
     */
    public void addConstraintIDOrigin(ConstraintID constraintID, ConstraintID originalID) {
        if (Objects.isNull(constraintID)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
        if (Objects.isNull(originalID)) {
            throw new IllegalArgumentException("Original constraintID cannot be null");
        }
        constraintToOrigConstraintIDMap.put(constraintID, originalID);
    }

    /**
     * @param constraintID not null
     * @return the original constraintID this constraintID comes from
     */
    public ConstraintID getOriginalConstraintID(ConstraintID constraintID) {
        if (Objects.isNull(constraintID)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
        return constraintToOrigConstraintIDMap.get(constraintID);
    }
}
