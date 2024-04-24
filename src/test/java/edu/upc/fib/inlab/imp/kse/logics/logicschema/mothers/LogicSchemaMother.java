package edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicSchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.PredicateSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;

import java.util.List;
import java.util.Set;

public class LogicSchemaMother {

    public static LogicSchema buildLogicSchemaWithIDs(String schemaString) {
        return new LogicSchemaWithIDsParser().parse(schemaString);
    }

    public static LogicSchema buildLogicSchemaWithIDsAndPredicates(String schemaString, PredicateSpec... predicateSpecs) {
        LogicSchemaWithIDsParser logicSchemaWithIDsParser = new LogicSchemaWithIDsParser();
        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec = logicSchemaWithIDsParser.parseToSpec(schemaString);
        logicSchemaSpec.addPredicateSpecs(predicateSpecs);
        LogicSchemaFactory<LogicConstraintWithIDSpec> factory = LogicSchemaFactory.defaultLogicSchemaWithIDsFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }

    public static LogicSchema buildLogicSchemaWithIDsAndPredicates(String schemaString, List<PredicateSpec> predicateSpecList) {
        LogicSchemaWithIDsParser logicSchemaWithIDsParser = new LogicSchemaWithIDsParser();
        LogicSchemaSpec<LogicConstraintWithIDSpec> logicSchemaSpec = logicSchemaWithIDsParser.parseToSpec(schemaString);
        logicSchemaSpec.addPredicateSpecs(predicateSpecList);
        LogicSchemaFactory<LogicConstraintWithIDSpec> factory = LogicSchemaFactory.defaultLogicSchemaWithIDsFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }

    public static LogicSchema createEmptySchema() {
        return new LogicSchema(Set.of(), Set.of());
    }

    public static LogicSchema buildLogicSchemaWithIDs(String schema, Set<String> customBuiltInOperators) {
        return new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(),
                                            new CustomBuiltInPredicateNameChecker(customBuiltInOperators))
                .parse(schema);
    }
}
