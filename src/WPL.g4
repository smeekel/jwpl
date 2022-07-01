grammar WPL;

module
  : classStatement*
  ;

classDefinition
  : definitionModifiers (CLASS | FN) IDENT classParameters? classInheritance? classBody
  ;

classParameters
  : '(' parameterList? ')'
  ;

parameterList
  : parameterList ',' parameterDefinition
  | parameterDefinition
  ;

parameterDefinition
  : CONST? IDENT
  ;

classInheritance
  : EXTENDS IDENT
  ;

classBody
  : '{' classStatement* '}'
  ;

oneOrMoreStatements
  : '{' classStatement* '}'
  | classStatement
  ;

classStatement
  : classDefinition
  | variableDefinition eos
  | ifStatement
  | forStatement
  | tryStatement
  | returnStatement eos
  | breakStatement eos
  | continueStatement eos
  | throwStatement eos
  | nakedExpression eos
  ;

nakedExpression
  : expression
  ;

forStatement
  : FOR forSetup ';' forCondition ';' forStep oneOrMoreStatements
  ;

forSetup
  : VAR IDENT ( '=' expression )?   # ForSetupLocalVar
  | IDENT '=' expression            # ForSetupAssignment
  |                                 # ForSetupEmpty
  ;

forCondition
  : expression
  |
  ;

forStep
  : expression
  |
  ;

tryStatement
  : TRY classBody catchElement finallyElement?
  ;

catchElement
  : CATCH IDENT? classBody
  ;

finallyElement
  : FINALLY classBody
  ;

returnStatement
  : RETURN expression
  ;

throwStatement
  : THROW expression
  ;

breakStatement
  : BREAK
  ;

continueStatement
  : CONTINUE
  ;

ifStatement
  : IF expression oneOrMoreStatements (ELSE oneOrMoreStatements)?
  ;

variableDefinition
  : definitionModifiers (VAR | CONST) IDENT ( '=' expression )?
  ;

definitionModifiers
  : visibility?
  ;

visibility
  : PUBLIC
  | PRIVATE
  | PROTECTED
  ;

callArguments
  : '(' argumentList? ')'
  ;

argumentList
  : expr+=expression ( ',' expr+=expression )*
  ;

lambdaExpression locals [String internalName = null]
  : lambdaParameters '=>' lambdaBody
  ;

lambdaParameters
  : classParameters
  | IDENT
  ;

lambdaBody
  : classBody
  | classStatement
  ;

expression
  : lambdaExpression                                  # lambdaExpress
  | expression '[' expression ']'                     # IndexExpr
  | expression '.' IDENT                              # MemberAccessExpr
  | expression '?.' IDENT                             # SafeMemberAccessExpr
  | expression callArguments                          # CallExpr
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
  | expression '??' expression                        # CoalescingExpr
  | <assoc=right> expression '=' expression           # AssignmentExpr
  | THIS                                              # ThisExpr
  | SUPER                                             # SuperExpr
  | IDENT                                             # IdentExpr
  | literal                                           # LiteralExpr
  | '(' expression ')'                                # SubExpr
  ;

eos
  : ';'
  | EOF
  ;

literal
  : numericLiteral
  | stringLiteral
  | booleanLiteral
  | noneLiteral
  ;

noneLiteral
  : NONE
  ;

booleanLiteral
  : TRUE
  | FALSE
  ;

stringLiteral
  : QuotedString
  ;

numericLiteral
  : DecimalLiteral      # DecimalLiteral
  | HexLiteral          # HexLiteral
  | BinaryLiteral       # BinaryLiteral
  ;

QuotedString
  : '"' ~('"')* '"'
  | '\'' ~('\'')* '\''
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
