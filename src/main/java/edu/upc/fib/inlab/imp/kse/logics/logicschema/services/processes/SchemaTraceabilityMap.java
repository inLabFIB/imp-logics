package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.exceptions.MapsDoNotJoinException;

import java.util.*;

/**
 * This class is responsible to recall which constraintsID (from one schema) comes from what constraintID (from an
 * original schema) when applying a logic process.
 */
public class SchemaTraceabilityMap {
    private final Map<ConstraintID, ConstraintID> constraintToOrigConstraintIDMap;

    public SchemaTraceabilityMap() {
        this.constraintToOrigConstraintIDMap = new LinkedHashMap<>();
    }

    /**
     * Construct a new SchemaTraceabilityMap copying the given map into a new map.
     *
     * @param toCopy not null
     */
    protected SchemaTraceabilityMap(SchemaTraceabilityMap toCopy) {
        this.constraintToOrigConstraintIDMap = new LinkedHashMap<>(toCopy.constraintToOrigConstraintIDMap);
    }

    /**
     * @return the number of mapped constraint IDs to original constraint IDs.
     */
    public int size() {
        return constraintToOrigConstraintIDMap.size();
    }

    /**
     * Method to collapse a list of maps into a unique one by means of joining them.
     * <p>
     * In particular, the new map will contain the finalConstraintIDs of the first map given pointing to the
     * originalConstraintIDs of the last map given.
     * <p>
     * E.g.: first map might be {A2 -> A1}, whereas the second might be {A1 -> A0} so that the final result will be {A2
     * -> A0}.
     *
     * @param maps not-null
     * @return a new SchemaTraceabilityMap
     */
    protected static SchemaTraceabilityMap collapseMaps(List<SchemaTraceabilityMap> maps) {
        if (maps.isEmpty()) return new SchemaTraceabilityMap();

        SchemaTraceabilityMap newestMap = new SchemaTraceabilityMap(maps.get(0));
        return maps.subList(1, maps.size()).stream()
                .reduce(newestMap, (joinedNewestMap, olderMap) -> olderMap.joinMap(joinedNewestMap));
    }

    /**
     * This method builds a new SchemaTraceabilityMap containing the constraints of the current map pointing to the
     * original constraintIDs of this map
     * <p>
     * In particular, the method joins the original constraintID of the current map, with the final constraintID of this
     * map.
     * <p>
     * E.g.: if the current SchemaTraceabilityMap contains the mapping F1 -> O1 and this SchemaTraceabilityMap contains
     * the mapping O1 -> OO1 the resulting map will contain F1 -> OO1
     *
     * @param current not null
     * @return a new SchemaTraceabilityMap changing the original constraintId of the current map, for the original
     * constraintId, as defined by previous, of the original constraintId.
     * @throws MapsDoNotJoinException if the current map has some constraintID whose original constraintID did not join
     *                                any original constraintID from this map
     */
    public SchemaTraceabilityMap joinMap(SchemaTraceabilityMap current) {
        if (Objects.isNull(current)) throw new IllegalArgumentException("Second map cannot be null");
        if (current.isEmpty()) return new SchemaTraceabilityMap(current);

        SchemaTraceabilityMap result = new SchemaTraceabilityMap(current);
        current.constraintToOrigConstraintIDMap.forEach((finalId, originalId) -> {
            ConstraintID newOriginalId = this.getOriginalConstraintID(originalId);
            if (Objects.isNull(newOriginalId)) throw new MapsDoNotJoinException("Could not join " + originalId);
            result.addConstraintIDOrigin(finalId, newOriginalId);
        });
        return result;
    }

    /**
     * @return whether the current traceability does not map any constraint to any original constraint id.
     */
    public boolean isEmpty() {
        return constraintToOrigConstraintIDMap.isEmpty();
    }

    /**
     * @param constraintID not null
     * @return the original constraintID of the given constraintID, might be null if not found
     */
    public ConstraintID getOriginalConstraintID(ConstraintID constraintID) {
        if (Objects.isNull(constraintID)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
        return constraintToOrigConstraintIDMap.get(constraintID);
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
}
