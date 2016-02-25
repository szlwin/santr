package santr.v3.execute;

import java.util.List;

import santr.v3.parser.data.RTree;



public class Context {
	
	private RTree rTree;
	
	private Object value;
	
	private Object param;
	
	private int index = 0;

	private boolean isFinish;
	
	public int getChildCount() {
		return this.rTree.getrTreeList()
				.size();
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String getName(){
		return rTree.getName();
	}
	
	protected void setrTree(RTree rTree) {
		this.rTree = rTree;
	}

	protected Object getValue() {
		return value;
	}
	
	protected List<RTree> getAllChild() {
		return rTree.getrTreeList();
	}
	

	protected int getIndex() {
		return index;
	}

	protected void setIndex(int index) {
		if(index > this.index){
			this.index = index;
		}
	}
	protected RTree getSubTree(int index){
		return this.rTree.getrTreeList().get(index);
	}
	
	protected boolean isFinish() {
		return isFinish;
	}

	protected void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public Object getChildValue(int index){
		RTree subTree = rTree.getrTreeList().get(index);

		return subTree.getContext().getValue();
	}
	
	public String getText(){
		return rTree.getText();
	}
	/*
	public String getNextToken(){
		List<RTree> rTreeList = rTree.getrTreeList();
		//Context context = rTree.getContext();
		for(int i = this.getIndex(); i < rTreeList.size();i++){
			RTree subTree = rTreeList.get(i);
			if(subTree.getType() == BData.FLAG_TYPE_TOKEN){
				this.setIndex(i);
				return subTree.getText();
			}
		}
		return null;
	}*/

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}
	
	
}
