package santr.v3.parser;

import java.util.List;

import javolution.util.FastTable;

import santr.gtree.model.BData;
import santr.gtree.model.GTree;
import santr.gtree.model.GrammarInfo;
import santr.gtree.model.LineInfo;
import santr.gtree.model.enume.GTYPE;
import santr.parser.exception.ParserInvaildException;
import santr.v3.parser.data.RTree;


public class TreeWorker {
	
	private RTree root = new RTree();
	
	private RTree currentTree;
	
	private ToolUtil toolUtil;
	
	private int end;
	
	//private TokenStream tokenStream;
	
	//private boolean isNeedMatch = true;
	//private boolean isMatch = false;
	public TreeWorker(RTree root, ToolUtil toolUtil, TokenStream tokenStream,int end,RTree currentTree,boolean isNeedMatch){
		this.root = root;
		this.currentTree = currentTree;
		this.toolUtil = toolUtil;
		this.end = end;
		//this.tokenStream = tokenStream;
		//this.isNeedMatch = isNeedMatch;
	}
	
	public TreeWorker(char[] expressCharArr, GrammarInfo grammarInfo, TokenStream tokenStream){

		//this.root = root;
		
		root.setName(grammarInfo.getRoot());
		root.setType(BData.FLAG_TYPE_VAR);
		//rTree.setEnd(expressCharArr.length);
		root.setLbs(grammarInfo.getgTree());
		
		currentTree = this.root;
		toolUtil = new ToolUtil(grammarInfo,tokenStream);
		toolUtil.setLeafByExpress(this.root);
		//end = tokenStream.getLast().getEnd();
		end = expressCharArr.length;
		//this.tokenStream = tokenStream;
	}
	
	public void work(TokenString tokenStr) throws ParserInvaildException{
		toolUtil.setTokenString(tokenStr);

		System.out.println(tokenStr.getText());

		if(toolUtil.checkIsToken()){
			
			 doWithToken();
			 
		}else{
			if(
				!toolUtil.isEmpty(currentTree) 
					|| tokenStr.getEnd() == end
					){
				doWithNode();
			}
		}
		clear();
	}
	
	private void clear(){
		upTree = null;
	}
	
	private void doWithToken() throws ParserInvaildException
	{

		//if(isNeedMatch){
			LineInfo lineInfo = filter();
			if(lineInfo == null){
				return;
			}
			moveToTree(lineInfo);
		//}else{
		//	online(currentTree.getLeaf());
		//}

		
		addTokenTree(toolUtil.getTokenString());
	}
	
	private void addTokenTree(TokenString tokenString) throws ParserInvaildException{
		//if(toolUtil.getTokenString().isLexer()){
		//	return;
		//}
		
		RTree rTree = toolUtil.createTokenTree(currentTree.getLeaf(),tokenString);
		
		if(!toolUtil.isEmpty(currentTree)){
			checkExpress(currentTree.getLast(),rTree.getLbs(),currentTree);
		}
		currentTree.addRTree(rTree);
		
		moveUp();
			
		addNextExpress();
	}
	
	private void doWithNode() throws ParserInvaildException
	{
		RTree rTree = toolUtil.createNodeTree(currentTree);
		if(rTree != null){
			
			currentTree.addRTree(rTree);
			
			moveUp();
			
			addNextExpress();
		}
		
	}
	
	private void copy(RTree src,RTree dest){
		dest.setLbs(src.getLbs());
		dest.setName(src.getName());
		dest.setType(src.getType());
		dest.setText(src.getText());
	}
	
	private void pop(GTree cleaf){
		RTree newTree = new RTree();
		if(currentTree == root){
			copy(root,newTree);
			//newTree.setLbs(root.getLbs());
			root = newTree;
		}else{
			RTree parent = currentTree.getParentTree();
			newTree.setLbs(currentTree.getLbs());
			newTree.setName(currentTree.getName());
			//newTree.setLbs(currentTree.getLbs().getRel().getParent());
			parent.setLast(newTree);
		}
		newTree.addRTree(currentTree);
		currentTree.setLbs(cleaf.getgTreeArray()[0]);
		currentTree = newTree;
	}
	
	private void moveUp(){
		RTree rTree = currentTree.getLast();
		while(currentTree!=root 
				&& toolUtil.isLastTree(rTree.getLbs())){
			RTree parentTree = currentTree.getParentTree();
			
			if(currentTree.getLeaf().getLevel() >0
					&& parentTree.getLeaf().getLevel() == 0){
				return;
			}
			currentTree = currentTree.getParentTree();
			rTree = currentTree.getLast();
		}
	}
	
	private void addNextExpress() {
		/*
		if(!toolUtil.isEmpty(currentTree)
				&& toolUtil.getTokenString().getEnd() != end){
			RTree lastTree = currentTree.getLast();
			if(toolUtil.isLastTree(lastTree.getLbs())
					&& currentTree.getParentTree().getLeaf().getLevel() >0 ){
				pop();
				return;
			}
		}*/
		
		RTree rTree = currentTree.getLast();
		RTree nextExpreTree = toolUtil.getNextExpressTree(rTree.getLbs());
		
		if(nextExpreTree != null){
			currentTree.addRTree(nextExpreTree);
			currentTree = nextExpreTree;
		}
	}

	private List<LineInfo> mList = new FastTable<LineInfo>();
	
	private LineInfo filter(){
		//isMatch = false;
		LineInfo upLineInfo = null;
		if(!mList.isEmpty()){
			int i = 0;
			for(; i < mList.size();i++){
				GTree gTree = toolUtil.getTree(mList.get(i));
				if(!checkMatch(gTree)){
					mList.remove(i);
					i = i-1;
				}
			}
		}else{
			List<LineInfo> lineInfoList = toolUtil.getLineInfo(currentTree);
			
			if(lineInfoList == null){
				toolUtil.scan(currentTree);
				lineInfoList = toolUtil.getLineInfo(currentTree);
			}
			
			if(lineInfoList.size() == 1){
				return lineInfoList.get(0);
			}else{
				mList.addAll(lineInfoList);
				int i = 0;
				for(; i < mList.size();i++){
					LineInfo lineInfo = mList.get(i);
					GTree cleaf = toolUtil.getTree(lineInfo);
					int deep = lineInfo.getDeep();
					if(deep == 0 
							&& currentTree.getLeaf() != null){
						if(currentTree == root){
							mList.clear();
							return lineInfo;
						}
						int index = cleaf.getToken().getTFlag(
								toolUtil.getTokenString().getId()).getLbs().getIndex();
						GTree lbs = cleaf.getgTreeArray()[index];
						if(!toolUtil.checkExpress(currentTree.getLast(), lbs, currentTree)){
							mList.remove(i);
							i = i-1;
						}else{
							mList.clear();
							return lineInfo;
						}
					}else if(lineInfo.getDeep()>0 ){
						if(!checkMatch(cleaf)){
							mList.remove(i);
							i = i-1;
						}
					}else{
						upLineInfo = lineInfo;
						mList.remove(i);
						i = i-1;
					}
				}
			}
			
			if(mList.isEmpty()){
				return upLineInfo;
			}
		}

		if(mList.size()==1){
			LineInfo lineInfo = mList.get(0);
			mList.clear();
			return lineInfo;
		}
		return null;
	}
	
	private boolean checkMatch(GTree gTree){
		
		return toolUtil.checkMatch(gTree);
		
	}
	private void moveToTree(LineInfo lineInfo) throws ParserInvaildException
	{
		GTree cleaf = null;
		
		int flag = lineInfo.getDeep();
		
		cleaf = toolUtil.getTree(lineInfo);
		
		if(flag == 0){
			online(cleaf);
		}else if(flag < 0){
			up(flag,cleaf);
		}else if(flag > 0 ){
			down(cleaf,lineInfo);
		}
	}
	
	private void checkExpress(RTree preTree,GTree checkLBS,RTree parentTree) throws ParserInvaildException
	{
		if(toolUtil.isEmpty(preTree) 
				&& preTree.getType() == BData.FLAG_TYPE_VAR){
			throw new ParserInvaildException(toolUtil.getTokenString().getText());
		}
		boolean isSuccess = toolUtil.checkExpress(preTree, checkLBS, parentTree);
		if(!isSuccess){
			System.out.println(parentTree.getLeaf().getName());
			System.out.println(parentTree.getLeaf().getToken().getTflag().length);
			throw new ParserInvaildException(toolUtil.getTokenString().getText());
		}
	}
	private RTree upTree = null;
	
	private void skipBylevel(RTree parent,int cLevel){
		//if(!toolUtil.isEmpty(currentTree)){
		//	return false;
		//}
		
		if(cLevel > 0){
			if(parent!=null
					&& parent.getLeaf().getLevel() > 0
					&& cLevel >= parent.getLeaf().getLevel()){
				upTree = parent;
				skipBylevel(parent.getParentTree(),cLevel);
			}
		}
	}
	
	private void online(GTree cleaf) throws ParserInvaildException
	{	
		
		if(!toolUtil.isEmpty(currentTree)
				//&& toolUtil.getTokenString().getEnd() != end
				){
			RTree lastTree = currentTree.getLast();
			if(toolUtil.isLastTree(lastTree.getLbs())
					|| cleaf != currentTree.getLeaf()
				){
				pop(cleaf);
			}
		}
		
		currentTree.setLeaf(cleaf);
		
		boolean isEmpty = toolUtil.isEmpty(currentTree);

		RTree rTree = toolUtil.createPreTree(cleaf);
		
		if(isEmpty){
			skipBylevel(currentTree.getParentTree(),cleaf.getLevel());
			
			if(upTree!=null){
				changeByLevel(rTree,cleaf,upTree);
			}else{
				if(rTree != null){
					currentTree.addRTree(rTree);
				}
			}
		}else{
			if(rTree != null){
				currentTree.addRTree(rTree);
			}
		}
	}
	
	private void changeByLevel(RTree rTree ,GTree leaf,RTree upTree){
		RTree parent = currentTree.getParentTree();
		parent.setLast(rTree);
		
		if(upTree == root){
			
			currentTree.addRTree(upTree);
			currentTree.setLbs(root.getLbs());
			currentTree.setParentTree(null);
			
			root.setLbs(leaf.getgTreeArray()[0]);
			root = currentTree;
			
		}else{
			RTree upParent = upTree.getParentTree();
			currentTree.setLbs(upParent.getLast().getLbs());
			upParent.setLast(currentTree);
			currentTree.addRTree(upTree);
			upTree.setLbs(currentTree.getLeaf().getgTreeArray()[0]);
		}
	}
	
	private void up(int flag, GTree cleaf) throws ParserInvaildException 
	{

		RTree rTree = toolUtil.createPreNodeTree(currentTree);

		if(rTree !=null){
			currentTree.addRTree(rTree);
			//this.moveUp();
		}else{
			if(toolUtil.isEmpty(currentTree)){
				throw new ParserInvaildException(toolUtil.getTokenString().getText());
			}
		}
		
		while(currentTree.getLeaf().getToken() ==null 
				|| !currentTree.getLeaf().getToken()
						.containsKey(toolUtil.getTokenString().getId())
				//cleaf != currentTree.getLeaf()
				){
			currentTree = currentTree.getParentTree();
		}
		
	}

	private void down(GTree cleaf, LineInfo lineInfo) throws ParserInvaildException 
	{
		if(lineInfo.getRunner() == null){
			TreeInfo treeInfo = toolUtil.createSubTree(cleaf,currentTree);
			currentTree.setLeaf(treeInfo.getTree().getLeaf().getParent());
			currentTree.addRTree(treeInfo.getTree());
			
			currentTree = treeInfo.getLast();	
		}else{
			TreeInfo treeInfo = toolUtil.createSubTree(currentTree,lineInfo.getRunner());
			//if(currentTree.getLeaf() == null){
			//	currentTree.setLeaf(treeInfo.getTree().getLeaf().getParent());
			//}
			currentTree.addRTree(treeInfo.getTree());
			
			currentTree = treeInfo.getLast();	
		}

		if(cleaf.getType() == GTYPE.LBS){
			cleaf = cleaf.getRel();
		}
		currentTree.setLeaf(cleaf);
		
		if(toolUtil.isEmpty(currentTree)){
			
			toolUtil.createAllPreTree(cleaf,currentTree);
			//List<RTree> rTreeList = toolUtil.createAllPreTree(cleaf);
			//if(!rTreeList.isEmpty()){
			//	for(int i = 0;i < rTreeList.size();i++){
			//		currentTree.addRTree(rTreeList.get(i));
			//	}
			//}
		}
		/*
		if(toolUtil.isEmpty(currentTree)){
			int unLindex = tokenStream.getLexerIndex()+1;
			TreeWorker treeWorker = new TreeWorker(this.root,this.toolUtil,this.tokenStream,this.end,this.currentTree,false);
			while(unLindex < tokenStream.getCurrentIndex()){
				TokenString tokenStr = tokenStream.getNext(unLindex);
				if(toolUtil.checkIsToken(tokenStr)){
					if(currentTree.getLeaf().getToken().containsKey(tokenStr.getId())){
						treeWorker.work(tokenStr);
						currentTree = treeWorker.getCurrentTree();
					}else{
						TreeWorker subTreeWorker = new TreeWorker(this.root,this.toolUtil,this.tokenStream,this.end,this.currentTree,true);
						subTreeWorker.work(tokenStr);
						currentTree = subTreeWorker.getCurrentTree();
					}
				}
				
				unLindex++;
			}
		}*/
	}

	protected RTree getCurrentTree() {
		return currentTree;
	}

	protected RTree getRoot() {
		return root;
	}
}
