package santr.v3.execute;

public class TreeValue {

	private boolean isToken;
	
	private Object value;
	
	private int index;

	public boolean isToken() {
		return isToken;
	}

	public void setToken(boolean isToken) {
		this.isToken = isToken;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
