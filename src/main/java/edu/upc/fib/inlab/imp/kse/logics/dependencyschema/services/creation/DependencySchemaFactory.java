package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;

import java.util.Set;

/**
 * Factory that creates a DependencySchema given a DependencySchemaSpec. It uses the DependencySchemaBuilder class.
 */
public class DependencySchemaFactory {

    private DependencySchemaFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static DependencySchema createDependencySchema(DependencySchemaSpec dependencySchemaSpec) {
        return new DependencySchemaBuilder()
                .addAllPredicates(dependencySchemaSpec.getPredicateSpecList())
                .addAllDependencies(dependencySchemaSpec.getDependencySpecList())
                .build();
    }

    public static DependencySchema createDependencySchema(DependencySchemaSpec dependencySchemaSpec,
                                                          Set<Predicate> relationalSchema) {
        return new DependencySchemaBuilder(relationalSchema)
                .addAllDependencies(dependencySchemaSpec.getDependencySpecList())
                .build();
    }
}
