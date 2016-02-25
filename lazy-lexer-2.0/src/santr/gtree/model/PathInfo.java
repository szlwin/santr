package santr.gtree.model;

public class PathInfo {

	private GTree leaf;
	
	private GTree lbs;

	public PathInfo(){
		
	}
	
	public PathInfo(GTree leaf,GTree lbs){
		this.leaf = leaf;
		this.lbs = lbs;
	}
	
	public GTree getLeaf() {
		return leaf;
	}

	public void setLeaf(GTree leaf) {
		this.leaf = leaf;
	}

	public GTree getLbs() {
		return lbs;
	}

	public void setLbs(GTree lbs) {
		this.lbs = lbs;
	}
	
	
}
