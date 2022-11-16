package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.logicschema.LogicConstraint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class LogicSchemaProcess {
    private final Map<LogicConstraint, LogicConstraint> newToOriginalConstraintMap = new HashMap<>();

    protected void recordOriginalConstraint(LogicConstraint newConstraint, LogicConstraint originalConstraint) {
        this.newToOriginalConstraintMap.put(newConstraint, originalConstraint);
    }

    protected void replaceOriginalConstraint(LogicConstraint newConstraint, LogicConstraint constraintToReplace) {
        LogicConstraint originalConstraint = this.newToOriginalConstraintMap.remove(constraintToReplace);
        if(Objects.isNull(originalConstraint)) throw new RuntimeException("Non existent constraintToReplace: "+constraintToReplace.getID());
        newToOriginalConstraintMap.put(newConstraint, originalConstraint);
    }

    public LogicConstraint getOriginalConstraint(LogicConstraint newConstraint) {
        return this.newToOriginalConstraintMap.get(newConstraint);
    }
}
