package edu.upc.imp.old.utils;

import edu.upc.imp.old.logicschema.LogicSchema;
import edu.upc.imp.old.parser.LogicSchemaParser;

public class LogicSchemaMother {
    public static LogicSchema buildLogicSchema(String schema){
        LogicSchemaParser parser = new LogicSchemaParser(schema);
        parser.parse();
        return parser.getLogicSchema();
    }
}
