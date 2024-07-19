package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.LinkedList;
import java.util.List;

public abstract class LogicSchemaGrammarToSpecVisitor<T extends LogicConstraintSpec> extends LogicSchemaGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;

    protected LogicSchemaSpec<T> logicSchemaSpec;

    protected LogicSchemaGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public LogicSchemaSpec<T> visitProg(LogicSchemaGrammarParser.ProgContext ctx) {
        logicSchemaSpec = new LogicSchemaSpec<>();
        visitChildren(ctx);
        return logicSchemaSpec;
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
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }

    protected BodySpec createBody(LogicSchemaGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (LogicSchemaGrammarParser.LiteralContext litContext : ctx.literal()) {
            literals.add((LiteralSpec) this.visitLiteral(litContext));
        }
        return new BodySpec(literals);
    }

    @Override
    public BuiltInLiteralSpec visitComparisonBuiltInLiteral(LogicSchemaGrammarParser.ComparisonBuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = visitTerm(ctx.term(0));
        TermSpec rightTermSpec = visitTerm(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.OPERATOR().getText(), termSpecList);
    }

    @Override
    public BuiltInLiteralSpec visitBooleanBuiltInLiteral(LogicSchemaGrammarParser.BooleanBuiltInLiteralContext ctx) {
        return new BuiltInLiteralSpec(ctx.BOOLEAN().getText(), List.of());
    }

    @Override
    public BuiltInLiteralSpec visitCustomBuiltInLiteral(LogicSchemaGrammarParser.CustomBuiltInLiteralContext ctx) {
        String operatorName = ctx.BUILTIN_PREDICATE().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.termsList());
        return new BuiltInLiteralSpec(operatorName, termSpecList);
    }

    @Override
    public OrdinaryLiteralSpec visitPositiveAtom(LogicSchemaGrammarParser.PositiveAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
    }

    @Override
    public OrdinaryLiteralSpec visitNegatedAtom(LogicSchemaGrammarParser.NegatedAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, false);
    }

    @Override
    public TermSpec visitTerm(LogicSchemaGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.getText());
    }
}
