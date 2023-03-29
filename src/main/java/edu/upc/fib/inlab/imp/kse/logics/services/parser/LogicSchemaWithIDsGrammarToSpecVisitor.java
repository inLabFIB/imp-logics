package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.ExpectingConstraintID;
import edu.upc.imp.parser.LogicSchemaGrammarParser;

public class LogicSchemaWithIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> {

    public LogicSchemaWithIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {

        if (ctx.ID() != null) {
            BodySpec body = createBody(ctx.body());
            String id = ctx.ID().getText();
            LogicConstraintWithIDSpec constraintSpec = new LogicConstraintWithIDSpec(id, body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;

        } else {
            throw new ExpectingConstraintID();
        }

    }
}
