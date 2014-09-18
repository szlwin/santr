package santr.common.context;

import java.util.Map;

import santr.gtree.model.GrammarInfo;
import santr.v3.ls.parser.GrammerTreeParser;

import javolution.util.FastMap;


public class ContextInfo {
	
	private  Map<String,GrammarInfo> gammerInfoMap = new FastMap<String,GrammarInfo>();
	
	public  void load(String name,String filePath) throws Exception{
		GrammerTreeParser grammerTreeParser =  new GrammerTreeParser();
		GrammarInfo grammarInfo = grammerTreeParser.parse(filePath);
		gammerInfoMap.put(name, grammarInfo);
		//com.lazy.lexer.v3.xml.parser.XmlParser xmlParser = new com.lazy.lexer.v3.xml.parser.XmlParser();
		//GrammarInfo1 grammarInfo = xmlParser.parser(filePath);
		//gammerInfoMap1.put(name, grammarInfo);
	}
	
	protected  GrammarInfo getG(String name){
		return gammerInfoMap.get(name);
	}
}
