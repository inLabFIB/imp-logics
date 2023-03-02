package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.services.creation.LogicSchemaFactory;
import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.imp.logics.services.creation.spec.LogicSchemaSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.DefaultTermTypeCriteria;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.logics.services.creation.spec.helpers.TermTypeCriteria;
import edu.upc.imp.parser.LogicSchemaGrammarLexer;
import edu.upc.imp.parser.LogicSchemaGrammarParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public abstract class LogicSchemaParser<T extends LogicConstraintSpec> {

    private final LogicSchemaGrammarToSpecVisitor<T> visitor;

    public LogicSchemaParser() {
        this(new DefaultTermTypeCriteria());
    }

    public LogicSchemaParser(TermTypeCriteria termTypeCriteria) {
        visitor = createVisitor(new StringToTermSpecFactory(termTypeCriteria));
    }

    protected abstract LogicSchemaGrammarToSpecVisitor<T> createVisitor(StringToTermSpecFactory stringToTermSpecFactory);

    protected abstract LogicSchemaFactory<T> createLogicSchemaFactory();

    public LogicSchema parse(String schemaString) {
        CharStream input = CharStreams.fromString(schemaString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.ProgContext tree = parser.prog();
        LogicSchemaSpec<T> logicSchemaSpec = visitor.visitProg(tree);
        LogicSchemaFactory<T> factory = createLogicSchemaFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }
}
