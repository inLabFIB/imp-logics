package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;

import java.util.List;
import java.util.Objects;

/**
 * A pipeline of DependencyProcesses. This class is useful for chaining several transformations over a dependency
 * schema.
 */
public class DependencyProcessPipeline implements DependencyProcess {

    private final List<DependencyProcess> logicProcesses;

    /**
     * @param logicProcesses a not null list of logic processes. No process in the list can be null.
     */
    public DependencyProcessPipeline(List<DependencyProcess> logicProcesses) {
        if (Objects.isNull(logicProcesses)) throw new IllegalArgumentException("LogicProcesses list cannot be null");
        checkNoNullProcess(logicProcesses);
        this.logicProcesses = logicProcesses;
    }

    private void checkNoNullProcess(List<DependencyProcess> logicProcesses) {
        if (logicProcesses.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("LogicProcesses list cannot contain a null process");
        }
    }

    /**
     * Executes the pipeline
     *
     * @param inputDependencySchema not null
     * @return a new dependency schema resulting from applying the pipeline
     */
    @Override
    public DependencySchema execute(DependencySchema inputDependencySchema) {
        if (Objects.isNull(inputDependencySchema)) throw new IllegalArgumentException();
        return logicProcesses.stream()
                .reduce(inputDependencySchema,
                        (currentLogicSchema, process) -> process.execute(currentLogicSchema),
                        (previousResult, currentResult) -> currentResult);
    }

}
