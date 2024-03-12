package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;

import java.util.Set;

public class LogicSchemaWithCustomBuiltInMother {
    public static LogicSchema buildLogicSchema(String schemaString, String... customBuiltIn) {
        Set<String> customBuiltInSet = Set.of(customBuiltIn);
        return new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(), new CustomBuiltInPredicateNameChecker(customBuiltInSet)).parse(schemaString);
    }
}
