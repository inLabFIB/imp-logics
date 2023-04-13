package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;

import java.util.Set;

public class LogicSchemaWithCustomBuiltInMother {
    public static LogicSchema buildLogicSchema(String schemaString, String... customBuiltIn) {
        Set<String> customBuiltInSet = Set.of(customBuiltIn);
        return new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(), new CustomBuiltInPredicateNameChecker(customBuiltInSet)).parse(schemaString);
    }
}
