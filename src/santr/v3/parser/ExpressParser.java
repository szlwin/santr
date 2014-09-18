package santr.v3.parser;

import santr.common.context.LexerUtil;
import santr.gtree.model.BData;
import santr.gtree.model.GrammarInfo;
import santr.gtree.model.TerminalData;
import santr.gtree.model.TokenChar;
import santr.parser.exception.ParserInvaildException;
import santr.v3.parser.data.RTree;


public class ExpressParser {

	private GrammarInfo grammarInfo;
	
	public RTree rTree = new RTree();
	
	private char[] expressCharArr;	
	
	private TokenStream tokenStream;
	
	private TreeWorker treeWorker;
	
	public void parser(String name,String express) throws ParserInvaildException{
		
		init(name,express);
		
		parserToken(grammarInfo,expressCharArr);
		
		parserExpress();
		
	}
	

	private void parserExpress() throws ParserInvaildException{
		treeWorker = new TreeWorker(expressCharArr, grammarInfo,rTree,tokenStream);

		int i = 0;
		
		while( i < tokenStream.getSize()){
			TokenString tokenStr = tokenStream.getNext();
			treeWorker.work(tokenStr);
			
			i++;
		}
		
	}

	private void init(String name,String express){
		this.grammarInfo = LexerUtil.getGrammar(name);
		
		expressCharArr = express.toCharArray();
		//rTree.setStart(0);
		rTree.setName(grammarInfo.getRoot());
		rTree.setType(BData.FLAG_TYPE_VAR);
		//rTree.setEnd(expressCharArr.length);
		rTree.setLbs(grammarInfo.getgTree());
	}
	
	private void parserToken(GrammarInfo grammarInfo,char[] expressCharArr){

		TokenChar[] str = grammarInfo.getTokenCharArr();
		
		//grammarInfo.getTokenList().toArray(str);
		
		ExpressLexer lexer = new ExpressLexer();
		
		tokenStream = lexer.lexer(str, 0, expressCharArr.length, expressCharArr,
				grammarInfo.getTerminalDataArr());
		
	}
	
	public RTree getTree(){
		return this.rTree;
	}

}
