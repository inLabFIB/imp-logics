package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;

public interface DependencySchemaVisitor<T> {

    T visit(DependencySchema dependencySchema);

    T visit(TGD tgd);

    T visit(EGD egd);
}
