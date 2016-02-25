package santr.common.context;
import santr.gtree.model.GrammarInfo;
import santr.v4.parser.ExpressParser;

public class LexerUtil {

	private static ContextInfo contextInfo = new ContextInfo();
	
	public static void load(String name,String filePath) throws Exception{
		contextInfo.load(name, filePath);
	}
	
	public static GrammarInfo getGrammar(String name){
		return contextInfo.getG(name);
	}
	
	public static ExpressParser getParser(){
		return new ExpressParser();
	}
}
