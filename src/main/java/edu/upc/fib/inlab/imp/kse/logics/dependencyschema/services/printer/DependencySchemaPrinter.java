package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableAtomList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.printer.LogicSchemaPrinter;

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
        ImmutableLiteralsList body = egd.getBody();
        BuiltInLiteral head = egd.getHead();
        return body.accept(logicSchemaPrinter)
                + " " + DEPENDENCY + " " +
                head.accept(logicSchemaPrinter);
    }
}
