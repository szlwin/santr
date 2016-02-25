package santr.gtree.model;

import santr.common.util.collections.SimpleList;

public class TFlag extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7733443053081611381L;
	
	private String tokenFlag;
	
	private GTree lbs;

	private int id;
	
	private int index = 0 ;
	
	private SimpleList<TFlag> list;
	
	public String getTokenFlag() {
		return tokenFlag;
	}

	public void setTokenFlag(String tokenFlag) {
		this.tokenFlag = tokenFlag;
	}

	public GTree getLbs() {
		return lbs;
	}

	public void setLbs(GTree lbs) {
		this.lbs = lbs;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void add(TFlag tFlag){
		if(list == null){
			list = new SimpleList<TFlag>();
			list.add(this);
		}
		list.add(tFlag);
	}
	
	public TFlag get(){
		if(list == null){
			return this;
		}
		TFlag tFlag =  list.get(index);
		index++;
		if(index == list.size()){
			index = 0;
		}
		
		return tFlag;
	}
	
}
