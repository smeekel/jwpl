grammar WPL;


module
  : statement* EOF
  ;

statement
  : importStatement
  | blockStatement
  | expression eoe
  | emptyStatement
  | ifStatement
  | tryStatement
  | functionDefinition
  | variableDefinition eoe
  | classDefinition
  | switchStatement
  | forStatement
  | breakStatement eoe
  | continueStatement eoe
  | returnStatement eoe
  | throwStatement eoe
  ;

throwStatement
  : THROW expression
  ;

returnStatement
  : RETURN expression
  ;

forStatement
  : FOR forSetup ';' forCondition ';' forStep statement
  ;

forSetup
  : variableDefinition
  | expression
  |
  ;

forCondition
  : expression
  |
  ;

forStep
  : expression
  |
  ;

switchStatement
  : SWITCH expression '{' caseStatements '}'
  ;

caseStatements
  : caseStatement* defaultStatement
  ;

caseStatement
  : CASE expression ':' statement
  ;

defaultStatement
  : DEFAULT ':' statement
  ;

variableDefinition
  : VAR   identifier ( '=' expression )?  # MutableVariableDefinition
  | CONST identifier '=' expression       # ConstVariableDefinition
  ;

classDefinition
  : CLASS identifier (EXTENDS identifier)? '{' classElements '}'
  ;

classElements
  : classElement*
  ;

classElement
  : memberAttribute* functionDefinition       # ClassFunctionDefinition
  | memberAttribute* variableDefinition eoe   # ClassVariableDefinition
  ;

memberAttribute
  : PUBLIC
  | PRIVATE
  | PROTECTED
  | STATIC
  ;

functionDefinition
  : FN identifier '(' functionParameters? ')' blockStatement
  ;

functionParameters
  : functionParameters ',' identifier
  | identifier
  ;

tryStatement
  : TRY blockStatement catchElement finallyElement?
  ;

catchElement
  : CATCH identifier? blockStatement
  ;

finallyElement
  : FINALLY blockStatement
  ;

breakStatement
  : BREAK eoe
  ;

continueStatement
  : CONTINUE eoe
  ;

ifStatement
  : IF expression statement (ELSE statement)?
  ;

emptyStatement
  : eoe
  ;

eoe
  : SemiColon
  ;

blockStatement
  : '{' statement* '}'
  ;

importStatement
  : IMPORT identList FROM ConstString   # ImportPartsStatement
  | IMPORT ConstString AS identifier    # ImportWholeStatement
  ;

identList
  : identifier (',' identifier)*
  ;

arguments
  : '(' (expression (',' expression)*)? ')'
  ;

expression
  : expression '[' expression ']'                     # MemberIndexExpr
  | expression '.'  identifier                        # MemberAccessorExpr
  | expression arguments                              # ArgumentExpr
  | expression '?.' identifier                        # SafeMemberAccessorExpr
  | expression '++'                                   # PostIncrementExpr
  | expression '--'                                   # PostDecrementExpr
  | '++' expression                                   # PreIncrementExpr
  | '--' expression                                   # PreDecrementExpr
  | '+'  expression                                   # UnaryPlusExpr
  | '-'  expression                                   # UnaryMinusExpr
  | '~'  expression                                   # BinaryNotExpr
  | '!'  expression                                   # NotExpr
  | expression ( '*' | '/' | '%' ) expression         # MultiplicativeExpr
  | expression ( '+' | '-' ) expression               # AdditiveExpr
  | expression ( '<<' | '>>' ) expression             # BitShiftExpr
  | expression ( '<' | '>' | '<=' | '>=' ) expression # RelationalExpr
  | expression ( '==' | '!=' ) expression             # EqualityExpr
  | expression '&' expression                         # BitAndExpr
  | expression '^' expression                         # BitXorExpr
  | expression '|' expression                         # BitOrExpr
  | expression '&&' expression                        # AndExpr
  | expression '||' expression                        # OrExpr
  | <assoc=right> expression '=' expression           # AssignmentExpr
  | identifier                                        # IdentifierExpr
  | literal                                           # LiteralExpr
  | THIS                                              # ThisExpr
  | SUPER                                             # SuperExpr
  | '(' expression ')'                                # SubExpr
  ;

identifier
  : IDENT
  ;

literal
  : numericLiteral
  | stringLiteral
  | booleanLiteral
  | nullLiteral
  ;

nullLiteral
  : NONE
  ;

booleanLiteral
  : TRUE
  | FALSE
  ;

stringLiteral
  : '"' ~('"')+ '"'
  | '\'' ~('\'')+ '\''
  ;

numericLiteral
  : DecimalLiteral
  | HexLiteral
  | BinaryLiteral
  ;

SingleLineComment
  : '//' ~[\r\n\u2028\u2029]* -> channel(HIDDEN)
  ;

MultiLineComment
  : '/*' .*? '*/' -> channel(HIDDEN)
  ;

DecimalLiteral
  : [0-9] [_0-9]*
  ;

HexLiteral
  : '0' [xX] [_a-fA-F0-9]+
  ;

BinaryLiteral
  : '0' [bB] [_0-1]+
  ;

ConstString
  : '"' ~('"')+ '"'
  ;


OpenBracket   : '[';
CloseBracket  : ']';
SemiColon     : ';';

BREAK         : 'break';
CASE          : 'case';
CATCH         : 'catch';
CLASS         : 'class';
CONTINUE      : 'continue';
CONST         : 'const';
DEFAULT       : 'default';
DELETE        : 'delete';
ELSE          : 'else';
EXTENDS       : 'extends';
FALSE         : 'false';
FINALLY       : 'finally';
FOR           : 'for';
FN            : 'fn';
FROM          : 'from';
IF            : 'if';
IMPORT        : 'import';
EXPORT        : 'export';
RETURN        : 'return';
SWITCH        : 'switch';
THIS          : 'this';
THROW         : 'throw';
TRY           : 'try';
TRUE          : 'true';
VAR           : 'var';
PUBLIC        : 'public';
PRIVATE       : 'private';
PROTECTED     : 'protected';
NONE          : 'none';
SUPER         : 'super';
AS            : 'as';
STATIC        : 'static';

IDENT
  : LETTERS ( LETTERS | NUMBERS )*
  ;

LETTERS           : [_a-zA-Z]+;
NUMBERS           : [0-9]+;
WhiteSpaces       : [\t\u000B\u000C\u0020\u00A0]+ -> channel(HIDDEN);
LineTerminator    : [\r\n\u2028\u2029] -> channel(HIDDEN);
