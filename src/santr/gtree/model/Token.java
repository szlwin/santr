package santr.gtree.model;

import javolution.util.FastMap;

public class Token extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7047289695652418797L;

	private TFlag tflag[];

	private FastMap<Integer,TFlag> tFlagMap = new FastMap<Integer,TFlag>();
	
	public TFlag[] getTflag() {
		return tflag;
	}

	public void setTflag(TFlag[] tflag) {
		this.tflag = tflag;
	}
	
	public TFlag getTFlag(int id){
		return tFlagMap.get(id);
		
	}

	public void putTFlag(int id,TFlag tFlag){
		tFlagMap.put(id, tFlag);
	}
	
	/*public Map<String,TFlag> getTFlagMap(){
		return tFlagMap;
	}*/
	public boolean containsKey(int id){
		return tFlagMap.containsKey(id);
	}
	
}
