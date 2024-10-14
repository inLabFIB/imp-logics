package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.DependencySchemaSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.EGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.HeadAtomsSpec;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec.TGDSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;

import java.util.LinkedList;
import java.util.List;

public class DependencySchemaGrammarToSpecVisitor extends DependencySchemaGrammarBaseVisitor<LogicElementSpec> {

    private final StringToTermSpecFactory stringToTermSpecFactory;

    protected DependencySchemaSpec dependencySchemaSpec;

    public DependencySchemaGrammarToSpecVisitor(StringToTermSpecFactory stringToTermSpecFactory) {
        this.stringToTermSpecFactory = stringToTermSpecFactory;
    }

    @Override
    public DependencySchemaSpec visitProg(DependencySchemaGrammarParser.ProgContext ctx) {
        dependencySchemaSpec = new DependencySchemaSpec();
        visitChildren(ctx);
        return dependencySchemaSpec;
    }

    @Override
    public TGDSpec visitTgd(DependencySchemaGrammarParser.TgdContext ctx) {
        BodySpec body = visitBody(ctx.body());
        HeadAtomsSpec head = visitTgd_head(ctx.tgd_head());
        TGDSpec tgdSpec = new TGDSpec(body, head);
        dependencySchemaSpec.addDependencySpecs(tgdSpec);
        return tgdSpec;
    }

    @Override
    public EGDSpec visitEgd(DependencySchemaGrammarParser.EgdContext ctx) {
        BodySpec body = visitBody(ctx.body());
        BuiltInLiteralSpec head = visitEgd_head(ctx.egd_head());
        EGDSpec egdSpec = new EGDSpec(body, head);
        dependencySchemaSpec.addDependencySpecs(egdSpec);
        return egdSpec;
    }

    @Override
    public HeadAtomsSpec visitTgd_head(DependencySchemaGrammarParser.Tgd_headContext ctx) {
        List<OrdinaryLiteralSpec> literals = new LinkedList<>();
        for (DependencySchemaGrammarParser.PositiveAtomContext atomContext : ctx.positiveAtom()) {
            literals.add(this.visitPositiveAtom(atomContext));
        }
        return new HeadAtomsSpec(literals);
    }

    @Override
    public BuiltInLiteralSpec visitEgd_head(DependencySchemaGrammarParser.Egd_headContext ctx) {
        TermSpec leftTermSpec = visitTerm(ctx.term(0));
        TermSpec rightTermSpec = visitTerm(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.EQ().getText(), termSpecList);
    }

    @Override
    public BodySpec visitBody(DependencySchemaGrammarParser.BodyContext ctx) {
        List<LiteralSpec> literals = new LinkedList<>();
        for (DependencySchemaGrammarParser.LiteralContext litContext : ctx.literal()) {
            literals.add((LiteralSpec) this.visitLiteral(litContext));
        }
        return new BodySpec(literals);
    }

    @Override
    public BuiltInLiteralSpec visitComparisonBuiltInLiteral(DependencySchemaGrammarParser.ComparisonBuiltInLiteralContext ctx) {
        TermSpec leftTermSpec = visitTerm(ctx.term(0));
        TermSpec rightTermSpec = visitTerm(ctx.term(1));
        List<TermSpec> termSpecList = List.of(leftTermSpec, rightTermSpec);
        return new BuiltInLiteralSpec(ctx.operator().getText(), termSpecList);
    }

    @Override
    public BuiltInLiteralSpec visitBooleanBuiltInLiteral(DependencySchemaGrammarParser.BooleanBuiltInLiteralContext ctx) {
        return new BuiltInLiteralSpec(ctx.BOOLEAN().getText(), List.of());
    }

    @Override
    public BuiltInLiteralSpec visitCustomBuiltInLiteral(DependencySchemaGrammarParser.CustomBuiltInLiteralContext ctx) {
        String operatorName = ctx.BUILTIN_PREDICATE().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.termsList());
        return new BuiltInLiteralSpec(operatorName, termSpecList);
    }

    @Override
    public OrdinaryLiteralSpec visitPositiveAtom(DependencySchemaGrammarParser.PositiveAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, true);
    }

    @Override
    public OrdinaryLiteralSpec visitNegatedAtom(DependencySchemaGrammarParser.NegatedAtomContext ctx) {
        String predicateName = ctx.atom().predicate().getText();
        List<TermSpec> termSpecList = createTermsList(ctx.atom().termsList());
        return new OrdinaryLiteralSpec(predicateName, termSpecList, false);
    }

    @Override
    public TermSpec visitTerm(DependencySchemaGrammarParser.TermContext ctx) {
        if (ctx.UNNAMED_VARIABLE() != null) return new UnnamedVariableSpec(ctx.getText());
        return stringToTermSpecFactory.createTermSpec(ctx.getText());
    }

    private List<TermSpec> createTermsList(DependencySchemaGrammarParser.TermsListContext ctx) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (DependencySchemaGrammarParser.TermContext termContext : ctx.term()) {
            termSpecList.add(this.visitTerm(termContext));
        }
        return termSpecList;
    }
}
