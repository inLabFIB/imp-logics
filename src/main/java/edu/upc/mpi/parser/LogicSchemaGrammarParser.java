package edu.upc.mpi.parser;

// Generated from C:\Users\Xavier\Documents\Doctorat\NetBeansProjects\MPILogics\src\edu\u005Cupc\mpi\parser\LogicSchemaGrammar.g4 by ANTLR 4.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LogicSchemaGrammarParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NOT=1, OPERATOR=2, ID=3, CONSTRAINTID=4, NEWLINE=5, WS=6, COMMENT=7, COMMA=8, 
		OPENPAR=9, CLOSEPAR=10, ARROW=11, EXTRAINFO=12;
	public static final String[] tokenNames = {
		"<INVALID>", "'not'", "OPERATOR", "ID", "CONSTRAINTID", "NEWLINE", "WS", 
		"COMMENT", "','", "'('", "')'", "':-'", "EXTRAINFO"
	};
	public static final int
		RULE_prog = 0, RULE_line = 1, RULE_constraint = 2, RULE_derivationRule = 3, 
		RULE_body = 4, RULE_literal = 5, RULE_builtInLiteral = 6, RULE_ordinaryLiteral = 7, 
		RULE_atom = 8, RULE_termsList = 9, RULE_predicate = 10, RULE_term = 11;
	public static final String[] ruleNames = {
		"prog", "line", "constraint", "derivationRule", "body", "literal", "builtInLiteral", 
		"ordinaryLiteral", "atom", "termsList", "predicate", "term"
	};

	@Override
	public String getGrammarFileName() { return "LogicSchemaGrammar.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public LogicSchemaGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgContext extends ParserRuleContext {
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(LogicSchemaGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(LogicSchemaGrammarParser.NEWLINE, i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitProg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitProg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << CONSTRAINTID) | (1L << NEWLINE) | (1L << COMMENT) | (1L << ARROW) | (1L << EXTRAINFO))) != 0)) {
				{
				{
				setState(25);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << CONSTRAINTID) | (1L << COMMENT) | (1L << ARROW) | (1L << EXTRAINFO))) != 0)) {
					{
					setState(24); line();
					}
				}

				setState(27); match(NEWLINE);
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(LogicSchemaGrammarParser.COMMENT, 0); }
		public ConstraintContext constraint() {
			return getRuleContext(ConstraintContext.class,0);
		}
		public TerminalNode EXTRAINFO() { return getToken(LogicSchemaGrammarParser.EXTRAINFO, 0); }
		public DerivationRuleContext derivationRule() {
			return getRuleContext(DerivationRuleContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			switch (_input.LA(1)) {
			case COMMENT:
				{
				setState(33); match(COMMENT);
				}
				break;
			case EXTRAINFO:
				{
				setState(34); match(EXTRAINFO);
				}
				break;
			case CONSTRAINTID:
			case ARROW:
				{
				setState(35); constraint();
				}
				break;
			case ID:
				{
				setState(36); derivationRule();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstraintContext extends ParserRuleContext {
		public TerminalNode CONSTRAINTID() { return getToken(LogicSchemaGrammarParser.CONSTRAINTID, 0); }
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(LogicSchemaGrammarParser.ARROW, 0); }
		public ConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constraint; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterConstraint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitConstraint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitConstraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstraintContext constraint() throws RecognitionException {
		ConstraintContext _localctx = new ConstraintContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_constraint);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			_la = _input.LA(1);
			if (_la==CONSTRAINTID) {
				{
				setState(39); match(CONSTRAINTID);
				}
			}

			setState(42); match(ARROW);
			setState(43); body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DerivationRuleContext extends ParserRuleContext {
		public BodyContext body() {
			return getRuleContext(BodyContext.class,0);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(LogicSchemaGrammarParser.ARROW, 0); }
		public DerivationRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_derivationRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterDerivationRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitDerivationRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitDerivationRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DerivationRuleContext derivationRule() throws RecognitionException {
		DerivationRuleContext _localctx = new DerivationRuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_derivationRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45); atom();
			setState(46); match(ARROW);
			setState(47); body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BodyContext extends ParserRuleContext {
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(LogicSchemaGrammarParser.COMMA); }
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public TerminalNode COMMA(int i) {
			return getToken(LogicSchemaGrammarParser.COMMA, i);
		}
		public BodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BodyContext body() throws RecognitionException {
		BodyContext _localctx = new BodyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49); literal();
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(50); match(COMMA);
				setState(51); literal();
				}
				}
				setState(56);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public BuiltInLiteralContext builtInLiteral() {
			return getRuleContext(BuiltInLiteralContext.class,0);
		}
		public OrdinaryLiteralContext ordinaryLiteral() {
			return getRuleContext(OrdinaryLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_literal);
		try {
			setState(59);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(57); builtInLiteral();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(58); ordinaryLiteral();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BuiltInLiteralContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TerminalNode OPERATOR() { return getToken(LogicSchemaGrammarParser.OPERATOR, 0); }
		public BuiltInLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_builtInLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterBuiltInLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitBuiltInLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitBuiltInLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BuiltInLiteralContext builtInLiteral() throws RecognitionException {
		BuiltInLiteralContext _localctx = new BuiltInLiteralContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_builtInLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61); term();
			setState(62); match(OPERATOR);
			setState(63); term();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OrdinaryLiteralContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(LogicSchemaGrammarParser.NOT, 0); }
		public TerminalNode OPENPAR() { return getToken(LogicSchemaGrammarParser.OPENPAR, 0); }
		public TerminalNode CLOSEPAR() { return getToken(LogicSchemaGrammarParser.CLOSEPAR, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public OrdinaryLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ordinaryLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterOrdinaryLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitOrdinaryLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitOrdinaryLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrdinaryLiteralContext ordinaryLiteral() throws RecognitionException {
		OrdinaryLiteralContext _localctx = new OrdinaryLiteralContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_ordinaryLiteral);
		try {
			setState(71);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(65); atom();
				}
				break;
			case NOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(66); match(NOT);
				setState(67); match(OPENPAR);
				setState(68); atom();
				setState(69); match(CLOSEPAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public TerminalNode OPENPAR() { return getToken(LogicSchemaGrammarParser.OPENPAR, 0); }
		public TermsListContext termsList() {
			return getRuleContext(TermsListContext.class,0);
		}
		public TerminalNode CLOSEPAR() { return getToken(LogicSchemaGrammarParser.CLOSEPAR, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_atom);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73); predicate();
			setState(74); match(OPENPAR);
			setState(75); termsList();
			setState(76); match(CLOSEPAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermsListContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(LogicSchemaGrammarParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LogicSchemaGrammarParser.COMMA, i);
		}
		public TermsListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_termsList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterTermsList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitTermsList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitTermsList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermsListContext termsList() throws RecognitionException {
		TermsListContext _localctx = new TermsListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_termsList);
		int _la;
		try {
			setState(87);
			switch (_input.LA(1)) {
			case CLOSEPAR:
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(79); term();
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(80); match(COMMA);
					setState(81); term();
					}
					}
					setState(86);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(LogicSchemaGrammarParser.ID, 0); }
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(LogicSchemaGrammarParser.ID, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LogicSchemaGrammarListener ) ((LogicSchemaGrammarListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LogicSchemaGrammarVisitor ) return ((LogicSchemaGrammarVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_term);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\16`\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\3\2\5\2\34\n\2\3\2\7\2\37\n\2\f\2\16\2\"\13\2\3\3\3\3\3"+
		"\3\3\3\5\3(\n\3\3\4\5\4+\n\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\6\3\6\3\6\7"+
		"\6\67\n\6\f\6\16\6:\13\6\3\7\3\7\5\7>\n\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\5\tJ\n\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\7\13U\n"+
		"\13\f\13\16\13X\13\13\5\13Z\n\13\3\f\3\f\3\r\3\r\3\r\2\16\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\2\2^\2 \3\2\2\2\4\'\3\2\2\2\6*\3\2\2\2\b/\3\2\2\2\n"+
		"\63\3\2\2\2\f=\3\2\2\2\16?\3\2\2\2\20I\3\2\2\2\22K\3\2\2\2\24Y\3\2\2\2"+
		"\26[\3\2\2\2\30]\3\2\2\2\32\34\5\4\3\2\33\32\3\2\2\2\33\34\3\2\2\2\34"+
		"\35\3\2\2\2\35\37\7\7\2\2\36\33\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2"+
		"\2\2!\3\3\2\2\2\" \3\2\2\2#(\7\t\2\2$(\7\16\2\2%(\5\6\4\2&(\5\b\5\2\'"+
		"#\3\2\2\2\'$\3\2\2\2\'%\3\2\2\2\'&\3\2\2\2(\5\3\2\2\2)+\7\6\2\2*)\3\2"+
		"\2\2*+\3\2\2\2+,\3\2\2\2,-\7\r\2\2-.\5\n\6\2.\7\3\2\2\2/\60\5\22\n\2\60"+
		"\61\7\r\2\2\61\62\5\n\6\2\62\t\3\2\2\2\638\5\f\7\2\64\65\7\n\2\2\65\67"+
		"\5\f\7\2\66\64\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2\2\29\13\3\2\2\2:"+
		"8\3\2\2\2;>\5\16\b\2<>\5\20\t\2=;\3\2\2\2=<\3\2\2\2>\r\3\2\2\2?@\5\30"+
		"\r\2@A\7\4\2\2AB\5\30\r\2B\17\3\2\2\2CJ\5\22\n\2DE\7\3\2\2EF\7\13\2\2"+
		"FG\5\22\n\2GH\7\f\2\2HJ\3\2\2\2IC\3\2\2\2ID\3\2\2\2J\21\3\2\2\2KL\5\26"+
		"\f\2LM\7\13\2\2MN\5\24\13\2NO\7\f\2\2O\23\3\2\2\2PZ\3\2\2\2QV\5\30\r\2"+
		"RS\7\n\2\2SU\5\30\r\2TR\3\2\2\2UX\3\2\2\2VT\3\2\2\2VW\3\2\2\2WZ\3\2\2"+
		"\2XV\3\2\2\2YP\3\2\2\2YQ\3\2\2\2Z\25\3\2\2\2[\\\7\5\2\2\\\27\3\2\2\2]"+
		"^\7\5\2\2^\31\3\2\2\2\13\33 \'*8=IVY";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}