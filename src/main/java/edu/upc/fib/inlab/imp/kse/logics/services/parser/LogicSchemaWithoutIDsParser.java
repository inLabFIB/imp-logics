package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;

public class LogicSchemaWithoutIDsParser extends LogicSchemaParser<LogicConstraintWithoutIDSpec> {

    public LogicSchemaWithoutIDsParser() {
        super();
    }

    public LogicSchemaWithoutIDsParser(TermTypeCriteria stringToTermSpecFactory, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        super(stringToTermSpecFactory, builtInPredicateNameChecker);
    }

    @Override
    protected LogicSchemaGrammarToSpecVisitor<LogicConstraintWithoutIDSpec> createVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        return new LogicSchemaWithoutIDsGrammarToSpecVisitor(stringToTermSpecFactory);
    }

    @Override
    protected LogicSchemaFactory<LogicConstraintWithoutIDSpec> createLogicSchemaFactory() {
        return LogicSchemaFactory.defaultLogicSchemaWithoutIDsFactory();
    }
}
