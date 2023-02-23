package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.services.creation.spec.BodySpec;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.logics.services.parser.exceptions.ExpectingConstraintID;
import edu.upc.imp.parser.LogicSchemaGrammarParser;

public class LogicSchemaWithIDsGrammarToSpecVisitor extends LogicSchemaGrammarToSpecVisitor<LogicConstraintWithIDSpec> {

    public LogicSchemaWithIDsGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithIDSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        BodySpec body = createBody(ctx.body());

        if (ctx.CONSTRAINTID() != null) {
            String id = ctx.CONSTRAINTID().getText().substring(1); //skipping '@' symbol
            LogicConstraintWithIDSpec constraintSpec = new LogicConstraintWithIDSpec(id, body);
            logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
            return constraintSpec;

        } else {
            throw new ExpectingConstraintID();
        }

    }
}
