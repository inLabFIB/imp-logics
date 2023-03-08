package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.imp.logics.services.parser.LogicSchemaParser;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;

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

    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static LogicConstraint create(String schema) {
        LogicSchema domainSchema = parser.parse(schema);
        return domainSchema.getAllLogicConstraints().stream().findFirst().orElseThrow();
    }
}
