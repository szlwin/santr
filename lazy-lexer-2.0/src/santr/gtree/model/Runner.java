package santr.gtree.model;

import java.util.List;

import javolution.util.FastTable;

public class Runner {
	
	private List<GTree> gTreeList = new FastTable<GTree>();

	private boolean isLoop = false;
	
	private GTree[] gTreeArr = null;
	
	public Runner(){

	}
	
	public Runner(GTree gTree){
		gTreeList.add(gTree);
	}
	
	public void add(GTree gTree){
		gTreeList.add(gTree);
	}
	
	
	public void addAll(List<GTree> list){
		gTreeList.addAll(list);
		//last = gTreeList.get(gTreeList.size()-1);
	}

	public List<GTree> getgTreeList() {
		return gTreeList;
	}
	
	public GTree getLast(){
		return gTreeList.get(gTreeList.size()-1);
	}

	public boolean isLoop() {
		return isLoop;
	}

	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}
	
	public void setLast(GTree gTree){
		if(gTreeList.isEmpty()){
			gTreeList.add(gTree);
		}else{
			gTreeList.set(gTreeList.size()-1, gTree);
		}
		
	}
	
	public GTree getFirst(){
		return gTreeList.get(0);
	}
	
	public GTree[] gTreeArr(){
		if(gTreeArr == null){
			gTreeArr =  new  GTree[gTreeList.size()];
			gTreeList.toArray(gTreeArr);
			gTreeList.clear();
		}
		
		
		return gTreeArr;
	}
}
