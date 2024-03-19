package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConjunctiveQuery;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.QueryBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.ConjunctiveQuerySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.TermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.ParserCanceledException;
import org.antlr.v4.runtime.*;

import java.util.Set;
import java.util.stream.Collectors;

public class ConjunctiveQueriesParser {

    private final QueriesGrammarToSpecVisitor visitor;

    public ConjunctiveQueriesParser() {
        this(new AllVariableTermTypeCriteria());
    }

    public ConjunctiveQueriesParser(TermTypeCriteria termTypeCriteria) {
        visitor = new QueriesGrammarToSpecVisitor(new StringToTermSpecFactory(termTypeCriteria));
    }

    public Set<ConjunctiveQuery> parse(String queriesString) {
        Set<ConjunctiveQuerySpec> queriesSpecs = parseToSpec(queriesString);
        return queriesSpecs.stream()
                .map(querySpec -> new QueryBuilder().buildQuery(querySpec))
                .collect(Collectors.toSet());
    }

    public Set<ConjunctiveQuery> parse(String queriesString, Set<Predicate> relationalSchema) {
        Set<ConjunctiveQuerySpec> queriesSpecs = parseToSpec(queriesString);
        return queriesSpecs.stream()
                .map(querySpec -> new QueryBuilder(relationalSchema).buildQuery(querySpec))
                .collect(Collectors.toSet());
    }

    public Set<ConjunctiveQuerySpec> parseToSpec(String queriesString) {
        CharStream input = CharStreams.fromString(queriesString);
        ConjunctiveQueriesGrammarLexer lexer = new ConjunctiveQueriesGrammarLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ConjunctiveQueriesParser.ErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConjunctiveQueriesGrammarParser parser = new ConjunctiveQueriesGrammarParser(tokens);
        parser.addErrorListener(new ConjunctiveQueriesParser.ErrorListener());
        ConjunctiveQueriesGrammarParser.ProgContext tree = parser.prog();
        return visitor.visitProg(tree);
    }

    private static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new ParserCanceledException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

}
