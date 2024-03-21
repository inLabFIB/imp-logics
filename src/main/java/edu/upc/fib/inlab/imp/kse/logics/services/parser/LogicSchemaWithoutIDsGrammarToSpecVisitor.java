package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.NotExpectingConstraintID;

public class LogicSchemaWithoutIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithoutIDSpec> {

    public LogicSchemaWithoutIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithoutIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        BodySpec body = createBody(ctx.body());

        if (ctx.CONSTRAINTID() != null) {
            throw new NotExpectingConstraintID();
        } else {
            LogicConstraintWithoutIDSpec constraintSpec = new LogicConstraintWithoutIDSpec(body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;
        }

    }
}
