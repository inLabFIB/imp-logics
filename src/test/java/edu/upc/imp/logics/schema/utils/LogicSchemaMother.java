package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;

import java.util.Set;

public class LogicSchemaMother {

    public static LogicSchema buildLogicSchemaWithIDs(String schemaString) {
        return new LogicSchemaWithIDsParser().parse(schemaString);
    }

    public static LogicSchema createEmptySchema() {
        return new LogicSchema(Set.of(), Set.of());
    }
}
