package edu.upc.imp.old.pipeline;

import edu.upc.imp.old.logicschema.LogicConstraint;
import edu.upc.imp.old.logicschema.LogicSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class LogicSchemaProcess {

    /**
     * NEW ConstraintID --> OLD Constraint
     */
    private final Map<Integer, LogicConstraint> newToOriginalConstraintMap = new HashMap<>();

    protected void recordOriginalConstraint(LogicConstraint newConstraint, LogicConstraint originalConstraint) {
        this.newToOriginalConstraintMap.put(newConstraint.getID(), originalConstraint);
    }

    protected void replaceOriginalConstraint(LogicConstraint newConstraint, LogicConstraint constraintToReplace) {
        LogicConstraint originalConstraint = this.newToOriginalConstraintMap.remove(constraintToReplace.getID());
        if (Objects.isNull(originalConstraint))
            throw new RuntimeException("Non existent constraintToReplace: " + constraintToReplace.getID());
        newToOriginalConstraintMap.put(newConstraint.getID(), originalConstraint);
    }

    public LogicConstraint getOriginalConstraint(int constraintId) {
        return this.newToOriginalConstraintMap.get(constraintId);
    }

    public abstract void execute();

    public abstract LogicSchema getOutputSchema();
}
