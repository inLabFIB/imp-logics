package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.NotExpectingConstraintIDException;

public class LogicSchemaWithoutIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithoutIDSpec> {

    public LogicSchemaWithoutIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    @Override
    public LogicConstraintWithoutIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        BodySpec body = createBody(ctx.body());

        if (ctx.CONSTRAINTID() != null) {
            throw new NotExpectingConstraintIDException();
        } else {
            LogicConstraintWithoutIDSpec constraintSpec = new LogicConstraintWithoutIDSpec(body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;
        }

    }
}
