package edu.upc.imp.logics.services.parser;

import edu.upc.imp.logics.services.creation.spec.*;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import edu.upc.imp.parser.LogicSchemaGrammarBaseVisitor;
import edu.upc.imp.parser.LogicSchemaGrammarParser;

import java.util.LinkedList;
import java.util.List;

public class LogicSchemaGrammarToSpecVisitor extends LogicSchemaGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;
    private final LogicSchemaSpec logicSchemaSpec = new LogicSchemaSpec();

    public LogicSchemaGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public LogicSchemaSpec visitProg(LogicSchemaGrammarParser.ProgContext ctx) {
        visitChildren(ctx);
        return logicSchemaSpec;
    }

    @Override
    public LogicConstraintSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        BodySpec body = createBody(ctx.body());

        LogicConstraintSpec constraintSpec;
        if (ctx.CONSTRAINTID() != null) {
            String id = ctx.CONSTRAINTID().getText().substring(1); //skipping '@' symbol
            constraintSpec = new LogicConstraintSpec(id, body);
        } else {
            throw new RuntimeException("Currently we do not support constraint specs without id");
        }

        logicSchemaSpec.addLogicConstraintSpecs(constraintSpec);
        return constraintSpec;
    }

    private BodySpec createBody(LogicSchemaGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (LogicSchemaGrammarParser.LiteralContext litContext: ctx.literal()) {
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
        for (LogicSchemaGrammarParser.TermContext termContext: ctx.term()) {
            termSpecList.add(this.createTermSpec(termContext));
        }
        return termSpecList;
    }

    @Override
    public BuiltInLiteralSpec visitBuiltInLiteral(LogicSchemaGrammarParser.BuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = createTermSpec(ctx.term(0));
        TermSpec rightTermSpec = createTermSpec(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.OPERATOR().getText(), termSpecList);
    }

    @Override
    public OrdinaryLiteralSpec visitOrdinaryLiteral(LogicSchemaGrammarParser.OrdinaryLiteralContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        boolean isPositive = ctx.NOT() == null;
        return new OrdinaryLiteralSpec(predicateName, termSpecList, isPositive);
    }

    private TermSpec createTermSpec(LogicSchemaGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.ID().getText());
    }
}
