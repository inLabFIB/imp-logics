package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySchemaSpec;

//TODO: document
public class DependencySchemaFactory {

    private DependencySchemaFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static DependencySchema createDependencySchema(DependencySchemaSpec dependencySchemaSpec) {
        DependencySchemaBuilder dependencySchemaBuilder = new DependencySchemaBuilder();
        dependencySchemaSpec.getPredicateSpecList().forEach(predicateSpec -> dependencySchemaBuilder.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        dependencySchemaSpec.getDependencySpecList().forEach(dependencySchemaBuilder::addDependency);
        return dependencySchemaBuilder.build();
    }
}
