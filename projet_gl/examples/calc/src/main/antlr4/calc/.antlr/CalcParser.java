// Generated from /user/5/.base/heuzec/home/ensimag/GL/Projet_GL/examples/calc/src/main/antlr4/calc/CalcParser.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class CalcParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INT=1, PLUS=2, MINUS=3, TIMES=4, WS=5;
	public static final int
		RULE_expr = 0, RULE_sum_expr = 1, RULE_mult_expr = 2, RULE_literal = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "sum_expr", "mult_expr", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'+'", "'-'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INT", "PLUS", "MINUS", "TIMES", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "CalcParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CalcParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public AbstractExpr tree;
		public Sum_exprContext sum_expr;
		public Sum_exprContext sum_expr() {
			return getRuleContext(Sum_exprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(CalcParser.EOF, 0); }
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			((ExprContext)_localctx).sum_expr = sum_expr();
			setState(9);
			match(EOF);

			                    // between braces ({}), an action for this rule,
			                    // i.e. a piece of Java code that will be executed
			                    // while matching the rule. Must assign a value to
			                    // _localctx.tree to produce the value declared in the
			                    // returns clause above.
			                    ((ExprContext)_localctx).tree =  ((ExprContext)_localctx).sum_expr.tree;
			            
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

	@SuppressWarnings("CheckReturnValue")
	public static class Sum_exprContext extends ParserRuleContext {
		public AbstractExpr tree;
		public Mult_exprContext e;
		public Mult_exprContext e2;
		public List<Mult_exprContext> mult_expr() {
			return getRuleContexts(Mult_exprContext.class);
		}
		public Mult_exprContext mult_expr(int i) {
			return getRuleContext(Mult_exprContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(CalcParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(CalcParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(CalcParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(CalcParser.MINUS, i);
		}
		public Sum_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sum_expr; }
	}

	public final Sum_exprContext sum_expr() throws RecognitionException {
		Sum_exprContext _localctx = new Sum_exprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_sum_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			((Sum_exprContext)_localctx).e = mult_expr();

			            ((Sum_exprContext)_localctx).tree =  ((Sum_exprContext)_localctx).e.tree; 
			        
			setState(24);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS || _la==MINUS) {
				{
				setState(22);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case PLUS:
					{
					setState(14);
					match(PLUS);
					setState(15);
					((Sum_exprContext)_localctx).e2 = mult_expr();

					            // action inside (expr)* => will be executed once for each
					            // match of "expr".
					            ((Sum_exprContext)_localctx).tree =  new Plus(_localctx.tree, ((Sum_exprContext)_localctx).e2.tree);
					        
					}
					break;
				case MINUS:
					{
					setState(18);
					match(MINUS);
					setState(19);
					((Sum_exprContext)_localctx).e2 = mult_expr();

					            ((Sum_exprContext)_localctx).tree =  new Minus(_localctx.tree, ((Sum_exprContext)_localctx).e2.tree);
					        
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(26);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Mult_exprContext extends ParserRuleContext {
		public AbstractExpr tree;
		public LiteralContext e;
		public LiteralContext e2;
		public List<LiteralContext> literal() {
			return getRuleContexts(LiteralContext.class);
		}
		public LiteralContext literal(int i) {
			return getRuleContext(LiteralContext.class,i);
		}
		public List<TerminalNode> TIMES() { return getTokens(CalcParser.TIMES); }
		public TerminalNode TIMES(int i) {
			return getToken(CalcParser.TIMES, i);
		}
		public Mult_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult_expr; }
	}

	public final Mult_exprContext mult_expr() throws RecognitionException {
		Mult_exprContext _localctx = new Mult_exprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_mult_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			((Mult_exprContext)_localctx).e = literal();

			            ((Mult_exprContext)_localctx).tree =  ((Mult_exprContext)_localctx).e.tree;
			        
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TIMES) {
				{
				{
				setState(29);
				match(TIMES);
				setState(30);
				((Mult_exprContext)_localctx).e2 = literal();

				            ((Mult_exprContext)_localctx).tree =  new Times(_localctx.tree, ((Mult_exprContext)_localctx).e2.tree);
				        
				}
				}
				setState(37);
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

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public IntLiteral tree;
		public Token INT;
		public TerminalNode INT() { return getToken(CalcParser.INT, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_literal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			((LiteralContext)_localctx).INT = match(INT);

			            try {
			                ((LiteralContext)_localctx).tree =  new IntLiteral(Integer.parseInt((((LiteralContext)_localctx).INT!=null?((LiteralContext)_localctx).INT.getText():null)));
			            } catch (NumberFormatException e) {
			                // The integer could not be parsed (probably it's too large).
			                // set _localctx.tree to null, and then fail with the semantic predicate
			                // {_localctx.tree != null}?. In decac, we'll have a more advanced error
			                // management.
			                ((LiteralContext)_localctx).tree =  null;
			            }
			        
			setState(40);
			if (!(_localctx.tree != null)) throw new FailedPredicateException(this, "$tree != null");
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return literal_sempred((LiteralContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean literal_sempred(LiteralContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return _localctx.tree != null;
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0005+\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0005"+
		"\u0001\u0017\b\u0001\n\u0001\f\u0001\u001a\t\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\"\b\u0002"+
		"\n\u0002\f\u0002%\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0000\u0000\u0004\u0000\u0002\u0004\u0006\u0000\u0000)\u0000"+
		"\b\u0001\u0000\u0000\u0000\u0002\f\u0001\u0000\u0000\u0000\u0004\u001b"+
		"\u0001\u0000\u0000\u0000\u0006&\u0001\u0000\u0000\u0000\b\t\u0003\u0002"+
		"\u0001\u0000\t\n\u0005\u0000\u0000\u0001\n\u000b\u0006\u0000\uffff\uffff"+
		"\u0000\u000b\u0001\u0001\u0000\u0000\u0000\f\r\u0003\u0004\u0002\u0000"+
		"\r\u0018\u0006\u0001\uffff\uffff\u0000\u000e\u000f\u0005\u0002\u0000\u0000"+
		"\u000f\u0010\u0003\u0004\u0002\u0000\u0010\u0011\u0006\u0001\uffff\uffff"+
		"\u0000\u0011\u0017\u0001\u0000\u0000\u0000\u0012\u0013\u0005\u0003\u0000"+
		"\u0000\u0013\u0014\u0003\u0004\u0002\u0000\u0014\u0015\u0006\u0001\uffff"+
		"\uffff\u0000\u0015\u0017\u0001\u0000\u0000\u0000\u0016\u000e\u0001\u0000"+
		"\u0000\u0000\u0016\u0012\u0001\u0000\u0000\u0000\u0017\u001a\u0001\u0000"+
		"\u0000\u0000\u0018\u0016\u0001\u0000\u0000\u0000\u0018\u0019\u0001\u0000"+
		"\u0000\u0000\u0019\u0003\u0001\u0000\u0000\u0000\u001a\u0018\u0001\u0000"+
		"\u0000\u0000\u001b\u001c\u0003\u0006\u0003\u0000\u001c#\u0006\u0002\uffff"+
		"\uffff\u0000\u001d\u001e\u0005\u0004\u0000\u0000\u001e\u001f\u0003\u0006"+
		"\u0003\u0000\u001f \u0006\u0002\uffff\uffff\u0000 \"\u0001\u0000\u0000"+
		"\u0000!\u001d\u0001\u0000\u0000\u0000\"%\u0001\u0000\u0000\u0000#!\u0001"+
		"\u0000\u0000\u0000#$\u0001\u0000\u0000\u0000$\u0005\u0001\u0000\u0000"+
		"\u0000%#\u0001\u0000\u0000\u0000&\'\u0005\u0001\u0000\u0000\'(\u0006\u0003"+
		"\uffff\uffff\u0000()\u0004\u0003\u0000\u0001)\u0007\u0001\u0000\u0000"+
		"\u0000\u0003\u0016\u0018#";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}