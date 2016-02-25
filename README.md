santr
=====
The santr is a simple parser for translating and executing structured text base on the garmmar tree,which is build by yourself. For a garmmar,santr can build and walk parse trees.

JDK Version: 1.6.

Warning
=======
As a result of the study,it's may be:<br>
(1) It's can't always check the grammar-error,if the express is uncorrect.<br>
(2) It's can't always parse the express corrected,if the grammar is very complex.

License
=======
Copyright (c) 2014.<br>
Author: szlwin.

Licensed under the BSD lincese.

Example
=======
You can learn how to use santr by the example.<br>

(1) First,you should create the grammar tree,and save it as Expr.ls file,the content is:<br>

	grammar Expr
	;
	
	prog : expr;
	
	@@KEYWORD :
		 K_AND     'and'
		 K_OR      'or'
		 K_TRUE    'true'
		 K_FALSE   'false'
		 K_NULL    'null'
	;
	
	expr : expr ('*'|'/') expr
	     | expr ('+'|'-') expr
	     | '!' expr
	     |  expr '++'
	     | expr ('='|'!='|'>='|'<='|'>'|'<'|) expr
	     | expr (K_AND|K_OR) expr
	     | fun
	     | ID ('.' ID)* 
	     | '-' INT
	     | INT
	     | '(' expr ')'
	     | STRING
	     | DATE
	     | BOOLEAN
	     | NULL
	;
	
	BOOLEAN : K_TRUE
			  | K_FALSE
	;
	
	NULL : K_NULL
	;
		  
	fun: ID '(' ( array )?  ')' ;
	
	array: param (',' param)*;
	
	param: ID  ('[' INT ']')?
		   | INT
	       | expr;
	  
	
	@ID : #STRING ;
	@INT : #NUMBER ;
	@STRING : '\'' #STRING '\'';
	@DATE : '\'' #STRING '\'';
	WS  : [ \t\r\n];
    
    
Then write a Test.java to build parser tree by the Expr.ls file.When you executing it, you can see the parser tree.
    
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
    		
    		ExpressParser parser = new ExpressParser();
    		parser.parser("expr", "'6'+max(2+3,min(one,two),three)");
    		
    
    	    TreeViewer viewer = new TreeViewer(parser.getTree());
    	    viewer.open();
    
    	}
    
    }
    
(2) Second,write a visitor to walk the parser tree:<br>
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
	
    import santr.parser.exception.ExecuteInvaildException;
    import santr.v4.execute.AbstractVisitor;
    import santr.v4.parser.ParserTree;
    import santr.v4.parser.RuleContext;
	
	public class ExprCVisitor extends AbstractVisitor<Map<String,Object>>{
    	private static final Map<String, Complute> compluteMap  
    	    = new HashMap<String, Complute>();
    	private static final Map<String, NumberCompare> numberCompareMap 
    	    = new HashMap<String, NumberCompare>();
    	private static final Map<String, StringCompare> stringCompareMap 
    	   = new HashMap<String, StringCompare>();
    	   
    	static{
    	    Complute[] allComplute = Complute.values ();
	
	        for (Complute complute : allComplute) {
	
	    	   compluteMap.put(complute.getOperator(), complute);
	        }
	        
	        NumberCompare[] allNumberCompare = NumberCompare.values ();
	
	        for (NumberCompare numberCompare : allNumberCompare) {
	
	        	numberCompareMap.put(numberCompare.getOperator(), numberCompare);
	        }
	        
	        StringCompare[] allStringCompare = StringCompare.values ();
	
	        for (StringCompare stringCompare : allStringCompare) {
	
	        	stringCompareMap.put(stringCompare.getOperator(), stringCompare);
	        }
	    }
	    
	    public enum Complute{
	    	
	        ADD("+"){double eval(double x,double y){return x+y;}},   
	        SUB("-"){double eval(double x,double y){return x-y;}},   
	        MUL("*"){double eval(double x,double y){return x*y;}},   
	        DIV("/"){double eval(double x,double y){return x/y;}};
	        abstract double eval(double x,double y);
	        
	        private String token;
	        
	        Complute(String token){
	        	this.token = token;
	        }
	        
	        public String getOperator(){
	        	return token;
	        }
	    }
	    public enum NumberCompare{   
	        EQUAL("="){boolean eval(Double x,Double y){return x==y;}},   
	        NOTEQUAL("!="){boolean eval(Double x,Double y){return x!=y;}},   
	        LETTER("<"){boolean eval(Double x,Double y){return x<y;}},   
	        LETTERE("<="){boolean eval(Double x,Double y){return x<=y;}},
	        GREATER(">"){boolean eval(Double x,Double y){return x>y;}},
	        GREATERE(">="){boolean eval(Double x,Double y){return x>=y;}}
	        ;
	        abstract boolean eval(Double x,Double y);
	        
	        private String token;
	        
	        NumberCompare(String token){
	        	this.token = token;
	        }
	        
	        public String getOperator(){
	        	return token;
	        }
	    }
	    
	    public enum StringCompare{   
	        EQUAL("="){boolean eval(String x,String y){return x.equals(y);}},   
	        NOTEQUAL("!="){boolean eval(String x,String y){return !x.equals(y);}}
	        ;
	        abstract boolean eval(String x,String y);
	        
	        private String token;
	        
	        StringCompare(String token){
	        	this.token = token;
	        }
	        
	        public String getOperator(){
	        	return token;
	        }
	    }
	    
	    //private Map<String,Object> paramMap = new HashMap<String,Object>();
	    public void execute(RuleContext context) throws ExecuteInvaildException {
	        //Get the tree name.
	        String name = context.getName();
	        if(name.equals("expr")){
	            executeExpr(context);
	        }else if(name.equals("fun")){
	            executeFun(context);
	        }else if(name.equals("array")){
	            executeArray(context);
	        }else if(name.equals("param")){
	            executeParam(context);
	        }else if(name.equals("INT")){
	            executeINT(context);
	        }else if(name.equals("ID")){
	            executeID(context);
	        }else if(name.equals("STRING")){
	            executeString(context);
	        }
	
	    }
	
	    //public void setParam(Map<String,Object> paramMap){
	    //    this.paramMap.putAll(paramMap);
	    //}
	
	
	    private void executeExpr(RuleContext context) throws ExecuteInvaildException{
	        if(context.getChildCount() == 1){
	            context.setValue(this.getChildValue(context,0));
	        }else{
	            //Get all the tree info.
	        	ParserTree[] treeInfoList =  this.getAllChild(context);
	        	
	            if(treeInfoList[0].isToken()){
	                //( expr )
	                context.setValue(this.getChildValue(treeInfoList[1]));
	            }else{
	            	String token = treeInfoList[1].getToken();
	            	if(compluteMap.containsKey(token)){
	            		//expr ('+'|'-') expr
	            		// expr ('*'|'/') expr
	                	context.setValue(
	                			compluteMap.get(token).eval(
	                					(Double)this.getChildValue(treeInfoList[0]),
	                					(Double)this.getChildValue(treeInfoList[2])));
	            	}else if(token.equals("and")){
	            		//expr and expr
	            		context.setValue((Boolean)this.getChildValue(treeInfoList[0]) && 
	                					(Boolean)this.getChildValue(treeInfoList[2]));
	            	}else if(token.equals("or")){
	            		//expr or expr
	            		context.setValue((Boolean)this.getChildValue(treeInfoList[0]) || 
	        					(Boolean)this.getChildValue(treeInfoList[2]));
	            	}else{
	            		//expr ('='|'!='|'>='|'<='|'>'|'<') expr
	            		Object left = this.getChildValue(treeInfoList[0]);
	            		Object right = this.getChildValue(treeInfoList[2]);
	            		if(left instanceof String){
	            			context.setValue(
	                    			stringCompareMap.get(token).eval((String)left,(String)right));
	            		}else{
	            			context.setValue(
	                    			numberCompareMap.get(token).eval((Double)left,(Double)right));
	            		}
	            	}
	            }
	        }
	    }
	
	    private void executeFun(RuleContext context) throws ExecuteInvaildException{
	        //Get the all param value.
	        List<Double> list = (List<Double>) this.getChildValue(context,2);
	
	        int flag = 0;
	        String funName = (String)this.getChildValue(context,0);
	
	        if(funName.equals("max")){
	            flag = 1;
	        }else if(funName.equals("min")){
	            flag = -1;
	        }
	
	        double value = list.get(0);
	        for(int i = 1;i < list.size();i++){
	            double num = list.get(i);
	            if((num - value)*flag>0 ){
	                value = num;
	            }
	        }
	        context.setValue(value);
	    }
	
	    private void executeArray(RuleContext context) throws ExecuteInvaildException{
	        List<Object> list = new ArrayList<Object>();
	        ParserTree[] treeInfoList =  this.getAllChild(context);
	        //param (',' param)*;
	        for(int i =0; i < treeInfoList.length;i++){
	            if(!treeInfoList[i].isToken()){
	              list.add(this.getChildValue(treeInfoList[i]));
	            }
	        }
	        context.setValue(list);
	    }
	
	    private void executeParam(RuleContext context) throws ExecuteInvaildException{
	        if(context.getChildCount()==1){
	            //ID | INT| fun| expr 
	            context.setValue(this.getChildValue(context,0));
	        }else{
	            // ID  '[' INT ']'
	            List<Double> paramList = (List<Double>)this.getChildValue(context,0);
	            context.setValue(paramList.get((Integer) this.getChildValue(context,2)));
	        }
	    }
	
	    private void executeINT(RuleContext context){
	        //Save the value to this tree.
	        context.setValue(Double.valueOf(context.getText()));
	    }
	
	    private void executeID(RuleContext context){
	    	//Save the value to this tree.
	        context.setValue(((Map<String,Object>)this.getParamer())
	        		.get(context.getText()));
	    }
	
	    private void executeString(RuleContext context){
	        //Save the value to this tree.
	        context.setValue(Double.valueOf(context.getText()));
	    }
	}

(3)Finally,you can get the result by executing the program as:<br>

	import java.util.HashMap;
	import java.util.Map;
	import santr.common.context.LexerUtil;
	import santr.v4.parser.ExpressParser;
	
	public class TestVisitor {
	       /**
	         * @param args
	         * void
	         * @throws Exception 
	         */
	         public static void main(String[] args) throws Exception {
	              //Load the grammar file,only need load once.
	              LexerUtil.load("expr", "demo/v2/ExprCT.ls");
	              
	              ExpressParser parser = LexerUtil.getParser();
	              parser.parser("expr", "one <= two and ( '6'+max(2+3,min(one,two),three) > 4 or two < three )");
	              
	              //Create the param.
	              Map<String,Object> param = new HashMap<String,Object>();
	              param.put("min", "min");
	              param.put("max", "max");
	              param.put("one", 2D);
	              param.put("two", 3D);
	              param.put("three", 10D);
	              	
	              ExprCVisitor tExprVisitor = new ExprCVisitor();
	              tExprVisitor.setParamer(param);
	              tExprVisitor.vist(parser.getTree());
	              System.out.println(parser.getTree().getRuleContext().getValue());
	        }
	
	}

It's will output true.
    
