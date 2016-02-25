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
	
	public TFlag getTFlag(Integer tId){
		return tFlagMap.get(tId).get();
		
	}

	public void putTFlag(int id,TFlag tFlag){
		if(!tFlagMap.containsKey(id)){
			//tFlag.add(tFlag);
			tFlagMap.put(id, tFlag);
		}else{
			tFlagMap.get(id).add(tFlag);
		}
		
	}
	
	/*public Map<String,TFlag> getTFlagMap(){
		return tFlagMap;
	}*/
	public boolean containsKey(int id){
		return tFlagMap.containsKey(id);
	}
	
}
