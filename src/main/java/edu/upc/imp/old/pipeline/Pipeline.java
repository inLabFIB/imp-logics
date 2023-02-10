package edu.upc.imp.old.pipeline;

import edu.upc.imp.old.logicschema.LogicConstraint;
import edu.upc.imp.old.logicschema.LogicSchema;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Pipeline {
    private final LogicSchema inputLogicSchema;
    private final List<Function<LogicSchema, LogicSchemaProcess>> logicProcessCreators;
    private final LinkedList<LogicSchemaProcess> logicProcessors;
    private LogicSchema outputLogicSchema;

    public Pipeline(LogicSchema inputLogicSchema, List<Function<LogicSchema, LogicSchemaProcess>> logicProcessCreators) {
        this.inputLogicSchema = inputLogicSchema;
        this.logicProcessCreators = logicProcessCreators;
        logicProcessors = new LinkedList<>();
    }

    public void execute() {
        LogicSchema currentLogicSchema = inputLogicSchema;
        for (Function<LogicSchema, LogicSchemaProcess> logicProcessCreator : logicProcessCreators) {
            LogicSchemaProcess logicSchemaProcess = logicProcessCreator.apply(currentLogicSchema);
            logicSchemaProcess.execute();
            currentLogicSchema = logicSchemaProcess.getOutputSchema();
            logicProcessors.add(logicSchemaProcess);
        }

        outputLogicSchema = currentLogicSchema;
    }

    public LogicSchema getLogicSchema() {
        return outputLogicSchema;
    }

    public Trace getTrace(int finalConstraintId) {
        checkIfExistFinalConstraint(finalConstraintId);
        return calculateTrace(finalConstraintId);
    }

    private void checkIfExistFinalConstraint(int finalConstraintId) {
        LogicConstraint constraintByNumber = this.outputLogicSchema.getConstraintByNumber(finalConstraintId);
        if (Objects.isNull(constraintByNumber)) {
            throw new RuntimeException("Logic constraint " + finalConstraintId + " does not appear in output schema");
        }
    }

    private Trace calculateTrace(int lcID) {
        LogicConstraint currentLogicConstraint = this.outputLogicSchema.getConstraintByNumber(lcID);
        Trace trace = new Trace(currentLogicConstraint);
        Iterator<LogicSchemaProcess> logicSchemaProcessIterator = logicProcessors.descendingIterator();

        int currentConstraintID = lcID;
        while (logicSchemaProcessIterator.hasNext()) {
            LogicSchemaProcess logicSchemaProcess = logicSchemaProcessIterator.next();
            LogicConstraint previousConstraint = logicSchemaProcess.getOriginalConstraint(currentConstraintID);
            trace.addPrevious(previousConstraint);
            currentConstraintID = previousConstraint.getID();
        }

        return trace;
    }
}
