grammar QueryGrammar;

@lexer::members {
  private CustomBuiltInPredicateNameChecker builtInPredicateNameChecker = new CustomBuiltInPredicateNameChecker(java.util.Set.of());

  public QueryGrammarLexer(CharStream input, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
    this(input);
    this.builtInPredicateNameChecker = builtInPredicateNameChecker;
  }
}

tokens {
  BUILTIN_PREDICATE
}

NOT:            'not';
BOOLEAN:        'TRUE' | 'FALSE';
OPERATOR:       '='|'<>'|'<'|'>'|'<='|'>=';
STRING:         SINGLE_QUOTE | DOUBLE_QUOTE;
NUMBER:         DECIMAL | FLOAT | REAL;
ALPHANUMERIC_WITH_PRIMA: ALPHANUMERIC [']* {if(builtInPredicateNameChecker.isBuiltInPredicateName(getText())) setType(QueryGrammarParser.BUILTIN_PREDICATE);};
CONSTRAINTID:   '@' ALPHANUMERIC;
NEWLINE:        '\r'? '\n';
WS:             [ \t]+ -> skip ; // toss out whitespace
COMMENT:        '%' ~[\r\n]*;
COMMA:          ',';
OPENPAR:        '(';
CLOSEPAR:       ')';
ARROW:          ':-';

fragment DEC_DOT_DEC:   (DEC_DIGIT+ '.' DEC_DIGIT+ |  DEC_DIGIT+ '.' | '.' DEC_DIGIT+);
fragment DEC_DIGIT:     [0-9];
fragment DECIMAL:       DEC_DIGIT+;
fragment FLOAT:         DEC_DOT_DEC;
fragment REAL:          (DECIMAL  | DEC_DOT_DEC) (('E'|'e') [+-]? DEC_DIGIT+);
fragment ALPHANUMERIC:  [a-zA-Z0-9_$?:]+;
fragment SINGLE_QUOTE:  '\'' (~'\'' | '\\\'')* '\'';
fragment DOUBLE_QUOTE:  '"' (~'"' | '\\"')* '"';

prog: NEWLINE* line? (NEWLINE+ line)* NEWLINE*;
line: (COMMENT | query);
query: OPENPAR termsList CLOSEPAR ARROW body;
body: literal (COMMA literal)*;
literal: builtInLiteral | ordinaryLiteral;
builtInLiteral: comparisonBuiltInLiteral | booleanBuiltInLiteral | customBuiltInLiteral;
comparisonBuiltInLiteral: term OPERATOR term;
booleanBuiltInLiteral: BOOLEAN OPENPAR CLOSEPAR;
customBuiltInLiteral: BUILTIN_PREDICATE OPENPAR termsList CLOSEPAR;
ordinaryLiteral: positiveAtom | negatedAtom;
positiveAtom: atom;
negatedAtom: NOT OPENPAR atom CLOSEPAR;
atom: predicate OPENPAR termsList CLOSEPAR;
termsList: | term (COMMA term)*;
predicate: ALPHANUMERIC_WITH_PRIMA;
term: STRING | NUMBER | ALPHANUMERIC_WITH_PRIMA;