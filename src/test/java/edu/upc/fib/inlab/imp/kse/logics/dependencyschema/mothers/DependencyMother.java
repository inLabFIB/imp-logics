package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;

public class DependencyMother {
    public static Dependency buildDependency(String dependencyString) {
        return DependencySchemaMother.buildDependencySchema(dependencyString).getAllDependencies().iterator().next();
    }
}
