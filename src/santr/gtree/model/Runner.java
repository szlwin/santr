package santr.gtree.model;

import java.util.List;

import javolution.util.FastTable;

public class Runner {
	
	private List<GTree> gTreeList = new FastTable<GTree>();;

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
}
