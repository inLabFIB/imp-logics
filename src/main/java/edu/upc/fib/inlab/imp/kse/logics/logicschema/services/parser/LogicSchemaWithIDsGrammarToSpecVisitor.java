package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.ExpectingConstraintIDException;

public class LogicSchemaWithIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> {

    public LogicSchemaWithIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    @Override
    public LogicConstraintWithIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        if (ctx.CONSTRAINTID() != null) {
            BodySpec body = createBody(ctx.body());
            String id = ctx.CONSTRAINTID().getText().replace("@", "");
            LogicConstraintWithIDSpec constraintSpec = new LogicConstraintWithIDSpec(id, body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;
        } else {
            throw new ExpectingConstraintIDException();
        }
    }
}
