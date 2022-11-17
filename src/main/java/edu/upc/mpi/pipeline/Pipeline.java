package edu.upc.mpi.pipeline;

import edu.upc.mpi.logicschema.LogicConstraint;
import edu.upc.mpi.logicschema.LogicSchema;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class Pipeline {
    private final LogicSchema inputLogicSchema;
    private List<Function<LogicSchema, LogicSchemaProcess>> logicProcessCreators;
    private LogicSchema outputLogicSchema;
    private final LinkedList<LogicSchemaProcess> logicProcessors;

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

    public Trace getTrace(LogicConstraint finalConstraint) {
        checkIfExistInLastProcess(finalConstraint);
        return calculateTrace(finalConstraint);
    }

    private void checkIfExistInLastProcess(LogicConstraint finalConstraint) {

    }

    private Trace calculateTrace(LogicConstraint lc) {
        Trace trace = new Trace(lc);
        Iterator<LogicSchemaProcess> logicSchemaProcessIterator = logicProcessors.descendingIterator();

        while(logicSchemaProcessIterator.hasNext()){
            LogicSchemaProcess logicSchemaProcess = logicSchemaProcessIterator.next();
            LogicConstraint previousConstraint = logicSchemaProcess.getOriginalConstraint(lc);
            trace.addPrevious(previousConstraint);
        }

        return trace;
    }
}
