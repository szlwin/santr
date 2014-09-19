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
        | fun
    ;
    
    fun: ID '(' ( array )?  ')' ;
    
    array: param (',' param)*;
    
    param: ID  ('[' INT ']')?
           | INT
           | fun
           | expr;
    
    @ID : ^[A-Za-z]+$;
    @INT : ^[0-9]*$;
    
    
Then write a Test.java to build parser tree by the Expr.ls file.When you executing it, you can see the result.
    
    import santr.common.context.LexerUtil;
    import santr.v3.parser.ExpressParser;
    import santr.view.parser.TreeViewer;
    
    
    public class Test {
    
    	/**
    	 * @param args
    	 * void
    	 * @throws Exception 
    	 */
    	public static void main(String[] args) throws Exception {
    	    //Load the grammar file,only need load once.
    		LexerUtil.load("expr", "Expr.ls");
    		
    		ExpressParser lexerExecuter = new ExpressParser();
    		lexerExecuter.parser("expr", "6+max(2+3,min(one,two),three)");
    		
    
    	    TreeViewer viewer = new TreeViewer(lexerExecuter.getTree());
    	    viewer.open();
    
    	}
    
    }
    
