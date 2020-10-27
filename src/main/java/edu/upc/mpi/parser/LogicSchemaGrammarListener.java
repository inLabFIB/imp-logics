package edu.upc.mpi.parser;

// Generated from C:\Users\Xavier\Documents\Doctorat\NetBeansProjects\MPILogics\src\edu\u005Cupc\mpi\parser\LogicSchemaGrammar.g4 by ANTLR 4.1
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogicSchemaGrammarParser}.
 */
public interface LogicSchemaGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(@NotNull LogicSchemaGrammarParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(@NotNull LogicSchemaGrammarParser.PredicateContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(@NotNull LogicSchemaGrammarParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(@NotNull LogicSchemaGrammarParser.LineContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#ordinaryLiteral}.
	 * @param ctx the parse tree
	 */
	void enterOrdinaryLiteral(@NotNull LogicSchemaGrammarParser.OrdinaryLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#ordinaryLiteral}.
	 * @param ctx the parse tree
	 */
	void exitOrdinaryLiteral(@NotNull LogicSchemaGrammarParser.OrdinaryLiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#derivationRule}.
	 * @param ctx the parse tree
	 */
	void enterDerivationRule(@NotNull LogicSchemaGrammarParser.DerivationRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#derivationRule}.
	 * @param ctx the parse tree
	 */
	void exitDerivationRule(@NotNull LogicSchemaGrammarParser.DerivationRuleContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterConstraint(@NotNull LogicSchemaGrammarParser.ConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitConstraint(@NotNull LogicSchemaGrammarParser.ConstraintContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#builtInLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBuiltInLiteral(@NotNull LogicSchemaGrammarParser.BuiltInLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#builtInLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBuiltInLiteral(@NotNull LogicSchemaGrammarParser.BuiltInLiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull LogicSchemaGrammarParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull LogicSchemaGrammarParser.TermContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#body}.
	 * @param ctx the parse tree
	 */
	void enterBody(@NotNull LogicSchemaGrammarParser.BodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#body}.
	 * @param ctx the parse tree
	 */
	void exitBody(@NotNull LogicSchemaGrammarParser.BodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(@NotNull LogicSchemaGrammarParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(@NotNull LogicSchemaGrammarParser.AtomContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#termsList}.
	 * @param ctx the parse tree
	 */
	void enterTermsList(@NotNull LogicSchemaGrammarParser.TermsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#termsList}.
	 * @param ctx the parse tree
	 */
	void exitTermsList(@NotNull LogicSchemaGrammarParser.TermsListContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(@NotNull LogicSchemaGrammarParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(@NotNull LogicSchemaGrammarParser.ProgContext ctx);

	/**
	 * Enter a parse tree produced by {@link LogicSchemaGrammarParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull LogicSchemaGrammarParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogicSchemaGrammarParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull LogicSchemaGrammarParser.LiteralContext ctx);
}