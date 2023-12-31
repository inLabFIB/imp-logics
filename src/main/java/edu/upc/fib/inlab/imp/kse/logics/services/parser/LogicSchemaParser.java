package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.LogicSchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicSchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;
import org.antlr.v4.runtime.*;

import java.util.Set;

public abstract class LogicSchemaParser<T extends LogicConstraintSpec> {

    private final LogicSchemaGrammarToSpecVisitor<T> visitor;
    private final CustomBuiltInPredicateNameChecker builtInPredicateNameChecker;

    public LogicSchemaParser() {
        this(new AllVariableTermTypeCriteria(), new CustomBuiltInPredicateNameChecker(Set.of()));
    }

    public LogicSchemaParser(TermTypeCriteria termTypeCriteria, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        visitor = createVisitor(new StringToTermSpecFactory(termTypeCriteria));
        this.builtInPredicateNameChecker = builtInPredicateNameChecker;
    }

    protected abstract LogicSchemaGrammarToSpecVisitor<T> createVisitor(StringToTermSpecFactory stringToTermSpecFactory);

    protected abstract LogicSchemaFactory<T> createLogicSchemaFactory();

    public LogicSchema parse(String schemaString) {
        LogicSchemaSpec<T> logicSchemaSpec = parseToSpec(schemaString);
        LogicSchemaFactory<T> factory = createLogicSchemaFactory();
        return factory.createLogicSchema(logicSchemaSpec);
    }

    public LogicSchemaSpec<T> parseToSpec(String schemaString) {
        CharStream input = CharStreams.fromString(schemaString);
        LogicSchemaGrammarLexer lexer = new LogicSchemaGrammarLexer(input, builtInPredicateNameChecker);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LogicSchemaGrammarParser parser = new LogicSchemaGrammarParser(tokens);
        LogicSchemaGrammarParser.ProgContext tree = parser.prog();
        return visitor.visitProg(tree);
    }

    private static class LexerErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new RuntimeException("line " + line + ":" + charPositionInLine + " " + msg);
        }

    }
}
