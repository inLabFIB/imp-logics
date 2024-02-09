package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableAtomList;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.services.printer.LogicSchemaPrinter;

public class DependencySchemaPrinter implements DependencySchemaVisitor<String> {

    public static final String DEPENDENCY = "->";
    private final LogicSchemaPrinter logicSchemaPrinter;

    public DependencySchemaPrinter() {
        this.logicSchemaPrinter = new LogicSchemaPrinter();
    }

    public String print(DependencySchema dependencySchema) {
        return this.visit(dependencySchema);
    }

    @Override
    public String visit(DependencySchema dependencySchema) {
        StringBuilder resultBuilder = new StringBuilder();
        dependencySchema.getAllDependencies().forEach(dependency -> {
            resultBuilder.append(dependency.accept(this));
            resultBuilder.append("\n");
        });
        dependencySchema.getAllDerivationRules().forEach(derivationRule -> {
            resultBuilder.append(derivationRule.accept(logicSchemaPrinter));
            resultBuilder.append("\n");
        });
        return resultBuilder.toString();
    }

    @Override
    public String visit(TGD tgd) {
        ImmutableLiteralsList body = tgd.getBody();
        ImmutableAtomList head = tgd.getHead();
        return body.accept(logicSchemaPrinter)
                + " " + DEPENDENCY + " " +
                head.accept(logicSchemaPrinter);
    }

    @Override
    public String visit(EGD egd) {
        return null;
    }
}
