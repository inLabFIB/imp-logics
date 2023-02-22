grammar LogicSchemaGrammar;

NOT: 'not';
OPERATOR: '='|'<>'|'<'|'>'|'<='|'>=';
ID: ([A-Za-z0-9_'?])+;
CONSTRAINTID: '@'[0-9]+;
NEWLINE:'\r'? '\n';
WS : [ \t]+ -> skip ; // toss out whitespace
COMMENT : '%' ~[\r\n]*;
COMMA: ',';
OPENPAR: '(';
CLOSEPAR: ')';
ARROW: ':-';


prog: NEWLINE* line? (NEWLINE+ line)* NEWLINE*;
line: (COMMENT | constraint | derivationRule);
constraint: (CONSTRAINTID)? ARROW body;
derivationRule: atom ARROW body;
body: literal (COMMA literal)*;
literal: builtInLiteral | ordinaryLiteral;
builtInLiteral: term OPERATOR term;
ordinaryLiteral: atom | NOT OPENPAR atom CLOSEPAR;
atom: predicate OPENPAR termsList CLOSEPAR;
termsList: | term (COMMA term)*;
predicate: ID;
term: ID;


