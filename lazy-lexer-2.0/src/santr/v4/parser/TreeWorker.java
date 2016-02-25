package santr.v4.parser;


import santr.gtree.model.LineInfo;
import santr.gtree.model.Runner;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.execute.Visitor;
import santr.v4.parser.ParserTree;


public class TreeWorker {
	
	protected ParserTree root;
	
	protected ParserTree currentTree;
	
	protected ToolUtil toolUtil;
	
	protected TreeJumper treeJumper;

	private boolean isEnterNextTree = false;
	
	protected boolean isFinish = false;
	
	protected boolean isNeedUp = false;
	
	protected LineInfo upInfo;
	
	protected Visitor<?> visitor;
	
	public TreeWorker(){
		
	}
	
	public void setVisitor(Visitor<?> visitor){
		this.visitor = visitor;
	}
	public TreeWorker(ToolUtil toolUtil) throws ExecuteInvaildException{

		this.toolUtil = toolUtil;
		init();
	}
	
	protected void parserTree() throws ExecuteInvaildException {
		if(isToken()){
			if(match()){
				
				if(this.treeJumper.getLineInfo().getDeep() == -2){
					if(this instanceof LevelTreeWork){
						this.addLastTree();
						//this.isFinish = true;
						//this.isNeedUp = true;
						//System.out.println(this.currentTree.getName()+":"+this.currentTree.getRuleContext().getLeaf().getId());
						this.moveToUp(treeJumper.getLineInfo());
					}else{
						this.addLastTree();
						vistTree();
						this.setCurrentTree(this.getCurrentTree().getParent());
						this.parserTree();
					}

				}else{
					if(!this.treeJumper.isUp()){
						refresh();
						//System.out.println(this.getCurrentTree().getName());
						
						this.enterNextTree();
					}else{
						
						this.addLastTree();
						
						moveToUp(treeJumper.getLineInfo());
						if(isFinish){
							return;
						}
						this.addTokenTree();
						
						this.enterNextTree();
					}
				}
			}
		}else{
			
			addTerminalTree();
		}
		//System.out.println(this.getCurrentTree().getName());
	}

	private void addLastTree() throws ExecuteInvaildException {
		toolUtil.addLastTree(this.getCurrentTree());
		
	}
	public void work() throws ExecuteInvaildException{
		
		TokenStreamInfo tokenStreamInfo = toolUtil.getTokenStream();
		TokenString tokenStr = tokenStreamInfo.getNext();
		while(tokenStr != null){
			toolUtil.setTokenString(tokenStr);
			parserTree();
			tokenStr = tokenStreamInfo.getNext();
		};
		
	}
	
	private void addTerminalTree() throws ExecuteInvaildException {
		boolean isLast = toolUtil.getTokenString().isLast();
		if(isCanAddTerminal()
				|| isLast){
			if(isLast){
				this.addAllLastTree();
			}else{
				this.toolUtil.addTerminalTree(this.getCurrentTree());
			}
			

			exitTree();
		}
		
	}
	
	private void addAllLastTree() throws ExecuteInvaildException {
		toolUtil.addAllLastTree(this.getCurrentTree());
		
	}
	private boolean isCanAddTerminal(){
		
		return !currentTree
				.getRuleContext()
				.isExitFork() && isEnterNextTree ;
	}
	/*
	private void enterTree() throws ParserInvaildException {
		if(toolUtil.checkIsToken()){
			while(!this.treeJumper.enter(currentTree)){
				 
				TokenString tokenString = toolUtil.getTokenStream().getNext();
				toolUtil.setTokenString(tokenString);
			}
			this.refresh();
			this.enterNextTree();
		}else{
			TokenStreamInfo tokenStreamInfo = toolUtil.getTokenStream();
			TokenString tokenString = tokenStreamInfo.getNext();
			TokenString preToken = toolUtil.getTokenString();
			
			if(tokenString == null){
				this.addPreTree(preToken);
				this.exitTree();
				return;
			}
			
			List<GTree> list = currentTree.getRuleContext().getFirstNode(toolUtil.getTokenString());
			
			
			filter(list,preToken,tokenString);
			
		}
	}*/
	/*
	private boolean filter(List<GTree> list,TokenString preToken,TokenString tokenString) throws ParserInvaildException {
		
		GTree node = null;
		boolean isDown = false;
		
		for(int i = 0; i < list.size();i++){
			GTree tmpNode = list.get(i);
			Token token = tmpNode.getParent().getToken();
			if(token != null){
				TFlag tFlag = token.getTFlag(tokenString.getId());
				
				if(tFlag == null){
					
					continue;
				}
				GTree checkLBS = tFlag.getLbs();
				
				int checkFlag = checkLBS.getFlag() - tmpNode.getFlag();

				if(checkFlag == 1){
					//GTree exPreLBS = checkLBS.getParent().getgTreeArray()[checkLBS.getIndex()-1];

					if( tmpNode.getGroup() == 0
							|| tmpNode.getGroup() == checkLBS.getGroup()){
						isDown = true;
						node = tmpNode;
						break;
					}
					//return preLBS == exPreLBS;
				}
			}

		}
		
		if(isDown){
			if(!node.getParent().getName().equals(currentTree.getName())){
				this.getCurrentTree().getRuleContext()
					.addFirstTree(node.getParent());
		
				this.getCurrentTree().getRuleContext().freshLeafByLast();
		
				this.setCurrentTree(this.getCurrentTree().getLast());
			}else{
				currentTree.getRuleContext().setLeaf(node.getParent());
			}
			
			toolUtil.addPreTerminalTree(this.currentTree, node,preToken);
			
			this.addTokenTree(tokenString);
			
			this.enterNextTree();
			
		}else{
			//currentTree.getRuleContext().setLeaf(node.getParent());
			
			//currentTree.getRuleContext()
			this.addPreTree(preToken);
			this.setCurrentTree(currentTree.getRuleContext().getNext());
			this.exitTree();
			toolUtil.setTokenString(tokenString);
			this.parserTree();
			
		}
		return isDown;
	}*/

	private void addTokenTree(TokenString tokenString) throws ExecuteInvaildException {
		toolUtil.addTokneTree(currentTree,tokenString);
		
	}

	private void addPreTree(TokenString tmpToken) throws ExecuteInvaildException {
		this.toolUtil.addTerminalTree(currentTree,tmpToken);
		
	}
	
	protected void refresh() throws ExecuteInvaildException {
		LineInfo lineInfo = treeJumper.getLineInfo();

		int fword = lineInfo.getDeep();
		if(fword == 0){
			parserCurrent(lineInfo);
			addTokenTree();
		}else{
			
			freshCurrentTree(lineInfo);
			
		}
		
	}
	
	protected void parserCurrent(LineInfo lineInfo) throws ExecuteInvaildException{
		revolve(lineInfo);
		
		addPreTree();
		//this.addAllPreTree();
	}
	
	protected void enterNextTree() throws ExecuteInvaildException {
		if(this.getCurrentTree().isEmpty()){
			return;
		}
		if(isParseFinsh()){
			exitTree();
			
		}else{
			addNextTree();
		}
	}
	
	private void parserSubLevelTree(ParserTree parserTree, ParserTree childFirstTree, boolean isNeed) throws ExecuteInvaildException{
		parserTree.setPreTree(this.getCurrentTree());
		LevelTreeWork levelTreeWork = new LevelTreeWork(this.toolUtil,parserTree,childFirstTree);
		if(isNeed){
			levelTreeWork.addAllPreTree();
			levelTreeWork.addTokenTree();
			levelTreeWork.enterNextTree();
		}
		levelTreeWork.work();
		
		changeRightByLevel(levelTreeWork.getRoot());
		if(this instanceof LevelTreeWork){
			this.vistTree();
		}
		if(levelTreeWork.isNeedUp()){
			
			if(this instanceof LevelTreeWork){
				//System.out.println(this.getCurrentTree().getRuleContext().getName()+":"+toolUtil.getTokenString().getText()+this.getCurrentTree().getRuleContext().containsSymbol(toolUtil.getTokenString().getId()));
				//if(this.getCurrentTree().getParent() != null){
					//System.out.println(this.getCurrentTree().getParent().getName());
					//System.out.println(this);
				//}
				
				//isNeedUp =!this.getCurrentTree().getRuleContext().containsSymbol(toolUtil.getTokenString().getId());
				this.moveToUp(levelTreeWork.getUpInfo());
				if(!this.isNeedUp){
					//System.out.println(this.getCurrentTree().getRuleContext().getName()+"add:"+toolUtil.getTokenString().getText());
					this.addTokenTree();
					
					//this.work();
				}
				this.enterNextTree();
			}else{
				if(levelTreeWork.getUpInfo().getDeep() == -2){
					//this.setCurrentTree(this.getCurrentTree().getParent());
					this.parserTree();
				}else{
					this.moveToUp(levelTreeWork.getUpInfo());
					this.addTokenTree();
				}

				//this.enterNextTree();
			}
		}else{
			this.exitTree();
		}
	}
	
	private void changeRightByLevel(ParserTree childTree){
		
		this.setCurrentTree(this.getCurrentTree()
				.getRuleContext().changeRightByLevel(childTree));
		//this.root = this.getRoot();
		
	}
	
	private void addNextTree() throws ExecuteInvaildException {
		ParserTree parserTree = currentTree.getRuleContext().createNextTree();
		
		
		isEnterNextTree = parserTree!=null;
		
		if(isEnterNextTree){
			ParserTree first = parserTree.getRuleContext().enterFirstTree();
			ParserTree levelTree = parserTree.getRuleContext().getLevelTree();
			if(levelTree!=null){
				isEnterNextTree = false;
				
				if(parserTree !=levelTree){
					currentTree.addRTree(parserTree);
					setCurrentTree(parserTree);
					levelTree.getParent().removeLast();
					levelTree.setParent(null);
				}else{
					if(levelTree.getParent()!=null){
						this.setCurrentTree(levelTree.getParent());
						this.currentTree.removeLast();
						levelTree.setParent(null);
					}
				}
				parserSubLevelTree(levelTree,first,false);
			}else{
				currentTree.addRTree(parserTree);
				//ParserTree first = toolUtil.enterFirstTree(currentTree.getLast());

				setCurrentTree(first);
			}
		}
		/*
		if(isEnterNextTree){
			if(parserTree.getRuleContext().isExitLevel()e
					){
				isEnterNextTree = false;

				parserSubLevelTree(parserTree,parserTree,false);
			}else{
				currentTree.addRTree(parserTree);
				ParserTree first = toolUtil.enterFirstTree(currentTree.getLast());

				setCurrentTree(first);
			}
		}*/
	}

	protected void addPreTree() throws ExecuteInvaildException {
		addPreTree(currentTree);
	}

	private void addPreTree(ParserTree currentTree) throws ExecuteInvaildException {
		this.toolUtil.addPreTerminalTree(currentTree);
	}
	
	protected void revolve(LineInfo lineInfo) throws ExecuteInvaildException {
		revolveTreeLeaf(lineInfo);
	}
	
	private void revolveTreeLeaf(LineInfo lineInfo) throws ExecuteInvaildException{
		if(currentTree.getRuleContext().isExitFork()){
			currentTree.refreshLeaf(lineInfo.getExpress());
		}
		
	}
	
	protected void moveToUp(LineInfo lineInfo) throws ExecuteInvaildException{
		while(
				!currentTree.getRuleContext()
					.containsSymbol(toolUtil.getTokenString().getId())
				//currentTree.getRuleContext().getLeaf() != lineInfo.getExpress()
				){
			vistTree();
			this.setCurrentTree(currentTree.getParent());
			
		};
		
	}

	private void freshCurrentTree(LineInfo lineInfo) throws ExecuteInvaildException {
		
		//revolveTreeLeaf(lineInfo);

		Runner runner = lineInfo.getRunner();
		ParserTree childFirstTree = null;
		if(lineInfo.getRunner() == null){
			
			childFirstTree = this.getCurrentTree().getRuleContext()
				.addFirstChildTree(lineInfo.getExpress());
			
			//this.getCurrentTree().getRuleContext().freshLeafByLast();

		}else{
			
			childFirstTree = this.getCurrentTree().getRuleContext()
				.getFirstTree(runner.gTreeArr());
			
			this.getCurrentTree().getRuleContext().freshLeafByLast();
		}
		
		ParserTree levelTree = this.currentTree.getRuleContext().getLevelTree();
		if(levelTree!=null){
			
			this.setCurrentTree(levelTree.getParent());
			this.currentTree.removeLast();
			
			levelTree.setParent(null);
			this.parserSubLevelTree(levelTree,childFirstTree,true);
		}else{
			this.setCurrentTree(childFirstTree);
			addAllPreTree();
			addTokenTree();
		}
		
	}

	protected void addAllPreTree() throws ExecuteInvaildException {
		this.toolUtil.addAllPreChild(
				this.getCurrentTree());
		
		//if(toolUtil.isEmpty(this.getCurrentTree())
		//		&& !this.getCurrentTree().getRuleContext().isTerminal()){
		//	throw new ParserInvaildException(toolUtil.getTokenString());
		//}
	}

	private void init() throws ExecuteInvaildException{
		treeJumper = new TreeJumper(toolUtil);
		root = new ParserTree();
		
		initRoot();
		//setCurrentTree(root);
	}
	
	private void initRoot() throws ExecuteInvaildException{
		
		toolUtil.initRoot(root);
		
		ParserTree first = toolUtil.enterFirstTree(root);
		

		if(first.getRuleContext().isExitLevel()){
			if(first.getParent()!=null){
				setCurrentTree(first.getParent());
				this.getCurrentTree().removeLast();
				first.setParent(null);
				this.parserSubLevelTree(first, first,false);
			}

		}else{
			this.setCurrentTree(first);
		}
		
	}
	
	protected void setCurrentTree(ParserTree tree){
		this.currentTree = tree;
	}
	
	//Skip to the tree which is match the symbol,if the tree is existed.
	private boolean match() {
		//if(!isToken()){
		//	return false;
		//}
		return treeJumper.jump(currentTree);
	}
	
	private boolean isToken(){
		return toolUtil.checkIsToken();
	}
	
	protected void vistTree() throws ExecuteInvaildException{
		if(this.visitor!=null){
			this.visitor.vist(this.getCurrentTree());
		}
	}
	
	//If the tree is end,skip to parent tree which is not end.
	protected void exitTree() throws ExecuteInvaildException{

		while(currentTree.getParent()!=null && isParseFinsh()){
			vistTree();
			this.setCurrentTree(currentTree.getParent());
		}
	}

	protected boolean isParseFinsh() {
		return //currentTree!=root 
				//&& 
				currentTree.getRuleContext().isParseFinsh();
	}

	protected void addTokenTree() throws ExecuteInvaildException {
		toolUtil.addTokneTree(currentTree);
	}

	protected ParserTree getCurrentTree() {
		return currentTree;
	}

	protected ParserTree getRoot() {
		while(root.getParent()!=null){
			root = root.getParent();
		}
		return root;
	}
	
}
