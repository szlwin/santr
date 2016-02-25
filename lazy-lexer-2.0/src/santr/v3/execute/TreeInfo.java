package santr.v3.execute;

public class TreeInfo {

	private String name;
	
	private boolean isToken;
	
	private String token;
	
	private int index;

	public boolean isToken() {
		return isToken;
	}

	protected void setToken(boolean isToken) {
		this.isToken = isToken;
	}

	public int getIndex() {
		return index;
	}

	protected void setIndex(int index) {
		this.index = index;
	}

	public String getToken() {
		return token;
	}

	protected void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	
}
