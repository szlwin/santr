package santr.v4.parser;

import santr.common.context.LexerUtil;
import santr.gtree.model.GrammarInfo;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.execute.Visitor;
import santr.v4.parser.ParserTree;


public class ExpressParser {

	private GrammarInfo grammarInfo;
	
	private ParserTree rTree;
	
	private char[] expressCharArr;
	
	private Visitor visitor;
	//private ExpressLexer lexer;
	//private TokenStream tokenStream;
	
	//private TreeWorker treeWorker;

	public void parser(String name,String express) throws ExecuteInvaildException{
		
		init(name,express);
		
		parser();
		
	}
	public void addVisitor(Visitor visitor){
		this.visitor = visitor;
	}
	private void parser() throws ExecuteInvaildException{
		TokenStreamInfo tokenStreamInfo = lexer(grammarInfo,expressCharArr);
		tokenStreamInfo.isFinish = true;
		ToolUtil toolUtil = new ToolUtil(grammarInfo,tokenStreamInfo);
		
		TreeWorker treeWorker = new TreeWorker(toolUtil);
		if(treeWorker.getRoot().getRuleContext().isExitLevel()){
			LevelTreeWork levelTreeWork = new LevelTreeWork(toolUtil,treeWorker.getRoot(),treeWorker.getRoot());
			levelTreeWork.work();
			rTree = treeWorker.getRoot();
		}else{
			
			treeWorker.work();
				
			//treeWorker.freshTree();
			//System.out.println(treeWorker.getCurrentTree().getName()+":"+treeWorker.getCurrentTree().getrTreeList().size());
			rTree = treeWorker.getRoot();
		}
		if(visitor!=null){
			visitor.vist(rTree);
		}
		
		//System.out.println(treeWorker.getCurrentTree().getName());
	}

	private void init(String name,String express){
		this.grammarInfo = LexerUtil.getGrammar(name);
		
		expressCharArr = express.toCharArray();
		//rTree.setStart(0);
	}
	
	private TokenStreamInfo lexer(GrammarInfo grammarInfo,char[] expressCharArr){

		//lexer = new ExpressLexer(grammarInfo.getTokenCharArr(),expressCharArr,grammarInfo.getTerminalDataArr());
		//return lexer.getTokenStream();
		ExpressLexer lexer = new ExpressLexer();
		
		return lexer.lexer(grammarInfo.getTokenTree(), 0, expressCharArr.length, expressCharArr,
				grammarInfo.getTerminalDataArr(),grammarInfo.getWsTokenArray());
		//return lexer.getTokenStream();
		//return lexer.lexer(grammarInfo.getTokenCharArr(), 0, expressCharArr.length, expressCharArr,
		//		grammarInfo.getTerminalDataArr());
	}
	
	public ParserTree getTree(){
		return rTree;
	}

}
