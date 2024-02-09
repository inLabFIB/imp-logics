package edu.upc.fib.inlab.imp.kse.logics.dependencies.visitor;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;

public interface DependencySchemaVisitor<T> {

    T visit(DependencySchema dependencySchema);

    T visit(TGD tgd);

    T visit(EGD egd);
}
