package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.*;

public class QueryGrammarToSpecVisitor extends QueryGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;

    private Set<QuerySpec> queries;

    public QueryGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public QuerySetSpec visitProg(QueryGrammarParser.ProgContext ctx) {
        queries = new LinkedHashSet<>();
        visitChildren(ctx);
        return new QuerySetSpec(queries);
    }

    @Override
    public QuerySpec visitConjunctiveQuery(QueryGrammarParser.ConjunctiveQueryContext ctx) {
        List<TermSpec> headTerms = createTermsList(ctx.termsList());
        BodySpec body = createBody(ctx.body());
        QuerySpec parsedQuerySpec = new QuerySpec(headTerms, body);
        queries.add(parsedQuerySpec);
        return parsedQuerySpec;
    }

    protected BodySpec createBody(QueryGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (QueryGrammarParser.LiteralContext litContext : ctx.literal()) {
            literals.add((LiteralSpec) this.visitLiteral(litContext));
        }
        return new BodySpec(literals);
    }

    @Override
    public OrdinaryLiteralSpec visitPositiveAtom(QueryGrammarParser.PositiveAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
    }

    private List<TermSpec> createTermsList(QueryGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (QueryGrammarParser.TermContext termContext : ctx.term()) {
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }

    @Override
    public TermSpec visitTerm(QueryGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.getText());
    }

}
