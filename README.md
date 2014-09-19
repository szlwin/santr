santr
=====
The santr is a simple parser for translating and executing structured text base on the garmmar tree,which is build by yourself. For a garmmar,santr can build and walk parse trees.

Warning
=======
As a result of the study,it's may be:<br>
(1) It's can't always check the grammar-error,if the express is uncorrect.<br>
(2) It's can't always parse the express corrected,if the grammar is very complex.

License
=======

Licensed under the BSD lincese.

Example
=======
You can learn how to use santr by the example.<br>

(1) First,you should create the grammar tree,and save it as Expr.ls file,the content is:<br>

    grammar Expr;
    
    prog : expr;
    
    expr : expr ('*'|'/') expr
     | expr ('+'|'-') expr
     | ID
     | INT
     | '(' expr ')'
     | fun;
     
     fun: ID '(' ( array )?  ')' ;
     
     array: param (',' param)*;
     
     param: ID  ('[' INT ']')?
       | INT
       | fun
       | expr;
       
       @ID : ^[A-Za-z]+$;
       
       @INT : ^[0-9]*$;
    


