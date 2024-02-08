package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.DependencySchemaFactory;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.spec.DependencySchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.ParserCanceledException;
import org.antlr.v4.runtime.*;

import java.util.Set;

public class DependencySchemaParser {

    private final DependencySchemaGrammarToSpecVisitor visitor;
    private final CustomBuiltInPredicateNameChecker builtInPredicateNameChecker;

    public DependencySchemaParser() {
        this(new AllVariableTermTypeCriteria(), new CustomBuiltInPredicateNameChecker(Set.of()));
    }

    public DependencySchemaParser(TermTypeCriteria termTypeCriteria, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        visitor = new DependencySchemaGrammarToSpecVisitor(new StringToTermSpecFactory(termTypeCriteria));
        this.builtInPredicateNameChecker = builtInPredicateNameChecker;
    }

    public DependencySchema parse(String schemaString) {
        DependencySchemaSpec dependencySchemaSpec = parseToSpec(schemaString);
        return DependencySchemaFactory.createDependencySchema(dependencySchemaSpec);
    }

    public DependencySchemaSpec parseToSpec(String schemaString) {
        CharStream input = CharStreams.fromString(schemaString);
        DependencySchemaGrammarLexer lexer = new DependencySchemaGrammarLexer(input, builtInPredicateNameChecker);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DependencySchemaGrammarParser parser = new DependencySchemaGrammarParser(tokens);
        DependencySchemaGrammarParser.ProgContext tree = parser.prog();
        return visitor.visitProg(tree);
    }

    private static class LexerErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new ParserCanceledException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }
}
