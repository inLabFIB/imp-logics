package edu.upc.imp.logics.parser;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.specification.DefaultStringToTermSpecFactory;
import edu.upc.imp.logics.specification.LogicSchemaBuilder;
import edu.upc.imp.logics.specification.LogicSchemaFactory;
import edu.upc.imp.logics.specification.LogicSchemaSpecification;
import edu.upc.imp.parser.LogicSchemaGrammarLexer;
import edu.upc.imp.parser.LogicSchemaGrammarParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class LogicSchemaParser {
    public LogicSchema parse(String schemaString) {
        ANTLRInputStream input = new ANTLRInputStream(schemaString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.ProgContext tree = parser.prog();

        LogicSchemaGrammarVisitorImpl visitor = new LogicSchemaGrammarVisitorImpl(new DefaultStringToTermSpecFactory());
        LogicSchemaSpecification logicSchemaSpecification = visitor.visitProg(tree);

        LogicSchemaFactory factory = new LogicSchemaFactory(new LogicSchemaBuilder());
        return factory.buildLogicSchema(logicSchemaSpecification);
    }
}
