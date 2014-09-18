package santr.gtree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private TerminalData[] terminalDataArr;
	
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
		this.tokenList.add(tokenChar);
	}

	public List<TokenChar> getTokenList() {
		return tokenList;
	}

	public TokenChar[] getTokenCharArr() {
		return tokenCharArr;
	}

	public void setTokenCharArr(TokenChar[] tokenCharArr) {
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

	//public void setTokenByGtree(TIntObjectHashMap<TokenChar> tokenByGtree) {
	//	this.tokenByGtree = tokenByGtree;
	//}
	
	//public TokenChar getToken(int id){
	//	return tokenByGtree.get(id);
	//}
}
