grammar DependencySchemaGrammar;

@lexer::members {
private edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker builtInPredicateNameChecker = new edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker(java.util.Set.of());

public DependencySchemaGrammarLexer(CharStream input, edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker builtInPredicateNameChecker) {
    this(input);
    this.builtInPredicateNameChecker = builtInPredicateNameChecker;
}
}

tokens {
  BUILTIN_PREDICATE
}

NOT:            'not';
BOOLEAN:        'TRUE' | 'FALSE';

EQ:             '=';
NEQ:            '<>';
LT:             '<';
LTE:            '<=';
GT:             '>';
GTE:            '>=';

STRING:         SINGLE_QUOTE | DOUBLE_QUOTE;
NUMBER:         DECIMAL | FLOAT | REAL;
ALPHANUMERIC_WITH_PRIMA: ALPHANUMERIC [']* {if(builtInPredicateNameChecker.isBuiltInPredicateName(getText())) setType(DependencySchemaGrammarParser.BUILTIN_PREDICATE);};
NEWLINE:        '\r'? '\n';
WS:             [ \t]+ -> skip ; // toss out whitespace
COMMENT:        '%' ~[\r\n]*;
COMMA:          ',';
OPENPAR:        '(';
CLOSEPAR:       ')';
ARROW:          ':-';
DEPENDENCY:     '->';

fragment DEC_DOT_DEC:   (DEC_DIGIT+ '.' DEC_DIGIT+ |  DEC_DIGIT+ '.' | '.' DEC_DIGIT+);
fragment DEC_DIGIT:     [0-9];
fragment DECIMAL:       DEC_DIGIT+;
fragment FLOAT:         DEC_DOT_DEC;
fragment REAL:          (DECIMAL  | DEC_DOT_DEC) (('E'|'e') [+-]? DEC_DIGIT+);
fragment ALPHANUMERIC:  [a-zA-Z0-9_$?:]+;
fragment SINGLE_QUOTE:  '\'' (~'\'' | '\\\'')* '\'';
fragment DOUBLE_QUOTE:  '"' (~'"' | '\\"')* '"';

prog: NEWLINE* line? (NEWLINE+ line)*  NEWLINE* EOF;
line: (COMMENT | dependency | derivationRule);
dependency: tgd | egd;
tgd: body DEPENDENCY tgd_head;
egd: body DEPENDENCY egd_head;
tgd_head: positiveAtom (COMMA positiveAtom)*;
egd_head: term EQ term;
derivationRule: atom ARROW body;
body: literal (COMMA literal)*;
literal: builtInLiteral | ordinaryLiteral;
builtInLiteral: comparisonBuiltInLiteral | booleanBuiltInLiteral | customBuiltInLiteral;
comparisonBuiltInLiteral: term operator term;
operator: EQ | NEQ | LT | LTE | GT | GTE;
booleanBuiltInLiteral: BOOLEAN OPENPAR CLOSEPAR;
customBuiltInLiteral: BUILTIN_PREDICATE OPENPAR termsList CLOSEPAR;
ordinaryLiteral: positiveAtom | negatedAtom;
positiveAtom: atom;
negatedAtom: NOT OPENPAR atom CLOSEPAR;
atom: predicate OPENPAR termsList CLOSEPAR;
termsList: | term (COMMA term)*;
predicate: ALPHANUMERIC_WITH_PRIMA;
term: STRING | NUMBER | ALPHANUMERIC_WITH_PRIMA;