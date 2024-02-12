package edu.upc.fib.inlab.imp.kse.logics.dependencies.mothers;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.parser.DependencySchemaParser;

import java.util.LinkedHashSet;

public class DependencySchemaMother {

    public static DependencySchema buildDependencySchema(String schemaString) {
        return new DependencySchemaParser().parse(schemaString);
    }

    public static DependencySchema buildEmptyDependencySchema() {
        return new DependencySchema(new LinkedHashSet<>(), new LinkedHashSet<>());
    }
}
