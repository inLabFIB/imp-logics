package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Query;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.QueryBuilder;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.QuerySetSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.TermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.exceptions.ParserCanceledException;
import org.antlr.v4.runtime.*;


import java.util.List;
import java.util.Set;

public class QueryParser {

    private final QueryGrammarToSpecVisitor visitor;

    public QueryParser() {
        this(new AllVariableTermTypeCriteria());
    }

    public QueryParser(TermTypeCriteria termTypeCriteria) {
        visitor = new QueryGrammarToSpecVisitor(new StringToTermSpecFactory(termTypeCriteria));
    }

    public List<Query> parse(String queriesString) {
        QuerySetSpec queriesSpecs = parseToSpec(queriesString);
        return queriesSpecs.querySpecSet().stream()
                .map(querySpec -> new QueryBuilder().buildQuery(querySpec))
                .toList();
    }

    public List<Query> parse(String queriesString, Set<Predicate> relationalSchema) {
        QuerySetSpec queriesSpecs = parseToSpec(queriesString);
        return queriesSpecs.querySpecSet().stream()
                .map(querySpec -> new QueryBuilder(relationalSchema).buildQuery(querySpec))
                .toList();
    }

    public QuerySetSpec parseToSpec(String queriesString) {
        CharStream input = CharStreams.fromString(queriesString);
        QueryGrammarLexer lexer = new QueryGrammarLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new QueryParser.ErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        QueryGrammarParser parser = new QueryGrammarParser(tokens);
        parser.addErrorListener(new QueryParser.ErrorListener());
        QueryGrammarParser.ProgContext tree = parser.prog();
        return visitor.visitProg(tree);
    }

    private static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new ParserCanceledException("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

}
