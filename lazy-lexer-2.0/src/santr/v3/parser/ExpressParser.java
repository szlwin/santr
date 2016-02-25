package santr.v3.parser;

import santr.common.context.LexerUtil;
import santr.gtree.model.GrammarInfo;
import santr.parser.exception.ParserInvaildException;
import santr.v3.parser.data.RTree;


public class ExpressParser {

	private GrammarInfo grammarInfo;
	
	private RTree rTree;
	
	private char[] expressCharArr;
	
	private ExpressLexer lexer;
	//private TokenStream tokenStream;
	
	//private TreeWorker treeWorker;
	public void parser(String name,String express) throws ParserInvaildException{
		
		init(name,express);
		
		parser();
		
	}
	
	private void parser() throws ParserInvaildException{
		TokenStream tokenStream = lexer(grammarInfo,expressCharArr);
		
		TreeWorker treeWorker = new TreeWorker(expressCharArr, grammarInfo,tokenStream);
		
		//TokenString tokenStr = lexer.lexer();

		//while( tokenStr != null ){
			
		//	treeWorker.work(tokenStr);
		//	tokenStr = lexer.lexer();
		//}
		
		int i = 0;
		int size = tokenStream.getSize();
		while( i < size ){
			
			treeWorker.work(tokenStream.getNext());
			i++;
		}
		rTree = treeWorker.getRoot();
	}

	private void init(String name,String express){
		this.grammarInfo = LexerUtil.getGrammar(name);
		
		expressCharArr = express.toCharArray();
		//rTree.setStart(0);
	}
	
	private TokenStream lexer(GrammarInfo grammarInfo,char[] expressCharArr){

		//lexer = new ExpressLexer(grammarInfo.getTokenCharArr(),expressCharArr,grammarInfo.getTerminalDataArr());
		//return lexer.getTokenStream();
		ExpressLexer lexer = new ExpressLexer();
		
		return lexer.lexer(grammarInfo.getTokenCharArr(), 0, expressCharArr.length, expressCharArr,
				grammarInfo.getTerminalDataArr());
		
	}
	
	public RTree getTree(){
		return rTree;
	}

}
