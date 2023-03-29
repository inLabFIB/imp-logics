package edu.upc.fib.inlab.imp.kse.logics.schema.utils;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;

public class ImmutableLiteralsListMother {
    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static ImmutableLiteralsList create(String listOfLiterals) {
        LogicSchema domainSchema = parser.parse("@1 :- " + listOfLiterals);
        return domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
    }

    public static ImmutableLiteralsList create(String literalsList, String derivationRules) {
        String logicSchemaString = derivationRules + "\n"
                + "@1 :- " + literalsList;
        LogicSchema domainSchema = parser.parse(logicSchemaString);
        return domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
    }
}
