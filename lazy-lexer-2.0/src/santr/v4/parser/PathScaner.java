package santr.v4.parser;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import santr.common.util.collections.SimpleList;
import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.gtree.model.Runner;
import santr.gtree.model.TFlag;
import santr.gtree.model.enume.GTYPE;
import santr.v4.parser.ParserTree;


public class PathScaner {
	
	private List<LineInfo> lineInfoList = new SimpleList<LineInfo>(6,3);
	private List<Runner> runnerList = new SimpleList<Runner>(6,3);
	
	private String token;
	
	private int tId;
	
	private GTree first;
	
	private Map<String,String> endMap;
	private Map<GTree,String> searchMap;
	public PathScaner(){
		endMap =  new FastMap<String,String>();
		searchMap = new FastMap<GTree,String>();
	}
	
	public void scan(ParserTree rTree,int tId,String token){
		//System.out.println(token);
		this.token = token;
		this.tId = tId;
		this.first = rTree.getRuleContext().getLeaf();
		//int start = rTree.getrTreeList() == null ? 0 : rTree.getrTreeList().size();
		int start = 0;
		Runner runner = new Runner();
		scanChild(first,start,runner);
		
		if(runnerList.isEmpty()){
			scanParent(rTree);
		}else{
			convert();
		}
	
	}
	
	private void scanParent(ParserTree rTree) {
		if(rTree == null)
			return;
		GTree gTree = rTree.getRuleContext().getLeaf();
		if(gTree.getToken() != null
				&& gTree.getToken().containsKey(tId)){
			//GTree tokenLbs = gTree.getToken().getTFlag(tId).getLbs();
			//if(rTree.getRuleContext().checkExpress(tokenLbs)){
				LineInfo lineInfo = createLine(gTree,null,-1);
				lineInfoList.add(lineInfo);
				return;
			//}
		}

		if(gTree.getType() == GTYPE.LEAF && gTree.getParent().isExistLevel()
				//&& gTree.getLevel() > 0
				){
			gTree = gTree.getParent();
			
			for(GTree subTree:gTree.getgTreeArray()){
				if(subTree.getToken() != null
						&& subTree.getToken().containsKey(tId)){
					//GTree tokenLbs = gTree.getToken().getTFlag(tId).getLbs();
					//if(rTree.getRuleContext().checkExpress(tokenLbs)){
						LineInfo lineInfo = createLine(subTree,null,-1);
						lineInfoList.add(lineInfo);
						return;
					//}
				}
			}
		}/*else{
			if(gTree.isExitFork() && !gTree.isExistLevel()){
				for(GTree subTree:gTree.getgTreeArray()){
					if(subTree.getToken() != null
							&& subTree.getToken().containsKey(tId)){
						GTree tokenLbs = subTree.getToken().getTFlag(tId).getLbs();
						if(rTree.getRuleContext().checkExpress(tokenLbs)){
							LineInfo lineInfo = createLine(subTree,null,0);
							lineInfoList.add(lineInfo);
							return;
						}
					}
				}
			}
		}*/


		scanParent(rTree.getParent()==null?rTree.getPreTree():rTree.getParent());
	}
/*
	private void scanSelf(GTree gTree){
		
		if(!gTree.isExitFork()
				&& gTree.getToken()!=null
				&& gTree.getToken().containsKey(tId)){
			LineInfo lineInfo = createLine(gTree,null,0);
			lineInfoList.add(lineInfo);
		}else{
			GTree gTreeArr[] = gTree.getgTreeArray();
			if(gTreeArr!=null){

				for(int i =0 ;i < gTreeArr.length;i++){
					if(gTreeArr[i].getType() == GTYPE.LEAF 
							&& gTreeArr[i].getToken() !=null 
							&& gTreeArr[i].getToken().containsKey(tId)){
						LineInfo lineInfo = createLine(gTreeArr[i],null,0);
						lineInfoList.add(lineInfo);
					}
				}
			}
		}
	}*/
	
	private void scanChild(GTree parentTree,int start, Runner runner){
		if(searchMap.containsKey(parentTree)){
			return;
		}
		searchMap.put(parentTree, null);
		GTree pTree = parentTree;
		if(parentTree.getType() == GTYPE.LBS){
			pTree = parentTree.getRel();
		}
		GTree gTreeArr[] = pTree.getgTreeArray();
		if(gTreeArr == null){
			return;
		}
		int index = gTreeArr.length;
		if(pTree.getToken() != null && pTree != first){
			index = pTree.getToken().getTflag()[0].getLbs().getIndex();
		}
		
		for(int i =start ; i <gTreeArr.length;i++ ){
			GTree subTree = gTreeArr[i];
			//System.out.println(subTree.getId());
			//subTree = subTree.getRel();
			if(subTree.getType() != GTYPE.NODE 
					//&& subTree.getId() >=0 
					&& (subTree.getIndex()<index) || subTree.getType() == GTYPE.LEAF
					&& !endMap.containsKey(subTree.getName()))
			{
				
				Runner subRunner = copy(runner);
				GTree rel = subTree.getRel();
				
				if(rel.getToken() != null
						&& rel.getToken().containsKey(tId)){
					
					TFlag tokenFlag = rel.getToken().getTFlag(tId);

					TFlag[] tflag = rel.getToken().getTflag();
					
					for(int j = 0; j < tflag.length;j++){
						if((tflag[j].getLbs().getGroup() == 0 || 
								tflag[j].getLbs().getGroup() == tokenFlag.getLbs().getGroup())
								&& tflag[j].getLbs().getIndex() < tokenFlag.getLbs().getIndex()){
							return;
						}
					}

					if(!endMap.containsKey(subTree.getName())){
						if(subTree.getType() == GTYPE.LEAF){
							subRunner.setLast(subTree);
						}else{
							subRunner.add(subTree);
						}
						runnerList.add(subRunner);
						endMap.put(subTree.getName(), null);
					}
				}else{

					if(subTree.getType() == GTYPE.LEAF){
						subRunner.setLast(subTree);
					}else{
						subRunner.add(subTree);
					}
	
					if(subTree.getType() == GTYPE.LBS){
						subTree = subTree.getRel();
					}
					if(subTree.getType() != GTYPE.NODE
							&& subTree != first){
						
						if(subTree.getId() == 0){
							subRunner.setLoop(true);
						}
						scanChild(subTree,0,subRunner);
					}
				}
			}
		}
	}
	
	private void convert(){
		for(int i = 0;i < runnerList.size();i++){
			Runner runner = runnerList.get(i);
			GTree gTree = runner.getLast();
			LineInfo lineInfo = createDownLine(runner.getFirst(),gTree,runner,1);
			lineInfoList.add(lineInfo);
		}
	}

	private Runner copy(Runner sourceRunner){
		Runner runner = new Runner();
		runner.addAll(sourceRunner.getgTreeList());
		runner.setLoop(sourceRunner.isLoop());
		return runner;
	}
	
	public List<LineInfo> getLineInfoList() {
		return lineInfoList;
	}
	
	private LineInfo createLine(GTree gTree,Runner runner,int deep){
		LineInfo lineInfo = new LineInfo();
		//if(first.getType() == GTYPE.LEAF){
		//	lineInfo.setStart(first.getParent().getId());
		//}else{
			lineInfo.setStart(first.getId());
		//}
		
		if(gTree.getType() == GTYPE.LEAF){
			lineInfo.setEnd(gTree.getParent().getId());
			lineInfo.setLeafId(gTree.getId());
		}else{
			lineInfo.setEnd(gTree.getId());
			lineInfo.setLeafId(-1);
		}
		//if(runner!=null && runner.isLoop()){
			lineInfo.setRunner(runner);
		//}
		lineInfo.setExpress(gTree);
		lineInfo.setId(tId);
		lineInfo.setDeep(deep);
		lineInfo.setToken(token);
		
		return lineInfo;
	}
	
	
	private LineInfo createDownLine(GTree gTree,GTree last, Runner runner,int deep){
		LineInfo lineInfo = new LineInfo();
		lineInfo.setStart(first.getId());
		//GTree gtree = lineInfoList.get(0).g;
		
		if(gTree.getType() == GTYPE.LEAF){
			lineInfo.setLeafId(gTree.getId());
		}else{
			lineInfo.setLeafId(-1);
		}
		
		if(gTree.getName().equals(first.getName())){
			runner.getgTreeList().remove(0);
		}
		
		lineInfo.setEnd(last.getId());
		lineInfo.setExpress(last.getRel());
		/*
		if(gTree.getType() == GTYPE.LEAF){
			lineInfo.setEnd(gTree.getParent().getId());
			lineInfo.setLeafId(gTree.getId());
		}else{
			lineInfo.setEnd(gTree.getId());
			lineInfo.setLeafId(-1);
		}*/
		//if(runner!=null && runner.isLoop()){
			lineInfo.setRunner(runner);
		//}
		
		lineInfo.setId(tId);
		lineInfo.setDeep(deep);
		lineInfo.setToken(token);
		
		return lineInfo;
	}
}
