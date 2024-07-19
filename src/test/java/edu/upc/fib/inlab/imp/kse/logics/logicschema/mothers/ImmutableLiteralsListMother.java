package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaParser;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ImmutableLiteralsListMother {
    private final static LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser();

    public static ImmutableLiteralsList create(String listOfLiterals) {
        LogicSchema domainSchema = parser.parse("@1 :- " + listOfLiterals);
        return domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
    }

    public static List<ImmutableLiteralsList> createListOfImmutableLiterals(List<String> listOfListOfLiterals, String derivationRules) {
        List<ImmutableLiteralsList> result = new LinkedList<>();
        for (String litOfLiterals : listOfListOfLiterals) {
            result.add(create(litOfLiterals, derivationRules));
        }
        return result;
    }

    public static ImmutableLiteralsList create(String literalsList, String derivationRules) {
        String logicSchemaString = derivationRules + "\n"
                + "@1 :- " + literalsList;
        LogicSchema domainSchema = parser.parse(logicSchemaString);
        return domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
    }

    public static ImmutableLiteralsList createWithCustomBuiltinLiterals(String listOfLiterals, Set<String> customBuiltInPredicateNames) {
        LogicSchemaParser<LogicConstraintWithIDSpec> parser = new LogicSchemaWithIDsParser(
                new AllVariableTermTypeCriteria(),
                new CustomBuiltInPredicateNameChecker(customBuiltInPredicateNames)
        );
        LogicSchema domainSchema = parser.parse("@1 :- " + listOfLiterals);
        return domainSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
    }
}
