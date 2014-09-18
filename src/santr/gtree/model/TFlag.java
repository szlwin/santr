package santr.gtree.model;

public class TFlag extends BData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7733443053081611381L;
	
	private String tokenFlag;
	
	private GTree lbs;

	private int id;
	
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
	
	
}
