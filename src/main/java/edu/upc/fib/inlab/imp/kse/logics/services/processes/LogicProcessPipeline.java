package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

import java.util.List;

public class LogicProcessPipeline {

    private final List<LogicProcess> logicProcesses;

    public LogicProcessPipeline(List<LogicProcess> logicProcesses) {
        this.logicProcesses = logicProcesses;
    }

    public LogicSchema execute(LogicSchema inputLogicSchema) {
        return logicProcesses.stream()
                .reduce(inputLogicSchema,
                        (currentLogicSchema, process) -> process.execute(currentLogicSchema),
                        (previousResult, currentResult) -> currentResult);
    }

}
