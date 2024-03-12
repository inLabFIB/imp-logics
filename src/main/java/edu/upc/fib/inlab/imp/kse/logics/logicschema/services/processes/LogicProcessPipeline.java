package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;

import java.util.List;
import java.util.Objects;

/**
 * A pipeline of LogicProcesses. This class is useful for chaining several transformations over a logic schema.
 */
public class LogicProcessPipeline implements LogicProcess {

    private final List<LogicProcess> logicProcesses;

    /**
     * @param logicProcesses a not null list of logic processes. No process in the list can be null.
     */
    public LogicProcessPipeline(List<LogicProcess> logicProcesses) {
        if (Objects.isNull(logicProcesses)) throw new IllegalArgumentException("LogicProcesses list cannot be null");
        checkNoNullProcess(logicProcesses);
        this.logicProcesses = logicProcesses;
    }

    private void checkNoNullProcess(List<LogicProcess> logicProcesses) {
        if (logicProcesses.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("LogicProcesses list cannot contain a null process");
        }
    }

    /**
     * Executes the pipeline
     *
     * @param inputLogicSchema not null
     * @return a new logic schema resulting from applying the pipeline
     */
    @Override
    public LogicSchema execute(LogicSchema inputLogicSchema) {
        if (Objects.isNull(inputLogicSchema)) throw new IllegalArgumentException();
        return logicProcesses.stream()
                .reduce(inputLogicSchema,
                        (currentLogicSchema, process) -> process.execute(currentLogicSchema),
                        (previousResult, currentResult) -> currentResult);
    }

}
