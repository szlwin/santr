package santr.v3.parser;

import santr.common.context.LexerUtil;
import santr.gtree.model.GrammarInfo;
import santr.parser.exception.ParserInvaildException;
import santr.v3.parser.data.RTree;


public class ExpressParser {

	private GrammarInfo grammarInfo;
	
	private RTree rTree;
	
	private char[] expressCharArr;
	
	//private TokenStream tokenStream;
	
	//private TreeWorker treeWorker;
	
	public void parser(String name,String express) throws ParserInvaildException{
		
		init(name,express);
		
		parser();
		
	}
	
	private void parser() throws ParserInvaildException{
		TokenStream tokenStream = lexer(grammarInfo,expressCharArr);
		
		TreeWorker treeWorker = new TreeWorker(expressCharArr, grammarInfo,tokenStream);

		int i = 0;
		while( i < tokenStream.getSize()){
			TokenString tokenStr = tokenStream.getNext();
			treeWorker.work(tokenStr);
			
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

		//TokenChar[] str = grammarInfo.getTokenCharArr();
		
		//grammarInfo.getTokenList().toArray(str);
		
		ExpressLexer lexer = new ExpressLexer();
		
		return lexer.lexer(grammarInfo.getTokenCharArr(), 0, expressCharArr.length, expressCharArr,
				grammarInfo.getTerminalDataArr());
		
	}
	
	public RTree getTree(){
		return rTree;
	}

}
