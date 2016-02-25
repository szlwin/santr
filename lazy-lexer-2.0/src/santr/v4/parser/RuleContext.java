package santr.v4.parser;

import java.util.List;
import santr.gtree.model.BData;
import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.gtree.model.TFlag;
import santr.gtree.model.Token;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.gtree.model.enume.TOKENTYPE;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.parser.PathScaner;
import santr.v4.parser.TokenString;

public class RuleContext {
	
	private GTree lbs;
	
	private GTree leaf;
	
	private ParserTree parserTree;

	private ParserTree levelTree;
	
	private boolean hasExecute = false;
	private Object param;
	private Object value;
	//private boolean needReExecute = false;
	public String getName() {
		return lbs.getName();
	}

	protected GTree getLbs() {
		return lbs;
	}
	
	protected void setLbs(GTree lbs) {
		this.lbs = lbs;
		this.leaf = lbs.getRel();
	}

	protected GTree getLeaf() {
		return leaf;
	}

	protected void setLeaf(GTree gTree) {
		this.leaf = gTree;
	}
	
	protected GTree getFirstChild(){
		if(this.leaf.isTerminalNode()
				|| this.leaf.getType() == GTYPE.TOKEN
				){
			return null;
		}

		GTree child = this.leaf.getgTreeArray()[0];
		
		if(child.getType() == GTYPE.LEAF
				|| child.getType() == GTYPE.TOKEN){
			return null;
		}
		return child;
	}

	public ParserTree getParserTree() {
		return parserTree;
	}

	protected void setParserTree(ParserTree parserTree) {
		this.parserTree = parserTree;
	}
	
	
	protected boolean isLast() {
		return lbs.isLast();
	}
	
	protected boolean checkExpress(GTree checkLBS) {
		
		if(checkLBS.getIndex() == 0 ){
			return this.getParserTree().isEmpty() 
					&& checkLBS.getGroup() == 0;
		}
		
		GTree preLBS = parserTree.getLast().getRuleContext().getLbs();
		int checkFlag = checkLBS.getFlag() - preLBS.getFlag();

		if(checkFlag == 1){
			//GTree exPreLBS = checkLBS.getParent().getgTreeArray()[checkLBS.getIndex()-1];

			return preLBS.getGroup() == 0
					|| preLBS.getGroup() == checkLBS.getGroup();
			//return preLBS == exPreLBS;
		}
		else if(checkFlag <= 0){
			int index = parserTree.getrTreeList().size()-1+checkFlag;
			GTree exPreLBS = parserTree.getrTreeList().get(index).getRuleContext().getLbs();
			if(checkLBS == exPreLBS && checkLBS.getGroup() == 0){
				return false;
			}
			return checkLBS.getFlag() == exPreLBS.getFlag() 
					&& (preLBS.getGroup() != checkLBS.getGroup()
							|| preLBS.getGroupType() != GROUPTYPE.ZERO_OR_ONE);
		}
		return false;
	}
	
	protected boolean isParseFinsh(){
		
		return isParseLast();
	}
	
	protected boolean isExitFork(){
		return getLeaf().isExitFork();
	}
	
	protected boolean isExitLevel(){
		if(this.getLeaf().getType() == GTYPE.LEAF){
			return this.getLeaf().getParent().isExistLevel();
		}
		return getLeaf().isExistLevel();
	}
	protected boolean isTerminal(){
		return getLeaf().isTerminalNode();
	}
	
	protected void addToken(TokenString tokenStr) throws ExecuteInvaildException{
		//System.out.println(this.getLeaf().getName()+":"+this.getLeaf().getId());
		TFlag tflag = this.getLeaf()
			.getToken()
			.getTFlag(tokenStr.getId());
		if(tflag == null){
			throw new ExecuteInvaildException(tokenStr);
		}
		
		GTree lbs = tflag.getLbs();
		
		//if(!this.checkExpress(lbs)){
		//	throw new ParserInvaildException(tokenStr);
		//}
		addToken(tokenStr,lbs);
		//ParserTree tokenTree = this.createTree(this.getParserTree(), lbs);
		//this.setValue(tokenTree, tokenStr);
	}
	//private ParserTree next;
	
	protected void addTerminalNode(GTree gTree,TokenString tokenStr) throws ExecuteInvaildException{
		if(gTree == null){
			throw new ExecuteInvaildException(tokenStr);
		}
		
		//if(getLeaf().isTerminalNode()){
		//	setValue(this.getParserTree(),tokenStr);
			//next = this.getParserTree().getParent();
		//}else{
			
			this.parserTree.addRTree(createTerminalNode(gTree,tokenStr));
			//this.parserTree.getRuleContext().setLeaf(
			//		this.parserTree.getLast().getRuleContext().getLbs().getParent().getRel());
		//}
	}
	
	protected void addTerminalNode(TokenString tokenStr) throws ExecuteInvaildException{
		
		if(getLeaf().isTerminalNode()){
			setValue(this.getParserTree(),tokenStr);
			//next = this.getParserTree().getParent();
			return;
		}
		GTree gTree = null;
		if(this.getParserTree().isEmpty()){
			gTree = getLeaf().getSelfFirstNodeByToken(tokenStr);
		}else{
			gTree = getLeaf().getSelfNodeByToken(tokenStr);
		}
		
		if(gTree == null){
			gTree = getLeaf().getNodeByToken(tokenStr,this.getParserTree().isEmpty());
		}
		this.addTerminalNode(gTree,tokenStr);
	}
	
	protected List<LineInfo> scan(int id,String token){
		GTree gTree = this.getLeaf();
		//if(gTree.isExistLine(id)){
		//	return gTree.get(id);
		//}
		
		PathScaner pathScaner = new PathScaner();
		pathScaner.scan(this.getParserTree(), id, token);
		List<LineInfo> lineList = pathScaner.getLineInfoList();
		GTree tmp = gTree;
		if(!lineList.isEmpty()&& 
				lineList.get(0).getDeep()==1){
			if(gTree.getType() == GTYPE.LEAF){
				tmp = gTree.getParent();
			}
		}

		for(LineInfo lineInfo : lineList){
			tmp.add(id, lineInfo);
		}
		//tmp.println();
		return lineList;
	}
	
	protected boolean  isParseLast(){
		return this.getLeaf().getRel().isTerminalNode()
				|| getParserTree()
				.getLast()
				.getRuleContext()
				.isLast();
	}
	
	protected ParserTree createFirstTree(ParserTree subTree){
		
		ParserTree targetTree = subTree;
		
		GTree gTree = targetTree.getRuleContext().getLeaf().getParent();
		
		do{
			
			if(gTree.getType() == GTYPE.LEAF){
				gTree = gTree.getParent();
			}
			
			if(equal(gTree)){
				break;
			}
			
			ParserTree parentTree = new ParserTree();
			
			initTree(parentTree,gTree);
			
			if(parentTree.getRuleContext().isExitLevel()){
				levelTree = parentTree;
			}
			
			parentTree.addRTree(targetTree);
			
			targetTree = parentTree;
			
			gTree = gTree.getParent();
			

		}while(!equal(gTree));
		
		return targetTree;
		
	}
	

	protected ParserTree createNodeFirstTree(ParserTree subTree){
		
		ParserTree targetTree = subTree;
		
		GTree gTree = targetTree.getRuleContext().getLbs();
		
		while(!equal(gTree)){
			if(gTree.getRel().getType() == GTYPE.NODE){
				gTree = gTree.getParent();
				continue;
			}
			if(gTree.getType() == GTYPE.LEAF){
				if(equal(gTree.getParent())){
					this.setLeaf(gTree);
					break;
				}
			}
			
			if(gTree.isExitFork()){
				gTree = gTree.getParent();
				continue;
			}
			
			if(equal(gTree)){
				break;
			}
			
			ParserTree parentTree = new ParserTree();
			
			initTree(parentTree,gTree);
			
			parentTree.addRTree(targetTree);
			
			targetTree = parentTree;
			
			if(gTree.getParent() == null){
				break;
			}

			gTree = gTree.getParent();
		}
		
		return targetTree;
		
	}
	
	protected void setValue(ParserTree rTree,TokenString tokenStr){

		if(tokenStr.getType() == TOKENTYPE.TOKEN){
			rTree.setType(BData.FLAG_TYPE_TOKEN);
		}else{
			rTree.setType(BData.FLAG_TYPE_NODE);
		}
		
		rTree.setText(tokenStr.getText());
		
	}
	
	protected ParserTree createTree(ParserTree parent,GTree gTree){
		ParserTree rTree = new ParserTree(parent);
		initTree(rTree,gTree);
		return rTree;
	}
	
	protected void initTree(ParserTree rTree,GTree gTree){
		
		RuleContext ruleContext = new RuleContext();
		ruleContext.setParserTree(rTree);

		ruleContext.setLbs(gTree);
		
		rTree.setType(BData.FLAG_TYPE_VAR);
		rTree.setRuleContext(ruleContext);
		
	}
	
	protected ParserTree createTerminalNode(GTree gTree,TokenString tokenStr){
		ParserTree parserTree = new ParserTree();
		
		initTree(parserTree,gTree);
		
		
		//ParserTree parserTree = this.createTree(this.getParserTree(), gTree);
		
		setValue(parserTree,tokenStr);
		
		ParserTree firstTree  = createNodeFirstTree(parserTree);
		
		//if(firstTree == parserTree){
		//	next = parserTree;
		//}else{
		//	next = parserTree.getParent();
		//}
		return firstTree;
	}
	
	private boolean equal(GTree target){
		return this.getLeaf().getId() == target.getId();
		
	}

	protected boolean containsSymbol(int tId) {
		Token token = getLeaf().getToken();
		return token!=null && token.containsKey(tId);
	}
	
	protected ParserTree createNextTree(){
		
		GTree last = this.getParserTree().getLast().getRuleContext().getLbs();
		GTree gTreeArr[] =  this.getLeaf().getgTreeArray();
		int index = last.getIndex()+1;
		if(index < gTreeArr.length){
			GTree gTree = gTreeArr[index];
			
			if(gTree.getType() == GTYPE.TOKEN
					//|| gTree.getRel().getType() == GTYPE.NODE
					){
				return null;
			}
			
			if(gTree.getGroup()==last.getGroup()){
				return this.createTree(gTree);
				//addFirstChildTree(gTree);
			}
		}
		
		return null;
		
	}
	
	private ParserTree createTree(GTree gTree) {
		ParserTree rTree = new ParserTree();
		initTree(rTree,gTree);
		return rTree;
	}
	
	protected ParserTree addFirstChildTree(GTree child){
		levelTree = null;
		ParserTree parserTree = new ParserTree();
			
		initTree(parserTree,child);
		if(parserTree.getRuleContext().isExitLevel()){
			levelTree = parserTree;
		}	
		ParserTree next =  createFirstTree(parserTree);
			
		this.getParserTree().addRTree(next);
		
		return parserTree;
	}
	
	protected ParserTree getFirstTree(GTree[] getgTreeArr) {
		levelTree = null;
		ParserTree parentTree = this.getParserTree();
		
		int size = getgTreeArr.length;
		
		for(int i = 0; i <size;i++){
			ParserTree parserTree = this.createTree(parentTree, getgTreeArr[i]);
			if(parserTree.getRuleContext().isExitLevel()){
				levelTree = parserTree;
			}
			parentTree = parserTree;
			
			if( i == size -1 ){
				return parserTree;
			}
		}
		return null;
	}

	protected void freshLeafByLast() {
		GTree subLBS = this.getLeaf().getSubLBS(
				this.getParserTree().getLast().getRuleContext().getLeaf());
		if(subLBS!=null){
			this.setLeaf(subLBS.getParent().getRel());
		}else{
			if(this.getParserTree().getLast().getRuleContext().isLeaf()){
				setLeaf(this.getParserTree().getLast()
						.getRuleContext().getLbs().getParent().getParent());
			}else{
				setLeaf(this.getParserTree().getLast()
						.getRuleContext().getLbs().getParent());
			}
		}
		/*if(this.isExitFork()){
			if(this.getParserTree().getLast().getRuleContext().isLeaf()){
				setLeaf(this.getParserTree().getLast()
						.getRuleContext().getLbs().getParent().getParent());
			}else{
				setLeaf(this.getParserTree().getLast()
						.getRuleContext().getLbs().getParent());
			}
		}*/

		//setLeaf(this.getParserTree().getLast()
		//		.getRuleContext().getLbs().getParent());
	}


	protected List<LineInfo> getLineInfo(int tokenId){
		return this.getLeaf().get(tokenId);
	}

	//protected ParserTree getNext() {
	//	return next;
	//}
	
	protected ParserTree changeRightByLevel(ParserTree childTree) {
		int childLevel = childTree.getRuleContext().getLeaf().getLevel();
		int level = this.getLeaf().getLevel();
		ParserTree levleTree = childTree;
		while(childLevel > 0 
				&& level > 0
				&& childLevel >= level
				){
			levleTree = levleTree.getrTreeList().get(0);
			childLevel = levleTree.getRuleContext().getLeaf().getLevel();
		}
		
		if(levleTree!=childTree){
			levleTree =levleTree.getParent();
			ParserTree first = levleTree.getrTreeList().get(0);
			this.getParserTree().addRTree(first);
			//first.getRuleContext().setSingleLbs(
			//		this.getParserTree().getRuleContext().getLeaf().getgTreeArray()[]);
			levleTree.getrTreeList().set(0, this.getParserTree());
			levleTree.getRuleContext().setSingleLbs(this.getParserTree().getRuleContext().getLbs());
			this.getParserTree().getRuleContext().setSingleLbs(
					levleTree.getRuleContext().getLeaf().getgTreeArray()[0]);
			this.getParserTree().setParent(levleTree);
			return levleTree;
		}else{
			this.getParserTree().addRTree(childTree);
		}
		
		return this.getParserTree();
	}
	
	protected boolean isSkipToken(int id){
		
		
		return this.getLeaf().isSkipToken(id);
	}

	protected GTree getSkipTokenTree(int id){
		
		
		return this.getLeaf().getSkipTokenTree(id);
	}
	
	protected ParserTree getLevelTree() {
		return levelTree;
	}
	
	protected ParserTree enterFirstTree() {
		this.levelTree = null;
		ParserTree child = this.getParserTree();
		
		if(child.getRuleContext().isExitLevel()){
			levelTree = child;
		}
		
		GTree gChild = child.getRuleContext().getFirstChild();

		while(gChild !=null 
				//&& !gChild.isExitFork() 
				// && gChild.getgTreeArray()[0].getRel().getType() != GTYPE.NODE
				 ){

			child =  createTree(child,gChild);
			
			if(child.getRuleContext().isExitLevel()){
				levelTree = child;
			}
			gChild = child.getRuleContext().getFirstChild();
		}
		
		return child;
	}
	protected boolean isLeaf(){
		return this.getLeaf().getType() == GTYPE.LEAF;
	}
	
	protected void setSingleLbs(GTree lbs) {
		this.lbs = lbs;
	}
	protected int getSize(){
		return getLeaf().getgTreeArray().length;
	}
	/*
	public boolean isParseFinsh(){
		ParserTree  parent = this.getParserTree().getParent();
		if(parent == null){
			return isParseLast();
		}
		if(isParseLast()){
			return !(getLeaf().getLevel() >0
					&& parent.getRuleContext().getLeaf().getLevel() == 0);
		}

		return false;
	}*/

	public Object getValue() {
		return this.value;
	}

	public void setParam(Object param) {
		this.param = param;
		
	}

	protected void setFinish(boolean hasExecute) {
		this.hasExecute = hasExecute;
		
	}

	public Object getParam() {
		return param;
	}

	protected boolean isFinish() {
		return hasExecute;
	}

	public String getText() {
		return this.getParserTree().getText();
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getChildCount() {
		return this.getParserTree().getrTreeList().size();
	}

	public ParserTree[] getAllChild() {
		ParserTree[] parserTreeArr = new ParserTree[this.getParserTree().getrTreeList().size()];
		return this.getParserTree().getrTreeList().toArray(parserTreeArr);
	}

	public ParserTree getChild(int index) {
		return this.getParserTree().getrTreeList().get(index);
	}

	//public boolean isNeedReExecute() {
	//	return needReExecute;
	//}

	private void addToken(TokenString tokenStr, GTree nextTree) {
		ParserTree tokenTree = this.createTree(this.getParserTree(), nextTree);
		this.setValue(tokenTree, tokenStr);
		
	}

	//public void setNeedReExecute(boolean needReExecute) {
	//	this.needReExecute = needReExecute;
	//}
	
	/*public List<GTree> getFirstNode(TokenString tokenStr){
		return getLeaf().getFirstNodeByToken(tokenStr);
		
	}*/
	/*
	public boolean addNextTree(){
		
		GTree last = this.getParserTree().getLast().getRuleContext().getLbs();
		GTree gTreeArr[] =  this.getLeaf().getgTreeArray();
		int index = last.getIndex()+1;
		if(index < gTreeArr.length){
			GTree gTree = gTreeArr[index];
			
			if(gTree.getType() == GTYPE.TOKEN
					//|| gTree.getRel().getType() == GTYPE.NODE
					){
				return false;
			}
			
			if(gTree.getGroup()==last.getGroup()){
				this.createTree(this.getParserTree(), gTree);
				//addFirstChildTree(gTree);
				return true;
			}
		}
		
		return false;
		
	}*/
	/*
	public void addFirstTree(GTree child){
		ParserTree parserTree = new ParserTree();
		
		initTree(parserTree,child);
		
		this.getParserTree().addRTree(parserTree);
	}*/
	
	/*
	public boolean isChange(ParserTree childTree) {
		int childLevel = childTree.getRuleContext().getLeaf().getLevel();
		int level = this.getLeaf().getLevel();
		if(childLevel > 0 
				&& level > 0
				&& childLevel > level){
			//ParserTree upParent = this.getParserTree().getParent();
			//childTree.setParent(upParent);
			ParserTree last = childTree.getLast();
			
			childTree.setLast(this.getParserTree());
			this.setSingleLbs(last.getRuleContext().getLbs());
			
			this.getParserTree().setTree(0,last);
			last.getRuleContext().setLbs(this.getLeaf().getgTreeArray()[0]);
			return true;
		}
		return false;
	}*/
}
