package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;

public interface DependencyProcess {
    /**
     * execute the transformation
     *
     * @param dependencySchema, usually not null
     * @return a new dependencySchema
     */
    DependencySchema execute(DependencySchema dependencySchema);
}
