package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class QueriesGrammarToSpecVisitor extends ConjunctiveQueriesGrammarBaseVisitor {

    private final StringToTermSpecFactory stringToTermSpecFactory;

    private Set<ConjunctiveQuerySpec> result;

    public QueriesGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public Set<ConjunctiveQuerySpec> visitProg(ConjunctiveQueriesGrammarParser.ProgContext ctx) {
        result = new HashSet<>();
        visitChildren(ctx);
        return result;
    }

    @Override
    public ConjunctiveQuerySpec visitConjunctiveQuery(ConjunctiveQueriesGrammarParser.ConjunctiveQueryContext ctx) {
        //TODO:!!!
        return null;
    }

    private List<TermSpec> createTermsList(ConjunctiveQueriesGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (ConjunctiveQueriesGrammarParser.TermContext termContext : ctx.term()) {
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }

    @Override
    public OrdinaryLiteralSpec visitPositiveAtom(ConjunctiveQueriesGrammarParser.PositiveAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
    }

    @Override
    public TermSpec visitTerm(ConjunctiveQueriesGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.getText());
    }

}
