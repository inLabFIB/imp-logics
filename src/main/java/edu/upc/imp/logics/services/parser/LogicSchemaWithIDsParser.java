package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.services.creation.LogicSchemaFactory;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.logics.services.creation.spec.helpers.TermTypeCriteria;

public class LogicSchemaWithIDsParser extends LogicSchemaParser<LogicConstraintWithIDSpec> {
    public LogicSchemaWithIDsParser() {
        super();
    }

    public LogicSchemaWithIDsParser(TermTypeCriteria termTypeCriteria) {
        super(termTypeCriteria);
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
