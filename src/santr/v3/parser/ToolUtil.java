package santr.v3.parser;

import java.util.List;

import santr.gtree.model.BData;
import santr.gtree.model.GTree;
import santr.gtree.model.GrammarInfo;
import santr.gtree.model.LineInfo;
import santr.gtree.model.Runner;
import santr.gtree.model.TFlag;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.gtree.model.enume.TOKENTYPE;
import santr.parser.exception.ParserInvaildException;
import santr.v3.parser.data.RTree;

import javolution.util.FastTable;


class ToolUtil {
	
	private GrammarInfo grammarInfo;
	
	private TokenString tokenStr;
	
	private TokenStream tokenStream;
	
	protected ToolUtil(GrammarInfo grammarInfo, TokenStream tokenStream){
		this.grammarInfo = grammarInfo;
		this.tokenStream = tokenStream;
	}
	
	protected void setTokenString(TokenString tokenStr){
		this.tokenStr = tokenStr;
	}
	
	protected TokenString getTokenString(){
		return this.tokenStr;
	}
	
	protected String getToken(){
		return this.tokenStr.getText();
	}
	
	protected boolean isEmpty(RTree rTree){
		return rTree.getrTreeList().isEmpty();
	}
	
	protected void setLeafByExpress(RTree rTree){
		GTree gTree = grammarInfo.getgTree();
		if(gTree.getToken() != null)
			rTree.setLeaf(gTree);
	}
	
	protected boolean checkIsToken(){
		return tokenStr.getType() == TOKENTYPE.TOKEN;
		
	}

	protected RTree createTokenTree(GTree cleaf,TokenString tokenStr){
		RTree rTree =  createTokenTree(tokenStr);
		
		int index = cleaf.getToken().getTFlag(tokenStr.getId()).getLbs().getIndex();
		rTree.setLbs(cleaf.getgTreeArray()[index]);
		
		return rTree;
	}
	
	protected void createNode(RTree rTree,TokenString tokenStr){
		
		//rTree.setStart(tokenStr.getStart());
		//rTree.setEnd(tokenStr.getEnd());
		if(tokenStr.getType() == TOKENTYPE.TOKEN){
			rTree.setType(BData.FLAG_TYPE_TOKEN);
		}else{
			rTree.setType(BData.FLAG_TYPE_NODE);
		}
		
		rTree.setText(tokenStr.getText());
		tokenStr.setLexer(true);
		this.tokenStream.setLexerIndex(tokenStr.getIndex());
		
	}
	
	protected RTree createTokenTree(TokenString tokenStr){
		RTree rTree = new RTree();
		
		//rTree.setStart(tokenStr.getStart());
		//rTree.setEnd(tokenStr.getEnd());
		if(tokenStr.getType() == TOKENTYPE.TOKEN){
			rTree.setType(BData.FLAG_TYPE_TOKEN);
		}else{
			rTree.setType(BData.FLAG_TYPE_NODE);
		}
		
		rTree.setText(tokenStr.getText());
		tokenStr.setLexer(true);
		this.tokenStream.setLexerIndex(tokenStr.getIndex());
		return rTree;
		
	}
	
	protected boolean isLastTree(GTree lbs) {
		GTree parentTree = lbs.getParent();
		int index = parentTree
				.getgTreeArray()
				.length-1;
		return  (lbs.getGroup() == 0 || lbs.getGroupType() == GROUPTYPE.ONE_OR_MANY)
				&& parentTree.getgTreeArray()[index] == lbs ;
	}
	
	protected RTree getNextExpressTree(GTree lbs) {
		GTree parentTree = lbs.getParent();
		
		GTree gTreeArr[] =  parentTree.getgTreeArray();
		if(lbs.getIndex() < gTreeArr.length-1){
			GTree gTree = gTreeArr[lbs.getIndex()+1];
			
			if(gTree.getType() == GTYPE.TOKEN){
				return null;
			}
			
			if(gTree.getRel().getType() == GTYPE.NODE){
				return null;
			}
			if(//gTree.getType() != GTYPE.TOKEN &&
				//	 gTree.getType() != GTYPE.NODE &&
					 gTree.getGroup()==lbs.getGroup()){
				RTree rTree = new RTree();;
				rTree.setType(BData.FLAG_TYPE_VAR);
				rTree.setLbs(gTree);
				
				GTree gLbs = gTree.getRel();
				if(gLbs.getToken()!=null){
					rTree.setLeaf(gLbs);
				}
				//rTree.setLeaf(this.getExpressTree(grammarInfo,gTree.getName()));
				rTree.setName(gTree.getName());
				
				return rTree;
			}
		}
		
		return null;
	}
	
	protected RTree createNodeTree(RTree rTree,TokenString tokenString) throws ParserInvaildException 
	{
		GTree gTree = null;;

		GTree nextLBS = null;
		if(!rTree.getrTreeList().isEmpty()){
			GTree preLBS = rTree.getLast()
					.getLbs();
			
			gTree = rTree.getLeaf();
			
			nextLBS = gTree.getgTreeArray()[preLBS.getIndex()+1];

		}else{
			gTree = rTree.getLbs().getRel();
			nextLBS = gTree.getgTreeArray()[0];
		}
		
		GTYPE gType = nextLBS.getRel().getType();

		if((nextLBS.getGroupType() == GROUPTYPE.ZERO_OR_MANY
				|| nextLBS.getGroupType() == GROUPTYPE.ZERO_OR_ONE)
				&& gType == GTYPE.EXPRESS){
			return null;
		}
		

		if(gTree.getType() == GTYPE.LEAF){
			gTree = gTree.getParent();
		}
		
		GTree lbs = gTree
				.getNodeByToken(tokenString);
		
		if(lbs == null){
			throw new ParserInvaildException(getTokenString());
		}
		
		
		if(gType == GTYPE.NODE
				//||lbs.getParent().getName()
				//.equals(nextLBS.getName())
				//|| lbs.getParent().getName()
				//.equals(rTree.getName())
				){
			RTree nodeTree = createTokenTree(tokenString);
			nodeTree.setName(lbs.getName());
			nodeTree.setLeaf(lbs.getRel());
			nodeTree.setLbs(nextLBS);
			return nodeTree;
		}

		TreeInfo treeInfo = createSubTree(lbs,rTree);

		//RTree nodeTree = createTokenTree(tokenString);
		//treeInfo.getLast().addRTree(nodeTree);
		this.createNode(treeInfo.getLast(), tokenString);
		treeInfo.getLast().setLeaf(lbs.getRel());
		rTree.setLeaf(lbs.getParent().getRel());
		treeInfo.getLast().setLbs(lbs);
		
		
		treeInfo.getLast().setName(lbs.getName());

		
		return treeInfo.getTree();
		
	}
	
	protected RTree createNodeTree(RTree rTree) 
			throws ParserInvaildException 
			{
		
		return createNodeTree(rTree,tokenStr);
	}
	
	protected boolean checkExpress(RTree preTree,GTree checkLBS,RTree parentTree){
		//if(checkLBS.getFlag() > parentTree.getrTreeList().size()){
		//	return false;
		//}
		GTree preLBS = preTree.getLbs();
		int checkFlag = checkLBS.getFlag() - preLBS.getFlag();
		if(checkFlag >1)
			return false;

		if(checkFlag ==1 ){
			//GTree exPreLBS = checkLBS.getParent().getgTreeArray()[checkLBS.getIndex()-1];

			return preLBS.getGroup() == 0
					|| preLBS.getGroup() == checkLBS.getGroup();
			//return preLBS == exPreLBS;
		}
		else{
			int index = parentTree.getrTreeList().size()-1+checkFlag;
			GTree exPreLBS = parentTree.getrTreeList().get(index).getLbs();
			if(checkLBS == exPreLBS && checkLBS.getGroup() == 0){
				return false;
			}
			return checkLBS.getFlag() == exPreLBS.getFlag() 
					&& (preLBS.getGroup() != checkLBS.getGroup()
							|| preLBS.getGroupType() != GROUPTYPE.ZERO_OR_ONE);
		}
	}
	
	protected RTree createPreNodeTree(RTree rTree) throws ParserInvaildException 
	{
		if(tokenStr.getIndex() == 0){
			throw new ParserInvaildException(getTokenString());
		}
		TokenString preTokenStr  = this.tokenStream.getNext(tokenStr.getIndex()-1);
		if(preTokenStr.isLexer()){
			return null;
		}
		return createNodeTree(rTree,
				preTokenStr);
	}
	
	protected List<RTree> createAllPreTree(GTree cleaf) throws ParserInvaildException{
		List<RTree> rTreeList =  new FastTable<RTree>();
		int unLindex = tokenStream.getLexerIndex()+1;
		int count = 0;
		while(unLindex < tokenStream.getCurrentIndex()-1){
			TokenString tmpToken = tokenStream.getNext(unLindex);
			

			if(tmpToken.getType() == TOKENTYPE.TOKEN){
				RTree rTree = createTokenTree(tmpToken);
				rTree.setLbs(cleaf.getgTreeArray()[count]);
				rTreeList.add(rTree);
			}else{
				GTree lbs = cleaf.getgTreeArray()[count];
				GTree gTree = lbs.getRel();

				if(gTree.getType() == GTYPE.NODE){
					RTree rTree = this.createTokenTree(tmpToken);
					rTree.setName(gTree.getName());
					rTree.setLbs(lbs);
					rTreeList.add(rTree);
				}else{
					RTree rTree = new RTree();
					rTree.setName(gTree.getName());
					rTree.setLbs(lbs);
					rTree.setType(BData.FLAG_TYPE_VAR);
					RTree nodeTree = createNodeTree(rTree,
							tmpToken);
					rTree.addRTree(nodeTree);
					rTreeList.add(rTree);
				}
			}

			count++;
			unLindex++;
		}
		return rTreeList;
		
	}
	protected RTree createPreTree(GTree cleaf) throws ParserInvaildException 
	{
		TFlag tFlag = cleaf.getToken().getTFlag(tokenStr.getId());
		int index = tFlag.getLbs().getIndex();
		if(index>0){
			if(tokenStr.getIndex() == 0){
				throw new ParserInvaildException(getTokenString());
			}
			TokenString preTokenStr  = this.tokenStream.getNext(tokenStr.getIndex()-1);
			if(preTokenStr.isLexer()){
				return null;
			}
			
			GTree glbs = cleaf.getgTreeArray()[index-1];
			GTree lbs = glbs.getRel();
			
			if(lbs.getType() == GTYPE.NODE){
				RTree noderTree = this.createTokenTree(preTokenStr);
				noderTree.setName(lbs.getName());
				noderTree.setLbs(glbs);
				noderTree.setLeaf(lbs);
				return noderTree;
			}
			
			RTree preTree = new RTree();
			
			preTree.setName(cleaf.getgTreeArray()[index-1].getName());
			preTree.setLbs(cleaf.getgTreeArray()[index-1]);
			
			
			RTree nodeTree = createNodeTree(preTree,
					preTokenStr);
			preTree.addRTree(nodeTree);
			return preTree;
		}
		return null;
	}
	
	protected TreeInfo createSubTree(GTree cleaf,RTree prTree){
		int id = -1;
		if(prTree.getLeaf() ==null){
			id = prTree.getLbs().getRel().getId();
		}else{
			id = prTree.getLeaf().getId();
		}
		
		TreeInfo treeInfo = new TreeInfo();
		GTree tmpcleaf = cleaf;
		RTree rTree = null;
		RTree preTree = null;
		RTree last = null;

		while(tmpcleaf.getId()!=id){
			RTree tmpRtree = new RTree();
			tmpRtree.setLeaf(tmpcleaf);
			if(tmpcleaf.getType() == GTYPE.LEAF){
				tmpcleaf = tmpcleaf.getParent();
			}
			if(tmpcleaf.getId()==id){
				break;
			}
			
			tmpRtree.setName(tmpcleaf.getName());
			//tmpRtree.setStart(tokenStr.getStart());
			//tmpRtree.setEnd(tokenStr.getEnd());
			tmpRtree.setType(BData.FLAG_TYPE_VARN);
			
			//if(cleaf.getType() == GTYPE.LEAF){
			//	tmpRtree.setLbs(tmpcleaf.getParent());
			//}else{
				tmpRtree.setLbs(tmpcleaf);
			//}
			if(last == null){
				last = tmpRtree;
			}
			if(preTree == null){
				preTree = tmpRtree;
			}else{
				tmpRtree.addRTree(preTree);
				preTree = tmpRtree;
			}
			rTree = tmpRtree;
			if(tmpcleaf.getParent() == null){
				break;
			}

			tmpcleaf = tmpcleaf.getParent();
		}
		
		treeInfo.setLast(last);
		treeInfo.setTree(rTree);
		return treeInfo;
		
	}
	
	protected List<LineInfo> getLineInfo(RTree rTree){
		GTree gLbs = rTree.getLbs().getRel();
		//GTree gTree = grammarInfo.getTree(gLbs.getId());
		
		return grammarInfo.get(gLbs.getId(),tokenStr.getId());
	}
	
	protected GTree getTree(LineInfo lineInfo) {
		int id = lineInfo.getLeafId();
		if(id < 0 ){
			id = lineInfo.getEnd();
		}
		GTree gtree =  grammarInfo.getTree(id);

		return gtree;
	}
	
	protected boolean checkMatch(LineInfo lineInfo) {
		GTree pGtree =  grammarInfo.getTree(lineInfo.getEnd());
		int count = tokenStream.getCurrentIndex() - tokenStream.getLexerIndex();
		if(pGtree.getToken() == null){
			GTree gTreeArr[] = pGtree.getgTreeArray();
			if(gTreeArr !=null){
				for(int i = 0; i < gTreeArr.length;i++){
					GTree gTree = gTreeArr[i];
					if(gTree.getToken()!=null
							&& gTree.getgTreeArray().length>count){
						if(checkMatch(gTree)){
							return true;
						}
					}
				}
			}
		}else{
			return checkMatch(pGtree);
		}
		return false;
	}
	
	protected boolean checkMatch(GTree pGtree) {
		GTree parentTree = pGtree.getRel();
		GTree gTreeArr[] = parentTree.getgTreeArray();
		
		
		int count = tokenStream.getCurrentIndex() - tokenStream.getLexerIndex()-1;
		int unLindex = tokenStream.getCurrentIndex()-1;
		boolean isMatch = true;
		
		while(count > 0){
			TokenString token = tokenStream.getNext(unLindex);
			if(token.getType() != TOKENTYPE.TOKEN){
				unLindex--;
				count--;
				continue;
			}
			GTree gTree = gTreeArr[count-1].getRel();
			
			if(token.getType() == TOKENTYPE.TOKEN 
					&& gTree.getType() != GTYPE.TOKEN){
				isMatch = false;
				break;
			}
			
			/*if((token.getType() == TOKENTYPE.STR 
					|| token.getType() == TOKENTYPE.FLAGSTR)
					&& gTree.getType() != GTYPE.NODE){
				isMatch = false;
				break;
			}*/
			if(token.getType() == TOKENTYPE.TOKEN){
				
				TFlag tflag = parentTree.getToken().getTFlag(token.getId());
				if(tflag == null 
						|| tflag.getLbs().getFlag()!=count-1){
					isMatch = false;
					break;
				}
				
			}
			/*else{
				GTree gtree = gTree.getNodeByToken(token);
				if(gtree == null){
					isMatch = false;
					break;
				}
			}*/
			
			unLindex--;
			count--;
		}
		//tokenStream.setLexerIndex(unLindex);
		return isMatch;
	}

	public boolean checkIsToken(TokenString tokenStr) {
		return tokenStr.getType()==TOKENTYPE.TOKEN;
	}

	public void scan(RTree rTree) {
		GTree gLbs = rTree.getLbs().getRel();
		GTree gTree = grammarInfo.getTree(gLbs.getId());
		PathScaner pathScaner = new PathScaner();
		pathScaner.scan(rTree, tokenStr.getId(), tokenStr.getText());
		List<LineInfo> lineList = pathScaner.getLineInfoList();
		LineInfo[] lineArray = new LineInfo[lineList.size()];
		lineList.toArray(lineArray);
		this.grammarInfo.getExpressGraph().addLine(gTree.getId(), lineArray);
	}

	public TreeInfo createSubTree(RTree prTree,Runner runner) {
		List<GTree> gList = runner.getgTreeList();
		int index = gList.size()-1;
		int flag = 0;
		if(gList.get(0).getName().equals(prTree.getName())){
			flag++;
		}
		TreeInfo treeInfo = new TreeInfo();
		GTree tmpcleaf = null;//gList.get(index);
		RTree rTree = null;
		RTree preTree = null;
		RTree last = null;
		
		

		while(index >= flag){
			tmpcleaf = gList.get(index);
			
			/*if(prTree.getName().equals(tmpcleaf.getName())){
				if(prTree.getLeaf() == null){
					prTree.setLeaf(tmpcleaf);
				}
				break;
			}*/
			
			RTree tmpRtree = new RTree();
			tmpRtree.setLeaf(tmpcleaf);
			//System.out.println("node"+tmpcleaf.getId()+" : "+tmpcleaf.getName());
			if(tmpcleaf.getType() == GTYPE.LEAF){
				if(index > 0){
					index--;
					
					tmpcleaf = gList.get(index);
				}else{
					tmpcleaf = tmpcleaf.getParent();
				}

			}

			
			tmpRtree.setName(tmpcleaf.getName());

			tmpRtree.setType(BData.FLAG_TYPE_VARN);
			
			tmpRtree.setLbs(tmpcleaf);
			
			if(last == null){
				last = tmpRtree;
			}
			if(preTree == null){
				preTree = tmpRtree;
			}else{
				tmpRtree.addRTree(preTree);
				preTree = tmpRtree;
			}
			rTree = tmpRtree;


			index --;
			
		}

		treeInfo.setLast(last);
		treeInfo.setTree(rTree);
		return treeInfo;
	}
	
}
