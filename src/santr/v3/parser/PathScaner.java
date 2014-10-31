package santr.v3.parser;

import java.util.List;

import javolution.util.FastTable;

import santr.gtree.model.GTree;
import santr.gtree.model.LineInfo;
import santr.gtree.model.Runner;
import santr.gtree.model.TFlag;
import santr.gtree.model.enume.GROUPTYPE;
import santr.gtree.model.enume.GTYPE;
import santr.v3.parser.data.RTree;


public class PathScaner {
	
	private List<LineInfo> lineInfoList = new FastTable<LineInfo>();
	private List<Runner> runnerList = new FastTable<Runner>();
	
	private String token;
	
	private int tId;
	
	private GTree first;
	public void scan(RTree rTree,int tId,String token){
		//System.out.println(token);
		this.token = token;
		this.tId = tId;
		if(rTree.getLeaf()!=null){
			this.first = rTree.getLeaf();
		}else{
			this.first = rTree.getLbs().getRel();
		}
		
		//online(rTree);
		down(first,new Runner());
		if(runnerList.isEmpty()){
			up(rTree.getParentTree());
		}else{
			convert();
		}
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
	
	private void online(RTree rTree){
		if(rTree.getLeaf()!=null
				&& rTree.getLeaf().getToken() !=null 
				&& rTree.getLeaf().getToken().containsKey(tId)){
			LineInfo lineInfo = createLine(rTree.getLeaf(),null,0);
			lineInfoList.add(lineInfo);
		}else{
			GTree gTree = rTree.getLbs().getRel();
			if(gTree.getToken() !=null 
					&& gTree.getToken().containsKey(tId)){
				LineInfo lineInfo = createLine(gTree,null,0);
				lineInfoList.add(lineInfo);
				return;
			}
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
		
	}
	
	private void up(RTree rTree){
		
		if(rTree == null)
			return;
		GTree gTree = rTree.getLeaf();
		if(gTree == null)
			gTree = rTree.getLbs().getRel();
		if(gTree.getToken() != null
				&& gTree.getToken().containsKey(tId)){
			GTree tokenLbs = gTree.getToken().getTFlag(tId).getLbs();
			if(checkExpress(rTree.getLast(),tokenLbs,rTree)){
				LineInfo lineInfo = createLine(gTree,null,-1);
				/*lineInfo.setStart(first.getId());
				if(gTree.getType() == GTYPE.LEAF){
					lineInfo.setEnd(gTree.getParent().getId());
					lineInfo.setLeafId(gTree.getId());
				}else{
					lineInfo.setEnd(gTree.getId());
					lineInfo.setLeafId(-1);
				}
				lineInfo.setId(tId);
				lineInfo.setDeep(-1);
				lineInfo.setToken(token);*/
				lineInfoList.add(lineInfo);
				return;
			}
		}
		up(rTree.getParentTree());
	}
	private void down(GTree parentTree,Runner runner){
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
		
		for(int i =0 ; i <gTreeArr.length;i++ ){
			GTree subTree = gTreeArr[i];

			//System.out.println(subTree.getId());

			if(subTree.getId() >=0 
					&& (subTree.getIndex()<index) || subTree.getType() == GTYPE.LEAF){

				Runner subRunner = copy(runner);

				if(subTree.getToken() != null
						&& subTree.getToken().containsKey(tId)){
					
					TFlag tokenFlag = subTree.getToken().getTFlag(tId);

					TFlag[] tflag = subTree.getToken().getTflag();
					
					for(int j = 0; j < tflag.length;j++){
						if((tflag[j].getLbs().getGroup() == 0 || 
								tflag[j].getLbs().getGroup() == tokenFlag.getLbs().getGroup())
								&& tflag[j].getLbs().getIndex() < tokenFlag.getLbs().getIndex()){
							return;
						}
					}
					
					subRunner.add(subTree);
					runnerList.add(subRunner);
				}else{

					subRunner.add(subTree);
	
					if(subTree.getType() == GTYPE.LBS){
						subTree = subTree.getRel();
					}
					if(subTree.getType() != GTYPE.NODE
							&& subTree != first){
						
						if(subTree.getId() == 0){
							subRunner.setLoop(true);
						}
						down(subTree,subRunner);
					}
				}
			}
		}
	}
	
	private void convert(){
		for(int i = 0;i < runnerList.size();i++){
			Runner runner = runnerList.get(i);
			GTree gTree = runner.getLast();
			LineInfo lineInfo = createLine(gTree,runner,1);
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
		lineInfo.setStart(first.getId());
		if(gTree.getType() == GTYPE.LEAF){
			lineInfo.setEnd(gTree.getParent().getId());
			lineInfo.setLeafId(gTree.getId());
		}else{
			lineInfo.setEnd(gTree.getId());
			lineInfo.setLeafId(-1);
		}
		if(runner!=null && runner.isLoop()){
			lineInfo.setRunner(runner);
		}
		
		lineInfo.setId(tId);
		lineInfo.setDeep(deep);
		lineInfo.setToken(token);
		
		return lineInfo;
	}
	

}
