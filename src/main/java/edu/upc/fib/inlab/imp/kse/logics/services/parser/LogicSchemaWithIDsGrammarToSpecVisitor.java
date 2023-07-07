package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.ExpectingConstraintID;

public class LogicSchemaWithIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> {

    public LogicSchemaWithIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        if (ctx.CONSTRAINTID() != null) {
            BodySpec body = createBody(ctx.body());
            String id = ctx.CONSTRAINTID().getText().replace("@", "");
            LogicConstraintWithIDSpec constraintSpec = new LogicConstraintWithIDSpec(id, body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;
        } else {
            throw new ExpectingConstraintID();
        }
    }
}
