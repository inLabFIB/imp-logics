package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.spec.DependencySchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicElementSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;

public class DependencySchemaGrammarToSpecVisitor extends DependencySchemaGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;

//    protected LogicSchemaSpec<T> logicSchemaSpec;

    public DependencySchemaGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public DependencySchemaSpec visitProg(DependencySchemaGrammarParser.ProgContext ctx) {
        return null;
    }

    //TODO: ADD METHODS (copy the ones needed from LogicSchemaGrammar)
}
