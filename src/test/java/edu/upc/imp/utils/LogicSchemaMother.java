package edu.upc.imp.utils;

import edu.upc.imp.logicschema.LogicSchema;
import edu.upc.imp.parser.LogicSchemaParser;

public class LogicSchemaMother {
    public static LogicSchema buildLogicSchema(String schema){
        LogicSchemaParser parser = new LogicSchemaParser(schema);
        parser.parse();
        return parser.getLogicSchema();
    }
}
