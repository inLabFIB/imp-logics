grammar LogicSchemaGrammar;

@lexer::members {
  private CustomBuiltInPredicateNameChecker builtInPredicateNameChecker = new CustomBuiltInPredicateNameChecker(java.util.Set.of());

  public LogicSchemaGrammarLexer(CharStream input, CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
    this(input);
    this.builtInPredicateNameChecker = builtInPredicateNameChecker;
  }
}

tokens {
  BUILTIN_PREDICATE
}

NOT: 'not';
BOOLEAN: 'TRUE' | 'FALSE';
OPERATOR: '='|'<>'|'<'|'>'|'<='|'>=';
ID: ([A-Za-z0-9_'?])+ {if(builtInPredicateNameChecker.isBuiltInPredicateName(getText())) setType(LogicSchemaGrammarParser.BUILTIN_PREDICATE);};
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

// 22
// 2.345
// 'Hola'
// 'Qualquier cosa. Como \n por ejemplo esto ¿?!"·$$!!!{} incluso con escape \' '