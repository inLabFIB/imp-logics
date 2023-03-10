package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;

public class LogicSchemaMother {

    public static LogicSchema buildLogicSchemaWithIDs(String schemaString) {
        return new LogicSchemaWithIDsParser().parse(schemaString);
    }
}
