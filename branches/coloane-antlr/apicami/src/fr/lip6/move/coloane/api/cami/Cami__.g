lexer grammar Cami;
@header {
package fr.lip6.move.coloane.api.cami;
            
        }

T8 : 'SC(' ;
T9 : ')' ;
T10 : 'OC(' ;
T11 : ',' ;
T12 : 'FC()' ;
T13 : 'OS(' ;
T14 : 'TD()' ;
T15 : 'FA()' ;
T16 : 'SS()' ;
T17 : 'RS(' ;
T18 : 'FS(' ;
T19 : 'TL()' ;
T20 : 'VI(' ;
T21 : 'FL()' ;
T22 : 'DQ()' ;
T23 : 'FQ()' ;
T24 : 'VQ(' ;
T25 : 'CQ(' ;
T26 : 'AQ(' ;
T27 : 'TQ(' ;
T28 : '7' ;
T29 : '8' ;
T30 : 'QQ(' ;
T31 : 'TR(' ;
T32 : 'WN(' ;
T33 : 'MO(' ;
T34 : 'KO(1,' ;
T35 : 'DF(-2,' ;
T36 : 'DR()' ;
T37 : '<EOF>' ;
T38 : 'RQ(' ;
T39 : 'FR(' ;
T40 : 'ZA(' ;
T41 : 'FE()' ;
T42 : 'DE(' ;
T43 : 'DE()' ;
T44 : 'RT(' ;
T45 : 'RO(' ;
T46 : 'ME(' ;
T47 : 'MT(' ;
T48 : 'CN(' ;
T49 : 'CB(' ;
T50 : 'CA(' ;
T51 : 'CT(' ;
T52 : 'CM(' ;
T53 : 'SU(' ;
T54 : 'SI(' ;
T55 : 'TD(' ;
T56 : 'OB(' ;
T57 : 'AT(' ;
T58 : 'DB()' ;
T59 : 'FB()' ;
T60 : 'PO(' ;
T61 : 'pO(' ;
T62 : 'DC(' ;
T63 : 'AD(' ;
T64 : 'DS(' ;
T65 : 'CE(' ;
T66 : 'FF(' ;

// $ANTLR src "Cami.g" 668
CAMI_STRING
	@init{int nbToRead = 0;}
    	:
	NUMBER {nbToRead = Integer.parseInt($NUMBER.text);}
	':' 
	fs=FIXED_LENGTH_STRING[nbToRead]{setText($fs.text);}
	;

// $ANTLR src "Cami.g" 676
fragment
FIXED_LENGTH_STRING
	[int len]
	:   
	( { len > 0 }?=> .{len--;})* // Gated predicate : deactivate the '.' when len chars have been read
	;

// $ANTLR src "Cami.g" 683
NUMBER	: 	
	'0'..'9'+
	;


// $ANTLR src "Cami.g" 688
NEWLINE
 : 	
	( '\r'?'\n' )+ {skip();}
	;
