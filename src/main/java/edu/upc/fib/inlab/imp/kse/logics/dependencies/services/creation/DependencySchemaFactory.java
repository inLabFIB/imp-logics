package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.spec.DependencySchemaSpec;

//TODO: document
public class DependencySchemaFactory {

    private DependencySchemaFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static DependencySchema createDependencySchema(DependencySchemaSpec dependencySchemaSpec) {
        DependencySchemaBuilder dependencySchemaBuilder = new DependencySchemaBuilder();
        dependencySchemaSpec.getPredicateSpecList().forEach(predicateSpec -> dependencySchemaBuilder.addPredicate(predicateSpec.name(), predicateSpec.arity()));
        dependencySchemaSpec.getDerivationRuleSpecList().forEach(dependencySchemaBuilder::addDerivationRule);
        dependencySchemaSpec.getDependencySpecList().forEach(dependencySchemaBuilder::addDependency);
        return dependencySchemaBuilder.build();
    }
}
