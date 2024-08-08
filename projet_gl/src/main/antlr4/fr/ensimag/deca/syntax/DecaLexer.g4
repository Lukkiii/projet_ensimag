lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Reserved keywords
ASM : 'asm';
INSTANCEOF : 'instanceof';
PRINTLN : 'println';
TRUE : 'true';
CLASS : 'class';
NEW : 'new';
PRINTLNX : 'printlnx';
WHILE : 'while';
EXTENDS : 'extends';
NULL : 'null';
PRINTX : 'printx';
ELSE : 'else';
READINT : 'readInt';
PROTECTED : 'protected';
FALSE : 'false';
READFLOAT : 'readFloat';
RETURN : 'return';
IF : 'if';
PRINT : 'print';
THIS : 'this';

fragment LETTER: 'a' .. 'z' | 'A' .. 'Z';
fragment DIGIT : '0' .. '9';

// Identifiers
IDENT: (LETTER | '$' | '_')(LETTER | DIGIT | '$' | '_')*;

// Special characters
LT: '<';
GT: '>';
EQUALS: '=';
PLUS: '+';
MINUS: '-';
TIMES: '*';
SLASH: '/';
PERCENT: '%';
DOT: '.';
COMMA: ',';
OPARENT: '(';
CPARENT: ')';
OBRACE: '{';
CBRACE: '}';
EXCLAM: '!';
SEMI: ';';
EQEQ: '==';
NEQ: '!=';
GEQ: '>=';
LEQ: '<=';
AND: '&&';
OR: '||';

fragment POSITIVE_DIGIT : '1'..'9';

// Integers
INT : '0' | POSITIVE_DIGIT DIGIT*;

fragment NUM: DIGIT+;
fragment SIGN: '+' | '-' | ;
fragment EXP: ('e' | 'E') SIGN NUM;
fragment DEC: NUM '.' NUM;
fragment FLOATDEC: (DEC | DEC EXP) ('F' | 'f' | );
fragment DIGITHEX: '0' .. '9' | 'A' .. 'F' | 'a' ..  'f';
fragment NUMHEX: DIGITHEX+;
fragment FLOATHEX: ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' | );

// Floating points
FLOAT: FLOATDEC | FLOATHEX;

fragment STRING_CAR : ~('"' | '\\' | '\n');
fragment EOL: '\n';

// Strings
STRING: '"' (STRING_CAR | '\\"' | '\\\\')* '"';
MULTI_LINE_STRING: '"' (STRING_CAR | EOL | '\\"' | '\\\\')* '"';

// Separators
SEPERATOR: (' ' | '\t' | '\r' | EOL){ skip(); };

// Comments
COMMENTS: '//' (~('\n'))* { skip(); };
MULTI_LINE_COMMENTS: '/*' .*? '*/' { skip(); } ;

// Include
fragment FILENAME: (LETTER | DIGIT | '_' | '-' | '.')+ ;
INCLUDE: '#include' (' ')* '"'FILENAME'"'  { doInclude(getText()); };