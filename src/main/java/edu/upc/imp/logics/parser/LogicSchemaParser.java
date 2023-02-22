package edu.upc.imp.logics.parser;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.specification.DefaultStringToTermSpecFactory;
import edu.upc.imp.logics.specification.LogicSchemaFactory;
import edu.upc.imp.logics.specification.LogicSchemaSpec;
import edu.upc.imp.parser.LogicSchemaGrammarLexer;
import edu.upc.imp.parser.LogicSchemaGrammarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class LogicSchemaParser {
    public LogicSchema parse(String schemaString) {
        CharStream input = CharStreams.fromString(schemaString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.ProgContext tree = parser.prog();

        LogicSchemaGrammarToSpecVisitor visitor = new LogicSchemaGrammarToSpecVisitor(new DefaultStringToTermSpecFactory());
        LogicSchemaSpec logicSchemaSpec = visitor.visitProg(tree);

        LogicSchemaFactory factory = new LogicSchemaFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }
}
