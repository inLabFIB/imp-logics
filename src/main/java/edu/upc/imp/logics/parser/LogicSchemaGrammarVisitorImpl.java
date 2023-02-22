package edu.upc.imp.logics.parser;

import edu.upc.imp.logics.specification.*;
import edu.upc.imp.parser.LogicSchemaGrammarBaseVisitor;
import edu.upc.imp.parser.LogicSchemaGrammarParser;

import java.util.LinkedList;
import java.util.List;

public class LogicSchemaGrammarVisitorImpl extends LogicSchemaGrammarBaseVisitor {

    private final LogicSchemaSpecification logicSchemaSpecification = new LogicSchemaSpecification();

    public LogicSchemaGrammarVisitorImpl(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    private final StringToTermSpecFactory stringToTermSpecFactory;

    @Override
    public LogicSchemaSpecification visitProg(LogicSchemaGrammarParser.ProgContext ctx) {
        visitChildren(ctx);
        return logicSchemaSpecification;
    }

    @Override
    public LogicConstraintSpec visitConstraint(LogicSchemaGrammarParser.ConstraintContext ctx) {
        List<LiteralSpec> body = visitBody(ctx.body());

        LogicConstraintSpec constraintSpec;
        if(ctx.CONSTRAINTID() != null) {
            String id = ctx.CONSTRAINTID().getText().substring(1); //skipping '@' symbol
            constraintSpec = new LogicConstraintSpec(id, body);
        } else {
            throw new RuntimeException("Currently we do not support constraint specs without id");
        }

        logicSchemaSpecification.addLogicConstraintSpec(constraintSpec);
        return constraintSpec;
    }

    @Override
    public DerivationRuleSpec visitDerivationRule(LogicSchemaGrammarParser.DerivationRuleContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> headTermsSpec = visitTermsList(ctx.atom().termsList());

        List<LiteralSpec> body = this.visitBody(ctx.body());
        return new DerivationRuleSpec(predicateName, headTermsSpec, body);
    }

    @Override
    public List<LiteralSpec> visitBody(LogicSchemaGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for(LogicSchemaGrammarParser.LiteralContext litContext: ctx.literal()){
            literals.add((LiteralSpec)this.visitLiteral(litContext));
        }
        return literals;
    }

    @Override
    public BuiltInLiteralSpec visitBuiltInLiteral(LogicSchemaGrammarParser.BuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = visitTerm(ctx.term(0));
        TermSpec rightTermSpec = visitTerm(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.OPERATOR().getText(), termSpecList);
    }

    @Override
    public OrdinaryLiteralSpec visitOrdinaryLiteral(LogicSchemaGrammarParser.OrdinaryLiteralContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = visitTermsList(ctx.atom().termsList());
        boolean isPositive = ctx.NOT() == null;
        return new OrdinaryLiteralSpec(predicateName, termSpecList, isPositive);
    }

    @Override
    public List<TermSpec> visitTermsList(LogicSchemaGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for(LogicSchemaGrammarParser.TermContext termContext: ctx.term()){
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }

    @Override
    public TermSpec visitTerm(LogicSchemaGrammarParser.TermContext ctx) {
        return stringToTermSpecFactory.createTermSpec(ctx.ID().getText());
    }
}
