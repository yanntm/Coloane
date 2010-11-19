lexer grammar ValuationLexerSNB;

options {
  language = Java;
}

@header {
  package main.antlr3.fr.lip6.move.coloane.extension;
}

LPAREN : '(' ;
RPAREN : ')' ;
LT : '<' ;
GT : '>' ;
LBRACE : '{' ;
RBRACE : '}' ;
PLUSPLUS : '++' ;
MINUSMINUS : '--' ;
PLUS : '+' ;
MINUS : '-' ;
TIMES : '*' ;
COMA : ',' ;
DOT : '.' ;
TILDE : '~' ;
ALL : 'ALL' ;
ORD : 'ORD' ;
UNION : 'UNION' ;
INTER : 'INTER' ;
DIFF : 'DIFF' ;

// whitespaces and identifiers
fragment LETTER : 'a'..'z' | 'A'..'Z' ;
IDENTIFIER : (LETTER)+ ;

fragment DIGIT : '0'..'9' ;
INTEGER : DIGIT+ ;

WS : (' ' | '\n' | '\r' | '\t')+ { $channel = HIDDEN; } ;