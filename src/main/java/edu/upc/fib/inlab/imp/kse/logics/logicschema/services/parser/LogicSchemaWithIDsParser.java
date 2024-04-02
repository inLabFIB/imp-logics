package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.TermTypeCriteria;

public class LogicSchemaWithIDsParser extends LogicSchemaParser<LogicConstraintWithIDSpec> {
    public LogicSchemaWithIDsParser() {
        super();
    }

    public LogicSchemaWithIDsParser(TermTypeCriteria termTypeCriteria, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        super(termTypeCriteria, builtInPredicateNameChecker);
    }

    @Override
    protected LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> createVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        return new LogicSchemaWithIDsGrammarToSpecVisitor(stringToTermSpecFactory);
    }

    @Override
    protected LogicSchemaFactory<LogicConstraintWithIDSpec> createLogicSchemaFactory() {
        return LogicSchemaFactory.defaultLogicSchemaWithIDsFactory();
    }
}
