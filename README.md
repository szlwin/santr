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
    		
    		ExpressParser parser = new ExpressParser();
    		parser.parser("expr", "6+max(2+3,min(one,two),three)");
    		
    
    	    TreeViewer viewer = new TreeViewer(parser.getTree());
    	    viewer.open();
    
    	}
    
    }
    
(2) Second,write a visitor to walk the parser tree:<br>

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    import santr.v3.execute.AbstractVisitor;
    import santr.v3.execute.Context;
    import santr.v3.execute.TreeInfo;

    public class TExprVisitor extends AbstractVisitor{

    	private Map<String,Object> paramMap = new HashMap<String,Object>();
    	
    	public void execute(Context context) {
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
    		}
    	}
    	
    	public void setParam(Map<String,Object> paramMap){
    		this.paramMap.putAll(paramMap);
    	}
    	
    	private void executeExpr(Context context){
    		if(context.getChildCount() == 1){
    			context.setValue(this.getChildValue(context,0));
    		}else{
    			//Get all the tree info.
    			List<TreeInfo> treeInfoList =  this.getAllChild(context);
    			if(treeInfoList.get(0).isToken()){
    				//( expr )
    				context.setValue(this.getChildValue(context,1));
    			}else{
    				String token = treeInfoList.get(1).getToken();
    				if(token.equals("+")){
    					//expr + expr
    					context.setValue((Integer)this.getChildValue(context,0)
    							+(Integer)this.getChildValue(context,2));
    				}else if(token.equals("-")){
    					//expr - expr
    					context.setValue((Integer)this.getChildValue(context,0)
    							-(Integer)this.getChildValue(context,2));
    				}else if(token.equals("/")){
    					//expr / expr
    					context.setValue((Integer)this.getChildValue(context,0)
    							/(Integer)this.getChildValue(context,2));
    				}else if(token.equals("*")){
    					//expr * expr
    					context.setValue((Integer)this.getChildValue(context,0)
    							*(Integer)this.getChildValue(context,2));
    				}
    			}
    		}
    	}
    	
    	private void executeFun(Context context){
    		//Get the all param value.
    		List<Integer> list = (List<Integer>) this.getChildValue(context,2);
    		
            int flag = 0;
    		String funName = (String)this.getChildValue(context,0);
    		
    		if(funName.equals("max")){
    			flag = 1;
    		}else if(funName.equals("min")){
    			flag = -1;
    		}
    		
    		int value = list.get(0);
    		for(int i = 1;i < list.size();i++){
    			int num = list.get(i);
    			if((num - value)*flag>0 ){
    				value = num;
    			}
    		}
    		context.setValue(value);
    	}
    	
    	private void executeArray(Context context){
    		List<Integer> list = new ArrayList<Integer>();
    		List<TreeInfo> treeInfoList =  this.getAllChild(context);
    		//param (',' param)*;
    		for(int i =0; i < treeInfoList.size();i++){
    			if(!treeInfoList.get(i).isToken()){
    				list.add((Integer)this.getChildValue(context,i));
    			}
    		}
    		context.setValue(list);
    	}
    	
    	private void executeParam(Context context){
    		if(context.getChildCount()==1){
    			//ID | INT| fun| expr 
    			context.setValue(this.getChildValue(context,0));
    		}else{
    			// ID  '[' INT ']'
    			List<Integer> paramList = (List<Integer>)this.getChildValue(context,0);
    			context.setValue(paramList.get((Integer) this.getChildValue(context,2)));
    		}
    	}
    	
    	private void executeINT(Context context){
    		//Save the value to this tree.
    		context.setValue(Integer.valueOf((String)context.getText()));
    	}
    	
    	private void executeID(Context context){
    		context.setValue(paramMap.get(context.getText()));
    	}
    }

(3)Finally,you can get the result by executing the program as:<br>

    import java.util.HashMap;
    import java.util.Map;
    import santr.common.context.LexerUtil;
    import santr.v3.parser.ExpressParser;
    
    public class TestVisitor {
    
    	/**
    	 * @param args
    	 * void
    	 * @throws Exception 
    	 */
    	public static void main(String[] args) throws Exception {
    		//Load the grammar file,only need load once.
    		LexerUtil.load("expr", "Expr.ls");
    		
    		ExpressParser parser = new ExpressParser();
    		parser.parser("expr", "6+max(2+3,min(one,two),three)");
    		
    		//Create the param.
    		Map<String,Object> param = new HashMap<String,Object>();
    		param.put("min", "min");
    		param.put("max", "max");
    		param.put("one", 2);
    		param.put("two", 3);
    		param.put("three", 10);
    		
    		TExprVisitor tExprVisitor = new TExprVisitor();
    		tExprVisitor.setParam(param);
    		tExprVisitor.vist(parser.getTree());
    	    System.out.println(tExprVisitor.getResult());
    	}
    
    }

It's will output 16.
    
