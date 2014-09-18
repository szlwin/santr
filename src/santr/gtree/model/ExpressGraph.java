package santr.gtree.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import santr.gtree.model.enume.GTYPE;

import javolution.util.FastMap;
import javolution.util.FastTable;



public class ExpressGraph {

	private FastMap<Integer,List<LineInfo>> eGraph[]; 
	
	private List<FastMap<Integer,List<LineInfo>>> eGraphList = new ArrayList<FastMap<Integer,List<LineInfo>>>();
	private String root;
	public void convert(GrammarInfo grammarInfo){
		List<GTree> gList = grammarInfo.getTreeList();
		
		//this.gTreeList = c;
		root = gList.get(0).getName();
		for(int i = 0; i < gList.size();i++){
			eGraphList.add(new FastMap<Integer,List<LineInfo>>());
		}
		
		for(int i = 0; i < gList.size();i++){
		
			GTree gTree = gList.get(i);
			convertTree(gTree);
			
		}
		for(int i = 0; i < gList.size();i++){
			
			GTree gTree = gList.get(i);
			if(gTree.getType() == GTYPE.EXPRESS
					|| gTree.getType() == GTYPE.LEAF){
				addPath(gTree,grammarInfo);
			}
			
			
		}
		/*for(int i = 0; i < gList.size();i++){
			
			GTree gTree = gList.get(i);
			convertNode(gTree);
			
			
		}*/
		
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
	/*
	private void addSubPath(GTree gTree,GTree subTree,String name){
		GTree[] gTreeArray =  gTree.getgTreeArray();
		if(gTreeArray == null){
			return;
		}
		for(int i = 0; i < gTreeArray.length;i++){
			GTree tmpGTree = gTreeArray[i];
			if(!name.equals(tmpGTree.getName())
					&& (tmpGTree.getType() == GTYPE.EXPRESS
					|| tmpGTree.getType() == GTYPE.LEAF)){
				addSubPath(tmpGTree,subTree,name);
				convertUp(tmpGTree,tmpGTree.getParent(),1,subTree);
				convertUp(tmpGTree,subTree.getParent(),1,null);
			}
			
		}
	}*/
	private void copy(GTree gTree,GrammarInfo grammarInfo){
		GTree[] gTreeArray =  gTree.getgTreeArray();
		for(int i = 0; i < gTreeArray.length;i++){
			GTree tmpGTree = gTreeArray[i];
			if(tmpGTree.getType() == GTYPE.LBS && 
					tmpGTree.getRel().getType() == GTYPE.EXPRESS){
				
				
				tmpGTree.setToken(tmpGTree.getRel().getToken());
				tmpGTree.setId(grammarInfo.getTreeList().size());
				
				grammarInfo.addTree(tmpGTree);
				eGraphList.add(new FastMap<Integer,List<LineInfo>>());
				convertUp(tmpGTree,tmpGTree.getParent(),1,null);
			}
		}
	}
	
	private boolean checkIsAllLbs(GTree gTree){
		if(gTree.getToken()!=null)
			return false;
		GTree[] gTreeArray =  gTree.getgTreeArray();
		for(int i = 0; i < gTreeArray.length;i++){
			if(gTreeArray[i].getType() == GTYPE.EXPRESS){
				return false;
			}
		}
		return true;
	}
	
	private void convertTree(GTree gTree){
		convertUp(gTree,gTree.getParent(),1,null);
		
		convertDown(gTree,gTree,0);
		
		convertOnline(gTree);
		
	}
	
	private void addNode(GTree gTree,GTree upGTree){
		if(upGTree != null){
			upGTree.addNode(gTree);
			addNode(gTree,upGTree.getParent());
		}
	}
	private void convertNode(GTree gTree){
		GTree gTreeArrT[] = gTree.getgTreeArray();
		if(gTreeArrT!=null){
			for(int i = 0; i < gTreeArrT.length;i++){
				GTree tmpGtree = gTreeArrT[i];
				
				if(tmpGtree.getRel().getType() == GTYPE.NODE){
					addNode(tmpGtree,gTree);
				}
			}
		}
	}
	
	private void convertOnline(GTree gTree){
		GTree gTreeArrT[] = gTree.getgTreeArray();
		if(gTreeArrT!=null){
			for(int i = 0; i < gTreeArrT.length;i++){
				GTree tmpGtree = gTreeArrT[i];
				
				if(tmpGtree.getRel().getType() == GTYPE.NODE){
					addNode(tmpGtree,gTree);
				}
				/*
				if(tmpGtree.getType() == GTYPE.LBS){
					tmpGtree = tmpGtree.getRel();
				}
				
				if(tmpGtree.getType() == GTYPE.NODE){
					addNode(tmpGtree,gTree);
				}*/
			}
		}
		if(gTree.getToken()!=null){
			TFlag tFlagArr[] = gTree.getToken().getTflag();
			
			for(int i = 0 ; i < tFlagArr.length;i++){
				GTree gTreeArr[] = gTree.getgTreeArray();
				
				TFlag tflag = tFlagArr[i];
				GTree lbs = tflag.getLbs();
				int index = lbs.getIndex() - 1;
				int skipGroup = -1;
				for(int k = index; k >=0;k--){
					GTree preGTree =  gTreeArr[k];
					if(preGTree.getType() == GTYPE.TOKEN){
						if(preGTree.getGroup() != lbs.getGroup()){
							skipGroup = preGTree.getGroup();
							continue;
						}else{
							break;
						}
					}else{
						if(preGTree.getGroup() != skipGroup){
							GTree tmp = gTreeArr[k];
							if(tmp.getType() == GTYPE.LBS){
								tmp = tmp.getRel();
							}
							GTree tmpParent = gTree;
							if(gTree.getType() == GTYPE.LEAF){
								tmpParent = gTree.getParent();
							}
							convertEDown(tmp,tFlagArr[i],tmpParent,0,gTree);
							
							if(lbs.getGroup() == preGTree.getGroup()){
								break;
							}
						}

					}
				}
				if(gTree.getType() == GTYPE.EXPRESS){
					convertToLine(gTree.getId(),new TFlag[]{tFlagArr[i]},gTree.getId(),0);
				}else if(gTree.getType() == GTYPE.LEAF){
					convertToLine(gTree.getParent().getId(),new TFlag[]{tFlagArr[i]},gTree.getParent().getId(),0,gTree.getId());
				}
			}
		}else{
			if(gTree.getType() == GTYPE.EXPRESS){
				GTree gTreeArr[] = gTree.getgTreeArray();
				if(gTreeArr != null){
					for(int i = 0;i < gTreeArr.length;i++){

						convertOnline(gTreeArr[i]);
					}
				}
			}
		}
	}
	
	private void convertEDown(GTree gTree, TFlag tFlag, GTree parentGTree, int deep, GTree leaf) {
		int tmpDeep = deep-1;
		if(leaf.getType() == GTYPE.LEAF){
			convertToLine(gTree.getId(),new TFlag[]{tFlag},parentGTree.getId(),tmpDeep,leaf.getId());
		}else{
			convertToLine(gTree.getId(),new TFlag[]{tFlag},parentGTree.getId(),tmpDeep);
		}
		
		if(gTree.getId() == parentGTree.getId()){
			return;
		}
		GTree gTreeArr[] = gTree.getgTreeArray();
		if(gTreeArr != null){
			for(int i = 0;i < gTreeArr.length;i++){
				if(gTreeArr[i].getType() == GTYPE.EXPRESS){
					convertEDown(gTreeArr[i],tFlag,parentGTree,tmpDeep,leaf);
				}
			}
		}

	}

	private void convertUp(GTree gTree,GTree upGTree,int deep, GTree subTree){
		
		if(upGTree != null){
			GTree gTreeArr[] = gTree.getgTreeArray();
			if(gTreeArr !=null && 
					gTreeArr[0].getType() == GTYPE.LEAF
					&& gTree.getType() == GTYPE.EXPRESS){
				for(int i = 0;i < gTreeArr.length;i++){
					convertUp(gTreeArr[i],upGTree,deep,subTree);
				}
				return;
			}

			if(upGTree.getParent()!=null){
				int pIndex =  upGTree.getParent().getgTreeArray().length;
				if(upGTree.getParent().getToken() != null ){
					pIndex = upGTree.getParent().getToken().getTflag()[0].getLbs().getIndex();
				}
				if(upGTree.getIndex() > pIndex){
					return;
				}
			
			}
			
			if(gTree.getToken() != null){
				int tmpDeep = deep;
				int start = gTree.getId();
				if(gTree.getType() == GTYPE.LEAF ){
					tmpDeep = tmpDeep - 1;
				    start = gTree.getParent().getId();
				}
				List<TFlag> flagList = new ArrayList<TFlag>();
				TFlag flagArr[] = gTree.getToken().getTflag();
				if(flagArr == null || flagArr.length==0){
					return;
				}
				int group = flagArr[0].getLbs().getGroup();
				int index = flagArr[0].getLbs().getIndex();
				for(int k = 0; k < flagArr.length;k++){
					TFlag tFlag = flagArr[k];
					GTree lbs = tFlag.getLbs();
					if(lbs.getGroup() == group ){
						if(index == lbs.getIndex() ){
							flagList.add(flagArr[k]);
						}else{
							continue;
						}
					}else{
						group = lbs.getGroup();
						index = lbs.getIndex();
						flagList.add(flagArr[k]);
					}
					//if(flagArr[k].getIndex() 
					//		!= gTreeArr[i].getgTreeArray().length-1){
					//	flagList.add(flagArr[k]);
					//}
				}
				flagArr = new TFlag[flagList.size()];
				flagList.toArray(flagArr);
				//To add leaf
				int pId = upGTree.getId();
				if(subTree !=null)
					pId = subTree.getId();
				if(gTree.getType() == GTYPE.LEAF ){
					convertToLine(pId,flagArr,start,deep++,gTree.getId());
				}else{
					convertToLine(pId,flagArr,start,deep++);
				}
				
			}
			//if(gTree.getType() == GTYPE.NODE 
					//&& (gTree.getParent() == upGTree ||
					//		gTree.getParent().getName().equals(upGTree.getName()))
			//				){
			
			if(gTree.getType() == GTYPE.NODE)
				upGTree.addNode(gTree);
			
			//if(upGTree.getParent()!=null){
			int pIndex =  upGTree.getgTreeArray().length;
			if(upGTree.getToken() != null ){
				pIndex = upGTree.getToken().getTflag()[0].getLbs().getIndex();
			}
			if(gTree.getIndex() > pIndex){
				return;
			}
			
			//}
			
			if(!gTree.getName().equals(upGTree.getName())
					//&& upGTree.getType()!=GTYPE.EXPRESS
					)
				convertUp(gTree,upGTree.getParent(),deep++,subTree);
		}
		
	}
	
	private boolean isFirst = true;
	private void convertDown(GTree gTree,GTree downTree,int deep){
		if(root.equals(downTree.getName())
				|| gTree.getName().equals(downTree.getName())){
			if(isFirst == false)
				return;
			else
				isFirst = false;
		}
		if(downTree.getType() == GTYPE.LBS){
			downTree = downTree.getRel();
		}
		GTree gTreeArr[] = downTree.getgTreeArray();
		
		if(gTreeArr != null){
			for(int i = 0; i < gTreeArr.length;i++){
				int tmpDeep = deep+1;
				GTree tmp = gTreeArr[i];
				//if(gTreeArr[i].getType() == GTYPE.LBS){
				//	tmp = gTreeArr[i].getRel();
				//}
				Token token =  tmp.getToken();
				if(token != null){
					int end = gTreeArr[i].getId();
					if(tmp.getType() == GTYPE.LEAF ){
						tmpDeep = tmpDeep - 1;
						end = tmp.getParent().getId();
					}
					List<TFlag> flagList = new ArrayList<TFlag>();
					TFlag flagArr[] = token.getTflag();
					if(flagArr==null || flagArr.length==0){
						continue;
					}
					int skipGroup = -1;
					int index = flagArr[0].getLbs().getIndex();
					for(int k = 0; k < flagArr.length;k++){
						TFlag tFlag = flagArr[k];
						GTree lbs = tFlag.getLbs();
						if(lbs.getGroup() == 0 ){
							if(index == lbs.getIndex() ){
								flagList.add(flagArr[k]);
							}else{
								break;
							}
						}else{
							if(!flagList.isEmpty()){
								TFlag ltFlag = flagList.get(flagList.size()-1);
								if(ltFlag.getLbs().getGroup() == 0)
									break;
							}
							if(lbs.getGroup() != skipGroup){
								flagList.add(flagArr[k]);
								skipGroup = lbs.getGroup();
							}else{
								continue;
							}
						}
						//if(flagArr[k].getIndex() 
						//		!= gTreeArr[i].getgTreeArray().length-1){
						//	flagList.add(flagArr[k]);
						//}
					}
					flagArr = new TFlag[flagList.size()];
					flagList.toArray(flagArr);
					if(tmp.getType() == GTYPE.LEAF ){
						convertToLine(gTree.getId(),flagArr,end,tmpDeep,tmp.getId());
					}else{
						convertToLine(gTree.getId(),flagArr,end,tmpDeep);
					}
					
				}
				//convertDown(gTree,gTreeArr[i],tmpDeep);
			}
		}
	}
	
	private void convertToLine(int start, TFlag[] tFlags, int end, int deep,int lid){
		LineInfo[] lineInfoArr = createLine(start,tFlags,end,deep,lid);
		
		addLine(start,lineInfoArr);
	}
	
	private void convertToLine(int start, TFlag[] tFlags, int end, int deep){
		convertToLine(start, tFlags, end, deep,-1);
	}
	
	private LineInfo[] createLine(int start,TFlag[] flag,int end, int deep,int lid){
		LineInfo[] linArr = new LineInfo[flag.length];
		for(int i = 0; i < flag.length; i++){
			linArr[i] = new LineInfo();
			linArr[i].setStart(start);
			linArr[i].setEnd(end);
			linArr[i].setId(flag[i].getId());
			linArr[i].setToken(flag[i].getTokenFlag());
			linArr[i].setDeep(deep);
			linArr[i].setLeafId(lid);
		}
		return linArr;
		
	}
	
	public void addLine(int id,LineInfo[] lineArray){

		FastMap<Integer,List<LineInfo>> lineMap = eGraphList.get(id);
		if(lineMap == null){
			lineMap =  new FastMap<Integer,List<LineInfo>>();
			eGraph[id]=lineMap;
		}

		for(int i = 0; i < lineArray.length;i++){
			/*
				System.out.println("start="+lineArray[i].getStart()
						+",end="+lineArray[i].getEnd()
						+",token="+lineArray[i].getToken()
						+",deep="+lineArray[i].getDeep()
						+",leaf="+lineArray[i].getLeafId());
			*/

			if(!lineMap.containsKey(lineArray[i].getId())){
				List<LineInfo> list = new FastTable<LineInfo>();
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
