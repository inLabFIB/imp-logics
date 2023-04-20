package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;

import java.util.LinkedList;
import java.util.List;

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

    public static List<ImmutableLiteralsList> createListOfImmutableLiterals(List<String> listOfListOfLiterals, String derivationRules) {
        List<ImmutableLiteralsList> result = new LinkedList<>();
        for (String litOfLiterals : listOfListOfLiterals) {
            result.add(create(litOfLiterals, derivationRules));
        }
        return result;
    }
}
