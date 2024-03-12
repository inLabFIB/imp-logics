package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithoutIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithoutIDsParser;

import java.util.LinkedList;
import java.util.List;

public class LogicConstraintMother {

    public static LogicConstraint createTrivialLogicConstraint(ConstraintID constraintID, Predicate p) {
        List<Term> terms = new LinkedList<>();
        for (int i = 0; i < p.getArity(); ++i) {
            terms.add(new Variable("x"));
        }
        return new LogicConstraint(constraintID, List.of(new OrdinaryLiteral(new Atom(p, terms))));
    }

    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parserWithIDs = new LogicSchemaWithIDsParser();
    private final static LogicSchemaParser<LogicConstraintWithoutIDSpec> parserWithoutIDs = new LogicSchemaWithoutIDsParser();

    /**
     * @param schema a not null string representing a logic constraint, together the related derivation rules, without
     *               specifying the id
     * @return the previous logic constraint parsed as a LogicConstraint object
     */
    public static LogicConstraint createWithoutID(String schema) {
        LogicSchema domainSchema = parserWithoutIDs.parse(schema);
        return domainSchema.getAllLogicConstraints().stream().findFirst().orElseThrow();
    }

    public static LogicConstraint createWithID(String schema) {
        LogicSchema domainSchema = parserWithIDs.parse(schema);
        return domainSchema.getAllLogicConstraints().stream().findFirst().orElseThrow();
    }
}
