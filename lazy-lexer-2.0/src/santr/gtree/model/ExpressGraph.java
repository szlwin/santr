package santr.gtree.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import santr.common.util.collections.SimpleList;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;

import javolution.util.FastMap;



public class ExpressGraph {

	private Map<Integer,List<LineInfo>> eGraph[]; 
	
	private List<Map<Integer,List<LineInfo>>> eGraphList = new ArrayList<Map<Integer,List<LineInfo>>>();

	private GTree root;
	public void convert(GrammarInfo grammarInfo){
		List<GTree> gList = grammarInfo.getTreeList();
		root = grammarInfo.getgTree();
		for(int i = 0; i < gList.size();i++){
			eGraphList.add(new FastMap<Integer,List<LineInfo>>());
		}
		
		for(int i = 0; i < gList.size();i++){
			
			GTree gTree = gList.get(i);
			if(gTree.getType() == GTYPE.EXPRESS
					|| gTree.getType() == GTYPE.LEAF){
				addPath(gTree,grammarInfo);
			}
		}
		
		for(int i = 0; i < gList.size();i++){
			GTree gTree = gList.get(i);
			if(gTree.getType() != GTYPE.NODE){
				convertTree(gTree);
			}
		}
		
		for(int i = 0; i < gList.size();i++){
			GTree gTree = gList.get(i);
			convertNode(gTree);
			

		}
		
		for(int i = 0; i < gList.size();i++){
			
			GTree gTree = gList.get(i);
			if(gTree.getType() != GTYPE.NODE){
				//gTree.println();
				eGraphList.set(gTree.getId(), gTree.getAllForkGraph());
			}
		}
		
		eGraph = new FastMap[eGraphList.size()];
		eGraphList.toArray(eGraph);
		//eGraphList.clear();
		//println();
	}
		
	public List<LineInfo> get(int id,int token){
		return eGraph[id].get(token);
	}
	
	private void addPath(GTree gTree,GrammarInfo grammarInfo){
		if(checkIsAllLbs(gTree)){
			copy(gTree,grammarInfo);
		}
	}
	
	private void copy(GTree gTree,GrammarInfo grammarInfo){
		GTree[] gTreeArray =  gTree.getgTreeArray();
		for(int i = 0; i < gTreeArray.length;i++){
			GTree tmpGTree = gTreeArray[i];
			if(tmpGTree.getType() == GTYPE.LBS && 
					tmpGTree.getRel().getType() == GTYPE.EXPRESS){
				
				//tmpGTree.setToken(tmpGTree.getRel().getToken());
				tmpGTree.setId(grammarInfo.getTreeList().size());

				grammarInfo.addTree(tmpGTree);
				gTree.addSubLBS(tmpGTree);
				eGraphList.add(new FastMap<Integer,List<LineInfo>>());
				//tmpGTree.getRel().addLBS(tmpGTree);
				//convertUp(tmpGTree);
				//convertDown(tmpGTree);
			}
		}
	}
	
	private boolean checkIsAllLbs(GTree gTree){
	
		if(gTree.getToken()!=null)
			return false;
		GTree[] gTreeArray =  gTree.getgTreeArray();
		if(gTreeArray == null){
			return false;
		}
		//System.out.println("checkIsAllLbs:"+gTree.getName());
		for(int i = 0; i < gTreeArray.length;i++){
			if(gTreeArray[i].getType() == GTYPE.EXPRESS){
				return false;
			}
		}
		return true;
	}
	
	private void convertTree(GTree gTree){
		convertUp(gTree);
		
		convertDown(gTree);
		
		convertOnline(gTree);
		
	}
	//private FastMap<GTree,String> gtreeMap = new FastMap<GTree,String>();
	private void addNode(GTree gTree,GTree upGTree, GTree start,FastMap<GTree,String> gtreeMap){
		//System.out.println("lbs   lbs:"+upGTree.getName()+",node:"+gTree.getName());
		if(upGTree == null){
			return;
		}
		boolean isAdd = true;
		if(upGTree.getToken()!=null ){
			isAdd = upGTree.getToken().getTflag()[0].getLbs().getGroup()>0;
		}
		if(!isAdd){
			return;
		}
		if(!gtreeMap.containsKey(upGTree)){
			gtreeMap.put(upGTree, null);
			if((upGTree.isExitFork() || upGTree == root))
				upGTree.addNode(gTree);
			
			if((upGTree.isExitFork()
					|| upGTree.getIndex() == 0
					|| upGTree.getGroupType() == GROUPTYPE.ZERO_OR_MANY
					|| upGTree.getGroupType() == GROUPTYPE.ZERO_OR_ONE)
					){
				addNode(gTree,upGTree.getParent(),start,gtreeMap);
			}
		}
		if(upGTree.getType() == GTYPE.EXPRESS){
			List<GTree> lbsList = upGTree.getLbsList();
			if(lbsList!=null){
				//System.out.println("lbs   lbs:"+upGTree.getName()+",node:"+gTree.getName());
				for(int i = 0; i< lbsList.size();i++){
					FastMap<GTree,String> nextgtreeMap = new FastMap<GTree,String>();
					nextgtreeMap.putAll(gtreeMap);
					addNode(gTree,lbsList.get(i),lbsList.get(i),nextgtreeMap);
				}
			}
		}

	}

	private void convertNode(GTree gTree){
		
		//System.out.println(gTree.getName());
		if(gTree.getType() == GTYPE.LBS){
			//if(gTree.getRel().getType() == GTYPE.LBS){
			//	System.out.println("lbs:"+gTree.getName());
			//}
			if(gTree.getRel().getNodeList() == null){
				return;
			}
			for(int i = 0; i < gTree.getRel().getNodeList().size();i++){
				GTree tmpGtree = gTree.getRel().getNodeList().get(i);
				
				//if(tmpGtree.getRel().getType() == GTYPE.NODE){
					addNode(tmpGtree,gTree,gTree,new FastMap<GTree,String>());
				//}
			}
			if(gTree.getRel().getSelfFirstList() == null){
				return;
			}
			
			for(int i = 0; i < gTree.getRel().getSelfFirstList().size();i++){
				GTree tmpGtree = gTree.getRel().getSelfFirstList().get(i);
				
				//if(tmpGtree.getRel().getType() == GTYPE.NODE){
					addNode(tmpGtree,gTree,gTree,new FastMap<GTree,String>());
				//}
			}
		}else{
			GTree gTreeArrT[] = gTree.getgTreeArray();
			if(gTreeArrT!=null){
				for(int i = 0; i < gTreeArrT.length;i++){
					GTree tmpGtree = gTreeArrT[i];
					
					if(tmpGtree.getRel().getType() == GTYPE.NODE){
						gTree.addNode(tmpGtree);
						
						if(tmpGtree.getParent().getgTreeArray().length  == 1){
							//start = gTree;
							addNode(tmpGtree,gTree,gTree.getParent(),new FastMap<GTree,String>());
						}else{
							if(i>0){
								continue;
							}
							boolean isSingle = true;
							GTree gTreeArr[] = tmpGtree.getParent().getgTreeArray();
							for(int j  = 1; j < gTreeArr.length;j++){
								if(gTreeArr[j].getGroup() ==0 && gTreeArr[j].getRel().getType()!=GTYPE.NODE){
									isSingle = false;
									break;
								}
							}
							if(isSingle){
								//start = gTree;
								addNode(tmpGtree,gTree,gTree.getParent(),new FastMap<GTree,String>());
							}
						}
							
					}
				}
			}
		
			/*GTree gTreeArrT[] = gTree.getgTreeArray();
			if(gTreeArrT!=null){
				for(int i = 0; i < gTreeArrT.length;i++){
					this.search(gTree, gTreeArrT[i]);
				}
			}*/
		}
	}
	
	private TFlag[] getFirstToken(TFlag flagArr[]){
		List<TFlag> tFlagList = new ArrayList<TFlag>();
		TFlag first = flagArr[0];
		tFlagList.add(first);
		
		for(int i = 1; i < flagArr.length;i++){
			TFlag tFlag = flagArr[i];
			if(tFlag.getLbs() == first.getLbs()){
				tFlagList.add(tFlag);
			}
		}
		TFlag firstFlagArr[] = new TFlag[tFlagList.size()];
		
		tFlagList.toArray(firstFlagArr);
		
		return firstFlagArr;
	}
	
	private TFlag[] getFirstTokenOfGroup(GTree start,TFlag flagArr[]){
		List<TFlag> tFlagList = new ArrayList<TFlag>();
		TFlag first = null;
		//tFlagList.add(first);
		
		for(int i = 0; i < flagArr.length;i++){
			TFlag tFlag = flagArr[i];
			if(first == null){
				if(tFlag.getLbs() ==  start){
					tFlagList.add(tFlag);
					first = tFlag;
				}
			}else{
				if(tFlag.getLbs() == first.getLbs() 
						|| tFlag.getLbs().getGroup() > first.getLbs().getGroup()){
					tFlagList.add(tFlag);
					first = tFlag;
				}
			}
		}
		TFlag firstFlagArr[] = new TFlag[tFlagList.size()];
		
		tFlagList.toArray(firstFlagArr);
		
		return firstFlagArr;
	}
	
	private void convertOnline(GTree gTree){
		if(gTree.getType() == GTYPE.NODE
				|| gTree.getType() == GTYPE.LBS){
			return;
		}

		if(gTree.isExitFork()){
			GTree gTreeArr[] = gTree.getgTreeArray();
			for(GTree leaf: gTreeArr){
				if(leaf.getToken()!=null){
					TFlag flagArr[] = getFirstToken(leaf.getToken().getTflag());
					
					LineInfo[] lineArr = this.createLine(gTree.getId(), flagArr, gTree.getId(), 0, leaf.getId(),leaf);
					
					this.addLine(lineArr, gTree);
				}else{
					if(gTree.getType() != GTYPE.LBS){
						continue;
					}
					GTree rel = leaf.getRel();
					GTree gTreeNArr[] = gTree.getgTreeArray();
					for(GTree nleaf: gTreeNArr){
						if(nleaf.getToken()!=null){
							TFlag flagArr[] = getFirstToken(nleaf.getToken().getTflag());
							
							LineInfo[] lineArr = this.createLine(gTree.getId(), flagArr, rel.getId(), 0, nleaf.getId(),nleaf);
							
							this.addLine(lineArr, gTree);
						}
					}
				}
			}
		}else{
			GTree gTreeArray[] = gTree.getgTreeArray();
			//System.out.println(gTree.getId()+":"+gTree.getName()+":"+gTree.getType());
			GTree last = gTreeArray[gTreeArray.length-1];
			last.setLast(last.getGroup() == 0 
					|| last.getGroupType() == GROUPTYPE.ZERO_OR_ONE);
			
			if(gTree.getToken() == null){
				return;
			}
			TFlag flagArr[] = gTree.getToken().getTflag();
			
			LineInfo[] lineArr = this.createLine(gTree.getId(), flagArr, gTree.getId(), 0, -1,gTree);
			
			this.addLine(lineArr, gTree);
			
			/*GTree gTreeArrT[] = gTree.getgTreeArray();
			if(gTreeArrT!=null){
				for(int i = 0; i < gTreeArrT.length;i++){
					GTree tmpGtree = gTreeArrT[i];
					
					if(tmpGtree.getRel().getType() == GTYPE.NODE){
						addNode(tmpGtree,gTree);
					}
				}
			}*/
		}
	}

	private boolean checkIsClose(GTree gTree){
		GTree gtreeArr[] = gTree.getgTreeArray();
		if(gtreeArr == null){
			return false;
		}
		GTree last = gtreeArr[gtreeArr.length-1];
		
		return last.getType() == GTYPE.TOKEN
				&& last.isLast();
	}
	
	private void convertUp(GTree gTree){
		if(gTree.getParent() == null
				|| gTree.getType() == GTYPE.LEAF){
			return;
		}
		
		GTree rel = gTree;
		if(gTree.getType() == GTYPE.LBS){
			rel = gTree.getRel();
		}

		if(!rel.isExitFork()
				&& checkIsClose(rel)){
			return;
		}

		if(!isLastOfGroup(gTree)){
			return;
		}
		//A->A1->B->B1->C->C1
		//parent == B1 || B
		GTree parent = gTree.getParent();

		//parent == B
		if(parent.getType() == GTYPE.LEAF){
			parent = parent.getParent();
		}

		GTree token = parent.getNext();
		if(token == null){
			return;
		}
		if(token.getType() == GTYPE.TOKEN){
			TFlag tFlagArr[] = parent.getParent().getToken().getTflag();
			
			TFlag tFlagDestArr[] = getFirstTokenOfGroup(token,tFlagArr);
			
			if(!gTree.isExitFork()){
				LineInfo[] lineArr = this.createLine(rel.getId(), tFlagDestArr, parent.getParent().getId(), -1, -1,parent.getParent());
				addLine(lineArr,rel);
			}else{
				GTree gTreeArr[] = rel.getgTreeArray();
				
				for(GTree leaf: gTreeArr){
					if(checkIsClose(leaf)){
						continue;
					}
					LineInfo[] lineArr = this.createLine(leaf.getId(), tFlagDestArr, parent.getParent().getId(), -1, -1,parent.getParent());
					//LineInfo[] lineArr = this.createLine(rel.getId(), tFlagDestArr, parent.getParent().getId(), -1, leaf.getId(),parent.getParent());
					
					
					addLine(lineArr,leaf);
				}
			}
		}
	}
	
	private void addLine(LineInfo[] lineArr,GTree gTree){
		for(LineInfo lineInfo: lineArr){
			gTree.add(lineInfo.getId(), lineInfo);
		}
	}
	
	private boolean isLastOfGroup(GTree gTree){
		if(gTree.isLast()){
			return true;
		}
		GTree nextGTree = gTree.getNext();
		
		return nextGTree == null || gTree.getGroup()!=nextGTree.getGroup();
	}
	
	private void convertDown(GTree gTree){
		//System.out.println(gTree.getId()+":"+gTree.getName());
		GTree rel = gTree;
		if(gTree.getType() == GTYPE.LBS){
			//rel = gTree.getRel();
			return;
		}
		
		if(gTree.getParent() == null
				|| rel.getType() == GTYPE.LEAF
				|| rel.getToken() == null){
			return;
		}
		
		//A->A1->B->B1->C->C1
		//parent ==  A1 || A
		GTree parent = gTree.getParent();
		boolean isLeaf = false;
		GTree leaf = null;
		if(parent.getType() == GTYPE.LEAF){
			//parent == A1 -> A
			leaf = parent;
			parent = parent.getParent();
			isLeaf = true;
		}
		
		GTree first = parent;
		if(parent == null){
			return;
		}
		
		if(parent.getType() == GTYPE.LEAF){
			first = parent.getParent();
			
		}
		int id = -1;
		if(leaf != null){
			id = leaf.getId();
		}
		LineInfo[] lineArr = createLine(first.getId(),
				this.getFirstToken(gTree.getToken().getTflag()),
				rel.getId(),1,id,rel);
		addLine(lineArr,first);
		
		if(isLeaf){
			Token token = leaf.getToken();
			
			if(isNeedUp(token)){
				GTree nextParent = leaf.getParent().getParent();
				if(nextParent == null){
					return;
				}
				//System.out.println(leaf.getParent().getName()+""+leaf.getParent().getId());
				if(isNeedUp(nextParent.getToken())){
					LineInfo[] lineArray = createLine(nextParent.getId(),
							this.getFirstToken(gTree.getToken().getTflag()),
							rel.getId(),1,id,rel);
					addLine(lineArray,nextParent);
				}
				
				if(nextParent.getType() == GTYPE.LEAF){
					GTree nextUPParent = nextParent.getParent();
					LineInfo[] lineArray = createLine(nextUPParent.getId(),
							this.getFirstToken(gTree.getToken().getTflag()),
							rel.getId(),1,nextParent.getId(),rel);
					addLine(lineArray,nextUPParent);
				}
			}
			

		}
		//A->A1->B->B1->C->C1
		//parent == B1 || B
		/*
		GTree parent = gTree.getParent();

		
		if(parent.getType() == GTYPE.LEAF){
			//parent == B1 -> A1
			parent = parent.getParent().getParent();
		}else{
			//parent == B -> A1
			parent = parent.getParent();
		}
		GTree first = parent;
		if(parent == null){
			return;
		}
		if(parent.getType() == GTYPE.LEAF){
			first = parent.getParent();
		}
		LineInfo[] lineArr = createLine(first.getId(),
				this.getFirstToken(gTree.getToken().getTflag()),
				rel.getId(),1,-1,rel);
		addLine(lineArr,first);*/
		
	}
	private boolean isNeedUp(Token token){
		if(token == null){
			return true;
		}else{
			TFlag tFlagArr[] = token.getTflag();
			for(TFlag tFlag:tFlagArr){
				if(tFlag.getLbs().getGroup()==0){
					return false;
				}
			}
		}
		return true;
	}
	private LineInfo[] createLine(int start,TFlag[] flag,int end, int deep,int lid,GTree express){
		LineInfo[] linArr = new LineInfo[flag.length];
		for(int i = 0; i < flag.length; i++){
			linArr[i] = new LineInfo();
			linArr[i].setStart(start);
			linArr[i].setEnd(end);
			linArr[i].setId(flag[i].getId());
			linArr[i].setToken(flag[i].getTokenFlag());
			linArr[i].setExpress(express);
			if(deep >0)
				linArr[i].setDeep(1);
			else if(deep<0)
				linArr[i].setDeep(-1);
			else
				linArr[i].setDeep(0);
			linArr[i].setLeafId(lid);
		}
		return linArr;
		
	}
	
	public void addLine(int id,LineInfo[] lineArray){

		Map<Integer,List<LineInfo>> lineMap = eGraphList.get(id);
		if(lineMap == null){
			lineMap =  new FastMap<Integer,List<LineInfo>>();
			eGraph[id]=lineMap;
		}

		for(int i = 0; i < lineArray.length;i++){
			if(!lineMap.containsKey(lineArray[i].getId())){
				List<LineInfo> list = new SimpleList<LineInfo>();
				list.add(lineArray[i]);
				lineMap.put(lineArray[i].getId(), list);
			}else{
				List<LineInfo> list = lineMap.get(lineArray[i].getId());
				boolean isExist =false;
				for(int j = 0; j < list.size();j++){
					LineInfo lInfo = list.get(j);
					if(lInfo.getToken().equals(lineArray[i].getToken())
							&& lInfo.getEnd() == lineArray[i].getEnd()
							&& lInfo.getLeafId() == lineArray[i].getLeafId()){
						isExist = true;
						break;
					}
				}
				if(!isExist){
					list.add(lineArray[i]);
				}
			}
			
		}
	}
	
	private void println(){
		for(int i = 0;i < eGraph.length;i++){
			Collection<List<LineInfo>> c = eGraph[i].values();
			Iterator<List<LineInfo>> it = c.iterator();
			while(it.hasNext()){
				List<LineInfo> lineInfoList = it.next();
				for(int j = 0; j < lineInfoList.size();j++){
					LineInfo lineInfo = lineInfoList.get(j);
					System.out.println("start="+lineInfo.getStart()
							+",end="+lineInfo.getEnd()
							+",token="+lineInfo.getToken()
							+",deep="+lineInfo.getDeep()
							+",leaf="+lineInfo.getLeafId());
				}

			}
		}
	}

}
