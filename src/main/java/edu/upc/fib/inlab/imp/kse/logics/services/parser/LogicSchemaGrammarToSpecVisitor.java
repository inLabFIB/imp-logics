package edu.upc.fib.inlab.imp.kse.logics.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.LinkedList;
import java.util.List;

public abstract class LogicSchemaGrammarToSpecVisitor<T extends LogicConstraintSpec> extends LogicSchemaGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;
    private final BuiltInPredicateNameChecker builtInPredicateNameChecker;

    protected LogicSchemaSpec<T> logicSchemaSpec;

    public LogicSchemaGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory, BooleanBuiltInPredicateNameChecker builtInPredicateNameChecker) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
        this.builtInPredicateNameChecker = builtInPredicateNameChecker;
    }

    @Override
    public LogicSchemaSpec<T> visitProg(LogicSchemaGrammarParser.ProgContext ctx) {
        logicSchemaSpec = new LogicSchemaSpec<>();
        visitChildren(ctx);
        return logicSchemaSpec;
    }

    protected BodySpec createBody(LogicSchemaGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (LogicSchemaGrammarParser.LiteralContext litContext : ctx.literal()) {
            literals.add((LiteralSpec) this.visitLiteral(litContext));
        }
        return new BodySpec(literals);
    }

    @Override
    public DerivationRuleSpec visitDerivationRule(LogicSchemaGrammarParser.DerivationRuleContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> headTermsSpec = createTermsList(ctx.atom().termsList());
        BodySpec body = this.createBody(ctx.body());
        DerivationRuleSpec derivationRuleSpec = new DerivationRuleSpec(predicateName, headTermsSpec, body);
        logicSchemaSpec.addDerivationRuleSpecs(derivationRuleSpec);
        return derivationRuleSpec;
    }

    private List<TermSpec> createTermsList(LogicSchemaGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (LogicSchemaGrammarParser.TermContext termContext : ctx.term()) {
            termSpecList.add(this.createTermSpec(termContext));
        }
        return termSpecList;
    }

    @Override
    public BuiltInLiteralSpec visitComparisonBuiltInLiteral(LogicSchemaGrammarParser.ComparisonBuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = createTermSpec(ctx.term(0));
        TermSpec rightTermSpec = createTermSpec(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.OPERATOR().getText(), termSpecList);
    }

    @Override
    public LiteralSpec visitAtom(LogicSchemaGrammarParser.AtomContext ctx) {
        String predicateName = ctx.predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.termsList());
        if (builtInPredicateNameChecker.isBuiltInPredicateName(predicateName)) {
            return new BuiltInLiteralSpec(predicateName, termSpecList);
        } else {
            return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
        }
    }

    @Override
    public OrdinaryLiteralSpec visitNegatedAtom(LogicSchemaGrammarParser.NegatedAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, false);
    }

    private TermSpec createTermSpec(LogicSchemaGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.ID().getText());
    }
}
