package santr.v4.parser;

import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.gtree.model.enume.TOKENTYPE;
import santr.parser.exception.ExecuteInvaildException;
import santr.v4.parser.ParserTree;

public class LevelTreeWork extends TreeWorker{

	private boolean isPop = false;
	
	//private TokenString popTokenString;
	
	private GTree tokenTree;
	public LevelTreeWork(ToolUtil toolUtil,ParserTree parserTree, ParserTree childFirstTree){
		this.toolUtil = toolUtil;
		treeJumper = new TreeJumper(this.toolUtil);
		this.root = parserTree;
		this.setCurrentTree(childFirstTree);
		//System.out.println("LevelWork:"+root.getName());
	}
	
	public void work() throws ExecuteInvaildException{
		if(isFinish){
			return;
		}
		TokenStreamInfo tokenStreamInfo = toolUtil.getTokenStream();
		
		TokenString tokenStr = tokenStreamInfo.getNext();
		//if(tokenStr != null){
		//	System.out.print("work:"+tokenStr.getText()+" ");
		//}
		
		while(tokenStr != null){
			toolUtil.setTokenString(tokenStr);
			parserTree();
			if(isFinish){
				break;
			}
			tokenStr = tokenStreamInfo.getNext();
			
			//if(tokenStr != null){
			//	System.out.print("work1:"+tokenStr.getText()+" ");
			//}
		};
		//if(toolUtil.getTokenString()!=null
		//		&& !toolUtil.getTokenString().isLexer()){
		//	parserTree();
		//}
	}
	
	protected void exitTree() throws ExecuteInvaildException{
		boolean isRoot = false;
		while(isParseFinsh()){
			if(currentTree.getParent() == null){
				//System.out.println("moveToUp");
				isRoot = true;
				break;
			}
			this.setCurrentTree(currentTree.getParent());
		}
		
		if(isRoot){
			if(isMatch()){
				this.pop();
			}else{
				//isNeedUp = true;
				isFinish = true;
			}
		}

	}
	
	protected void moveToUp(LineInfo lineInfo) throws ExecuteInvaildException{
		
		if(lineInfo.getDeep()==-2){
			refreshEndState(lineInfo);
			return;
		}
		
		boolean isRoot = false;
		//System.out.println("movet:"+currentTree.getRuleContext().getLeaf().getId()+":"+lineInfo.getExpress().getId());
		while(
				//currentTree.getRuleContext().getLeaf() != lineInfo.getExpress()
				!this.getCurrentTree().getRuleContext().containsSymbol(toolUtil.getTokenString().getId())
				){
			//System.out.println("move:"+currentTree.getRuleContext().getLeaf().getId()+":"+lineInfo.getExpress().getId());
			if(currentTree.getParent() == null){
				isRoot = true;
				//System.out.println("moveToUp");
				break;
			}

			this.setCurrentTree(currentTree.getParent());
			
		};
		
		if(isRoot){
			if(lineInfo.isNeedMatch() && isMatch()){
				this.pop();
			}else{
				refreshEndState(lineInfo);
			}
		}
	}
	
	protected void parserCurrent(LineInfo lineInfo) throws ExecuteInvaildException{
		revolve(lineInfo);
		if(this.isPop){
			
			//if(this.getCurrentTree().getRuleContext().isChange(
			//		this.getCurrentTree().getrTreeList().get(0))){
			//	root = this.getCurrentTree().getParent();
			//}
			
			revolve(lineInfo);
			isPop = false;
		}else{
			this.addPreTree();
		}
	}
	


	public LineInfo getUpInfo() {
		return upInfo;
	}
	
	private void refreshEndState(LineInfo lineInfo){
		isNeedUp = true;
		upInfo = lineInfo;
		if(upInfo.isNeedMatch()){
			upInfo.setNeedMatch(false);
		}
		isFinish = true;
	}
	
	private boolean isMatch(){
		TokenString tokenString = toolUtil.getTokenString();
		if(tokenString.isLexer()){
			tokenString = toolUtil.getTokenStream().getNextOne();
			
			//if(tokenString != null){
			//	System.out.print("nextOne:"+tokenString.getText()+" ");
			//}
			//toolUtil.setTokenString(tokenString);
		}
		if(tokenString == null || tokenString.getType() == TOKENTYPE.STR){
			return false;
		}
		//popTokenString = tokenString;
		//toolUtil.setTokenString(tokenString);
		//System.out.println("1234:"+popTokenString.getText());
		//System.out.println("1234:"+this.getCurrentTree().getRuleContext()
		//		.isSkipToken(popTokenString.getId()));
		//System.out.println("1234:"+popTokenString.getText()+"----"+this.root.getRuleContext().getLeaf().getId());
		
		
		//return this.root.getRuleContext().getLeaf()
		//		.isSkipToken(popTokenString.getId());
		GTree tokenTree = this.root.getRuleContext()
				.getSkipTokenTree(tokenString.getId());
		if(tokenTree!=null){
			this.tokenTree = tokenTree.getParent();
		}
		return tokenTree!=null;
	}
	
	private void pop() {
		//System.out.println("1234:"+popTokenString.getText()+"----"+this.getCurrentTree().getRuleContext().getLeaf().getId()+"--"+this.root.getRuleContext().getLeaf().getId());
		//GTree gTree = this.root.getRuleContext().getLeaf().getParent()
		//		.get(popTokenString.getId())
		//		.get(0).getExpress();
		ParserTree newTree = toolUtil.createTree(tokenTree);
		newTree.getRuleContext().setSingleLbs(root.getRuleContext().getLbs());
		newTree.addRTree(root);
		newTree.setPreTree(root.getPreTree());
		root.setPreTree(null);
		root.getRuleContext().setSingleLbs(tokenTree.getgTreeArray()[0]);
		this.setCurrentTree(newTree);
		this.root = newTree;
		isPop = true;
	}
	
	public boolean isNeedUp(){
		return this.isNeedUp;
	}
}
