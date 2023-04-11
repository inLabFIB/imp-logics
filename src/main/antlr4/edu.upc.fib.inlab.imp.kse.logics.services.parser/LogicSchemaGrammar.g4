grammar LogicSchemaGrammar;

@lexer::members {
  private java.util.Set<String> special = new java.util.HashSet<>();

  public LogicSchemaGrammarLexer(CharStream input, java.util.Set<String> special) {
    this(input);
    this.special = special;
  }
}

tokens {
  BUILTIN_PREDICATE
}


NOT: 'not';
BOOLEAN: 'TRUE' | 'FALSE';
OPERATOR: '='|'<>'|'<'|'>'|'<='|'>=';
ID: ([A-Za-z0-9_'?])+ {if(special.contains(getText())) setType(LogicSchemaGrammarParser.BUILTIN_PREDICATE);};
CONSTRAINTIDSTART: '@';
NEWLINE:'\r'? '\n';
WS : [ \t]+ -> skip ; // toss out whitespace
COMMENT : '%' ~[\r\n]*;
COMMA: ',';
OPENPAR: '(';
CLOSEPAR: ')';
ARROW: ':-';

prog: NEWLINE* line? (NEWLINE+ line)* NEWLINE*;
line: (COMMENT | constraint | derivationRule);
constraint: (CONSTRAINTIDSTART ID)? ARROW body;
derivationRule: atom ARROW body;
body: literal (COMMA literal)*;
literal: builtInLiteral | ordinaryLiteral;
builtInLiteral: comparisonBuiltInLiteral | booleanBuiltInLiteral | customBuiltInLiteral;
comparisonBuiltInLiteral: term OPERATOR term;
booleanBuiltInLiteral: BOOLEAN OPENPAR CLOSEPAR;
customBuiltInLiteral: BUILTIN_PREDICATE OPENPAR termsList CLOSEPAR;
ordinaryLiteral: atom | negatedAtom;
negatedAtom: NOT OPENPAR atom CLOSEPAR;
atom: predicate OPENPAR termsList CLOSEPAR;
termsList: | term (COMMA term)*;
predicate: ID;
term: ID;
