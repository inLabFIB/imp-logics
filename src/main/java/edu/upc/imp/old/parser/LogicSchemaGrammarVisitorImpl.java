package edu.upc.imp.old.parser;

import edu.upc.imp.old.augmented_logicschema.EventPredicate;
import edu.upc.imp.old.logicschema.*;
import edu.upc.imp.parser.LogicSchemaGrammarBaseVisitor;
import edu.upc.imp.parser.LogicSchemaGrammarParser;
import edu.upc.imp.parser.LogicSchemaGrammarParser.AtomContext;
import edu.upc.imp.parser.LogicSchemaGrammarParser.LiteralContext;
import edu.upc.imp.parser.LogicSchemaGrammarParser.TermContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class LogicSchemaGrammarVisitorImpl extends LogicSchemaGrammarBaseVisitor {
    private LogicSchema logicSchema;

    /**
     * Constructs a LogicSchemaGrammarVisitorImpl
     * @param schema empty schema in which to add the resultant constriants and predicates
     */
    public LogicSchemaGrammarVisitorImpl(LogicSchema schema){
        logicSchema = schema;
    }

        /**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public List<Literal> visitBody(@NotNull LogicSchemaGrammarParser.BodyContext ctx) {
            List<Literal> literals = new LinkedList();
            for(LiteralContext litContext: ctx.literal()){
                literals.add((Literal) this.visitLiteral(litContext));
            }
            return literals;
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public Atom visitAtom(@NotNull LogicSchemaGrammarParser.AtomContext ctx) {
            Predicate predicate = this.visitPredicate(ctx.predicate());
            List<Term> terms = this.visitTermsList(ctx.termsList());
            return new Atom(predicate, terms);
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public List<Term> visitTermsList(@NotNull LogicSchemaGrammarParser.TermsListContext ctx) {
            List<Term> terms = new LinkedList();
            for(TermContext termContext: ctx.term()){
                terms.add(this.visitTerm(termContext));
            }
            return terms;
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public Predicate visitPredicate(@NotNull LogicSchemaGrammarParser.PredicateContext ctx) {
            String name = ctx.ID().getText();
            Predicate result = logicSchema.getPredicate(name);
            if(result == null){
                int arity = ((AtomContext)ctx.getParent()).termsList().term().size();
                
                if(name.startsWith("ins_") || name.startsWith("del_")){
                    EventPredicate.EventType eventType = name.startsWith("ins_")? EventPredicate.EventType.INSERT: EventPredicate.EventType.DELETE;
                    String realName = name.substring("ins_".length());
                    Predicate realPredicate = logicSchema.getPredicate(realName);
                    if(realPredicate == null){
                        realPredicate = new PredicateImpl(realName, arity);
                        logicSchema.addPredicate(realPredicate);
                    }
                    result = new EventPredicate(realPredicate, eventType);
                    logicSchema.addPredicate(result);
                }
                else {
                    result = new PredicateImpl(name, arity);
                }
                
                logicSchema.addPredicate(result);
            }
            return result;
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public LogicConstraint visitConstraint(@NotNull LogicSchemaGrammarParser.ConstraintContext ctx) {
        List<Literal> body = visitBody(ctx.body());

        LogicConstraint constraint;
        if (ctx.ID() != null) {
            String number = ctx.ID().getText().replace("@", "");
            int id = Integer.parseInt(number);
            constraint = new LogicConstraint(id, body);
        } else {
            constraint = new LogicConstraint(body);
        }

        logicSchema.addConstraint(constraint);
        return constraint;
    }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public DerivationRule visitDerivationRule(@NotNull LogicSchemaGrammarParser.DerivationRuleContext ctx) {
            Atom head = this.visitAtom(ctx.atom());
            List<Literal> body = this.visitBody(ctx.body());
            return new DerivationRule(head, body);
        }


	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public OrdinaryLiteral visitOrdinaryLiteral(@NotNull LogicSchemaGrammarParser.OrdinaryLiteralContext ctx) {
            boolean isPositive = ctx.NOT() == null;
            Atom atom = this.visitAtom(ctx.atom());
            return new OrdinaryLiteral(atom, isPositive);
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public LogicSchema visitProg(@NotNull LogicSchemaGrammarParser.ProgContext ctx) {
            visitChildren(ctx);
            return this.logicSchema;
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public Term visitTerm(@NotNull LogicSchemaGrammarParser.TermContext ctx) {
            return new Term(ctx.ID().getText());
        }

	/**
	 * {@inheritDoc}
	 * <p/>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 */
	@Override public BuiltInLiteral visitBuiltInLiteral(@NotNull LogicSchemaGrammarParser.BuiltInLiteralContext ctx) {
            Term leftTerm = this.visitTerm(ctx.term(0));
            Term rightTerm = this.visitTerm(ctx.term(1));
            String operator = ctx.OPERATOR().getText();
            return new BuiltInLiteral(leftTerm, rightTerm, operator);
        }


}
