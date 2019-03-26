grammar M;

program : programSection*
        ;

programSection : functionDecl
               | classDecl
               | variableDecl
               ;

functionDecl : functionType? ID '(' parameterListDecl? ')' block
             ;

classDecl : CLASS ID '{' memberDecl* '}'
          ;

variableDecl : typeType ID ('=' expression)? ';'
             ;

memberDecl : functionDecl | variableDecl
           ;

parameterListDecl : parameterDecl (',' parameterDecl)*
                  ;

parameterDecl : typeType ID
              ;

functionType : typeType
             | VOID
             ;

typeType : typeType '[' ']' #arrayType
         | basicType        #nonArrayType
         ;

basicType : INT
          | BOOL
          | STRING
          | ID
          ;

statement : block                                                                              #blockStmt
          | expression? ';'                                                                    #expressionStmt
          | IF '(' expression ')' thenStmt=statement (ELSE elseStmt=statement)?                #ifElseStmt
          | WHILE '(' expression ')' statement                                                 #whileStmt
          | FOR '(' init=expression? ';' cond=expression? ';' update=expression? ')' statement #forStmt
          | CONTINUE ';'                                                                       #continueStmt
          | BREAK ';'                                                                          #breakStmt
          | RETURN expression? ';'                                                             #returnStmt
          ;

block : '{' blockStatement* '}'
      ;

blockStatement : statement    #stmt
               | variableDecl #varDeclStmt
               ;

expression : expression op=('++' | '--')                         #suffixExpr
           | expression '.' ID                                   #memExpr
           | arr=expression '[' sub=expression ']'               #arrayExpr
           | expression '(' parameterList? ')'                   #funcCallExpr
           | <assoc=right> op=('++'|'--') expression             #prefixExpr
           | <assoc=right> op=('+' | '-') expression             #prefixExpr
           | <assoc=right> op=('!' | '~') expression             #prefixExpr
           | <assoc=right> NEW creator                           #newExpr
           | lhs=expression op=('*' | '/' | '%') rhs=expression  #binaryExpr
           | lhs=expression op=('+' | '-') rhs=expression        #binaryExpr
           | lhs=expression op=('<<'|'>>') rhs=expression        #binaryExpr
           | lhs=expression op=('<' | '>') rhs=expression        #binaryExpr
           | lhs=expression op=('<='|'>=') rhs=expression        #binaryExpr
           | lhs=expression op=('=='|'!=') rhs=expression        #binaryExpr
           | lhs=expression op='&' rhs=expression                #binaryExpr
           | lhs=expression op='^' rhs=expression                #binaryExpr
           | lhs=expression op='|' rhs=expression                #binaryExpr
           | <assoc=right> lhs=expression op='&&' rhs=expression #binaryExpr
           | <assoc=right> lhs=expression op='||' rhs=expression #binaryExpr
           | <assoc=right> lhs=expression op='=' rhs=expression  #assignExpr
           | ID                                                  #idExpr
           | THIS                                                #thisExpr
           | NUMBER                                              #numExpr
           | STR                                                 #strExpr
           | NullLiteral                                         #nullExpr
           | BoolConstant                                        #boolExpr
           | '(' expression ')'                                  #bracketsExpr
           ;

nonArrayTypeCreator : INT
                    | BOOL
                    | STRING
                    | ID ('(' ')')?
                    ;

creator : basicType ('[' expression ']')+ ('[' ']')+ ('[' expression ']')+ #errorCreator
        | basicType ('[' expression ']')+ ('[' ']')*                       #arrayCreator
        | nonArrayTypeCreator                                              #nonArrayCreator
        ;

parameterList : expression (',' expression)*
              ;

BOOL : 'bool';
INT : 'int';
STRING : 'string';
fragment NULL : 'null';
VOID : 'void';
fragment TRUE : 'true';
fragment FALSE : 'false';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
BREAK : 'break';
CONTINUE : 'continue';
RETURN : 'return';
NEW : 'new';
CLASS : 'class';
THIS : 'this';

NUMBER : [1-9] [0-9]*
       | '0'
       ;

ESC : '\\"'
    | '\\\\'
    ;

STR : '"' (ESC | .)*? '"';

BoolConstant : TRUE
             | FALSE
             ;

NullLiteral : NULL;

ID : LETTER (LETTERLINE | DIGIT)*;
fragment LETTER : [a-zA-Z];
fragment LETTERLINE : [a-zA-Z_];
fragment DIGIT : [0-9];

COMMENT : '//' .*? '\r'? '\n' -> skip;
WS : [ \n\t\r]+ -> skip;