package santr.gtree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

public class GrammarInfo extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4039660737456200717L;
	

	private GTree gTree;
	
	private String root;
	
	private Map<String,GTree> gTreeMap = new HashMap<String,GTree>();
	
	///private TIntObjectHashMap<TokenChar> tokenByGtree;
	
	private String name;
	
	private GTree gTreeArr[];
	
	private ExpressGraph expressGraph;
	
	private List<GTree> gtreeList = new ArrayList<GTree>();
	
	private List<TokenChar> tokenList = new ArrayList<TokenChar>();
	
	private TokenChar[] tokenCharArr;
	
	private TokenChar[] wsTokenCharArr;
	
	private TerminalData[] terminalDataArr;
	
	private TokenTree tokenTree = new TokenTree();
	private TokenTree wsTokenTree;
	
	private Map<String,String> keyWordMap;
	
	private Map<String,String> keyWordNMap;
	private char[] wsTokenArray;
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public GTree getgTree() {
		return gTree;
	}

	public void setgTree(GTree gTree) {
		this.gTree = gTree;
	}

	public Map<String, GTree> getgTreeMap() {
		return gTreeMap;
	}

	public void setgTreeMap(Map<String, GTree> gTreeMap) {
		this.gTreeMap = gTreeMap;
	}

	//public Map<String, Map<String,GTree>> getTokenByGtree() {
	//	return tokenByGtree;
	//}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExpressGraph getExpressGraph() {
		return expressGraph;
	}

	public void setExpressGraph(ExpressGraph expressGraph) {
		this.expressGraph = expressGraph;
	}


	public List<LineInfo> get(int id,int token){
		return this.expressGraph.get(id, token);
	}
	
	public void addTree(GTree gtree){
		gtreeList.add(gtree);
	}
	
	public GTree getTree(int id){
		return gTreeArr[id];
	}

	public List<GTree> getTreeList() {
		return gtreeList;
	}
	
	public void addToken(TokenChar tokenChar){
		if(keyWordMap!=null 
				&& keyWordMap.containsKey(tokenChar.getTokenStr())){
			tokenChar.setKeyWord(true);
		}
		this.tokenList.add(tokenChar);
	}

	public List<TokenChar> getTokenList() {
		return tokenList;
	}

	public TokenChar[] getTokenCharArr() {
		return tokenCharArr;
	}

	public void setTokenCharArr(TokenChar[] tokenCharArr) {
		for(int i = 0; i < tokenCharArr.length;i++){
			tokenTree.addTree(tokenCharArr[i]);
		}
		this.tokenCharArr = tokenCharArr;
	}

	public GTree[] getgTreeArr() {
		return gTreeArr;
	}

	public void setgTreeArr(GTree[] gTreeArr) {
		this.gTreeArr = gTreeArr;
	}

	public TerminalData[] getTerminalDataArr() {
		return terminalDataArr;
	}

	public void setTerminalDataArr(TerminalData[] terminalDataArr) {
		this.terminalDataArr = terminalDataArr;
	}

	public TokenTree getTokenTree() {
		return tokenTree;
	}
	
	
	/*
	public TokenChar[] getWsTokenCharArr() {
		return wsTokenCharArr;
	}

	public void setWsTokenCharArr(TokenChar[] wsTokenCharArr) {
		if(wsTokenTree == null){
			wsTokenTree = new TokenTree();
		}
		for(int i = 0; i < wsTokenCharArr.length;i++){
			wsTokenTree.addTree(wsTokenCharArr[i]);
		}
		this.wsTokenCharArr = wsTokenCharArr;
	}

	public TokenTree getWsTokenTree() {
		return wsTokenTree;
	}*/

	public char[] getWsTokenArray() {
		return wsTokenArray;
	}

	public void setWsTokenArray(char[] wsTokenArray) {
		if(this.wsTokenArray == null){
			this.wsTokenArray = new char[128];
		}
		for(int i = 0;i < wsTokenArray.length;i++){
			this.wsTokenArray[wsTokenArray[i]] = wsTokenArray[i];
		}

	}

	public void addKeyWord(String key, String value) {
		if(keyWordMap == null){
			keyWordMap = new FastMap<String,String>();
		}
		if(keyWordNMap == null){
			keyWordNMap = new FastMap<String,String>();
		}
		keyWordMap.put(value, key);
		keyWordNMap.put(key, value);
	}

	public String getKeyWord(String key) {
		if(keyWordNMap == null){
			return null;
		}
		
		return keyWordNMap.get(key);
	}
	//public void setTokenByGtree(TIntObjectHashMap<TokenChar> tokenByGtree) {
	//	this.tokenByGtree = tokenByGtree;
	//}
	
	//public TokenChar getToken(int id){
	//	return tokenByGtree.get(id);
	//}
}
