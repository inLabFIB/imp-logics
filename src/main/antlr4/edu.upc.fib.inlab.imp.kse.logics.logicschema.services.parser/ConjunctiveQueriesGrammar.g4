grammar ConjunctiveQueriesGrammar;

tokens {
  BUILTIN_PREDICATE
}

NOT:            'not';
BOOLEAN:        'TRUE' | 'FALSE';
OPERATOR:       '='|'<>'|'<'|'>'|'<='|'>=';
STRING:         SINGLE_QUOTE | DOUBLE_QUOTE;
NUMBER:         DECIMAL | FLOAT | REAL;
ALPHANUMERIC_WITH_PRIMA: ALPHANUMERIC [']*;
CONSTRAINTID:   '@' ALPHANUMERIC;
NEWLINE:        '\r'? '\n';
WS:             [ \t]+ -> skip ; // toss out whitespace
COMMENT:        '%' ~[\r\n]*;
COMMA:          ',';
OPENPAR:        '(';
CLOSEPAR:       ')';
ARROW:          ':-';
LEFT_ARROW:     '<-';

fragment DEC_DOT_DEC:   (DEC_DIGIT+ '.' DEC_DIGIT+ |  DEC_DIGIT+ '.' | '.' DEC_DIGIT+);
fragment DEC_DIGIT:     [0-9];
fragment DECIMAL:       DEC_DIGIT+;
fragment FLOAT:         DEC_DOT_DEC;
fragment REAL:          (DECIMAL  | DEC_DOT_DEC) (('E'|'e') [+-]? DEC_DIGIT+);
fragment ALPHANUMERIC:  [a-zA-Z0-9_$?:]+;
fragment SINGLE_QUOTE:  '\'' (~'\'' | '\\\'')* '\'';
fragment DOUBLE_QUOTE:  '"' (~'"' | '\\"')* '"';

prog: NEWLINE* line? (NEWLINE+ line)* NEWLINE*;
line: (COMMENT | conjunctiveQuery);
conjunctiveQuery: OPENPAR termsList CLOSEPAR LEFT_ARROW body;
body: literal (COMMA literal)*;
literal: ordinaryLiteral;
ordinaryLiteral: positiveAtom;
positiveAtom: atom;
atom: predicate OPENPAR termsList CLOSEPAR;
termsList: | term (COMMA term)*;
predicate: ALPHANUMERIC_WITH_PRIMA;
term: STRING | NUMBER | ALPHANUMERIC_WITH_PRIMA;