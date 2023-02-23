package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;

public class LogicSchemaWithoutIDsParser extends LogicSchemaParser<LogicConstraintWithoutIDSpec> {
    @Override
    protected LogicSchemaGrammarToSpecVisitor<LogicConstraintWithoutIDSpec> createVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        return new LogicSchemaWithoutIDsGrammarToSpecVisitor(stringToTermSpecFactory);
    }
}
