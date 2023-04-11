package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;

public class LogicSchemaWithIDsParser extends LogicSchemaParser<LogicConstraintWithIDSpec> {
    public LogicSchemaWithIDsParser() {
        super();
    }

    public LogicSchemaWithIDsParser(TermTypeCriteria termTypeCriteria, BooleanBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        super(termTypeCriteria, builtInPredicateNameChecker);
    }

    @Override
    protected LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> createVisitor(StringToTermSpecFactory stringToTermSpecFactory, BooleanBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        return new LogicSchemaWithIDsGrammarToSpecVisitor(stringToTermSpecFactory, builtInPredicateNameChecker);
    }

    @Override
    protected LogicSchemaFactory<LogicConstraintWithIDSpec> createLogicSchemaFactory() {
        return LogicSchemaFactory.defaultLogicSchemaWithIDsFactory();
    }
}
