package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    public QuerySpec visitQuery(QueryGrammarParser.QueryContext ctx) {
        List<TermSpec> headTerms = createTermsList(ctx.termsList());
        BodySpec body = createBody(ctx.body());
        QuerySpec parsedQuerySpec = new QuerySpec(headTerms, body);
        queries.add(parsedQuerySpec);
        return parsedQuerySpec;
    }

    @Override
    public BuiltInLiteralSpec visitComparisonBuiltInLiteral(QueryGrammarParser.ComparisonBuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = visitTerm(ctx.term(0));
        TermSpec rightTermSpec = visitTerm(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.OPERATOR().getText(), termSpecList);
    }

    @Override
    public BuiltInLiteralSpec visitBooleanBuiltInLiteral(QueryGrammarParser.BooleanBuiltInLiteralContext ctx) {
        return new BuiltInLiteralSpec(ctx.BOOLEAN().getText(), List.of());
    }

    @Override
    public BuiltInLiteralSpec visitCustomBuiltInLiteral(QueryGrammarParser.CustomBuiltInLiteralContext ctx) {
        String operatorName = ctx.BUILTIN_PREDICATE().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.termsList());
        return new BuiltInLiteralSpec(operatorName, termSpecList);
    }

    @Override
    public OrdinaryLiteralSpec visitPositiveAtom(QueryGrammarParser.PositiveAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
    }

    @Override
    public OrdinaryLiteralSpec visitNegatedAtom(QueryGrammarParser.NegatedAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, false);
    }

    @Override
    public TermSpec visitTerm(QueryGrammarParser.TermContext ctx) {
        if (ctx.UNNAMED_VARIABLE() != null) return new UnnamedVariableSpec(ctx.getText());
        return stringToTermSpecFactory.createTermSpec(ctx.getText());
    }

    private List<TermSpec> createTermsList(QueryGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (QueryGrammarParser.TermContext termContext : ctx.term()) {
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }

    protected BodySpec createBody(QueryGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (QueryGrammarParser.LiteralContext litContext : ctx.literal()) {
            literals.add((LiteralSpec) this.visitLiteral(litContext));
        }
        return new BodySpec(literals);
    }

}
